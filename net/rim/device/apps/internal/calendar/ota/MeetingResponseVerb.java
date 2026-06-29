package net.rim.device.apps.internal.calendar.ota;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.system.ObjectGroup;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.apps.api.calendar.caldb.CalDB;
import net.rim.device.apps.api.calendar.caldb.CalendarService;
import net.rim.device.apps.api.calendar.caldb.CalendarServiceManager;
import net.rim.device.apps.api.calendar.modelcontrollerinterface.Attendee;
import net.rim.device.apps.api.calendar.modelcontrollerinterface.Event;
import net.rim.device.apps.api.calendar.modelcontrollerinterface.EventUtilities;
import net.rim.device.apps.api.calendar.modelcontrollerinterface.MeetingInfo;
import net.rim.device.apps.api.calendar.modelcontrollerinterface.RecurUtilities;
import net.rim.device.apps.api.calendar.ota.CICALConfiguration;
import net.rim.device.apps.api.calendar.ota.OTACalendarSyncDataManager;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.sync.Checksumable;
import net.rim.device.apps.api.sync.OTASyncData;

final class MeetingResponseVerb extends Verb {
   private int _verbAction;
   private CICALMeetingAttachmentModel _attachment;
   private RIMModel _message;
   private CalendarServiceManager _calendarServiceManager;
   public static final int ACCEPT = 0;
   public static final int ACCEPT_WITH_COMMENTS = 1;
   public static final int TENTATIVE = 2;
   public static final int TENTATIVE_WITH_COMMENTS = 3;
   public static final int DECLINE = 4;
   public static final int DECLINE_WITH_COMMENTS = 5;
   private static OTACalendarSyncDataManager _otaSyncDataManager = OTACalendarSyncDataManager.getInstance();

   MeetingResponseVerb(int type, CICALMeetingAttachmentModel attachment, RIMModel message) {
      super(0);
      this._attachment = attachment;
      this._message = message;
      this._verbAction = type;
      super._ordering = 335872 + this._verbAction;
      this._calendarServiceManager = CalendarServiceManager.getInstance();
   }

   private final Event getResponseEvent(Event originalEvent) {
      OTASyncData syncData = _otaSyncDataManager.get(originalEvent);
      CalendarService calendarService = this._calendarServiceManager.findCalendarService(originalEvent);
      CalDB calendar = calendarService.getCalendarDatabase();
      CICALConfiguration cicalConfiguration = calendarService.getCICALConfiguration();
      Event actualEvent = (Event)calendar.get(originalEvent.getLUID());
      OTASyncData actualSyncData = null;
      if (actualEvent != null) {
         actualSyncData = _otaSyncDataManager.get(actualEvent);
      }

      if (originalEvent == actualEvent) {
         return actualEvent;
      }

      if (syncData != null && syncData.equals(actualSyncData)) {
         return actualEvent;
      }

      if (!cicalConfiguration.canPopulateCalendarFromMeetingRequest()) {
         return originalEvent;
      }

      if (originalEvent instanceof Checksumable) {
         Checksumable originalChecksumable = (Checksumable)originalEvent;
         if (actualEvent instanceof Checksumable) {
            Checksumable actualChecksumable = (Checksumable)actualEvent;
            long originalChecksum = originalChecksumable.getChecksum(null);
            long actualChecksum = actualChecksumable.getChecksum(null);
            if (originalChecksum == actualChecksum) {
               return actualEvent;
            }
         }
      }

      return actualEvent != null ? actualEvent : originalEvent;
   }

   private final void sendResponse(String comments) {
      Object[] events = this._attachment.getResolvedEvents(this._message);
      Event event = null;
      OTASyncData syncData = null;
      Event responseEvent = null;
      boolean eventsValid = true;
      boolean eventExists = false;
      if (events != null && events.length > 0) {
         for (int i = 0; i < events.length; i++) {
            event = (Event)events[i];
            syncData = _otaSyncDataManager.get(event);
            CalendarService calendarService = this._calendarServiceManager.findCalendarService(event);
            CalDB calendar = calendarService.getCalendarDatabase();
            CICALConfiguration cicalConfiguration = calendarService.getCICALConfiguration();
            eventExists = calendar.get(event.getLUID()) != null;
            if ((this._verbAction == 0 || this._verbAction == 1 || this._verbAction == 2 || this._verbAction == 3)
               && (!cicalConfiguration.canPopulateCalendarFromMeetingRequest() || cicalConfiguration.canPopulateCalendarFromMeetingRequest() && !eventExists)) {
               event = RecurUtilities.processRecurrenceChanges(calendarService, event);
               calendar.addWithAction(event, 7);
               responseEvent = this.getResponseEvent(event);
               RecurUtilities.ensureMarkedAsExclusion(calendar, responseEvent);
            }

            if (this._attachment.getType() == 1 && (!cicalConfiguration.canPopulateCalendarFromMeetingRequest() || syncData == null)) {
               syncData = this._attachment.getSyncData();
               if (syncData == null) {
                  _otaSyncDataManager.add(event, syncData);
                  PersistentObject.commit(syncData);
               }

               eventsValid &= syncData != null;
            }
         }
      } else {
         eventsValid = false;
      }

      if (eventsValid) {
         try {
            responseEvent = this.getResponseEvent((Event)events[0]);
            int status = 1;
            byte FBFlag = 2;
            switch (this._verbAction) {
               case -1:
                  break;
               case 0:
               case 1:
               default:
                  status = 2;
                  break;
               case 2:
               case 3:
                  status = 4;
                  FBFlag = 1;
                  break;
               case 4:
               case 5:
                  status = 3;
            }

            if (responseEvent != null) {
               if (ObjectGroup.isInGroup(responseEvent)) {
                  responseEvent = (Event)ObjectGroup.expandGroup(responseEvent);
               }

               responseEvent.setFreeBusy(FBFlag);
               CICALMeetingEmailer.sendMeetingResponse(this._attachment, responseEvent, this._message, status, comments);
            }

            int i = 0;
            boolean exclusionMarked = false;

            while (i < events.length) {
               responseEvent = this.getResponseEvent((Event)events[i]);
               CalendarService calendarService = this._calendarServiceManager.findCalendarService(event);
               CalDB calendar = calendarService.getCalendarDatabase();
               CICALConfiguration cicalConfiguration = calendarService.getCICALConfiguration();
               if (responseEvent != null) {
                  if (this._verbAction != 4 && this._verbAction != 5) {
                     CalendarService meetingService = CalendarServiceManager.getInstance().findCalendarService(event);
                     String myEmailAddress = EventUtilities.getEmailAddress(meetingService.getServiceRecord());
                     if (myEmailAddress != null) {
                        Event newEvent = responseEvent;
                        if (ObjectGroup.isInGroup(newEvent)) {
                           newEvent = (Event)ObjectGroup.expandGroup(newEvent);
                        }

                        MeetingInfo meetingInfo = newEvent.getMeetingInfo();
                        Attendee myEntry = meetingInfo.getAttendee(myEmailAddress);
                        if (myEntry != null) {
                           newEvent.setFreeBusy(FBFlag);
                           if (this._verbAction != 0 && this._verbAction != 1) {
                              myEntry.setType(4);
                           } else {
                              myEntry.setType(2);
                           }

                           synchronized (calendar.getLockObject()) {
                              syncData.updateChecksum(EventUtilities.getHashData(newEvent), false);
                           }

                           _otaSyncDataManager.remove(event);
                           _otaSyncDataManager.add(newEvent, syncData);
                           calendar.addWithAction(newEvent, 3);
                        }
                     }
                  } else {
                     if (events.length > 1) {
                        for (int j = 0; j < events.length; j++) {
                           Event nextResponseEvent = this.getResponseEvent((Event)events[j]);
                           if (!cicalConfiguration.canPopulateCalendarFromMeetingRequest()
                              && !exclusionMarked
                              && nextResponseEvent != null
                              && nextResponseEvent.getRelatedLUID() != 0) {
                              EventUtilities.removeEvent(nextResponseEvent, true);
                              exclusionMarked = true;
                           }
                        }
                     }

                     Event actualEvent = (Event)calendar.get(responseEvent.getLUID());
                     if (actualEvent != null && !exclusionMarked) {
                        EventUtilities.removeEvent(actualEvent, true);
                     }

                     if (!exclusionMarked) {
                        EventUtilities.removeEvent(responseEvent, true);
                     }

                     if (!exclusionMarked) {
                        RecurUtilities.scanAndDeleteRelatedEvents(calendar, responseEvent);
                     }
                  }
               }

               i++;
            }
         } finally {
            return;
         }
      }
   }

   @Override
   public final String toString() {
      ResourceBundle resources = ResourceBundle.getBundle(8008824311162635875L, "net.rim.device.apps.internal.resource.CalendarOTA");
      switch (this._verbAction) {
         case -1:
            return super.toString();
         case 0:
         default:
            return resources.getString(300);
         case 1:
            return resources.getString(301);
         case 2:
            return resources.getString(605);
         case 3:
            return resources.getString(606);
         case 4:
            return resources.getString(302);
         case 5:
            return resources.getString(303);
      }
   }

   @Override
   public final Object invoke(Object parameter) {
      ResourceBundle resources = ResourceBundle.getBundle(8008824311162635875L, "net.rim.device.apps.internal.resource.CalendarOTA");
      String title = null;
      Object[] events = this._attachment.getEvents();
      MeetingInfo meetingInfo = ((Event)events[0]).getMeetingInfo();
      if (meetingInfo.meetingCanBeModified()) {
         Dialog.alert(resources.getString(608));
         return null;
      }

      switch (this._verbAction) {
         case -1:
            return null;
         case 0:
         case 2:
         case 4:
            this.sendResponse(null);
            return new ContextObject(39);
         case 1:
         default:
            title = resources.getString(400);
            break;
         case 3:
            title = resources.getString(607);
            break;
         case 5:
            title = resources.getString(401);
      }

      String comments = MeetingCommentsScreen.showScreen(title);
      if (comments != null) {
         this.sendResponse(comments);
         return new ContextObject(39);
      } else {
         return null;
      }
   }
}
