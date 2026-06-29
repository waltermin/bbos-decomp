package net.rim.device.cldc.io.proxyhttp;

import javax.microedition.io.StreamConnection;
import net.rim.device.cldc.io.http.HttpServerProtocolBase;
import net.rim.device.cldc.io.ippp.StreamProtocol;
import net.rim.device.cldc.io.utility.URL;

public final class ServerProtocol extends HttpServerProtocolBase {
   public ServerProtocol(URL url, StreamConnection subConnection) {
      super(url, null, subConnection, subConnection.openDataInputStream(), subConnection.openDataOutputStream());
   }

   public final String getGroupUID() {
      return !(super._subConnection instanceof StreamProtocol) ? null : ((StreamProtocol)super._subConnection).getGroupUID();
   }

   public final boolean isChannelEncrypted() {
      return !(super._subConnection instanceof StreamProtocol) ? false : ((StreamProtocol)super._subConnection).isChannelEncrypted();
   }
}
