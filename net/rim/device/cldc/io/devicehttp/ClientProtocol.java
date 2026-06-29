package net.rim.device.cldc.io.devicehttp;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Vector;
import javax.microedition.io.Connector;
import javax.microedition.io.SocketConnection;
import net.rim.device.api.crypto.HMAC;
import net.rim.device.api.crypto.HMACKey;
import net.rim.device.api.crypto.MD5Digest;
import net.rim.device.api.io.ConnectionCloseListener;
import net.rim.device.api.io.ConnectionCloseProvider;
import net.rim.device.api.io.ConnectionClosedException;
import net.rim.device.api.io.NoCopyByteArrayOutputStream;
import net.rim.device.api.io.SocketConnectionEnhanced;
import net.rim.device.api.io.http.AuthScheme;
import net.rim.device.api.io.http.HttpProtocolConstants;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.system.CDMAInfo;
import net.rim.device.api.system.Phone;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.util.NumberUtilities;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.cldc.io.http.HttpProtocolBase;
import net.rim.device.cldc.io.http.RequestLine;
import net.rim.device.cldc.io.http.StatusLine;
import net.rim.device.cldc.io.utility.URL;
import net.rim.device.internal.browser.wap.ClientID;
import net.rim.device.internal.browser.wap.WPTCPServiceRecord;

public class ClientProtocol
   extends HttpProtocolBase
   implements HttpProtocolConstants,
   SocketConnection,
   SocketConnectionEnhanced,
   ConnectionCloseProvider,
   ConnectionCloseListener {
   private InputStream _httpInputStream;
   private RequestLine _requestLine;
   private StatusLine _statusLine;
   private SocketConnection _socketConnection;
   private ByteArrayOutputStream _outputStream;
   protected InputStream _socketInputStream;
   private OutputStream _socketOutputStream;
   private Object _syncObject = new Object();
   private boolean _successfulRequest;
   private boolean _connectMethod;
   private String _originalHost;
   private boolean _usePersistentConnections;
   private int _originalMode;
   private boolean _originalTimeouts;
   private boolean _explicitHttpProxy;
   private ConnectionCloseListener _closeListener;
   private AuthScheme _authScheme;
   private ServiceRecord _serviceRecord;
   private boolean _closeCalled;
   private boolean _newPersistentConnectionOpened;
   private static final String PROXY_AUTHENTICATION_INFO = "Proxy-Authentication-Info";

   public ClientProtocol(
      String originalHost,
      URL url,
      SocketConnection socketConnection,
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
      super(url);
      if (socketConnection instanceof SocketConnectionEnhanced) {
         label104:
         try {
            ((SocketConnectionEnhanced)socketConnection).setSocketOptionEx((short)256, 120000);
         } finally {
            break label104;
         }
      }

      this._serviceRecord = record;
      this._usePersistentConnections = usePersistentConnections;
      this._originalHost = originalHost;
      this._originalTimeouts = originalTimeouts;
      this._originalMode = originalMode;
      this._explicitHttpProxy = explicitHttpProxy;
      this._requestLine = new RequestLine();
      this._statusLine = new StatusLine();
      this._requestLine.setVersion(useHttp11 ? "HTTP/1.1" : "HTTP/1.0");
      this._requestLine.setMethod("GET");
      StringBuffer temp = new StringBuffer();
      if (explicitHttpProxy) {
         temp.append(url.toStringWithoutRIMParams());
      } else {
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
      }

      this._requestLine.setRequestURI(temp.toString());
      String tempStr = super._url.getHost();
      if (tempStr != null) {
         int port = super._url.getPort();
         String scheme = super._url.getScheme();
         if ((port != 80 || !"http".equals(scheme)) && (port != 443 || !"https".equals(scheme))) {
            tempStr = tempStr + ':' + port;
         }
      } else {
         tempStr = "";
      }

      super._requestHeaders.setProperty("Host", tempStr);
      super._requestHeaders.setProperty(this._explicitHttpProxy ? "Proxy-Connection" : "Connection", keepAlive ? "keep-alive" : "close");
      this.setSocketConnection(socketConnection);
      this._socketOutputStream = out;
      this._socketInputStream = in;
      if (originalHost != null) {
         this._authScheme = HttpConnectionManager.getInstance().getAuthScheme(originalHost);
      }

      if (this._authScheme == null) {
         this.deriveNewAuth();
      }
   }

   @Override
   protected OutputStream getOutputStream() {
      if (this._connectMethod) {
         this.transitionToState(1);
         return this._socketOutputStream;
      }

      if (this._outputStream == null) {
         this._outputStream = new ByteArrayOutputStream();
      }

      return this._outputStream;
   }

   @Override
   public final void setRequestMethod(String method) {
      super.setRequestMethod(method);
      this._requestLine.setMethod(method);
      if (method.equals("CONNECT")) {
         this._connectMethod = true;
         StringBuffer temp = new StringBuffer();
         String tempStr = super._url.getHost();
         if (tempStr != null) {
            temp.append(tempStr).append(':').append(super._url.getPort());
            this._requestLine.setRequestURI(temp.toString());
         }
      }
   }

   private boolean responseHasNoBody() {
      return super._responseCode == 304
         || super._responseCode == 204
         || super._responseCode == 205
         || super._responseCode >= 100 && super._responseCode < 200
         || this._requestLine.getMethod().equals("HEAD");
   }

   @Override
   protected InputStream getInputStream() {
      if (this._httpInputStream == null && this._socketInputStream != null && super._responseHeaders != null) {
         if (this.responseHasNoBody()) {
            this._httpInputStream = new ByteArrayInputStream(new byte[0]);
         } else {
            String transferEncoding = super._responseHeaders.getPropertyValue("Transfer-Encoding");
            String contentLength = super._responseHeaders.getPropertyValue("Content-Length");
            boolean keepAlive = this.useKeepAlive();
            int contentLen = 0;
            if (StringUtilities.strEqualIgnoreCase(transferEncoding, "chunked", 1701707776)) {
               this._httpInputStream = new ChunkedInputStream(this._socketInputStream, !keepAlive);
            } else if (contentLength != null && (super._responseCode != 200 || !this._requestLine.getMethod().equals("CONNECT"))) {
               contentLen = Integer.parseInt(contentLength);
               this._httpInputStream = new LengthControlledInputStream(this._socketInputStream, contentLen, true, !keepAlive);
            } else {
               this._httpInputStream = this._socketInputStream;
            }
         }
      }

      return this._httpInputStream;
   }

   protected void setSocketConnection(SocketConnection socketConnection) {
      this._socketConnection = socketConnection;
   }

   private void handleIOExceptionBeforeResponse(IOException e) throws IOException {
      if (this._usePersistentConnections && !this._closeCalled && !this._newPersistentConnectionOpened && this._originalHost != null) {
         this.reopenLowerConnection();
         this._newPersistentConnectionOpened = true;
      } else {
         throw e;
      }
   }

   private void reopenLowerConnection() throws IOException {
      label84:
      try {
         this._socketOutputStream.close();
      } finally {
         break label84;
      }

      label81:
      try {
         this._socketInputStream.close();
      } finally {
         break label81;
      }

      label78:
      try {
         this._socketConnection.close();
      } finally {
         break label78;
      }

      if (this._originalHost != null) {
         this.setSocketConnection((SocketConnection)Connector.open(this._originalHost, this._originalMode, this._originalTimeouts));
         if (this._socketConnection == null) {
            throw new IOException();
         }

         this._socketOutputStream = this._socketConnection.openOutputStream();
         this._socketInputStream = this._socketConnection.openInputStream();
         this._httpInputStream = null;
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private boolean checkPersistentConnection() {
      if (this._usePersistentConnections && !this._closeCalled && this._originalHost != null && !this._newPersistentConnectionOpened) {
         if (!(this._socketConnection instanceof ConnectionCloseProvider)) {
            try {
               this._socketConnection.getAddress();
               return true;
            } catch (Throwable var3) {
               this.handleIOExceptionBeforeResponse(e);
               return false;
            }
         }

         ConnectionCloseProvider connectionCloseProvider = (ConnectionCloseProvider)this._socketConnection;
         if (!connectionCloseProvider.isConnectionEstablished()) {
            this.reopenLowerConnection();
            this._newPersistentConnectionOpened = true;
            return false;
         }
      }

      return true;
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   protected void writeRequest() {
      this.checkPersistentConnection();
      byte[] data = null;
      if (this._outputStream != null && this._outputStream.size() > 0) {
         this._outputStream.close();
         data = this._outputStream.toByteArray();
         super._requestHeaders.setProperty("Content-Length", Integer.toString(data.length));
      } else {
         String method = this._requestLine.getMethod();
         if (method.equals("POST") || method.equals("PUT")) {
            super._requestHeaders.setProperty("Content-Length", "0");
         }
      }

      this.addProxyAuthHeaders();
      NoCopyByteArrayOutputStream bytesOut = new NoCopyByteArrayOutputStream();
      DataOutputStream dout = new DataOutputStream(bytesOut);
      this._requestLine.writeToStream(dout);
      super._requestHeaders.writeToStream(dout);
      if (data != null) {
         dout.write(data);
      }

      dout.close();
      this.checkPersistentConnection();

      ConnectionClosedException ioce;
      try {
         try {
            this.writeRequestBytes(bytesOut);
            return;
         } catch (ConnectionClosedException var7) {
            ioce = var7;
         }
      } catch (Throwable var8) {
         this.handleIOExceptionBeforeResponse(ioce);
         this.writeRequestBytes(bytesOut);
         return;
      }

      this.handleIOExceptionBeforeResponse(ioce);
      this.writeRequestBytes(bytesOut);
   }

   private void writeRequestBytes(NoCopyByteArrayOutputStream out) {
      this._socketOutputStream.write(out.getByteArray(), 0, out.size());
      this._socketOutputStream.flush();
   }

   @Override
   protected void readResponse() {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: bipush 0
      // 001: istore 1
      // 002: aload 0
      // 003: invokespecial net/rim/device/cldc/io/devicehttp/ClientProtocol.deriveNewAuth ()V
      // 006: aload 0
      // 007: invokespecial net/rim/device/cldc/io/devicehttp/ClientProtocol.checkPersistentConnection ()Z
      // 00a: ifne 011
      // 00d: aload 0
      // 00e: invokevirtual net/rim/device/cldc/io/devicehttp/ClientProtocol.writeRequest ()V
      // 011: aload 0
      // 012: getfield net/rim/device/cldc/io/devicehttp/ClientProtocol._statusLine Lnet/rim/device/cldc/io/http/StatusLine;
      // 015: aload 0
      // 016: getfield net/rim/device/cldc/io/devicehttp/ClientProtocol._socketInputStream Ljava/io/InputStream;
      // 019: invokevirtual net/rim/device/cldc/io/http/StatusLine.readFromStream (Ljava/io/InputStream;)V
      // 01c: goto 064
      // 01f: astore 2
      // 020: aload 0
      // 021: aload 2
      // 022: invokespecial net/rim/device/cldc/io/devicehttp/ClientProtocol.handleIOExceptionBeforeResponse (Ljava/io/IOException;)V
      // 025: aload 0
      // 026: invokevirtual net/rim/device/cldc/io/devicehttp/ClientProtocol.writeRequest ()V
      // 029: aload 0
      // 02a: getfield net/rim/device/cldc/io/devicehttp/ClientProtocol._statusLine Lnet/rim/device/cldc/io/http/StatusLine;
      // 02d: aload 0
      // 02e: getfield net/rim/device/cldc/io/devicehttp/ClientProtocol._socketInputStream Ljava/io/InputStream;
      // 031: invokevirtual net/rim/device/cldc/io/http/StatusLine.readFromStream (Ljava/io/InputStream;)V
      // 034: goto 064
      // 037: astore 2
      // 038: aload 0
      // 039: aload 2
      // 03a: invokespecial net/rim/device/cldc/io/devicehttp/ClientProtocol.handleIOExceptionBeforeResponse (Ljava/io/IOException;)V
      // 03d: aload 0
      // 03e: invokevirtual net/rim/device/cldc/io/devicehttp/ClientProtocol.writeRequest ()V
      // 041: aload 0
      // 042: getfield net/rim/device/cldc/io/devicehttp/ClientProtocol._statusLine Lnet/rim/device/cldc/io/http/StatusLine;
      // 045: aload 0
      // 046: getfield net/rim/device/cldc/io/devicehttp/ClientProtocol._socketInputStream Ljava/io/InputStream;
      // 049: invokevirtual net/rim/device/cldc/io/http/StatusLine.readFromStream (Ljava/io/InputStream;)V
      // 04c: goto 064
      // 04f: astore 2
      // 050: aload 0
      // 051: aload 2
      // 052: invokespecial net/rim/device/cldc/io/devicehttp/ClientProtocol.handleIOExceptionBeforeResponse (Ljava/io/IOException;)V
      // 055: aload 0
      // 056: invokevirtual net/rim/device/cldc/io/devicehttp/ClientProtocol.writeRequest ()V
      // 059: aload 0
      // 05a: getfield net/rim/device/cldc/io/devicehttp/ClientProtocol._statusLine Lnet/rim/device/cldc/io/http/StatusLine;
      // 05d: aload 0
      // 05e: getfield net/rim/device/cldc/io/devicehttp/ClientProtocol._socketInputStream Ljava/io/InputStream;
      // 061: invokevirtual net/rim/device/cldc/io/http/StatusLine.readFromStream (Ljava/io/InputStream;)V
      // 064: aload 0
      // 065: getfield net/rim/device/cldc/io/http/HttpProtocolBase._responseHeaders Lnet/rim/device/api/io/http/HttpHeaders;
      // 068: new java/io/DataInputStream
      // 06b: dup
      // 06c: aload 0
      // 06d: getfield net/rim/device/cldc/io/devicehttp/ClientProtocol._socketInputStream Ljava/io/InputStream;
      // 070: invokespecial java/io/DataInputStream.<init> (Ljava/io/InputStream;)V
      // 073: invokevirtual net/rim/device/api/io/http/HttpHeaders.readFromStream (Ljava/io/DataInputStream;)V
      // 076: aload 0
      // 077: aload 0
      // 078: getfield net/rim/device/cldc/io/devicehttp/ClientProtocol._statusLine Lnet/rim/device/cldc/io/http/StatusLine;
      // 07b: invokevirtual net/rim/device/cldc/io/http/StatusLine.getStatusAsInt ()I
      // 07e: putfield net/rim/device/cldc/io/http/HttpProtocolBase._responseCode I
      // 081: aload 0
      // 082: getfield net/rim/device/cldc/io/http/HttpProtocolBase._responseCode I
      // 085: bipush 100
      // 087: if_icmplt 0a5
      // 08a: aload 0
      // 08b: getfield net/rim/device/cldc/io/http/HttpProtocolBase._responseCode I
      // 08e: sipush 200
      // 091: if_icmpge 0a5
      // 094: aload 0
      // 095: getfield net/rim/device/cldc/io/devicehttp/ClientProtocol._statusLine Lnet/rim/device/cldc/io/http/StatusLine;
      // 098: invokevirtual net/rim/device/cldc/io/http/StatusLine.reset ()V
      // 09b: aload 0
      // 09c: getfield net/rim/device/cldc/io/http/HttpProtocolBase._responseHeaders Lnet/rim/device/api/io/http/HttpHeaders;
      // 09f: invokevirtual net/rim/device/api/io/http/HttpHeaders.reset ()V
      // 0a2: goto 006
      // 0a5: aload 0
      // 0a6: getfield net/rim/device/cldc/io/http/HttpProtocolBase._responseCode I
      // 0a9: sipush 407
      // 0ac: if_icmpne 11d
      // 0af: aload 0
      // 0b0: getfield net/rim/device/cldc/io/devicehttp/ClientProtocol._explicitHttpProxy Z
      // 0b3: ifeq 11d
      // 0b6: aload 0
      // 0b7: getfield net/rim/device/cldc/io/devicehttp/ClientProtocol._serviceRecord Lnet/rim/device/api/servicebook/ServiceRecord;
      // 0ba: ifnull 11d
      // 0bd: iload 1
      // 0be: bipush 3
      // 0c0: if_icmplt 0c6
      // 0c3: goto 13b
      // 0c6: aload 0
      // 0c7: invokevirtual net/rim/device/cldc/io/devicehttp/ClientProtocol.getInputStream ()Ljava/io/InputStream;
      // 0ca: astore 2
      // 0cb: aload 2
      // 0cc: ifnull 0e7
      // 0cf: sipush 256
      // 0d2: newarray 8
      // 0d4: astore 3
      // 0d5: aload 2
      // 0d6: aload 3
      // 0d7: invokevirtual java/io/InputStream.read ([B)I
      // 0da: istore 4
      // 0dc: iload 4
      // 0de: bipush -1
      // 0e0: if_icmpne 0d5
      // 0e3: aload 2
      // 0e4: invokevirtual java/io/InputStream.close ()V
      // 0e7: aload 0
      // 0e8: aconst_null
      // 0e9: putfield net/rim/device/cldc/io/devicehttp/ClientProtocol._httpInputStream Ljava/io/InputStream;
      // 0ec: aload 0
      // 0ed: invokespecial net/rim/device/cldc/io/devicehttp/ClientProtocol.useKeepAlive ()Z
      // 0f0: ifne 0f7
      // 0f3: aload 0
      // 0f4: invokespecial net/rim/device/cldc/io/devicehttp/ClientProtocol.reopenLowerConnection ()V
      // 0f7: aload 0
      // 0f8: invokespecial net/rim/device/cldc/io/devicehttp/ClientProtocol.deriveNewAuth ()V
      // 0fb: aload 0
      // 0fc: getfield net/rim/device/cldc/io/devicehttp/ClientProtocol._statusLine Lnet/rim/device/cldc/io/http/StatusLine;
      // 0ff: invokevirtual net/rim/device/cldc/io/http/StatusLine.reset ()V
      // 102: aload 0
      // 103: getfield net/rim/device/cldc/io/http/HttpProtocolBase._responseHeaders Lnet/rim/device/api/io/http/HttpHeaders;
      // 106: invokevirtual net/rim/device/api/io/http/HttpHeaders.reset ()V
      // 109: aload 0
      // 10a: getfield net/rim/device/cldc/io/http/HttpProtocolBase._requestHeaders Lnet/rim/device/api/io/http/HttpHeaders;
      // 10d: ldc_w "Proxy-Authorization"
      // 110: invokevirtual net/rim/device/api/io/http/HttpHeaders.removeProperties (Ljava/lang/String;)V
      // 113: aload 0
      // 114: invokevirtual net/rim/device/cldc/io/devicehttp/ClientProtocol.writeRequest ()V
      // 117: iinc 1 1
      // 11a: goto 006
      // 11d: aload 0
      // 11e: getfield net/rim/device/cldc/io/http/HttpProtocolBase._responseHeaders Lnet/rim/device/api/io/http/HttpHeaders;
      // 121: ldc_w "Proxy-Authentication-Info"
      // 124: invokevirtual net/rim/device/api/io/http/HttpHeaders.getPropertyValue (Ljava/lang/String;)Ljava/lang/String;
      // 127: astore 2
      // 128: aload 2
      // 129: ifnull 13b
      // 12c: aload 0
      // 12d: getfield net/rim/device/cldc/io/devicehttp/ClientProtocol._authScheme Lnet/rim/device/api/io/http/AuthScheme;
      // 130: ifnull 13b
      // 133: aload 0
      // 134: getfield net/rim/device/cldc/io/devicehttp/ClientProtocol._authScheme Lnet/rim/device/api/io/http/AuthScheme;
      // 137: aload 2
      // 138: invokevirtual net/rim/device/api/io/http/AuthScheme.updateAuth (Ljava/lang/String;)V
      // 13b: aload 0
      // 13c: aload 0
      // 13d: getfield net/rim/device/cldc/io/devicehttp/ClientProtocol._statusLine Lnet/rim/device/cldc/io/http/StatusLine;
      // 140: invokevirtual net/rim/device/cldc/io/http/StatusLine.getStatusPhrase ()Ljava/lang/String;
      // 143: putfield net/rim/device/cldc/io/http/HttpProtocolBase._responseMessage Ljava/lang/String;
      // 146: aload 0
      // 147: getfield net/rim/device/cldc/io/devicehttp/ClientProtocol._syncObject Ljava/lang/Object;
      // 14a: dup
      // 14b: astore 2
      // 14c: monitorenter
      // 14d: aload 0
      // 14e: bipush 1
      // 14f: putfield net/rim/device/cldc/io/devicehttp/ClientProtocol._successfulRequest Z
      // 152: aload 2
      // 153: monitorexit
      // 154: return
      // 155: astore 5
      // 157: aload 2
      // 158: monitorexit
      // 159: aload 5
      // 15b: athrow
      // try (9 -> 14): 15 null
      // try (9 -> 14): 27 net/rim/device/api/io/ConnectionClosedException
      // try (9 -> 14): 39 null
      // try (157 -> 162): 163 null
      // try (163 -> 166): 163 null
   }

   private boolean responseIsHttp11OrHigher() {
      String httpResponseVersion = this._statusLine.getVersion();
      if (httpResponseVersion != null) {
         int length = httpResponseVersion.length();
         if (length >= 8 && httpResponseVersion.startsWith("HTTP/")) {
            int dot = httpResponseVersion.indexOf(46);
            if (dot >= 6 && dot + 1 < length) {
               if (length == 8 && httpResponseVersion.charAt(5) == '1') {
                  char minorVersionChar = httpResponseVersion.charAt(7);
                  if (minorVersionChar == '1') {
                     return true;
                  }

                  if (minorVersionChar == '0') {
                     return false;
                  }
               }

               try {
                  int majorVersion = Integer.parseInt(httpResponseVersion.substring(5, dot));
                  int minorVersion = Integer.parseInt(httpResponseVersion.substring(dot + 1));
                  return majorVersion == 1 && minorVersion >= 1 || majorVersion > 1;
               } finally {
                  return false;
               }
            }
         }
      }

      return false;
   }

   private boolean useKeepAlive() {
      String connectionHeader = super._responseHeaders.getPropertyValue(this._explicitHttpProxy ? "Proxy-Connection" : "Connection");
      return (
            this.responseIsHttp11OrHigher()
               ? connectionHeader == null || !StringUtilities.strEqualIgnoreCase(connectionHeader, "close", 1701707776)
               : connectionHeader != null && StringUtilities.strEqualIgnoreCase(connectionHeader, "keep-alive", 1701707776)
         )
         && !this._connectMethod;
   }

   @Override
   public void close() {
      if (!this._closeCalled) {
         this._closeCalled = true;
         if (super._state != 2) {
            boolean keepAlive = (
                  this._httpInputStream instanceof LengthControlledInputStream && ((LengthControlledInputStream)this._httpInputStream).getLength() <= 0
                     || this._httpInputStream instanceof ChunkedInputStream && ((ChunkedInputStream)this._httpInputStream).eof()
                     || this._httpInputStream instanceof ByteArrayInputStream && this._httpInputStream.available() <= 0
                     || this._successfulRequest && this.responseHasNoBody()
               )
               && this.useKeepAlive();
            synchronized (this._syncObject) {
               if ((!super._inputStreamOpened || !this._successfulRequest) && this._socketInputStream != null) {
                  if (!this._successfulRequest) {
                     keepAlive = false;
                  }

                  if (!keepAlive) {
                     label301:
                     try {
                        this._socketInputStream.close();
                     } finally {
                        break label301;
                     }
                  }
               }
            }

            super.close();
            if (this._originalHost != null && this._authScheme != null) {
               HttpConnectionManager.getInstance().addAuthScheme(this._originalHost, this._authScheme);
            }

            boolean close = true;
            if (this._usePersistentConnections && this._originalHost != null && keepAlive) {
               close = !HttpConnectionManager.getInstance()
                  .addConnection(this._originalHost, this._socketConnection, this._socketInputStream, this._socketOutputStream);
            } else {
               HttpConnectionManager.getInstance().removeActiveConnection(this._socketConnection);
            }

            if (close) {
               if (this._socketInputStream != null) {
                  label287:
                  try {
                     this._socketInputStream.close();
                  } finally {
                     break label287;
                  }
               }

               if (this._socketOutputStream != null) {
                  label282:
                  try {
                     this._socketOutputStream.close();
                  } finally {
                     break label282;
                  }
               }

               this._socketConnection.close();
            }
         }
      }
   }

   @Override
   public boolean connectionStatusAvailable() {
      return !(this._socketConnection instanceof ConnectionCloseProvider)
         ? false
         : ((ConnectionCloseProvider)this._socketConnection).connectionStatusAvailable();
   }

   @Override
   public boolean isConnectionEstablished() {
      return !(this._socketConnection instanceof ConnectionCloseProvider) ? false : ((ConnectionCloseProvider)this._socketConnection).isConnectionEstablished();
   }

   @Override
   public void setConnectionCloseListener(ConnectionCloseListener listener) {
      if (this._socketConnection instanceof ConnectionCloseProvider) {
         ((ConnectionCloseProvider)this._socketConnection).setConnectionCloseListener(this);
      }

      this._closeListener = listener;
   }

   @Override
   public void connectionClosed(ConnectionCloseProvider connection) {
      if (this._closeListener != null) {
         this._closeListener.connectionClosed(this);
      }
   }

   @Override
   public void setSocketOption(byte option, int value) {
      this._socketConnection.setSocketOption(option, value);
   }

   @Override
   public int getSocketOption(byte option) {
      return this._socketConnection.getSocketOption(option);
   }

   @Override
   public void setSocketOptionEx(short option, long value) {
      if (!(this._socketConnection instanceof SocketConnectionEnhanced)) {
         throw new IllegalArgumentException();
      }

      ((SocketConnectionEnhanced)this._socketConnection).setSocketOptionEx(option, value);
   }

   @Override
   public long getSocketOptionEx(short option) {
      if (!(this._socketConnection instanceof SocketConnectionEnhanced)) {
         throw new IllegalArgumentException();
      } else {
         return ((SocketConnectionEnhanced)this._socketConnection).getSocketOptionEx(option);
      }
   }

   @Override
   public String getLocalAddress() {
      return this._socketConnection.getLocalAddress();
   }

   @Override
   public int getLocalPort() {
      return this._socketConnection.getLocalPort();
   }

   @Override
   public String getAddress() {
      return this._socketConnection.getAddress();
   }

   private void addProxyAuthHeaders() {
      String response = null;
      if (this._authScheme != null) {
         response = this._authScheme.getAuthResponse();
      }

      if (super._requestHeaders.getPropertyValue("Proxy-Authorization") == null) {
         if (response != null) {
            super._requestHeaders.setProperty("Proxy-Authorization", response);
         }
      }
   }

   private void deriveNewAuth() {
      this._authScheme = null;
      WPTCPServiceRecord record = WPTCPServiceRecord.getRecord(this._serviceRecord);
      if (record != null) {
         AuthScheme newScheme = null;
         if (!record.getPropertyAsBoolean(21)) {
            Vector challenges = super._responseHeaders.getPropertyValues("Proxy-Authenticate");
            if (challenges == null) {
               return;
            }

            int size = challenges.size();

            for (int i = 0; i < size; i++) {
               newScheme = AuthScheme.getAuthScheme((String)challenges.elementAt(i));
               if (newScheme != null) {
                  break;
               }
            }
         } else {
            newScheme = AuthScheme.getAuthScheme("basic");
         }

         if (newScheme != null) {
            label412:
            try {
               String requestURI = this._requestLine.getRequestURI();
               if (!requestURI.startsWith("http://")) {
                  requestURI = "http://" + requestURI;
               }

               URL uri = new URL(requestURI);
               newScheme.setParameter("rimhost", uri.getHost());
               newScheme.setParameter("method", this._requestLine.getMethod());
               if (this._connectMethod) {
                  newScheme.setParameter("uri", uri.getHost());
               } else {
                  String query = uri.getQuery();
                  String path = uri.getPath();
                  if (path == null) {
                     path = "/";
                  }

                  newScheme.setParameter("uri", path + (query == null ? "" : '?' + query));
               }
            } finally {
               break label412;
            }

            String apn = this._serviceRecord.getAPN();
            String username = null;
            String password = null;
            int usernameType = record.getPropertyAsInt(9);
            switch (usernameType) {
               case 0:
               case 2:
               case 3:
               case 22:
                  break;
               case 1:
               default:
                  username = record.getPropertyAsString(11);
                  break;
               case 4:
               case 13:
                  username = ClientID.getClientId(1, apn, usernameType == 4);
                  break;
               case 5:
               case 14:
                  username = ClientID.getClientId(9, apn, usernameType == 12);
                  break;
               case 6:
               case 15:
                  username = ClientID.getClientId(3, apn, usernameType == 6);
                  break;
               case 7:
               case 16:
                  username = ClientID.getClientId(4, apn, usernameType == 7);
                  break;
               case 8:
               case 17:
                  username = ClientID.getClientId(5, apn, usernameType == 8);
                  break;
               case 9:
               case 18:
                  username = ClientID.getClientId(6, apn, usernameType == 9);
                  break;
               case 10:
               case 19:
                  username = ClientID.getClientId(7, apn, usernameType == 10);
                  break;
               case 11:
               case 20:
                  username = ClientID.getClientId(8, apn, usernameType == 11);
                  break;
               case 12:
               case 21:
                  username = ClientID.getClientId(3, apn, usernameType == 6);
                  break;
               case 23:
               case 24:
                  username = ClientID.getClientId(11, apn, usernameType == 23);
            }

            int pwdType = record.getPropertyAsInt(10);
            switch (pwdType) {
               case 0:
               case 2:
                  break;
               case 1:
               default:
                  password = record.getPropertyAsString(12);
                  break;
               case 3:
                  if (RadioInfo.getNetworkType() == 4) {
                     MD5Digest md5 = new MD5Digest();
                     md5.update(StringUtilities.toUpperCase(Integer.toHexString(CDMAInfo.getESN()), 1701707776).getBytes());
                     md5.update(CDMAInfo.getAKey().getBytes());
                     StringBuffer buffer = new StringBuffer();
                     byte[] digest = md5.getDigest();

                     for (int i = 0; i < digest.length; i++) {
                        NumberUtilities.appendNumber(buffer, digest[i], 16, 2);
                     }

                     password = StringUtilities.toUpperCase(buffer.toString(), 1701707776);
                  }
                  break;
               case 4:
               case 13:
                  password = ClientID.getClientId(1, apn, pwdType == 4);
                  break;
               case 5:
               case 14:
                  password = ClientID.getClientId(9, apn, pwdType == 12);
                  break;
               case 6:
               case 15:
                  password = ClientID.getClientId(3, apn, pwdType == 6);
                  break;
               case 7:
               case 16:
                  password = ClientID.getClientId(4, apn, pwdType == 7);
                  break;
               case 8:
               case 17:
                  password = ClientID.getClientId(5, apn, pwdType == 8);
                  break;
               case 9:
               case 18:
                  password = ClientID.getClientId(6, apn, pwdType == 9);
                  break;
               case 10:
               case 19:
                  password = ClientID.getClientId(7, apn, pwdType == 10);
                  break;
               case 11:
               case 20:
                  password = ClientID.getClientId(8, apn, pwdType == 11);
                  break;
               case 12:
               case 21:
                  password = ClientID.getClientId(3, apn, pwdType == 6);
                  break;
               case 22:
                  String key = record.getPropertyAsString(12);
                  if (key != null) {
                     label398:
                     try {
                        HMAC passwordMac = new HMAC(new HMACKey(key.getBytes()), new MD5Digest());
                        passwordMac.update(Phone.getInstance().getNumber(0).getBytes());
                        passwordMac.update(StringUtilities.toLowerCase(Integer.toHexString(CDMAInfo.getESN()), 1701707776).getBytes());
                        StringBuffer buffer = new StringBuffer();
                        byte[] digest = passwordMac.getMAC();

                        for (int i = 0; i < 6; i++) {
                           NumberUtilities.appendNumber(buffer, digest[i], 16, 2);
                        }

                        password = StringUtilities.toLowerCase(buffer.toString(), 1701707776);
                     } finally {
                        break label398;
                     }
                  }
                  break;
               case 23:
               case 24:
                  password = ClientID.getClientId(11, apn, pwdType == 23);
            }

            if (newScheme != null && username != null && password != null) {
               this._authScheme = newScheme;
               newScheme.setParameter("username", username);
               newScheme.setParameter("password", password);
            }
         }
      }
   }
}
