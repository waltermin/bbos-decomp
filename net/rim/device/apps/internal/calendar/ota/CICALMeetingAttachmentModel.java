package net.rim.device.apps.internal.calendar.ota;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.system.ObjectGroup;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.api.addressbook.EmailAddressModel;
import net.rim.device.apps.api.calendar.caldb.CalDB;
import net.rim.device.apps.api.calendar.caldb.CalendarKey;
import net.rim.device.apps.api.calendar.caldb.CalendarService;
import net.rim.device.apps.api.calendar.caldb.CalendarServiceManager;
import net.rim.device.apps.api.calendar.modelcontrollerinterface.Attendee;
import net.rim.device.apps.api.calendar.modelcontrollerinterface.AttendeeFactory;
import net.rim.device.apps.api.calendar.modelcontrollerinterface.Event;
import net.rim.device.apps.api.calendar.modelcontrollerinterface.EventUtilities;
import net.rim.device.apps.api.calendar.modelcontrollerinterface.MeetingInfo;
import net.rim.device.apps.api.calendar.modelcontrollerinterface.RecurUtilities;
import net.rim.device.apps.api.calendar.ota.CICALEmailAttachment;
import net.rim.device.apps.api.calendar.ota.OTACalendarSyncDataManager;
import net.rim.device.apps.api.framework.model.ActionProvider;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.ConversionProvider;
import net.rim.device.apps.api.framework.model.EncryptableProvider;
import net.rim.device.apps.api.framework.model.PersistableRIMModel;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.framework.model.SyncBuffer;
import net.rim.device.apps.api.framework.model.VerbProvider;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.sync.OTASyncData;
import net.rim.device.apps.api.sync.OTASyncDataManager;
import net.rim.device.apps.internal.blackberryemail.email.CMIMEReferenceIdInterested;
import net.rim.device.apps.internal.blackberryemail.email.EmailMessageModel;

public class CICALMeetingAttachmentModel
   implements PersistableRIMModel,
   VerbProvider,
   ConversionProvider,
   ActionProvider,
   CMIMEReferenceIdInterested,
   EncryptableProvider,
   CICALEmailAttachment {
   private int _type;
   private Object[] _meetings;
   private Attendee _sender;
   private Object _dataEncoding;
   private int _deviceSequence;
   private int _hostSequence;
   private int _cmimeReferenceId = -1;

   @Override
   public int getCMIMEReferenceIdentifier() {
      OTACalendarSyncDataManager syncManager = OTACalendarSyncDataManager.getInstance();
      OTASyncData syncData = syncManager.get((Event)this._meetings[0]);
      return syncData != null ? syncData.getOwnerId() : -1;
   }

   @Override
   public Verb getVerbs(Object context, Verb[] verbs) {
      if (!ContextObject.getFlag(context, 87)) {
         return null;
      }

      RIMModel message = (RIMModel)ContextObject.get(context, 424670468422402792L);
      Verb[] defaultVerbs = (Verb[])ContextObject.get(context, 248);
      if (defaultVerbs != null && defaultVerbs[0] != null) {
         OTASyncDataManager syncManager = OTACalendarSyncDataManager.getInstance();
         Event event = this.getEvent(0);
         OTASyncData syncData = syncManager.get(event);
         if (this.getType() == 1 && syncData == null) {
            syncData = this.getSyncData();
            if (syncData != null) {
               syncManager.add(event, syncData);
            }
         }

         boolean openSupported = true;
         if (message instanceof EmailMessageModel) {
            var email = (EmailMessageModel & EmailMessageModel)message;
            if (email.getStatus() == Integer.MAX_VALUE || email.getStatus() == 33554431) {
               openSupported = false;
            }
         }

         if ((this.getType() != 1 || syncData != null) && openSupported) {
            defaultVerbs[0] = new OpenMeetingEmailAttachmentVerb(message, this, context);
         }
      }

      return null;
   }

   public int getType() {
      return this._type;
   }

   public Object[] getEvents() {
      return this._meetings;
   }

   @Override
   public boolean perform(long actionId, Object context) {
      boolean result = true;
      boolean editableFlag = true;
      Object obj = ContextObject.get(context, 2164559162753216116L);
      if (obj instanceof Boolean) {
         editableFlag = !(Boolean)obj;
      }

      ContextObject co = ContextObject.castOrCreate(context);
      boolean multiSelected = co.getFlag(103);
      boolean shouldDelete = actionId == -8870086693994175796L && !multiSelected;
      if (shouldDelete && editableFlag && this._type == 1) {
         Object[] events = this.getEvents();
         String myEmailAddress = null;
         Event primaryEvent = (Event)events[0];
         CalendarService calendarService = CalendarServiceManager.getInstance().findCalendarService(primaryEvent);
         if (calendarService != null) {
            myEmailAddress = EventUtilities.getEmailAddress(calendarService.getServiceRecord());
         }

         if (myEmailAddress != null) {
            for (int i = events.length - 1; i >= 0; i--) {
               Event thisEvent = (Event)events[i];
               CalDB calDB = CalendarServiceManager.getInstance().findCalendarDatabase(thisEvent);
               Event actualEvent = (Event)calDB.get(thisEvent.getLUID());
               if (actualEvent != null) {
                  MeetingInfo meetingInfo = actualEvent.getMeetingInfo();
                  Attendee myEntry = meetingInfo.getAttendee(myEmailAddress);
                  if (myEntry != null && meetingInfo.hasOrganizer()) {
                     if (this._cmimeReferenceId != -1) {
                        OTASyncData syncData = OTACalendarSyncDataManager.getInstance().get(actualEvent);
                        int ownerId = syncData.getOwnerId();
                        if (ownerId != -1 && ownerId != this._cmimeReferenceId) {
                           continue;
                        }
                     }

                     Attendee organizer = meetingInfo.getOrganizer();
                     String organizerAddress = ((EmailAddressModel)organizer.getAddress()).getAddress();
                     String myAddress = ((EmailAddressModel)myEntry.getAddress()).getAddress();
                     int status = myEntry.getType();
                     if (status == 1 && StringUtilities.compareToIgnoreCase(organizerAddress, myAddress) != 0) {
                        calDB.remove(actualEvent);
                        RecurUtilities.scanAndDeleteRelatedEvents(calDB, actualEvent);
                     }
                  }
               }
            }
         }
      }

      return result;
   }

   @Override
   public boolean convert(Object context, Object target) {
      if (ContextObject.getFlag(context, 43) && ContextObject.getFlag(context, 19)) {
         byte[] data = this.getBackupData();
         byte[] header = new byte[]{
            0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 82, 73, 77, 45, 67, 97, 108, 101, 110, 100, 97, 114, 0, 99, 97, 108, 101, 110, 100, 97, 114, 0
         };
         byte[] attachmentData = new byte[header.length + data.length];
         System.arraycopy(header, 0, attachmentData, 0, header.length);
         System.arraycopy(data, 0, attachmentData, header.length, data.length);
         ((SyncBuffer)target).addBytes(22, attachmentData);
         return true;
      } else {
         return false;
      }
   }

   public int getEventCount() {
      return this._meetings.length;
   }

   Attendee getSender() {
      return this._sender;
   }

   String getSubject() {
      return this._meetings[0] != null ? ((Event)this._meetings[0]).getSubject() : null;
   }

   public byte[] getBackupData() {
      return PersistentContent.decodeByteArray(this._dataEncoding);
   }

   OTASyncData getSyncData() {
      return this._deviceSequence == 0 && this._hostSequence == 0 ? null : new OTASyncData(this._hostSequence, this._deviceSequence);
   }

   Object[] getResolvedEvents(RIMModel cmimeMessage) {
      Object[] events = this._meetings;
      if (events.length == 0 && !(cmimeMessage instanceof EmailMessageModel)) {
         return events;
      }

      CalendarKey calendarKey = ((Event)events[0]).getCalendarKey();
      if (calendarKey.getCalendarServiceID() == CalendarServiceManager.getInstance().getBaseSystemCalendarService().getUniqueServiceID()) {
         ServiceRecord cmimeServiceRecord = ((EmailMessageModel)cmimeMessage).getServiceRecordForMessage();
         if (cmimeServiceRecord == null) {
            return events;
         }

         ServiceRecord cicalServiceRecord = EventUtilities.getCICALServiceFromOtherService(cmimeServiceRecord);
         if (cicalServiceRecord == null) {
            return events;
         }

         CalendarService calendarService = CalendarServiceManager.getInstance().findCalendarServiceByKey(cicalServiceRecord);
         if (calendarService != null && calendarService.getUniqueServiceID() != calendarKey.getCalendarServiceID()) {
            Object[] groupedEvents = events;
            events = new Object[groupedEvents.length];

            for (int i = 0; i < groupedEvents.length; i++) {
               events[i] = groupedEvents[i];
               if (ObjectGroup.isInGroup(events[i])) {
                  events[i] = ObjectGroup.expandGroup(events[i]);
               }

               calendarKey = new CalendarKey(calendarService.getUniqueServiceID(), calendarService.getPrimaryCalendarFolderID());
               ((Event)events[i]).setCalendarKey(calendarKey);
            }
         }
      }

      return events;
   }

   public Event getEvent(int index) {
      return (Event)this._meetings[index];
   }

   Event setEvent(Event event, int index) {
      event = this.ensureGrouped(event);
      if (index < this._meetings.length) {
         this._meetings[index] = event;
      }

      return event;
   }

   @Override
   public boolean checkCrypt(boolean compress, boolean encrypt) {
      return PersistentContent.checkEncoding(this._dataEncoding);
   }

   @Override
   public Object reCrypt(boolean compress, boolean encrypt) {
      this._dataEncoding = PersistentContent.reEncode(this._dataEncoding);
      return null;
   }

   @Override
   public void setCMIMEReferenceIdentifier(int id, int folderid) {
      OTACalendarSyncDataManager syncManager = OTACalendarSyncDataManager.getInstance();
      OTASyncData syncData = syncManager.get((Event)this._meetings[0]);
      if (syncData != null) {
         syncData.setOwnerId(id);
      }

      this._cmimeReferenceId = id;
   }

   CICALMeetingAttachmentModel(int type, Object[] meetings, byte[] data) {
      this._type = type;
      this._meetings = meetings;
      this._dataEncoding = PersistentContent.encode(data);
      int size = this._meetings != null ? this._meetings.length : 0;

      for (int i = 0; i < size; i++) {
         this._meetings[i] = this.ensureGrouped((Event)this._meetings[i]);
      }

      if (meetings != null) {
         Event primaryMeeting = (Event)meetings[0];
         MeetingInfo meetingInfo = primaryMeeting.getMeetingInfo();
         switch (type) {
            case 0:
               break;
            case 1:
            default:
               OTACalendarSyncDataManager syncManager = OTACalendarSyncDataManager.getInstance();
               OTASyncData syncData = syncManager.get(primaryMeeting);
               if (syncData != null) {
                  this._deviceSequence = syncData.getDeviceSequence();
                  this._hostSequence = syncData.getHostSequence();
               }
            case 3:
               if (meetingInfo.hasOrganizer()) {
                  this._sender = primaryMeeting.getMeetingInfo().getOrganizer();
                  return;
               }

               String selfAddress = null;
               CalendarService calendarService = CalendarServiceManager.getInstance().findCalendarService(primaryMeeting);
               if (calendarService != null) {
                  ServiceRecord cicalServiceRecord = calendarService.getServiceRecord();
                  selfAddress = EventUtilities.getEmailAddress(cicalServiceRecord);
               }

               if (selfAddress == null) {
                  selfAddress = ResourceBundle.getBundle(8008824311162635875L, "net.rim.device.apps.internal.resource.CalendarOTA").getString(1002);
               }

               this._sender = AttendeeFactory.createAttendeeFromRFC822(0, selfAddress);
               return;
            case 2:
               this._sender = (Attendee)meetingInfo.getAttendees().nextElement();
         }
      }
   }

   private Event ensureGrouped(Event event) {
      if (ObjectGroup.isInGroup(event)) {
         event = (Event)ObjectGroup.expandGroup(event);
      }

      try {
         if (event instanceof EncryptableProvider) {
            EncryptableProvider ep = (EncryptableProvider)event;
            if (!ep.checkCrypt(true, true)) {
               ep.reCrypt(true, true);
            }
         }

         ObjectGroup.createGroup(event);
         return event;
      } finally {
         ;
      }
   }
}
