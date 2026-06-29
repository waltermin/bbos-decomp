package net.rim.device.cldc.io.proxyhttp;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import javax.microedition.io.StreamConnection;
import net.rim.device.api.io.http.HttpProtocolConstants;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.cldc.io.http.HttpProtocolBase;
import net.rim.device.cldc.io.http.RequestLine;
import net.rim.device.cldc.io.http.StatusLine;
import net.rim.device.cldc.io.ippp.IPPPTransportBase;
import net.rim.device.cldc.io.ippp.SocketBaseIOException;
import net.rim.device.cldc.io.ippp.SocketInputStream;
import net.rim.device.cldc.io.ippp.SocketOutputStream;
import net.rim.device.cldc.io.proxyhttp.compression.HttpCompressionManager;
import net.rim.device.cldc.io.proxyhttp.compression.MessageEncoder;
import net.rim.device.cldc.io.utility.URL;

public final class ClientProtocol extends HttpProtocolBase implements HttpProtocolConstants {
   private InputStream _inputStream;
   private RequestLine _requestLine;
   private StatusLine _statusLine;
   private StreamConnection _streamConnection;
   private ByteArrayOutputStream _outputStream;
   private boolean _compress;
   private DataOutputStream _streamOutputStream;
   private Object _syncObject = new Object();
   private boolean _successfulRequest;
   private String _uid;
   private boolean _inputSet;
   private boolean _usePipe;
   private boolean _ykEncoded;
   private boolean _ykMixedEncoded;
   private static final String MSG_Connection_is_closed = "Connection is closed";
   private static final String HEADER_X_RIM_CONTINUE_TIME = "x-rim-conttime";

   public ClientProtocol(URL url, StreamConnection streamConnection, boolean redirectHttps, boolean trustAll, boolean compress, String uid) {
      super(url);
      this._compress = compress;
      this._uid = uid;
      this._requestLine = new RequestLine();
      this._statusLine = new StatusLine();
      this._requestLine.setVersion("HTTP/1.1");
      this._requestLine.setMethod("GET");
      StringBuffer temp = (StringBuffer)(new Object());
      String tempStr = super._url.getPath();
      if (tempStr != null) {
         temp.append(tempStr);
      }

      tempStr = super._url.getQuery();
      if (tempStr != null) {
         temp.append('?').append(tempStr);
      }

      tempStr = super._url.getFragment();
      if (tempStr != null) {
         temp.append('#').append(tempStr);
      }

      this._requestLine.setRequestURI(temp.toString());
      tempStr = super._url.getHost();
      if (tempStr != null) {
         int port = super._url.getPort();
         String scheme = super._url.getScheme();
         if ((port != 80 || !"http".equals(scheme)) && (port != 443 || !"https".equals(scheme))) {
            tempStr = ((StringBuffer)(new Object())).append(tempStr).append(':').append(port).toString();
         }
      } else {
         tempStr = "";
      }

      super._requestHeaders.setProperty("host", tempStr);
      if (redirectHttps) {
         super._requestHeaders.setProperty("x-rim-redirect-https", "");
      }

      if (trustAll) {
         super._requestHeaders.setProperty("x-rim-trust-all", "");
      }

      this._streamConnection = streamConnection;
   }

   @Override
   protected final OutputStream getOutputStream() {
      if (this._outputStream == null) {
         this._outputStream = (ByteArrayOutputStream)(new Object());
      }

      return this._outputStream;
   }

   @Override
   public final void setRequestMethod(String method) {
      super.setRequestMethod(method);
      this._requestLine.setMethod(method);
   }

   @Override
   public final InputStream getInputStream() {
      if (this._inputStream == null) {
         synchronized (this._syncObject) {
            this._inputStream = this._streamConnection.openInputStream();
         }
      }

      if (!this._inputSet) {
         this._inputSet = true;
         if (this._usePipe && this._inputStream instanceof SocketInputStream) {
            this._inputStream = ((SocketInputStream)this._inputStream).getPipeInputStream(this._ykEncoded, this._ykMixedEncoded);
         } else if (this._ykMixedEncoded) {
            this._inputStream = (InputStream)(new Object(this._inputStream));
         } else if (this._ykEncoded) {
            this._inputStream = (InputStream)(new Object(this._inputStream));
         }
      }

      return this._inputStream;
   }

   public final void requestPipeInputStream() {
      this._usePipe = true;
   }

   @Override
   protected final void writeRequest() {
      OutputStream out = this._streamConnection.openOutputStream();
      if (out instanceof SocketOutputStream) {
         ((SocketOutputStream)out).setAutoFlushMode(false);
      }

      this._streamOutputStream = this._streamConnection.openDataOutputStream();
      byte[] data = null;
      if (this._outputStream != null && this._outputStream.size() > 0) {
         this._outputStream.close();
         data = this._outputStream.toByteArray();
         super._requestHeaders.setProperty("Content-Length", Integer.toString(data.length));
      } else {
         String method = this._requestLine.getMethod();
         if (!this._compress || method.equals("POST") || method.equals("PUT")) {
            super._requestHeaders.setProperty("Content-Length", "0");
         }
      }

      super._requestHeaders.setProperty("x-rim-conttime", Integer.toString(IPPPTransportBase.getTransportInteractivePingPacketTimeout()));
      if (this._compress) {
         MessageEncoder messageEncoder = HttpCompressionManager.getInstance().getMessageEncoderFor(this._uid);
         this._streamOutputStream.write(messageEncoder.getVersion());
         messageEncoder.encodeRequestLine(this._requestLine.toString(), this._streamOutputStream);
         int size = super._requestHeaders.size();

         for (int i = 0; i < size; i++) {
            messageEncoder.encodeHeader(super._requestHeaders.getPropertyKey(i), super._requestHeaders.getPropertyValue(i), this._streamOutputStream);
         }

         this._streamOutputStream.write(0);
      } else {
         this._requestLine.writeToStream(this._streamOutputStream);
         super._requestHeaders.writeToStream(this._streamOutputStream);
      }

      if (data != null) {
         this._streamOutputStream.write(data);
      }

      this._streamOutputStream.flush();
   }

   @Override
   protected final void readResponse() throws SocketBaseIOException {
      synchronized (this._syncObject) {
         this._inputStream = this._streamConnection.openInputStream();
      }

      do {
         if (!this._compress) {
            this._statusLine.readFromStream((InputStream)(new Object(this._inputStream)));
            super._responseHeaders.readFromStream((DataInputStream)(new Object(this._inputStream)));
         } else {
            HttpCompressionManager comprMngr = HttpCompressionManager.getInstance();

            int compressionVersion;
            try {
               compressionVersion = this._inputStream.read();
            } catch (SocketBaseIOException e) {
               comprMngr.setDefaultCompressionVersionFor(this._uid);
               throw e;
            }

            if (compressionVersion == -1) {
               throw new Object("");
            }

            MessageEncoder messageEncoder = comprMngr.setCompressionVersionFor(this._uid, compressionVersion);
            if (messageEncoder == null) {
               throw new Object("Unsupported compression version");
            }

            this._statusLine.parseFromString(messageEncoder.decodeResponseLine(this._inputStream));
            String[] nameValuePair = new Object[2];

            while (true) {
               messageEncoder.decodeHeader(this._inputStream, nameValuePair);
               if (nameValuePair[0] == null || nameValuePair[0].length() <= 0) {
                  break;
               }

               super._responseHeaders.addProperty(nameValuePair[0], nameValuePair[1] == null ? "" : nameValuePair[1]);
               nameValuePair[0] = null;
               nameValuePair[1] = null;
            }
         }

         super._responseCode = this._statusLine.getStatusAsInt();
         super._responseMessage = this._statusLine.getStatusPhrase();
      } while (super._responseCode == 100);

      String transferEncoding = super._responseHeaders.getPropertyValue("x-rim-transfer-encoding");
      this._ykMixedEncoded = StringUtilities.strEqualIgnoreCase(transferEncoding, "yk-mixed");
      this._ykEncoded = StringUtilities.strEqualIgnoreCase(transferEncoding, "yk");
      synchronized (this._syncObject) {
         this._successfulRequest = true;
      }
   }

   @Override
   public final void close() {
      synchronized (this._syncObject) {
         if ((!super._inputStreamOpened || !this._successfulRequest) && this._inputStream != null) {
            this._inputStream.close();
         }
      }

      super.close();
      if (this._streamOutputStream != null) {
         this._streamOutputStream.close();
      }

      this._streamConnection.close();
   }

   public final String getGroupUID() {
      return this._uid;
   }
}
