package net.rim.device.apps.internal.calendar.viewer;

import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.util.Arrays;
import net.rim.device.apps.api.calendar.caldb.CalDB;
import net.rim.device.apps.api.calendar.caldb.CalendarKey;
import net.rim.device.apps.api.calendar.caldb.CalendarOptions;
import net.rim.device.apps.api.calendar.caldb.CalendarService;
import net.rim.device.apps.api.calendar.caldb.CalendarServiceManager;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.service.ServiceIdentifier;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.internal.i18n.CommonResource;

public final class DeletePriorVerb extends Verb {
   private long _time;

   public DeletePriorVerb(long time) {
      super(598352);
      this._time = time;
   }

   @Override
   public final String toString() {
      return CommonResources.getString(1100);
   }

   @Override
   public final Object invoke(Object parameter) {
      CalendarOptions calendarOptions = CalendarOptions.getOptions();
      CalDB[] calDBs = new CalDB[0];
      CalendarServiceManager calendarServiceManager = CalendarServiceManager.getInstance();
      ServiceIdentifier[] services = calendarServiceManager.getCalendarServices();

      for (int i = 0; i < services.length; i++) {
         CalendarService calendarService = (CalendarService)services[i];
         CalendarKey[] calendarKeys = calendarService.getCalendarKeys();

         for (int j = 0; j < calendarKeys.length; j++) {
            CalendarKey calendarKey = calendarKeys[j];
            if (calendarOptions.isShowAppointments(calendarKey)) {
               Arrays.add(calDBs, calendarService.getCalendarDatabase());
            }
         }
      }

      if (calDBs.length == 0 || calDBs.length > 1) {
         Arrays.add(calDBs, calendarServiceManager.getBaseSystemCalendarDatabase());
      }

      UiApplication app = UiApplication.getUiApplication();
      boolean wasBackground = !app.isForeground();
      String[] choices = new String[]{CommonResources.getString(1000), CommonResource.getString(19)};
      int[] values = new int[]{0, -1, 51, 527827200, 16810638, 16788086, 1701539702, 1870004480};
      if (wasBackground) {
         app.requestForeground();
      }

      switch (Dialog.ask(CommonResources.getString(3002), choices, values, -1)) {
         case 0:
            new Thread(new DeletePriorVerb$DeletePriorThread(calDBs, this._time, app)).start();
         default:
            if (wasBackground) {
               app.requestBackground();
            }

            return null;
      }
   }
}
