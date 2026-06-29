package net.rim.device.cldc.io.https;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Hashtable;
import javax.microedition.io.Connector;
import javax.microedition.io.HttpsConnection;
import javax.microedition.io.SecurityInfo;
import javax.microedition.io.SocketConnection;
import javax.microedition.pki.Certificate;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.cldc.io.ippp.SocketBaseIOException;
import net.rim.device.cldc.io.proxyhttp.ClientProtocol;
import net.rim.device.cldc.io.ssl.ProxySecureConnection;
import net.rim.device.cldc.io.ssl.ProxySecurityInfo;
import net.rim.device.cldc.io.utility.URL;
import net.rim.device.cldc.io.utility.URLParameters;

public final class ProxyHttpsConnection implements HttpsConnection {
   private SocketConnection _subConnection;
   private String _urlToOpen;
   private URL _url;
   private int _mode;
   private boolean _timeouts;
   private boolean _redirectHttps;
   private InputStream _input;
   private OutputStream _output;
   private DataBuffer _buffer;
   private boolean _isClosed;
   private String _requestMethod;
   private Hashtable _hashtable;
   private ClientProtocol _clientProtocol;
   private String _uid;
   private static String CONNECTION_TIMEOUT = "ConnectionTimeout";
   private static String CERTIFICATE_HEADER = "x-rim-ssl-certificate";
   private static String PROTOCOL_HEADER = "x-rim-ssl-protocol";
   private static String CIPHERSUITE_HEADER = "x-rim-ssl-ciphersuite";
   private static String TLS = "TLS";
   private static String SSL = "SSL";
   private static String VERSION_THREE_ONE = "3.1";
   private static String VERSION_THREE = "3.0";

   @Override
   public final void close() {
      if (!this._isClosed) {
         this._clientProtocol.close();
         this._isClosed = true;
      }
   }

   public final String getGroupUID() {
      return this._clientProtocol.getGroupUID();
   }

   public final void handleException(SocketBaseIOException e) {
      ProxySecureConnection.handleException(e, this._urlToOpen.toString());
   }

   @Override
   public final DataOutputStream openDataOutputStream() {
      return (DataOutputStream)(new Object(this.openOutputStream()));
   }

   @Override
   public final InputStream openInputStream() {
      if (this._isClosed) {
         throw new Object();
      }

      InputStream inputStream = null;

      try {
         inputStream = this._clientProtocol.openInputStream();
      } catch (SocketBaseIOException e) {
         this.handleException(e);
         this.replay();
         inputStream = this._input;
      }

      return new ProxyHttpsConnection$ProxyInputStream(this, inputStream);
   }

   @Override
   public final DataInputStream openDataInputStream() {
      return (DataInputStream)(new Object(this.openInputStream()));
   }

   @Override
   public final long getDate() {
      try {
         return this._clientProtocol.getDate();
      } catch (SocketBaseIOException e) {
         this.handleException(e);
         this.replay();
         return this._clientProtocol.getDate();
      }
   }

   @Override
   public final long getExpiration() {
      try {
         return this._clientProtocol.getExpiration();
      } catch (SocketBaseIOException e) {
         this.handleException(e);
         this.replay();
         return this._clientProtocol.getExpiration();
      }
   }

   @Override
   public final String getFile() {
      return this._clientProtocol.getFile();
   }

   @Override
   public final String getHeaderField(int n) {
      try {
         return this._clientProtocol.getHeaderField(n);
      } catch (SocketBaseIOException e) {
         this.handleException(e);
         this.replay();
         return this._clientProtocol.getHeaderField(n);
      }
   }

   @Override
   public final String getHeaderField(String name) {
      try {
         return this._clientProtocol.getHeaderField(name);
      } catch (SocketBaseIOException e) {
         this.handleException(e);
         this.replay();
         return this._clientProtocol.getHeaderField(name);
      }
   }

   @Override
   public final long getHeaderFieldDate(String name, long def) {
      try {
         return this._clientProtocol.getHeaderFieldDate(name, def);
      } catch (SocketBaseIOException e) {
         this.handleException(e);
         this.replay();
         return this._clientProtocol.getHeaderFieldDate(name, def);
      }
   }

   @Override
   public final int getHeaderFieldInt(String name, int def) {
      try {
         return this._clientProtocol.getHeaderFieldInt(name, def);
      } catch (SocketBaseIOException e) {
         this.handleException(e);
         this.replay();
         return this._clientProtocol.getHeaderFieldInt(name, def);
      }
   }

   @Override
   public final String getHeaderFieldKey(int n) {
      try {
         return this._clientProtocol.getHeaderFieldKey(n);
      } catch (SocketBaseIOException e) {
         this.handleException(e);
         this.replay();
         return this._clientProtocol.getHeaderFieldKey(n);
      }
   }

   @Override
   public final String getHost() {
      return this._clientProtocol.getHost();
   }

   @Override
   public final long getLastModified() {
      try {
         return this._clientProtocol.getLastModified();
      } catch (SocketBaseIOException e) {
         this.handleException(e);
         this.replay();
         return this._clientProtocol.getLastModified();
      }
   }

   @Override
   public final int getPort() {
      return this._clientProtocol.getPort();
   }

   @Override
   public final String getProtocol() {
      return this._clientProtocol.getProtocol();
   }

   @Override
   public final String getQuery() {
      return this._clientProtocol.getQuery();
   }

   @Override
   public final String getRef() {
      return this._clientProtocol.getRef();
   }

   @Override
   public final String getRequestMethod() {
      return this._clientProtocol.getRequestMethod();
   }

   @Override
   public final String getRequestProperty(String key) {
      return this._clientProtocol.getRequestProperty(key);
   }

   @Override
   public final int getResponseCode() {
      try {
         return this._clientProtocol.getResponseCode();
      } catch (SocketBaseIOException e) {
         this.handleException(e);
         this.replay();
         return this._clientProtocol.getResponseCode();
      }
   }

   @Override
   public final String getResponseMessage() {
      try {
         return this._clientProtocol.getResponseMessage();
      } catch (SocketBaseIOException e) {
         this.handleException(e);
         this.replay();
         return this._clientProtocol.getResponseMessage();
      }
   }

   @Override
   public final String getURL() {
      return this._clientProtocol.getURL();
   }

   @Override
   public final OutputStream openOutputStream() {
      if (this._isClosed) {
         throw new Object();
      }

      OutputStream outputStream = null;

      try {
         outputStream = this._clientProtocol.openOutputStream();
      } catch (SocketBaseIOException e) {
         this.handleException(e);
         this.replay();
         outputStream = this._output;
      }

      return new ProxyHttpsConnection$ProxyOutputStream(this, outputStream);
   }

   @Override
   public final void setRequestMethod(String method) {
      try {
         this._clientProtocol.setRequestMethod(method);
         this._requestMethod = method;
      } catch (SocketBaseIOException e) {
         this.handleException(e);
         this.replay();
         this._clientProtocol.setRequestMethod(method);
      }
   }

   @Override
   public final void setRequestProperty(String key, String value) {
      try {
         this._clientProtocol.setRequestProperty(key, value);
         if (this._hashtable == null) {
            this._hashtable = (Hashtable)(new Object());
         }

         this._hashtable.put(key, value);
      } catch (SocketBaseIOException e) {
         this.handleException(e);
         this.replay();
         this._clientProtocol.setRequestProperty(key, value);
      }
   }

   @Override
   public final String getEncoding() {
      return this._clientProtocol.getEncoding();
   }

   @Override
   public final long getLength() {
      return this._clientProtocol.getLength();
   }

   @Override
   public final String getType() {
      return this._clientProtocol.getType();
   }

   @Override
   public final SecurityInfo getSecurityInfo() {
      String unparsedCertificate = this.getHeaderField(CERTIFICATE_HEADER);
      String protocol = this.getHeaderField(PROTOCOL_HEADER);
      String cipherSuite = this.getHeaderField(CIPHERSUITE_HEADER);
      if (unparsedCertificate != null && protocol != null && cipherSuite != null) {
         Certificate[] certChain = ProxySecureConnection.parseCertChain(unparsedCertificate);
         if (certChain != null && certChain.length != 0) {
            String protocolVersion = null;
            if (protocol.indexOf(SSL) != -1) {
               protocol = SSL;
               protocolVersion = VERSION_THREE;
            } else if (protocol.indexOf(TLS) != -1) {
               protocol = TLS;
               protocolVersion = VERSION_THREE_ONE;
            }

            return new ProxySecurityInfo(cipherSuite, protocol, protocolVersion, certChain[0]);
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   private final void replay() {
      if (this._subConnection != null) {
         this._subConnection.close();
      }

      if (this._input != null) {
         this._input.close();
      }

      if (this._output != null) {
         this._output.close();
      }

      byte[] request = null;
      if (this._buffer != null) {
         request = this._buffer.getArray();
      }

      this._subConnection = (SocketConnection)Connector.open(this._urlToOpen.toString(), this._mode, this._timeouts);
      this._clientProtocol = new ClientProtocol(this._url, this._subConnection, this._redirectHttps, true, false, this._uid);
      if (this._requestMethod != null) {
         this._clientProtocol.setRequestMethod(this._requestMethod);
      }

      if (this._hashtable != null) {
         Enumeration enumeration = this._hashtable.keys();

         while (enumeration.hasMoreElements()) {
            String key = (String)enumeration.nextElement();
            this._clientProtocol.setRequestProperty(key, (String)this._hashtable.get(key));
         }
      }

      this._output = this._clientProtocol.openOutputStream();
      if (request != null) {
         this._output.write(request);
         this._output.flush();
      }

      this._buffer = null;
      this._input = this._clientProtocol.openInputStream();
   }

   public ProxyHttpsConnection(String urlToOpen, URL url, int mode, boolean timeouts, boolean redirectHttps, boolean trustAll, boolean compress, String uid) {
      this._subConnection = (SocketConnection)Connector.open(urlToOpen, mode, timeouts);
      this._url = url;
      this._uid = uid;
      URLParameters urlParameters = this._url.getRIMParameters();
      if (urlParameters != null && urlParameters.containParameter(CONNECTION_TIMEOUT)) {
         urlParameters.remove(CONNECTION_TIMEOUT);
      }

      this._clientProtocol = new ClientProtocol(this._url, this._subConnection, redirectHttps, trustAll, compress, this._uid);
      this._urlToOpen = urlToOpen;
      this._mode = mode;
      this._timeouts = timeouts;
      this._redirectHttps = redirectHttps;
   }
}
