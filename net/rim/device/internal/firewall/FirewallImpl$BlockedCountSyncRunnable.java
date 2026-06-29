package net.rim.device.internal.firewall;

import net.rim.device.api.system.RealtimeClockListener;

final class FirewallImpl$BlockedCountSyncRunnable implements RealtimeClockListener {
   int _counter;
   long _prev;
   private final FirewallImpl this$0;

   private FirewallImpl$BlockedCountSyncRunnable(FirewallImpl _1) {
      this.this$0 = _1;
      this._counter = 0;
      this._prev = 0;
   }

   @Override
   public final void clockUpdated() {
      if (this._counter == 0) {
         long curr = 0;

         for (int i = 5; i > 0; i--) {
            FirewallImpl$Blocking b = (FirewallImpl$Blocking)this.this$0._blockings.get(i);
            if (b != null) {
               curr += b._count;
            }
         }

         if (curr != this._prev) {
            this._prev = curr;
            this.this$0.commit();
         }
      }

      if (this._counter++ == 60) {
         this._counter = 0;
      }
   }

   FirewallImpl$BlockedCountSyncRunnable(FirewallImpl x0, FirewallImpl$1 x1) {
      this(x0);
   }
}
