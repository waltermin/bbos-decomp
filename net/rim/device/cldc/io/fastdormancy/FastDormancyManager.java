package net.rim.device.cldc.io.fastdormancy;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.system.RealtimeClockListener;
import net.rim.device.cldc.io.daemon.ProtocolDaemon;

public final class FastDormancyManager implements RealtimeClockListener, GlobalEventListener {
   private boolean _fastDormancy = true;
   private long _lastTimeSet;
   private static final long ID;
   private static final int FAST_DORMANCY_FALSE_TIMEOUT;

   public static final void FastDormancyMain(String[] args) {
      ApplicationRegistry.getApplicationRegistry().put(9030476570863075412L, new FastDormancyManager());
   }

   public static final FastDormancyManager getInstance() {
      return (FastDormancyManager)ApplicationRegistry.getApplicationRegistry().waitFor(9030476570863075412L);
   }

   public final boolean getFastDormancy() {
      return this._fastDormancy;
   }

   public final void setFastDormancy(boolean fastDormancy) {
      ProtocolDaemon protocolDaemon = ProtocolDaemon.getInstance();
      synchronized (this) {
         this._lastTimeSet = System.currentTimeMillis();
         boolean valueChanged = this._fastDormancy != fastDormancy;
         this._fastDormancy = fastDormancy;
         if (valueChanged) {
            if (!this._fastDormancy) {
               protocolDaemon.addRealtimeClockListener(this);
               protocolDaemon.addGlobalEventListener(this);
            } else {
               protocolDaemon.removeRealtimeClockListener(this);
               protocolDaemon.removeGlobalEventListener(this);
            }
         }
      }
   }

   @Override
   public final void clockUpdated() {
      ProtocolDaemon protocolDaemon = ProtocolDaemon.getInstance();
      synchronized (this) {
         if (this._lastTimeSet + 300000 <= System.currentTimeMillis()) {
            this._fastDormancy = true;
            protocolDaemon.removeRealtimeClockListener(this);
            protocolDaemon.removeGlobalEventListener(this);
         }
      }
   }

   @Override
   public final synchronized void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid == 8877632280522743328L) {
         this._lastTimeSet = System.currentTimeMillis();
      }
   }
}
