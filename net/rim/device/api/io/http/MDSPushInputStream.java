package net.rim.device.api.io.http;

import java.io.InputStream;
import net.rim.device.cldc.io.proxyhttp.ServerProtocol;

public final class MDSPushInputStream extends PushInputStream {
   private String _source;
   private HttpServerConnection _connection;
   private boolean _papResponse;
   private boolean _channelEncrypted;

   public MDSPushInputStream(HttpServerConnection connection, InputStream in) {
      super(in);
      this._connection = connection;

      label29:
      try {
         if (this._connection.getHeaderField("X-Wap-Push-OTA-Version") != null) {
            this._papResponse = true;
         }
      } finally {
         break label29;
      }

      if (connection instanceof ServerProtocol) {
         this._source = ((ServerProtocol)connection).getGroupUID();
         this._channelEncrypted = ((ServerProtocol)connection).isChannelEncrypted();
      }
   }

   @Override
   public final String getSource() {
      return this._source;
   }

   @Override
   public final int getConnectionType() {
      return 3;
   }

   @Override
   public final boolean isChannelEncrypted() {
      return this._channelEncrypted;
   }

   @Override
   public final void decline(int reasonCode) {
      this.close();
      if (this._connection instanceof ServerProtocol) {
         ServerProtocol serverConnection = (ServerProtocol)this._connection;
         if (this._papResponse) {
            this.sendPAPResponse(serverConnection, this.mapPAPErrorCode(reasonCode));
            return;
         }

         serverConnection.setResponseCode(400);
      }
   }

   @Override
   public final void accept() {
      this.close();
      if (this._connection instanceof ServerProtocol) {
         ServerProtocol serverConnection = (ServerProtocol)this._connection;
         if (this._papResponse) {
            this.sendPAPResponse(serverConnection, 400);
            return;
         }

         serverConnection.setResponseCode(200);
      }
   }

   private final void sendPAPResponse(ServerProtocol protocol, int status) {
      protocol.setResponseCode(204);
      protocol.setResponseProperty("X-Wap-Push-Status", Integer.toString(status));
   }

   private final int mapPAPErrorCode(int code) {
      switch (code) {
         case 234:
            return 256;
         case 235:
         default:
            return 235;
         case 236:
            return 236;
         case 237:
            return 237;
         case 238:
            return 238;
      }
   }
}
