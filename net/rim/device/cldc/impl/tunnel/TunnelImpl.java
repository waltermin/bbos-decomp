package net.rim.device.cldc.impl.tunnel;

import net.rim.device.cldc.io.tunnel.Tunnel;
import net.rim.device.cldc.io.tunnel.TunnelConfig;

final class TunnelImpl implements Tunnel {
   private TunnelConfig _config;
   private TunnelManagerImpl _manager;

   TunnelImpl(TunnelConfig config) {
      this._config = config;
   }

   final void setManager(TunnelManagerImpl manager) {
      this._manager = manager;
   }

   @Override
   public final TunnelConfig getConfig() {
      return this._config;
   }

   @Override
   public final int getStatus() {
      return this._manager.getStatus();
   }

   @Override
   public final int getIdentifier() {
      return this._manager.getIdentifier();
   }

   @Override
   public final void close() {
      if (this._manager != null) {
         this._manager.close(this);
      }

      this._manager = null;
   }

   @Override
   public final void kick() {
      this._manager.kick(false);
   }

   @Override
   public final void reset() {
      this._manager.reset();
   }

   @Override
   public final Object setup(int callType, Object context) {
      return this._manager.setup(callType, context);
   }
}
