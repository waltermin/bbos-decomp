package net.rim.device.api.system;

final class TimedRunnable {
   private Runnable _runnable;
   private long _time;
   private boolean _repeat;

   TimedRunnable(Runnable runnable, long time, boolean repeat) {
      this.reset(runnable, time, repeat);
   }

   final void reset(Runnable runnable, long time, boolean repeat) {
      this._runnable = runnable;
      this._time = time;
      this._repeat = repeat;
   }

   final void clear() {
      this._runnable = null;
   }

   final boolean getRepeat() {
      return this._repeat;
   }

   final long getTime() {
      return this._time;
   }

   final Runnable getRunnable() {
      return this._runnable;
   }
}
