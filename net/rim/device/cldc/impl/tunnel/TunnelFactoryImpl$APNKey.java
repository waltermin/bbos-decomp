package net.rim.device.cldc.impl.tunnel;

final class TunnelFactoryImpl$APNKey {
   private String _apn;

   public TunnelFactoryImpl$APNKey(String apn) {
      this._apn = apn.toLowerCase();
   }

   @Override
   public final boolean equals(Object obj) {
      return this._apn.equalsIgnoreCase(((TunnelFactoryImpl$APNKey)obj)._apn);
   }

   @Override
   public final int hashCode() {
      return this._apn.hashCode();
   }
}
