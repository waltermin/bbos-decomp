package net.rim.device.apps.internal.calendar.viewer;

import net.rim.device.apps.api.calendar.calconstants.CalOptionCache;
import net.rim.device.apps.api.framework.verb.Verb;

final class CalendarAppController$EventViewerVerbWrapper implements Runnable {
   Verb _verb;
   Object _parameter;
   private final CalendarAppController this$0;

   public CalendarAppController$EventViewerVerbWrapper(CalendarAppController _1, Verb verb, Object parameter) {
      this.this$0 = _1;
      this._verb = verb;
      this._parameter = parameter;
   }

   @Override
   public final void run() {
      CalOptionCache.setObjectWithFocus(this._verb.invoke(this._parameter));
      if (this.this$0._curr != null) {
         this.this$0._curr.setTimeWithFocus(CalOptionCache.getTimeWithFocus());
      }
   }
}
