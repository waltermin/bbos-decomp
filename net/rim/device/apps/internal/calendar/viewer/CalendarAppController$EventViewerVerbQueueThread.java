package net.rim.device.apps.internal.calendar.viewer;

import java.util.Vector;
import net.rim.device.apps.api.framework.verb.Verb;

final class CalendarAppController$EventViewerVerbQueueThread extends Thread {
   private Vector _queue;
   private final CalendarAppController this$0;

   CalendarAppController$EventViewerVerbQueueThread(CalendarAppController _1) {
      this.this$0 = _1;
      this._queue = new Vector();
   }

   public final void addVerbToQueue(Verb verb, Object parameter) {
      synchronized (this._queue) {
         CalendarAppController$EventViewerVerbWrapper wrapper = new CalendarAppController$EventViewerVerbWrapper(this.this$0, verb, parameter);
         this._queue.addElement(wrapper);
         this._queue.notify();
      }
   }

   @Override
   public final void run() {
      while (true) {
         try {
            CalendarAppController$EventViewerVerbWrapper nextViewer = null;
            synchronized (this._queue) {
               if (this._queue.size() == 0) {
                  label67:
                  try {
                     this._queue.wait();
                  } finally {
                     break label67;
                  }
               }

               nextViewer = (CalendarAppController$EventViewerVerbWrapper)this._queue.elementAt(0);
               this._queue.removeElementAt(0);
            }

            if (nextViewer != null && this.this$0._calendarApp != null) {
               this.this$0._calendarApp.invokeAndWait(nextViewer);
            }
         } finally {
            continue;
         }
      }
   }
}
