package net.rim.device.apps.internal.calendar.ota;

import java.util.Enumeration;
import java.util.TimeZone;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.system.ObjectGroup;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.system.RIMGlobalMessagePoster;
import net.rim.device.apps.api.calendar.caldb.CalDB;
import net.rim.device.apps.api.calendar.caldb.CalendarService;
import net.rim.device.apps.api.calendar.caldb.CalendarServiceManager;
import net.rim.device.apps.api.calendar.modelcontrollerinterface.Attendee;
import net.rim.device.apps.api.calendar.modelcontrollerinterface.Event;
import net.rim.device.apps.api.calendar.modelcontrollerinterface.EventUtilities;
import net.rim.device.apps.api.calendar.modelcontrollerinterface.MeetingInfo;
import net.rim.device.apps.api.calendar.modelcontrollerinterface.RecurUtilities;
import net.rim.device.apps.api.calendar.ota.CICALEventLogger;
import net.rim.device.apps.api.calendar.ota.OTACalendarConstants;
import net.rim.device.apps.api.sync.OTASyncData;
import net.rim.device.apps.api.transmission.TransmissionService;
import net.rim.device.apps.api.transmission.rim.RIMMessagingIncomingMessage;
import net.rim.device.apps.api.utility.general.Copyable;
import net.rim.device.apps.api.utility.props.LongProp;
import net.rim.device.internal.proxy.Proxy;

class OTAMeetingListener extends OTABaseListener implements OTACalendarConstants, GlobalEventListener {
   private OTAMeetingListener$PendingMeetingResponseManager _responseQueue = new OTAMeetingListener$PendingMeetingResponseManager(this);
   private CalendarServiceManager _calendarServiceManager;
   private static final long REMINDER_FIELD = 6784165211264038547L;

   OTAMeetingListener() {
      this._responseQueue.processAllPendingResponses();
      this._calendarServiceManager = CalendarServiceManager.getInstance();
      Proxy.getInstance().addGlobalEventListener(this);
   }

   private int locateMeetingAttachment(RIMMessagingIncomingMessage message, Event meeting) {
      for (int i = message.getAttachmentCount() - 1; i >= 0; i--) {
         Object attachment = message.getAttachment(i);
         if (attachment instanceof CICALMeetingAttachmentModel) {
            CICALMeetingAttachmentModel meetingAttachment = (CICALMeetingAttachmentModel)attachment;
            if (meeting == null || meetingAttachment.getEvent(0) == meeting) {
               return i;
            }
         }
      }

      return -1;
   }

   private CICALMeetingAttachmentModel getMeetingAttachment(RIMMessagingIncomingMessage message, Event meeting) {
      int index = this.locateMeetingAttachment(message, meeting);
      return index >= 0 ? (CICALMeetingAttachmentModel)message.getAttachment(index) : null;
   }

   private void removeMeetingAttachment(RIMMessagingIncomingMessage message, Event meeting) {
      int index = this.locateMeetingAttachment(message, meeting);
      if (index >= 0) {
         message.removeAttachment(index);
      }
   }

   @Override
   public boolean receiveObject(TransmissionService service, Object anObject, Object contextObject) {
      if (!(anObject instanceof RIMMessagingIncomingMessage)) {
         return false;
      }

      Object ticket = PersistentContent.getTicket();
      RIMMessagingIncomingMessage message = (RIMMessagingIncomingMessage)anObject;
      CICALMeetingAttachmentModel meetingAttachment = this.getMeetingAttachment(message, null);
      if (meetingAttachment == null) {
         return false;
      }

      if (ticket != null) {
         CICALEventLogger.logEvent(1380799568, 4, meetingAttachment.getBackupData());
      }

      Event meeting = null;
      boolean result = true;
      Object[] events = null;
      events = meetingAttachment.getEvents();
      if (events == null) {
         return false;
      }

      int i = 0;
      int attachmentType = meetingAttachment.getType();
      if (attachmentType == 2 && ticket == null) {
         this._responseQueue.add(events);
         CICALEventLogger.logEvent(1297241932, 4, meetingAttachment.getBackupData());
         return false;
      }

      while (i < events.length) {
         meeting = (Event)events[i];
         CalendarService calendarService = this._calendarServiceManager.findCalendarService(meeting);
         if (calendarService.getServiceRecord().getType() != 0) {
            result = false;
         } else {
            switch (attachmentType) {
               case 0:
                  super._otaSyncDataManager.remove(meeting);
                  result = false;
                  break;
               case 1:
               default:
                  result &= this.processMeetingRequest(meeting, message, i);
                  break;
               case 2:
                  result &= this.processMeetingResponse(meeting, message, i);
                  break;
               case 3:
                  result &= this.processMeetingCancel(meeting, message, i);
            }

            i++;
         }
      }

      ticket = null;
      RIMGlobalMessagePoster.postGlobalEvent(5483692278053761660L, 0, 0, null, null);
      return result;
   }

   private void copyMinimalEventInfo(Event realEvent, Event tmpEvent) {
      TimeZone timeZone = TimeZone.getDefault();
      tmpEvent.setAllDayFlag(realEvent.getAllDayFlag());
      tmpEvent.setStartDate(realEvent.getStartDate(timeZone), timeZone);
      tmpEvent.setInstanceDuration(realEvent.getInstanceDuration());
      Object ticket = PersistentContent.getTicket();
      if (ticket != null) {
         tmpEvent.setLocation(realEvent.getLocation());
      }

      ticket = null;
      tmpEvent.setRecurrence(realEvent.getRecurrenceCopy());
      long reminderDelta = -1;
      LongProp reminder = (LongProp)realEvent.get(6784165211264038547L);
      if (reminder != null) {
         reminderDelta = reminder.getLong();
      }

      if (reminderDelta != 0) {
         reminder = (LongProp)tmpEvent.get(6784165211264038547L);
         if (reminder != null) {
            reminder.setLong(reminderDelta);
         }
      }
   }

   private boolean processMeetingRequest(Event meeting, RIMMessagingIncomingMessage incoming, int index) {
      long luid = meeting.getLUID();
      CalendarService calendarService = CalendarServiceManager.getInstance().findCalendarService(meeting);
      CalDB calendar = calendarService.getCalendarDatabase();
      if (!this.checkInSequence(meeting, calendarService)) {
         CICALEventLogger.logEvent(1381126442, 3, null, luid);
         super._otaSyncDataManager.remove(meeting);
         return false;
      }

      if (calendarService.getCICALConfiguration().canPopulateCalendarFromMeetingRequest()) {
         CICALEventLogger.logEvent(1381126443, 4, null, luid);
         if (ObjectGroup.isInGroup(meeting)) {
            meeting = (Event)ObjectGroup.expandGroup(meeting);
         }

         OTASyncData syncData = super._otaSyncDataManager.get(meeting);
         if (syncData == null) {
            syncData = new OTASyncData(0, 1);
         }

         synchronized (calendar.getLockObject()) {
            syncData.updateChecksum(EventUtilities.getHashData(meeting), false);
         }

         super._otaSyncDataManager.add(meeting, syncData);
         meeting = RecurUtilities.processRecurrenceChanges(calendarService, meeting);
         calendar.add(meeting);
         RecurUtilities.ensureMarkedAsExclusion(calendar, meeting);
      }

      return false;
   }

   private boolean processMeetingResponse(Event meeting, RIMMessagingIncomingMessage incoming, int index) {
      OTASyncData syncData = super._otaSyncDataManager.get(meeting);
      long luid = meeting.getLUID();
      CalendarService calendarService = CalendarServiceManager.getInstance().findCalendarService(meeting);
      CalDB calendar = calendarService.getCalendarDatabase();
      if (!this.checkInSequence(meeting, calendarService)) {
         CICALEventLogger.logEvent(1381126954, 3, null, luid);
         super._otaSyncDataManager.remove(meeting);
         this.removeMeetingAttachment(incoming, meeting);
         return false;
      }

      Event realEvent = (Event)calendar.get(meeting.getLUID());
      boolean changesMade = false;
      if (realEvent == null) {
         long relatedLUID = meeting.getRelatedLUID();
         if (relatedLUID == 0) {
            CICALEventLogger.logEvent(1380139309, 4, null, luid);
            if (incoming != null) {
               this.removeMeetingAttachment(incoming, meeting);
            }

            return false;
         }

         Event relatedEvent = (Event)calendar.get(relatedLUID);
         if (relatedEvent == null) {
            CICALEventLogger.logEvent(1380139306, 4, null, luid);
            if (incoming != null) {
               this.removeMeetingAttachment(incoming, meeting);
            }

            return false;
         }

         if (ObjectGroup.isInGroup(relatedEvent)) {
            relatedEvent = (Event)ObjectGroup.expandGroup(relatedEvent);
         }

         realEvent = (Event)((Copyable)relatedEvent).copy();
         realEvent.setUID(meeting.getUID());
         realEvent.setRelatedLUID(relatedLUID);
         TimeZone tz = TimeZone.getTimeZone(meeting.getTimeZoneID());
         realEvent.setStartDate(meeting.getStartDate(tz), tz);
         realEvent.setRelatedTime(meeting.getStartDate(tz));
         RecurUtilities.ensureMarkedAsExclusion(calendar, realEvent);
         changesMade = true;
         if (syncData == null) {
            syncData = new OTASyncData(0, 1);
         }
      }

      MeetingInfo meetingInfo = meeting.getMeetingInfo();
      MeetingInfo realMeetingInfo = realEvent.getMeetingInfo();
      if (meetingInfo != null && realMeetingInfo != null) {
         Enumeration attendees = meetingInfo.getAttendees();

         while (attendees.hasMoreElements()) {
            Attendee attendee = (Attendee)attendees.nextElement();
            Object address = attendee.getAddress();
            if (address != null) {
               Attendee realAttendee = realMeetingInfo.getAttendee(address.toString());
               if (realAttendee != null && attendee.getType() != realAttendee.getType()) {
                  if (ObjectGroup.isInGroup(realEvent)) {
                     realEvent = (Event)ObjectGroup.expandGroup(realEvent);
                     realMeetingInfo = realEvent.getMeetingInfo();
                     realAttendee = realMeetingInfo.getAttendee(address.toString());
                  }

                  CICALEventLogger.logEvent(1381126955, 4, null, luid);
                  realAttendee.setType(attendee.getType());
                  changesMade = true;
               }
            }
         }
      }

      super._otaSyncDataManager.remove(meeting);
      if (syncData != null) {
         super._otaSyncDataManager.add(realEvent, syncData);
      } else {
         CICALEventLogger.logEvent(1381126945, 2, null, luid);
      }

      if (changesMade) {
         calendar.addWithAction(realEvent, 7);
      }

      if (incoming != null) {
         CICALMeetingAttachmentModel meetingAttachment = this.getMeetingAttachment(incoming, meeting);
         if (meetingAttachment == null) {
            return false;
         }

         if (ObjectGroup.isInGroup(meeting)) {
            meeting = (Event)ObjectGroup.expandGroup(meeting);
         }

         this.copyMinimalEventInfo(realEvent, meeting);

         label132:
         try {
            String subject = meeting.getSubject();
            if (subject == null || subject.length() <= 0) {
               meeting.setSubject((String)incoming.getSubject());
            }
         } finally {
            break label132;
         }

         meetingAttachment.setEvent(meeting, index);
      }

      return false;
   }

   private boolean processMeetingCancel(Event meeting, RIMMessagingIncomingMessage incoming, int index) {
      CalendarService calendarService = CalendarServiceManager.getInstance().findCalendarService(meeting);
      CalDB calendar = calendarService.getCalendarDatabase();
      boolean inSequence = this.checkInSequence(meeting, calendarService);
      long luid = meeting.getLUID();
      super._otaSyncDataManager.remove(meeting);
      if (!inSequence) {
         CICALEventLogger.logEvent(1380139306, 3, null, luid);
         this.removeMeetingAttachment(incoming, meeting);
         return false;
      }

      Event realEvent = (Event)calendar.get(luid);
      if (realEvent != null) {
         CICALEventLogger.logEvent(1380139307, 4, null, luid);
         EventUtilities.removeEvent(realEvent, true);
         CICALMeetingAttachmentModel meetingAttachment = this.getMeetingAttachment(incoming, meeting);
         if (meetingAttachment == null) {
            return false;
         }

         if (ObjectGroup.isInGroup(meeting)) {
            meeting = (Event)ObjectGroup.expandGroup(meeting);
         }

         this.copyMinimalEventInfo(realEvent, meeting);

         label61:
         try {
            String subject = meeting.getSubject();
            if (subject == null || subject.length() <= 0) {
               meeting.setSubject((String)incoming.getSubject());
            }
         } finally {
            break label61;
         }

         meetingAttachment.setEvent(meeting, index);
         return false;
      } else {
         if (meeting.getRelatedLUID() == 0) {
            CICALEventLogger.logEvent(1380139309, 4, null, luid);
            if (calendarService.getCICALConfiguration().canPopulateCalendarFromMeetingRequest()) {
               this.removeMeetingAttachment(incoming, meeting);
               return false;
            }
         } else {
            RecurUtilities.ensureMarkedAsDeleted(calendar, meeting);
         }

         return false;
      }
   }

   @Override
   public void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid == 6345609069135580235L) {
         this._responseQueue.processAllPendingResponses();
      }
   }
}
