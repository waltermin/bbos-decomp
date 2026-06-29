package net.rim.tools.compiler.util;

import java.util.Vector;

public final class ExecutionTimer {
   private String _name;
   private long _time;

   public ExecutionTimer(String name, Vector timers) {
      timers.addElement(this);
      this._name = name;
      this._time = System.currentTimeMillis();
   }

   public final void stop() {
      this._time = System.currentTimeMillis() - this._time;
   }

   @Override
   public final String toString() {
      int duration = (int)this._time;
      int seconds = duration / 1000;
      int millis = duration % 1000;
      StringBuffer buf = ((StringBuffer)(new Object(this._name.length() + 11))).append(this._name).append('(').append(seconds).append('.');
      if (millis < 100) {
         buf.append('0');
         if (millis < 10) {
            buf.append('0');
         }
      }

      return buf.append(millis).append("s)").toString();
   }
}
