package net.rim.plazmic.internal.contentpreview.dispatcher.message;

import net.rim.plazmic.internal.contentpreview.dispatcher.DispatcherEventHandler;

public final class ServerProperties extends Model {
   private int _version;

   public ServerProperties(int version) {
      this._version = version;
   }

   @Override
   public final void toEvent(DispatcherEventHandler handler) {
      handler.serverProperties(this._version);
   }

   @Override
   final String getClassName() {
      return "ServerProperties";
   }

   @Override
   final String getProperties() {
      return this.toPropertyString("version", String.valueOf(this._version));
   }
}
