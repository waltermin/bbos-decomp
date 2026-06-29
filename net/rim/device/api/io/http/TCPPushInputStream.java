package net.rim.device.api.io.http;

import java.io.InputStream;
import net.rim.device.cldc.io.http.HttpServerProtocolBase;

public final class TCPPushInputStream extends PushInputStream {
   private String _source;
   private HttpServerConnection _connection;
   private boolean _papResponse;

   public TCPPushInputStream(HttpServerConnection connection, InputStream in) {
      super(in);
      this._connection = connection;

      try {
         if (this._connection instanceof HttpServerProtocolBase) {
            this._source = ((HttpServerProtocolBase)this._connection).getAddress();
            return;
         }
      } finally {
         return;
      }
   }

   @Override
   public final String getSource() {
      return this._source;
   }

   @Override
   public final int getConnectionType() {
      return 2;
   }

   @Override
   public final void decline(int reasonCode) {
      this.close();
      if (this._papResponse) {
         this.sendPAPResponse(this.mapPAPErrorCode(reasonCode));
      } else {
         this._connection.setResponseCode(400);
      }
   }

   @Override
   public final void accept() {
      this.close();
      if (this._papResponse) {
         this.sendPAPResponse(400);
      } else {
         this._connection.setResponseCode(200);
      }
   }

   private final void sendPAPResponse(int status) {
      this._connection.setResponseCode(204);
      this._connection.setResponseProperty("X-Wap-Push-Status", Integer.toString(status));
   }

   private final int mapPAPErrorCode(int code) {
      switch (code) {
         case 233:
            return 256;
         case 234:
         case 235:
         case 236:
         case 237:
         case 238:
         default:
            return code;
      }
   }

   @Override
   public final boolean isChannelEncrypted() {
      return false;
   }

   public final void setDoWAPResponse(boolean value) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }
}
