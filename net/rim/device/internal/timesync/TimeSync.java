package net.rim.device.internal.timesync;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.internal.system.InternalServices;

public class TimeSync {
   public static final long ID = 1339175110175922940L;
   public static final int OFF = 0;
   protected static final int LEGACY_ENABLED = 1;
   public static final int BLACKBERRY = 2;
   public static final int NETWORK = 3;

   public boolean isEnabled() {
      throw null;
   }

   public int getSource() {
      throw null;
   }

   public boolean setSource(int _1) {
      throw null;
   }

   public boolean doLazySync() {
      throw null;
   }

   public boolean isAvailable() {
      throw null;
   }

   public void synchronize(boolean _1) {
      throw null;
   }

   public static TimeSync getInstance() {
      return (TimeSync)ApplicationRegistry.getApplicationRegistry().get(1339175110175922940L);
   }

   public static long ApplyNetworkTimeZone(long GMTNetworkTime) {
      return GMTNetworkTime + InternalServices.getNetworkTimeZoneOffset() + InternalServices.getNetworkDSTOffset();
   }

   public static long GetNetworkTime(long deviceTime) {
      return deviceTime + InternalServices.getNetworkTimeOffset();
   }
}
