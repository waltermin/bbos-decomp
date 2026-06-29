package net.rim.device.internal.timesync;

import net.rim.device.api.servicebook.ServiceRouting;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.DeviceInternal;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.internal.proxy.Proxy;
import net.rim.device.internal.system.InternalServices;
import net.rim.vm.PersistentInteger;

public final class TimeSyncImpl extends TimeSync implements GlobalEventListener, TimeSyncEvent {
   private int _timeSourceId;
   private int _timeSource;
   private boolean _lazySync;
   private static final long SYNC_SOURCE_ID = -442051743619216161L;

   public static final void TimeSyncMain() {
      new TimeSyncImpl();
   }

   private TimeSyncImpl() {
      EventLogger.register(1339175110175922940L, "net.rim.timesync", 2);
      EventLogger.logEvent(1339175110175922940L, 1231972724, 0);
      this._timeSourceId = PersistentInteger.getId(-442051743619216161L, this.getDefaultSource());
      this._timeSource = PersistentInteger.get(this._timeSourceId);
      ApplicationRegistry.getApplicationRegistry().put(1339175110175922940L, this);
      Proxy.getInstance().addGlobalEventListener(this);
   }

   private final int getDefaultSource() {
      return (RadioInfo.getNetworkType() == 5 || RadioInfo.getNetworkType() == 4) && InternalServices.isNetworkTimeZoneSupported() ? 3 : 2;
   }

   @Override
   public final void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid == 8877632280522743328L) {
         this._lazySync = false;
      }
   }

   @Override
   public final synchronized boolean isEnabled() {
      return this._timeSource != 0;
   }

   @Override
   public final synchronized int getSource() {
      return this._timeSource == 1 ? this.getDefaultSource() : this._timeSource;
   }

   @Override
   public final synchronized boolean setSource(int source) {
      if (this._timeSource != source && source >= 0 && source <= 3) {
         if (source == 1) {
            this._timeSource = this.getDefaultSource();
         } else {
            this._timeSource = source;
         }

         PersistentInteger.set(this._timeSourceId, this._timeSource);
         return true;
      } else {
         return false;
      }
   }

   @Override
   public final synchronized boolean doLazySync() {
      return this._lazySync;
   }

   @Override
   public final synchronized boolean isAvailable() {
      return this._timeSource == 2 && ServiceRouting.getInstance().isServiceRoutable(null, -1)
         || this._timeSource == 3 && RadioInfo.getState() == 1 && RadioInfo.getSignalLevel() != -256;
   }

   @Override
   public final void synchronize(boolean syncNow) {
      EventLogger.logEvent(1339175110175922940L, 1400467026, 5);
      if (!this._lazySync || syncNow) {
         this._lazySync = true;
         if (syncNow) {
            if (this._timeSource == 2) {
               TimeSyncThread thread = new TimeSyncThread();
               Proxy.getInstance().startThread(thread);
               return;
            }

            if (this._timeSource == 3) {
               if (InternalServices.isNetworkTimeZoneSupported()) {
                  if (InternalServices.isNetworkTimeValid()) {
                     DeviceInternal.setDateTime(TimeSync.GetNetworkTime(System.currentTimeMillis()));
                     return;
                  }

                  EventLogger.logEvent(1339175110175922940L, 1316252022, 3);
                  return;
               }

               EventLogger.logEvent(1339175110175922940L, 1316255091, 2);
            }
         }
      }
   }
}
