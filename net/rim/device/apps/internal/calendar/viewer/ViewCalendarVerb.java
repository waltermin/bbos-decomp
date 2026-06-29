package net.rim.device.apps.internal.calendar.viewer;

import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.ApplicationManager;
import net.rim.device.api.system.RIMGlobalMessagePoster;

public final class ViewCalendarVerb extends CalendarViewerVerb {
   private ApplicationDescriptor _calendarAppDescriptor = ApplicationDescriptor.currentApplicationDescriptor();

   public ViewCalendarVerb() {
      super(404, 204800, 0);
   }

   @Override
   public final Object invoke(Object parameter) {
      ApplicationManager appManager = ApplicationManager.getApplicationManager();
      String calendarModuleName = this._calendarAppDescriptor.getModuleName();

      try {
         int pid = -1;
         ApplicationDescriptor[] descriptors = appManager.getVisibleApplications();

         for (int i = 0; i < descriptors.length; i++) {
            if (descriptors[i].getModuleName().equals(calendarModuleName)) {
               pid = appManager.getProcessId(descriptors[i]);
               appManager.requestForeground(pid);
               break;
            }
         }

         if (pid == -1) {
            this._calendarAppDescriptor = (ApplicationDescriptor)(new Object(this._calendarAppDescriptor, new String[]{"noview"}));
            pid = appManager.runApplication(this._calendarAppDescriptor);
         }

         if (pid != -1) {
            RIMGlobalMessagePoster.postGlobalEvent(pid, 6359528386020392909L, 0, 0, parameter, null);
            return null;
         }
      } finally {
         return null;
      }

      return null;
   }
}
