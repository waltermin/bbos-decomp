package net.rim.device.cldc.io.devicehttps;

import java.io.InputStream;
import java.io.OutputStream;
import javax.microedition.io.HttpsConnection;
import javax.microedition.io.SecureConnection;
import javax.microedition.io.SecurityInfo;
import javax.microedition.io.SocketConnection;
import javax.microedition.io.StreamConnection;
import net.rim.device.api.io.ParentStreamProvider;
import net.rim.device.api.io.http.HttpHeaders;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.cldc.io.http.HttpProtocolBase;
import net.rim.device.cldc.io.utility.URL;

public final class ClientProtocol extends net.rim.device.cldc.io.devicehttp.ClientProtocol implements HttpsConnection {
   private SecureConnection _secureConnection;

   public final SecureConnection getSecureConnection() {
      return this._secureConnection;
   }

   public final StreamConnection getTLSSubConnection() {
      return !(this._secureConnection instanceof ParentStreamProvider) ? null : ((ParentStreamProvider)this._secureConnection).getParentStream();
   }

   @Override
   public final SecurityInfo getSecurityInfo() {
      return this._secureConnection != null ? this._secureConnection.getSecurityInfo() : null;
   }

   public ClientProtocol(
      String originalHost,
      URL url,
      SecureConnection secureConnection,
      InputStream in,
      OutputStream out,
      boolean explicitHttpProxy,
      boolean useHttp11,
      int originalMode,
      boolean originalTimeouts,
      boolean keepAlive,
      ServiceRecord record,
      boolean usePersistentConnections
   ) {
      super(
         originalHost,
         url,
         secureConnection,
         in,
         out,
         explicitHttpProxy,
         useHttp11,
         originalMode,
         originalTimeouts,
         keepAlive,
         record,
         usePersistentConnections
      );
      this._secureConnection = secureConnection;
   }

   @Override
   protected final void setSocketConnection(SocketConnection socketConnection) {
      super.setSocketConnection(socketConnection);
      if (socketConnection instanceof Object) {
         this._secureConnection = (SecureConnection)socketConnection;
      }
   }

   @Override
   protected final synchronized void transitionToState(int newState) {
      if (newState == 1 && super._state != 1) {
         HttpHeaders hdrs = (HttpHeaders)this.getRequestHeaders();
         hdrs.addProperty("X-RIM-HTTPS", "1.1");
         StreamConnection sub = this.getTLSSubConnection();
         if (sub instanceof Object) {
            HttpProtocolBase proxyConn = (HttpProtocolBase)sub;
            int size = hdrs.size();
            String proxyStr = "proxy-";
            int i = 0;

            while (i < size) {
               String hdr = hdrs.getPropertyKey(i);
               if (StringUtilities.startsWithIgnoreCase(hdr, proxyStr, 1701707776)) {
                  label131:
                  try {
                     proxyConn.setRequestProperty(hdr, hdrs.getPropertyValue(i));
                  } finally {
                     break label131;
                  }

                  hdrs.removeProperty(i);
                  size--;
               } else if (!StringUtilities.strEqualIgnoreCase(hdr, "User-Agent", 1701707776)
                  && !StringUtilities.strEqualIgnoreCase(hdr, "x-wap-profile", 1701707776)) {
                  i++;
               } else {
                  label123:
                  try {
                     proxyConn.setRequestProperty(hdr, hdrs.getPropertyValue(i));
                  } finally {
                     break label123;
                  }

                  i++;
               }
            }

            i = proxyConn.getResponseCode();
            if (i == 407) {
               super._responseHeaders = (HttpHeaders)proxyConn.getResponseHeaders();
               super._responseCode = 407;
               super._state = 1;
               this._secureConnection = null;
               return;
            }

            if (i != 200) {
               throw new Object();
            }
         }

         if (this._secureConnection != null && super._socketInputStream == null) {
            super._socketInputStream = this._secureConnection.openInputStream();
         }
      }

      super.transitionToState(newState);
   }
}
