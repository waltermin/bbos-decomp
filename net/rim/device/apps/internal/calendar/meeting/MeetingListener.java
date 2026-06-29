package net.rim.device.apps.internal.calendar.meeting;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.apps.api.calendar.caldb.CalendarService;
import net.rim.device.apps.api.calendar.caldb.CalendarServiceManager;
import net.rim.device.apps.api.calendar.modelcontrollerinterface.Event;
import net.rim.device.apps.api.calendar.modelcontrollerinterface.MeetingInfo;
import net.rim.device.apps.internal.calendar.eventprovider.DeleteEventVerb$DeleteEventVerbListener;
import net.rim.device.apps.internal.calendar.eventprovider.SaveEventVerb$SaveEventVerbListener;

class MeetingListener implements DeleteEventVerb$DeleteEventVerbListener, SaveEventVerb$SaveEventVerbListener {
   @Override
   public boolean proceedWithSave(Event eventToSave) {
      ResourceBundle rb = ResourceBundle.getBundle(8008824311162635875L, "net.rim.device.apps.internal.resource.CalendarOTA");
      CalendarService calendarService = CalendarServiceManager.getInstance().findCalendarService(eventToSave);
      MeetingInfo mInfo = eventToSave.getMeetingInfo();
      if (mInfo.meetingCanBeModified() && mInfo.getAttendeeCount() > 0 && Dialog.ask(3, rb.getString(1003), 4) == -1) {
         MeetingUtilities.dontNotifyAttendees(calendarService, eventToSave.getLUID());
      }

      return true;
   }

   @Override
   public boolean proceedWithDelete(Event eventToDelete) {
      if (eventToDelete != null) {
         ResourceBundle rb = ResourceBundle.getBundle(8008824311162635875L, "net.rim.device.apps.internal.resource.CalendarOTA");
         CalendarService calendarService = CalendarServiceManager.getInstance().findCalendarService(eventToDelete);
         MeetingInfo mInfo = eventToDelete.getMeetingInfo();
         if (mInfo.meetingCanBeModified() && mInfo.getAttendeeCount() > 0 && Dialog.ask(3, rb.getString(1000), 4) == -1) {
            MeetingUtilities.dontNotifyAttendees(calendarService, eventToDelete.getLUID());
         }
      }

      return true;
   }
}
