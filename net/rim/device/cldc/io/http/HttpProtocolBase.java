package net.rim.device.cldc.io.http;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import javax.microedition.io.HttpConnection;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.io.http.HttpDateParser;
import net.rim.device.api.io.http.HttpHeaders;
import net.rim.device.api.io.http.HttpProtocolConstants;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.cldc.io.utility.URL;
import net.rim.device.internal.browser.util.PipeInput;
import net.rim.device.internal.system.MIDletSecurity;

public class HttpProtocolBase implements HttpConnection, HttpProtocolConstants {
   protected int _state;
   protected URL _url;
   protected String _requestMethod = "GET";
   protected int _responseCode;
   protected String _responseMessage;
   protected HttpHeaders _requestHeaders;
   protected HttpHeaders _responseHeaders;
   protected boolean _inputStreamOpened;
   protected boolean _outputStreamOpened;
   private ResourceBundle _resources;
   private boolean _untrustedMIDlet = MIDletSecurity.checkUntrustedMIDlet();
   protected static final int STATE_SETUP;
   protected static final int STATE_CONNECTED;
   protected static final int STATE_CLOSED;
   protected static final int STATE_REQUEST_WRITTEN;
   protected static final int STATE_ABOUT_TO_WRITE_REQUEST;
   private static final String UNTRUSTED_USER_AGENT_TOKEN;

   @Override
   public void close() {
      this.transitionToState(2);
   }

   protected OutputStream getOutputStream() {
      throw null;
   }

   protected void writeRequest() {
      throw null;
   }

   protected void readResponse() {
      throw null;
   }

   protected synchronized void transitionToState(int newState) {
      if (newState == 1) {
         if (this._state == 2) {
            throw new Object("Stream closed");
         }

         if (this._state == 0) {
            this._state = 4;
            this.writeRequest();
            this._state = 3;
            this.readResponse();
            this._state = 1;
         }
      } else if (newState == 2) {
         this._state = 2;
      } else {
         throw new Object();
      }
   }

   protected final String mapResponseCodeToResponseMessage(int responseCode) {
      String message = null;

      try {
         return this._resources.getString(responseCode);
      } finally {
         return "";
      }
   }

   public final HttpHeaders getResponseHeaders() {
      this.transitionToState(1);
      return this._responseHeaders;
   }

   protected final HttpHeaders getRequestHeaders() {
      return this._requestHeaders;
   }

   protected InputStream getInputStream() {
      throw null;
   }

   public final String getURLWithoutRIMParams() {
      return this._url.toStringWithoutRIMParams();
   }

   public final boolean receivedHttp100() {
      return this._responseCode == 100;
   }

   @Override
   public final String getFile() {
      return this._url != null ? this._url.getPath() : null;
   }

   @Override
   public final String getHost() {
      if (this._url != null) {
         String host = this._url.getHost();
         return host != null && host.length() == 0 ? null : host;
      } else {
         return null;
      }
   }

   @Override
   public final long getLastModified() {
      String value = this.getHeaderField("Last-Modified");
      return value != null ? HttpDateParser.parse(value) : 0;
   }

   @Override
   public final int getPort() {
      return this._url != null ? this._url.getPort() : 0;
   }

   @Override
   public final String getProtocol() {
      return this._url != null ? this._url.getScheme() : null;
   }

   @Override
   public final String getQuery() {
      return this._url != null ? this._url.getQuery() : null;
   }

   @Override
   public final String getRef() {
      return this._url != null ? this._url.getFragment() : null;
   }

   @Override
   public final String getRequestMethod() {
      return this._requestMethod;
   }

   @Override
   public final String getRequestProperty(String key) {
      return ((HttpHeaders)this.getRequestHeaders()).getPropertyValue(key);
   }

   @Override
   public final int getResponseCode() {
      this.transitionToState(1);
      return this._responseCode;
   }

   @Override
   public final long getExpiration() {
      String value = this.getHeaderField("Expires");
      return value != null ? HttpDateParser.parse(value) : 0;
   }

   @Override
   public final String getResponseMessage() {
      this.transitionToState(1);
      return this._responseMessage;
   }

   @Override
   public final String getURL() {
      return this._url.toString();
   }

   @Override
   public final long getDate() {
      String value = this.getHeaderField("Date");
      return value != null ? HttpDateParser.parse(value) : 0;
   }

   @Override
   public void setRequestMethod(String method) {
      if (this._state != 0) {
         throw new Object();
      }

      if (!this._outputStreamOpened) {
         this._requestMethod = method;
      }
   }

   @Override
   public final void setRequestProperty(String key, String value) {
      if (this._state != 0) {
         throw new Object();
      }

      if (key == null) {
         throw new Object();
      }

      if (value == null) {
         throw new Object();
      }

      if (!this._outputStreamOpened) {
         if (this._untrustedMIDlet && StringUtilities.strEqualIgnoreCase("User-Agent", key, 1701707776)) {
            if (value.length() > 0 && value.charAt(value.length() - 1) != ' ') {
               value = ((StringBuffer)(new Object())).append(value).append(' ').append("UNTRUSTED/1.0").toString();
            } else {
               value = ((StringBuffer)(new Object())).append(value).append("UNTRUSTED/1.0").toString();
            }
         }

         ((HttpHeaders)this.getRequestHeaders()).setProperty(key, value);
      }
   }

   @Override
   public final String getType() {
      try {
         return this.getHeaderField("Content-Type");
      } finally {
         ;
      }
   }

   @Override
   public final String getEncoding() {
      try {
         return this.getHeaderField("Content-Encoding");
      } finally {
         ;
      }
   }

   @Override
   public final long getLength() {
      try {
         return this.getHeaderFieldInt("Content-Length", -1);
      } finally {
         return -1;
      }
   }

   @Override
   public final InputStream openInputStream() {
      this.transitionToState(1);
      if (!this._inputStreamOpened) {
         InputStream in = this.getInputStream();
         if (in == null) {
            throw new Object();
         }

         this._inputStreamOpened = true;
         return in;
      } else {
         throw new Object("Stream already open");
      }
   }

   @Override
   public final DataInputStream openDataInputStream() {
      InputStream in = this.openInputStream();
      if (!(in instanceof Object)) {
         return (DataInputStream)(in instanceof Object ? new Object((PipeInput)in) : new Object(in));
      } else {
         return (DataInputStream)in;
      }
   }

   @Override
   public final OutputStream openOutputStream() {
      if (this._state == 2) {
         throw new Object("Stream closed");
      } else if (!this._outputStreamOpened) {
         this._outputStreamOpened = true;
         return new HttpOutputStream(this, this.getOutputStream());
      } else {
         throw new Object("Stream already open");
      }
   }

   @Override
   public final DataOutputStream openDataOutputStream() {
      return (DataOutputStream)(new Object(this.openOutputStream()));
   }

   @Override
   public final String getHeaderFieldKey(int n) {
      return ((HttpHeaders)this.getResponseHeaders()).getPropertyKey(n);
   }

   @Override
   public final int getHeaderFieldInt(String name, int def) {
      String value = ((HttpHeaders)this.getResponseHeaders()).getPropertyValue(name);

      try {
         if (value != null) {
            return Integer.parseInt(value);
         }
      } finally {
         return def;
      }

      return def;
   }

   @Override
   public final long getHeaderFieldDate(String name, long def) {
      String value = ((HttpHeaders)this.getResponseHeaders()).getPropertyValue(name);

      try {
         if (value != null) {
            return HttpDateParser.parse(value);
         }
      } finally {
         return def;
      }

      return def;
   }

   @Override
   public final String getHeaderField(String name) {
      return ((HttpHeaders)this.getResponseHeaders()).getPropertyValue(name);
   }

   @Override
   public final String getHeaderField(int n) {
      return ((HttpHeaders)this.getResponseHeaders()).getPropertyValue(n);
   }

   protected HttpProtocolBase(URL url) {
      this();
      this._url = url;
   }

   private HttpProtocolBase() {
      this._resources = ResourceBundle.getBundle(-6246750274064102835L, "net.rim.device.internal.resource.HTTP");
      this._state = 0;
      this._requestHeaders = new HttpHeaders();
      this._responseHeaders = new HttpHeaders();
      if (this._untrustedMIDlet) {
         this._requestHeaders.setProperty("User-Agent", "UNTRUSTED/1.0");
      }
   }
}
