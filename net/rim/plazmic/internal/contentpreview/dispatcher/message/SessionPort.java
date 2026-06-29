package net.rim.plazmic.internal.contentpreview.dispatcher.message;

import net.rim.plazmic.internal.contentpreview.dispatcher.DispatcherEventHandler;

public final class SessionPort extends Model {
   private int _port;
   public static final String rcsid;

   public SessionPort(int port) {
      this._port = port;
   }

   public final int getPort() {
      return this._port;
   }

   @Override
   public final void toEvent(DispatcherEventHandler handler) {
      handler.sessionPort(this._port);
   }

   @Override
   final String getClassName() {
      return "SessionPort";
   }

   @Override
   final String getProperties() {
      return this.toPropertyString("sessionPort", String.valueOf(this._port));
   }
}
