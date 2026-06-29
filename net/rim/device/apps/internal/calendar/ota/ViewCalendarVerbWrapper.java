package net.rim.device.apps.internal.calendar.ota;

import java.util.Calendar;
import java.util.TimeZone;
import net.rim.device.apps.api.calendar.modelcontrollerinterface.Event;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.framework.verb.WrapperVerb;
import net.rim.device.cldc.util.CalendarExtensions;

class ViewCalendarVerbWrapper extends WrapperVerb {
   ViewCalendarVerbWrapper(Verb viewCalendarVerb, Event event) {
      super(viewCalendarVerb, null, viewCalendarVerb.getOrdering());
      ContextObject contextObject = new ContextObject();
      Calendar calendar = Calendar.getInstance();
      ((CalendarExtensions)calendar).setTimeLong(event.getStartDate(TimeZone.getDefault()));
      contextObject.put(4143325197084129318L, calendar);
      contextObject.putIntegerData(2);
      super._parameter = contextObject;
   }
}
