package net.rim.device.apps.internal.bis.api.io.http;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Hashtable;
import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.apps.internal.bis.utils.ArgUtils;

public final class HttpClient {
   private String _connectionUID;
   private long _connectTimeout;
   private HttpCookies _httpCookies;
   private HttpListener _listener;
   private int _readBlockPercent;
   private boolean _useWapGateway;
   private static final String SET_COOKIE = "Set-Cookie";
   private static final String XML_ACCEPT = "text/xml, text/*";
   private static final String XML_CONTENT_TYPE = "text/xml; charset=utf-8";
   private static final int MOP_UNAUTHORIZED_STATUS = 401;
   private static Hashtable _defaultRequestProperties = (Hashtable)(new Object());

   public HttpClient() {
   }

   public HttpClient(String connectionUID, long connectTimeout, boolean useWapGateway) {
      this(connectionUID, connectTimeout, null, 0, useWapGateway);
   }

   public HttpClient(String connectionUID, long connectTimeout, HttpListener listener, int readBlockPercent, boolean useWapGateway) {
      this._connectionUID = connectionUID;
      this._connectTimeout = connectTimeout;
      this._listener = listener;
      this._readBlockPercent = readBlockPercent;
      this._useWapGateway = useWapGateway;
      this._httpCookies = new HttpCookies();
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final HttpResponse doHttpExchangeInternal(
      String url, String requestMethod, Hashtable requestProperties, byte[] requestPayload, String[] responseProperties
   ) {
      HttpConnection conn = null;
      InputStream is = null;
      OutputStream os = null;
      int reasonForClosingConnection = 0;
      boolean var25 = false /* VF: Semaphore variable */;
      boolean var28 = false /* VF: Semaphore variable */;

      HttpResponse var42;
      try {
         try {
            var28 = true;
            var25 = true;
            conn = (HttpConnection)Connector.open(((StringBuffer)(new Object())).append(url).append(this.getConnectionParams()).toString());
            if (this._listener != null) {
               this._listener.connectionEstablished(true);
            }

            conn.setRequestMethod(requestMethod);
            Hashtable defaultProperties = getDefaultRequestProperties();
            if (defaultProperties != null) {
               Enumeration propertiesKeys = defaultProperties.keys();

               while (propertiesKeys.hasMoreElements()) {
                  String key = (String)propertiesKeys.nextElement();
                  String value = ArgUtils.getStringValue(defaultProperties, key);
                  conn.setRequestProperty(key, value);
               }
            }

            if (requestProperties != null) {
               Enumeration propertiesKeys = requestProperties.keys();

               while (propertiesKeys.hasMoreElements()) {
                  String key = (String)propertiesKeys.nextElement();
                  String value = ArgUtils.getStringValue(requestProperties, key);
                  conn.setRequestProperty(key, value);
               }
            }

            this.addCookiesToRequest(conn);
            if (requestPayload != null && requestPayload.length > 0) {
               os = conn.openOutputStream();
               os.write(requestPayload);
               os.flush();
            }

            int responseCode = conn.getResponseCode();
            if (this._listener != null) {
               this._listener.sentRequest();
            }

            String location = conn.getHeaderField("Location");
            is = conn.openInputStream();
            this.storeCookies(conn, responseCode);
            String contentType = conn.getType();
            int length = (int)conn.getLength();
            if (this._listener != null) {
               this._listener.readingResponse(length);
            }

            byte[] responseData = null;
            int readBlockSize = this._readBlockPercent > 0 ? length / (100 / this._readBlockPercent) : length;
            if (length > 0) {
               int actual = 0;
               int bytesread = 0;
               responseData = new byte[length];

               while (bytesread != length && actual != -1) {
                  if (length - bytesread < readBlockSize) {
                     actual = is.read(responseData, bytesread, length - bytesread);
                  } else {
                     actual = is.read(responseData, bytesread, readBlockSize);
                  }

                  bytesread += actual;
                  if (this._listener != null) {
                     this._listener.readResponseProgressUpdate(actual);
                  }
               }
            } else {
               ByteArrayOutputStream baos = (ByteArrayOutputStream)(new Object());

               int ch;
               for (int bytesread = 0; (ch = is.read()) != -1; bytesread++) {
                  baos.write(ch);
               }

               if (baos.size() > 0) {
                  responseData = baos.toByteArray();
               }

               baos.close();
            }

            if (this._listener != null) {
               this._listener.finishedReadingResponse();
            }

            Hashtable responsePropertiesMap = null;
            String retryAfterValue = conn.getHeaderField("Retry-After");
            if (retryAfterValue != null) {
               responsePropertiesMap = (Hashtable)(new Object());
               responsePropertiesMap.put("Retry-After", retryAfterValue);
            }

            if (responseProperties != null && responseProperties.length > 0) {
               if (responsePropertiesMap == null) {
                  responsePropertiesMap = (Hashtable)(new Object());
               }

               int numResponseProperties = responseProperties.length;

               for (int i = 0; i < numResponseProperties; i++) {
                  String propertyValue = conn.getHeaderField(responseProperties[i]);
                  if (propertyValue != null) {
                     responsePropertiesMap.put(responseProperties[i], propertyValue);
                  }
               }
            }

            var42 = new HttpResponse(responseCode, responseData, contentType, responsePropertiesMap, location);
            var25 = false;
            var28 = false;
         } finally {
            if (var28) {
               if (this._listener != null) {
                  this._listener.connectionEstablished(false);
               }

               throw new Object();
            }
         }
      } finally {
         if (var25) {
            if (is != null) {
               is.close();
            }

            if (os != null) {
               os.close();
            }

            if (conn != null) {
               conn.close();
               if (this._listener != null) {
                  this._listener.connectionClosed(reasonForClosingConnection);
               }
            } else if (this._listener != null) {
               this._listener.connectionEstablished(false);
            }
         }
      }

      if (is != null) {
         is.close();
      }

      if (os != null) {
         os.close();
      }

      if (conn != null) {
         conn.close();
         if (this._listener != null) {
            this._listener.connectionClosed(reasonForClosingConnection);
         }
      } else if (this._listener != null) {
         this._listener.connectionEstablished(false);
      }

      return var42;
   }

   protected final void storeCookies(HttpConnection conn, int responseCode) {
      if (responseCode != 401) {
         int i = 0;

         for (String fieldName = conn.getHeaderFieldKey(i); fieldName != null; fieldName = conn.getHeaderFieldKey(++i)) {
            if (fieldName.equalsIgnoreCase("Set-Cookie")) {
               String cookie = conn.getHeaderField(i);
               if (cookie != null && cookie.length() > 0) {
                  this._httpCookies.parseSetCookieHeader(cookie);
               }
            }
         }
      } else {
         this.clearCookie();
      }
   }

   protected final void addCookiesToRequest(HttpConnection conn) {
      String cookieHeaderValue = this._httpCookies.getCookieHeader();
      if (cookieHeaderValue.length() > 0) {
         conn.setRequestProperty("Cookie", cookieHeaderValue);
      }
   }

   public final HttpResponse doHttpExchange(
      String url, String requestMethod, Hashtable requestProperties, byte[] requestPayload, String[] responseProperties, boolean handleRedirects
   ) {
      HttpResponse response = this.doHttpExchangeInternal(url, requestMethod, requestProperties, requestPayload, responseProperties);
      if (handleRedirects) {
         for (int statusCode = response.getHttpResponseCode(); statusCode >= 300 && statusCode < 400; statusCode = response.getHttpResponseCode()) {
            String redirectURL = response.getResourceLocation();
            String retryAfter = response.getResponseProperty("Retry-After");
            if (retryAfter != null) {
               long retryAfterSeconds = Long.parseLong(retryAfter);

               label42:
               try {
                  Thread.sleep(retryAfterSeconds * 1000);
               } finally {
                  break label42;
               }
            }

            response = this.doHttpExchangeInternal(redirectURL, requestMethod, requestProperties, null, responseProperties);
         }
      }

      return response;
   }

   public final HttpResponse doXmlExchange(
      String url, String requestMethod, Hashtable requestProperties, byte[] requestPayload, String[] responseProperties, boolean handleRedirects
   ) {
      if (requestProperties == null) {
         requestProperties = (Hashtable)(new Object());
      }

      if (requestPayload != null) {
         requestProperties.put("Content-Type", "text/xml; charset=utf-8");
      }

      requestProperties.put("Accept", "text/xml, text/*");
      return this.doHttpExchange(url, requestMethod, requestProperties, requestPayload, responseProperties, handleRedirects);
   }

   private final String getConnectionParams() {
      String connectionParams = "";
      if (this._connectionUID != null) {
         String connectUIDParam = ((StringBuffer)(new Object(";connectionuid="))).append(this._connectionUID).toString();
         if (this._useWapGateway) {
            connectionParams = ((StringBuffer)(new Object())).append(connectionParams).append(";WAPGatewayIP=").append(connectUIDParam).toString();
         } else {
            connectionParams = ((StringBuffer)(new Object())).append(connectionParams).append(connectUIDParam).toString();
         }
      }

      if (this._connectTimeout > 0) {
         connectionParams = ((StringBuffer)(new Object())).append(connectionParams).append(";connectiontimeout=").append(this._connectTimeout).toString();
      }

      return ((StringBuffer)(new Object())).append(connectionParams).append(";deviceside=false").toString();
   }

   public final void clearCookie() {
      this._httpCookies.clear();
   }

   private static final Hashtable getDefaultRequestProperties() {
      return _defaultRequestProperties;
   }

   static {
      String appVersion = ApplicationDescriptor.currentApplicationDescriptor().getVersion();
      _defaultRequestProperties.put("Cache-Control", "no-cache");
      _defaultRequestProperties.put("Pragma", "no-cache");
      _defaultRequestProperties.put("Connection", "Keep-Alive");
      _defaultRequestProperties.put("User-Agent", ((StringBuffer)(new Object("BISClient/"))).append(appVersion).toString());
   }
}
