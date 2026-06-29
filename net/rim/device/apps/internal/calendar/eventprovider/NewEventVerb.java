package net.rim.device.apps.internal.calendar.eventprovider;

import java.util.TimeZone;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.util.FactoryUtil;
import net.rim.device.apps.api.calendar.calconstants.CalOptionCache;
import net.rim.device.apps.api.calendar.controller.CalendarEventViewer;
import net.rim.device.apps.api.calendar.controller.CalendarEventViewerProvider;
import net.rim.device.apps.api.calendar.modelcontrollerinterface.Event;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.internal.i18n.CommonResource;

public class NewEventVerb extends CalendarEventVerb {
   public NewEventVerb() {
      super(CommonResource.getBundle(), 13, 598272);
   }

   @Override
   public Object invoke(Object parameter) {
      Event newEvent;
      if (!(parameter instanceof Event)) {
         newEvent = (Event)FactoryUtil.createInstance(-1986287563994289176L, null);
         newEvent.setStartDate(CalOptionCache.getTimeWithFocus(), TimeZone.getDefault());
         newEvent.setInstanceDuration(CalOptionCache.getSuggestedUserDuration());
      } else {
         newEvent = (Event)parameter;
      }

      CalendarEventViewerProvider cevp = (CalendarEventViewerProvider)newEvent;
      ContextObject context = new ContextObject(31);
      CalendarEventViewer ev = cevp.getCalendarEventViewer(context);
      Verb[] verbs = new Verb[]{new SaveViewedEventVerb(ev, newEvent, true), null};
      ev.openViewer(ResourceBundle.getBundle(912302513268743237L, "net.rim.device.apps.internal.resource.Calendar").getString(131), verbs, 0, -1, true);
      return newEvent;
   }
}
