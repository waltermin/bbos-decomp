package net.rim.device.apps.internal.lbs.maplet;

import net.rim.device.api.ui.UiApplication;

final class MapletMapField$Timer implements Runnable {
   private int _period;
   private int _total;
   private int _current;
   private int _pid;
   private final MapletMapField this$0;

   public MapletMapField$Timer(MapletMapField this$0, int period, int total) {
      this.this$0 = this$0;
      this._pid = -1;
      this._period = period;
      this._total = total;
   }

   public final void setPID(int pid) {
      if (this._pid != -1) {
         label19:
         try {
            UiApplication.getUiApplication().cancelInvokeLater(this._pid);
         } finally {
            break label19;
         }
      }

      this._pid = pid;
   }

   public final void cancel() {
      if (this._pid != -1) {
         label21:
         try {
            UiApplication.getUiApplication().cancelInvokeLater(this._pid);
         } finally {
            break label21;
         }

         this._pid = -1;
      }
   }

   @Override
   public final void run() {
      this._current = this._current + this._period;
      if (this._current > this._total) {
         this.cancel();
      } else {
         if (this._pid != -1) {
            this.this$0.updateProgress(this._pid, this._current, this._total);
         }
      }
   }
}
