package net.rim.device.apps.internal.calendar.ota;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.apps.api.calendar.controller.CalendarEventViewer;
import net.rim.device.apps.api.calendar.controller.CalendarEventViewerProvider;
import net.rim.device.apps.api.calendar.modelcontrollerinterface.Event;
import net.rim.device.apps.api.framework.verb.Verb;

class OpenMeetingEmailDataVerb extends Verb {
   private MeetingEmailDataModel _meetingData;

   OpenMeetingEmailDataVerb(MeetingEmailDataModel meetingData) {
      super(16863840);
      this._meetingData = meetingData;
   }

   @Override
   public String toString() {
      return ResourceBundle.getBundle(8008824311162635875L, "net.rim.device.apps.internal.resource.CalendarOTA").getString(800);
   }

   @Override
   public Object invoke(Object parameter) {
      Event event = this._meetingData.getEvent();
      if (event instanceof Object) {
         CalendarEventViewerProvider viewerProvider = (CalendarEventViewerProvider)event;
         CalendarEventViewer viewer = viewerProvider.getCalendarEventViewer(null);
         if (viewer != null) {
            int resourceId = 0;
            switch (this._meetingData._type) {
               case 0:
                  break;
               case 1:
               default:
                  resourceId = 402;
                  break;
               case 2:
                  resourceId = 403;
                  break;
               case 3:
                  resourceId = 404;
            }

            String title = ResourceBundle.getBundle(8008824311162635875L, "net.rim.device.apps.internal.resource.CalendarOTA").getString(resourceId);
            viewer.openViewer(title, null, -1, 0, true);
         }
      }

      return null;
   }
}
