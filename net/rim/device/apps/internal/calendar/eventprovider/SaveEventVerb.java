package net.rim.device.apps.internal.calendar.eventprovider;

import java.util.Enumeration;
import java.util.TimeZone;
import java.util.Vector;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.apps.api.addressbook.EmailAddressModel;
import net.rim.device.apps.api.calendar.caldb.CalDB;
import net.rim.device.apps.api.calendar.caldb.CalendarKey;
import net.rim.device.apps.api.calendar.caldb.CalendarOptions;
import net.rim.device.apps.api.calendar.caldb.CalendarService;
import net.rim.device.apps.api.calendar.caldb.CalendarServiceManager;
import net.rim.device.apps.api.calendar.modelcontrollerinterface.Attendee;
import net.rim.device.apps.api.calendar.modelcontrollerinterface.Event;
import net.rim.device.apps.api.calendar.modelcontrollerinterface.MeetingInfo;
import net.rim.device.apps.api.calendar.modelcontrollerinterface.RecurUtilities;
import net.rim.device.apps.api.calendar.ota.CICALConfiguration;
import net.rim.device.apps.api.calendar.ota.OTACalendarSyncDataManager;
import net.rim.device.apps.api.framework.model.Recur;
import net.rim.device.apps.api.sync.OTASyncData;
import net.rim.device.apps.api.utility.general.Copyable;
import net.rim.device.apps.internal.calendar.meeting.MeetingUtilities;
import net.rim.device.internal.i18n.CommonResource;

public class SaveEventVerb extends CalendarEventVerb {
   private Event _event;
   private Event _series;
   private long _originalOccurrenceInstant;
   private boolean _newEvent;
   private Vector _listeners = (Vector)(new Object());
   public static long SAVE_EVENT_KEY = 2692357427663009499L;
   private static OTACalendarSyncDataManager _otaSyncDataManager = OTACalendarSyncDataManager.getInstance();

   public SaveEventVerb(Event event, boolean newEvent) {
      super(CommonResource.getBundle(), 18, 598336);
      this._event = event;
      this._newEvent = newEvent;
   }

   public SaveEventVerb(Event series, long originalOccurrenceInstant, Event occurrence) {
      this(occurrence, true);
      this._series = series;
      this._originalOccurrenceInstant = originalOccurrenceInstant;
   }

   public void addSaveEventVerbListener(SaveEventVerb$SaveEventVerbListener listener) {
      if (!this._listeners.contains(listener)) {
         this._listeners.addElement(listener);
      }
   }

   @Override
   public Object invoke(Object parameter) {
      Event mainEventToSave = this._event;
      Event series = this._series;
      CalendarServiceManager calendarServiceManager = CalendarServiceManager.getInstance();
      if (!this.checkForExclusionWarning(mainEventToSave)) {
         return null;
      }

      MeetingInfo oldMeetingInfo = mainEventToSave.getMeetingInfo();
      if (oldMeetingInfo != null && oldMeetingInfo.meetingCanBeModified()) {
         oldMeetingInfo = (MeetingInfo)((Copyable)oldMeetingInfo).copy();
      }

      this.updateEventContents(mainEventToSave);
      CalendarService calendarService = calendarServiceManager.findCalendarService(mainEventToSave);
      if (!this.proceedWithSave(mainEventToSave)) {
         return null;
      }

      MeetingInfo newMeetingInfo = mainEventToSave.getMeetingInfo();
      if (oldMeetingInfo != null && newMeetingInfo != null && newMeetingInfo.meetingCanBeModified()) {
         label158:
         try {
            if (!MeetingUtilities.getNotifyAttendees(calendarService, mainEventToSave.getLUID())) {
               Enumeration attendees = newMeetingInfo.getAttendees();

               while (attendees.hasMoreElements()) {
                  Attendee attendee = (Attendee)attendees.nextElement();
                  Object address = attendee.getAddress();
                  EmailAddressModel emailAddress = (EmailAddressModel)address;
                  Attendee oldAttendee = oldMeetingInfo.getAttendee(emailAddress.getAddress());
                  if (oldAttendee != null) {
                     attendee.setType(oldAttendee.getType());
                  }
               }
            }
         } finally {
            break label158;
         }
      }

      CalDB calDB = calendarService.getCalendarDatabase();
      synchronized (calDB.getLockObject()) {
         if (mainEventToSave.getRelatedLUID() != 0) {
            Event recurrenceSeries = (Event)calDB.get(mainEventToSave.getRelatedLUID());
            if (recurrenceSeries == null) {
               return null;
            }

            TimeZone tz = TimeZone.getTimeZone(mainEventToSave.getTimeZoneID());
            long exclusionStartTime = mainEventToSave.getStartDate(tz);
            long exclusionOriginalStartTime = mainEventToSave.getRelatedTime();
            long duration = mainEventToSave.getInstanceDuration();
            int modifier = 1;
            if (duration == 0) {
               modifier = 0;
            }

            long exclusionEndTime = exclusionStartTime + mainEventToSave.getInstanceDuration() - modifier;
            long searchTimeBack = exclusionOriginalStartTime - modifier;
            long searchTimeForward = exclusionOriginalStartTime;
            boolean result = false;
            boolean adjustStartTime = false;
            if (series != null) {
               duration = recurrenceSeries.getInstanceDuration();
               searchTimeBack = this._originalOccurrenceInstant - modifier;
               if (duration == 0) {
                  modifier = 1;
               }

               searchTimeForward = this._originalOccurrenceInstant + recurrenceSeries.getInstanceDuration() + modifier;
               if (recurrenceSeries.getAllDayFlag()) {
                  adjustStartTime = true;
               }
            }

            result = recurrenceSeries.conflictsWithNextInSeries(true, adjustStartTime, exclusionEndTime, searchTimeForward, tz)
               || recurrenceSeries.conflictsWithNextInSeries(false, adjustStartTime, exclusionStartTime, searchTimeBack, tz);
            CICALConfiguration cicalConfiguration = calendarServiceManager.getCICALConfiguration(mainEventToSave);
            if (result && !cicalConfiguration.canMoveOccurenceScope()) {
               ResourceBundle rb = ResourceBundle.getBundle(912302513268743237L, "net.rim.device.apps.internal.resource.Calendar");
               Dialog.alert(rb.getString(368));
               return null;
            }
         }

         if (series == null && this.hasEventTimeChanged(mainEventToSave)) {
            this.removeExceptions(mainEventToSave, calDB);
         }

         OTASyncData syncData = _otaSyncDataManager.get(mainEventToSave);
         if (syncData != null) {
            syncData.removeChecksum();
         }

         calDB.add(mainEventToSave);
         if (series != null) {
            RecurUtilities.ensureMarkedAsExclusion(calDB, mainEventToSave);
         }

         CalendarKey[] visibleCalendarKeys = CalendarOptions.getOptions().getVisibleCalendars();
         if (visibleCalendarKeys.length == 1 && visibleCalendarKeys[0].getCalendarServiceID() != calendarService.getUniqueServiceID()) {
            CalendarOptions.getOptions().resetCalendarServiceFilter();
         }

         return mainEventToSave;
      }
   }

   protected boolean isNewEvent() {
      return this._newEvent;
   }

   protected boolean hasEventTimeChanged(Event _1) {
      throw null;
   }

   protected void updateEventContents(Event _1) {
      throw null;
   }

   protected boolean proceedWithSave(Event eventToSave) {
      boolean result = true;
      if (!this._listeners.isEmpty()) {
         Enumeration enumeration = this._listeners.elements();

         while (enumeration.hasMoreElements() && result) {
            SaveEventVerb$SaveEventVerbListener listener = (SaveEventVerb$SaveEventVerbListener)enumeration.nextElement();
            result &= listener.proceedWithSave(eventToSave);
         }
      }

      return result;
   }

   protected boolean checkForExclusionWarning(Event eventToSave) {
      if (eventToSave.isRecurring() && this.hasEventTimeChanged(eventToSave) && eventToSave.getReadOnlyRecurrence().getExclusionCount() > 0) {
         ResourceBundle rb = ResourceBundle.getBundle(912302513268743237L, "net.rim.device.apps.internal.resource.Calendar");
         if (Dialog.ask(2, rb.getString(165), 3) != 3) {
            return false;
         }
      }

      return true;
   }

   private void removeExceptions(Event event, CalDB calDB) {
      long uid = event.getLUID();
      CalendarService calendarService = CalendarServiceManager.getInstance().findCalendarService(event);
      if (event.isRecurring() && event.getReadOnlyRecurrence().getExclusionCount() > 0) {
         Recur r = event.getRecurrenceCopy();
         r.removeAllExclusions();
         event.setRecurrence(r);
      }

      Enumeration enumeration = calDB.getElements();

      while (enumeration.hasMoreElements()) {
         Object o = enumeration.nextElement();
         if (o != null && o instanceof Object) {
            Event e = (Event)o;
            if (e.getRelatedLUID() == uid) {
               MeetingUtilities.dontNotifyAttendees(calendarService, e.getLUID());
               calDB.remove(e);
            }
         }
      }
   }
}
