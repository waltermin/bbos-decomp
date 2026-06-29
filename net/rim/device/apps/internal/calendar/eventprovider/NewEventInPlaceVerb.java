package net.rim.device.apps.internal.calendar.eventprovider;

import java.util.TimeZone;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.util.DateTimeUtilities;
import net.rim.device.api.util.FactoryUtil;
import net.rim.device.apps.api.calendar.calconstants.CalOptionCache;
import net.rim.device.apps.api.calendar.caldb.CalDB;
import net.rim.device.apps.api.calendar.caldb.CalendarKey;
import net.rim.device.apps.api.calendar.caldb.CalendarOptions;
import net.rim.device.apps.api.calendar.caldb.CalendarService;
import net.rim.device.apps.api.calendar.modelcontrollerinterface.Event;
import net.rim.device.apps.api.calendar.ota.CICALConfiguration;
import net.rim.device.internal.i18n.CommonResource;

class NewEventInPlaceVerb extends CalendarEventVerb {
   public NewEventInPlaceVerb() {
      super(CommonResource.getBundle(), 13, 598272);
   }

   @Override
   public Object invoke(Object parameter) {
      String userText = CalOptionCache.getSuggestedUserText().trim();
      String subject = userText;
      String location = null;
      int userTextLength = userText.length();
      if (userTextLength > 0 && userText.charAt(userTextLength - 1) == ')') {
         int mismatchTokenCount = 1;

         for (int i = userTextLength - 2; i >= 0; i--) {
            char currentChar = userText.charAt(i);
            if (currentChar == '(') {
               if (--mismatchTokenCount == 0) {
                  location = userText.substring(i + 1, userTextLength - 1).trim();
                  if (location.length() == 0) {
                     subject = userText;
                  } else if (i >= 1) {
                     subject = userText.substring(0, i).trim();
                  } else {
                     subject = null;
                  }
                  break;
               }
            } else if (currentChar == ')') {
               mismatchTokenCount++;
            }
         }
      }

      Event newEvent = null;
      long startInstant = CalOptionCache.getTimeWithFocus();
      TimeZone tz = TimeZone.getDefault();
      long duration = CalOptionCache.getSuggestedUserDuration();
      long endInstant = startInstant + duration;
      CalendarService calendarService = CalendarOptions.getOptions().getDefaultServiceForNewEvent();
      CICALConfiguration cicalConfiguration = calendarService.getCICALConfiguration();
      if (!DateTimeUtilities.isSameDate(startInstant, endInstant, tz, null, null) && !cicalConfiguration.canAppointmentsSpanMidnight()) {
         ResourceBundle rb = ResourceBundle.getBundle(912302513268743237L, "net.rim.device.apps.internal.resource.Calendar");
         Dialog.alert(rb.getString(2));
      } else {
         newEvent = (Event)FactoryUtil.createInstance(-1986287563994289176L, null);
         newEvent.setStartDate(CalOptionCache.getTimeWithFocus(), TimeZone.getDefault());
         newEvent.setInstanceDuration(CalOptionCache.getSuggestedUserDuration());
         newEvent.setSubject(subject == null ? "" : subject);
         newEvent.setLocation(location == null ? "" : location);
         CalDB calDB = calendarService.getCalendarDatabase();
         calDB.add(newEvent);
         CalendarKey[] visibleCalendarKeys = CalendarOptions.getOptions().getVisibleCalendars();
         if (visibleCalendarKeys.length == 1 && visibleCalendarKeys[0].getCalendarServiceID() != calendarService.getUniqueServiceID()) {
            CalendarOptions.getOptions().resetCalendarServiceFilter();
         }
      }

      return newEvent;
   }
}
