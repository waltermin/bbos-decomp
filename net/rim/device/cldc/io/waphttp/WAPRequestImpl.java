package net.rim.device.cldc.io.waphttp;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.microedition.io.SecurityInfo;
import net.rim.device.api.io.http.HttpHeaders;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.cldc.io.http.HttpProtocolBase;
import net.rim.device.cldc.io.utility.SessionStats;
import net.rim.device.cldc.io.utility.URL;
import net.rim.device.cldc.io.utility.URLParameters;
import net.rim.device.internal.browser.util.Pipe;

public class WAPRequestImpl extends HttpProtocolBase implements WAPRequest {
   private Pipe _pipe;
   private boolean _aborted;
   private InputStream _inputStream;
   private Object _abortContext;
   private WAPConnectionImpl _connection;
   private WAPConnectionParams _params;
   private ByteArrayOutputStream _outputStream;
   private Object _abortSync = new Object();
   private static final String CONNECTION_UID = "ConnectionUID";
   private static final String CONNECTION_TIMEOUT = "ConnectionTimeout";
   private static final String WAP_GATEWAY_IP = "WapGatewayIP";
   private static final String WAP_GATEWAY_PORT = "WapGatewayPort";
   private static final String WAP_GATEWAY_APN = "WapGatewayAPN";
   private static final String WAP_SOURCE_PORT = "WapSourcePort";
   private static final String WAP_ENABLE_WTLS = "WapEnableWTLS";
   private static final String APN_TunnelAuthUsername = "TunnelAuthUsername";
   private static final String APN_TunnelAuthPassword = "TunnelAuthPassword";
   private static final int WAP_GATEWAY_PORT_DEFAULT = 9201;
   private static final int WAP_GATEWAY_SPORT_DEFAULT = 9203;
   private static final int WAP_SOURCE_PORT_DEFAULT = 8205;

   public void setStatus(int status) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   HttpHeaders getRequestHeaders2() {
      return this.getRequestHeaders();
   }

   public void setInputStream(InputStream stream) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   public void setPipe(Pipe pipe) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   public String getURLWithoutRIMParameters() {
      return super._url.toStringWithoutRIMParams();
   }

   public boolean isAborted() {
      synchronized (this._abortSync) {
         return this._aborted;
      }
   }

   void setAbortContext(Object context) {
      synchronized (this._abortSync) {
         this._abortContext = context;
      }
   }

   void clearAbortContext() {
      synchronized (this._abortSync) {
         this._abortContext = null;
      }
   }

   Object getAbortLock() {
      return this._abortSync;
   }

   byte getRequestMethodByte() {
      if (super._requestMethod.equals("GET")) {
         return 64;
      } else if (super._requestMethod.equals("POST")) {
         return 96;
      } else if (super._requestMethod.equals("OPTIONS")) {
         return 65;
      } else if (super._requestMethod.equals("HEAD")) {
         return 66;
      } else if (super._requestMethod.equals("PUT")) {
         return 97;
      } else if (super._requestMethod.equals("TRACE")) {
         return 68;
      } else if (super._requestMethod.equals("DELETE")) {
         return 67;
      } else {
         return (byte)(super._requestMethod.equals("CONNECT") ? 1 : 64);
      }
   }

   byte[] getPostData() {
      return this._outputStream != null ? this._outputStream.toByteArray() : null;
   }

   @Override
   public SessionStats getSessionStats() {
      return this._connection != null ? this._connection.getSessionStats() : null;
   }

   @Override
   public SecurityInfo getRIMSecurityInfo() {
      return this._connection != null ? this._connection.getRIMSecurityInfo() : null;
   }

   @Override
   public WAPConnectionParams getParameters() {
      return this._params;
   }

   @Override
   public Pipe getPipe() {
      return this._pipe;
   }

   @Override
   protected final InputStream getInputStream() {
      return this._inputStream;
   }

   @Override
   protected final OutputStream getOutputStream() {
      if (this._outputStream == null) {
         this._outputStream = new ByteArrayOutputStream();
      }

      return this._outputStream;
   }

   public WAPRequestImpl(URL url) {
      super(url);
      this.processUrlParameters(url.getRIMParameters());
   }

   @Override
   public void close() {
      synchronized (this._abortSync) {
         this._aborted = true;
         if (this._connection != null && this._abortContext != null) {
            this._connection.abort(this._abortContext);
         }
      }

      super.close();
   }

   @Override
   protected final void readResponse() throws IOException {
      WAPConnectionRegistry.getInstance();
      WAPConnectionImpl connection = (WAPConnectionImpl)WAPConnectionRegistry.getConnection(this._params);
      if (connection == null) {
         throw new IOException("Failed to open connection");
      }

      synchronized (this._abortSync) {
         this._connection = connection;
         if (this._aborted) {
            return;
         }
      }

      connection.sendRequest(this);
   }

   @Override
   protected final void writeRequest() {
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private void processUrlParameters(URLParameters parameters) throws IOException {
      this._params = new WAPConnectionParams();
      boolean xEnableWTLSFound = false;
      String temp = parameters.getValue("ConnectionTimeout");
      if (temp != null) {
         label288:
         try {
            this._params._timeout = Integer.parseInt(temp.trim());
         } finally {
            break label288;
         }
      }

      temp = parameters.getValue("ConnectionUID");
      if (temp == null || !this._params.loadFrom(temp.trim())) {
         boolean valueSet = false;
         temp = parameters.getValue("WapGatewayIP");
         if (temp != null) {
            temp = temp.trim();
            int index1 = temp.indexOf(46);
            if (index1 != -1) {
               int index2 = temp.indexOf(46, index1 + 1);
               if (index2 != -1) {
                  int index3 = temp.indexOf(46, index2 + 1);
                  if (index3 != -1) {
                     label280:
                     try {
                        int pos1 = Integer.parseInt(temp.substring(0, index1)) & 0xFF;
                        int pos2 = Integer.parseInt(temp.substring(index1 + 1, index2)) & 0xFF;
                        int pos3 = Integer.parseInt(temp.substring(index2 + 1, index3)) & 0xFF;
                        int pos4 = Integer.parseInt(temp.substring(index3 + 1, temp.length())) & 0xFF;
                        this._params._wapServerAddress = pos1 << 24 | pos2 << 16 | pos3 << 8 | pos4;
                        valueSet = true;
                     } finally {
                        break label280;
                     }
                  }
               }
            }
         }

         if (!valueSet) {
            throw new IOException("WapGatewayIP should be assigned an IP address");
         }

         temp = parameters.getValue("WapGatewayAPN");
         switch (RadioInfo.getNetworkType()) {
            case 3:
            case 7:
               if (temp == null) {
                  throw new IOException("WapGatewayAPN should be provided");
               }

               this._params._wapServerAPN = temp.trim();
               if (this._params._wapServerAPN.length() == 0) {
                  throw new IOException("WapGatewayAPN should be provided with a value");
               }
               break;
            default:
               if (temp == null) {
                  this._params._wapServerAPN = "";
               } else {
                  this._params._wapServerAPN = temp.trim();
               }
         }

         temp = parameters.getValue("WapEnableWTLS");
         if (temp != null) {
            temp = temp.trim();
            if (StringUtilities.strEqualIgnoreCase(temp, "openwave", 1701707776)) {
               this._params._secureAccess = true;
               this._params._wtlsMode = 1;
               this._params._wtlsClientIdType = -1;
            } else {
               this._params._secureAccess = StringUtilities.strEqualIgnoreCase(temp, "true", 1701707776);
            }

            xEnableWTLSFound = true;
         }

         temp = parameters.getValue("WapGatewayPort");
         if (temp == null) {
            this._params._wapServerPort = !xEnableWTLSFound ? 9201 : (this._params._secureAccess ? 9203 : 9201);
         } else {
            boolean var21 = false /* VF: Semaphore variable */;

            try {
               var21 = true;
               this._params._wapServerPort = Integer.parseInt(temp.trim());
               if (!xEnableWTLSFound) {
                  if (this._params._wapServerPort == 9203) {
                     this._params._secureAccess = true;
                     var21 = false;
                  } else {
                     var21 = false;
                  }
               } else {
                  var21 = false;
               }
            } finally {
               if (var21) {
                  throw new IOException("WapGatewayPort parameter is invalid");
               }
            }
         }

         temp = parameters.getValue("WapSourcePort");
         if (temp == null) {
            this._params._srcPort = 8205;
         } else {
            boolean var16 = false /* VF: Semaphore variable */;

            try {
               var16 = true;
               this._params._srcPort = Integer.parseInt(temp.trim());
               var16 = false;
            } finally {
               if (var16) {
                  throw new IOException("WapSourcePort parameter is invalid");
               }
            }
         }

         temp = parameters.getValue("TunnelAuthUsername");
         if (temp != null) {
            this._params._authUsername = temp.trim();
         }

         temp = parameters.getValue("TunnelAuthPassword");
         if (temp != null) {
            this._params._authPassword = temp.trim();
         }
      }
   }

   public WAPRequestImpl(URL url, WAPConnectionParams params) {
      super(url);
      this._params = params;
   }
}
