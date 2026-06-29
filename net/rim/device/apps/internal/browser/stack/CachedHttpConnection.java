package net.rim.device.apps.internal.browser.stack;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import javax.microedition.io.HttpConnection;
import net.rim.device.api.io.http.HttpHeaders;
import net.rim.device.api.io.http.HttpProtocolConstants;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.api.utility.general.URI;
import net.rim.device.apps.internal.browser.util.DateCache;
import net.rim.device.apps.internal.browser.util.RendererControl;

public final class CachedHttpConnection implements HttpConnection, HttpProtocolConstants {
   private CacheResult _cacheResult;
   private InputStream _inputStream;
   private HttpConnection _connection;
   private boolean _closed;
   private String _url;
   private URI _decodedUrl;
   private HttpHeaders _requestHeaders;

   @Override
   public final void close() {
      if (this._inputStream != null) {
         this._inputStream.close();
         this._inputStream = null;
      }

      this._closed = true;
      if (this._connection != null) {
         this._connection.close();
         this._connection = null;
      }
   }

   public final CacheResult getCacheResult() {
      return this._cacheResult;
   }

   public final void setURL(String url) {
      this._url = url;

      try {
         this._decodedUrl = (URI)(new Object(this._url));
      } finally {
         return;
      }
   }

   public final InputStream openMarkableInputStream() {
      InputStream in = this.openInputStream();
      if (in != null && !in.markSupported()) {
         this._inputStream = in = (InputStream)(new Object(in));
      }

      return in;
   }

   @Override
   public final String getFile() {
      return this._decodedUrl != null ? this._decodedUrl.getFileName() : null;
   }

   @Override
   public final String getHeaderField(int n) {
      HttpHeaders headers = this._cacheResult.getResponseHeaders();
      return headers != null ? headers.getPropertyValue(n) : null;
   }

   @Override
   public final String getHeaderField(String name) {
      HttpHeaders headers = this._cacheResult.getResponseHeaders();
      return headers != null ? headers.getPropertyValue(name) : null;
   }

   @Override
   public final long getHeaderFieldDate(String name, long def) {
      HttpHeaders headers = this._cacheResult.getResponseHeaders();
      if (headers != null) {
         String value = headers.getPropertyValue(name);

         try {
            if (value != null) {
               return DateCache.parse(value);
            }
         } finally {
            return def;
         }
      }

      return def;
   }

   @Override
   public final int getHeaderFieldInt(String name, int def) {
      HttpHeaders headers = this._cacheResult.getResponseHeaders();
      if (headers != null) {
         String value = headers.getPropertyValue(name);

         try {
            if (value != null) {
               return Integer.parseInt(value);
            }
         } finally {
            return def;
         }
      }

      return def;
   }

   @Override
   public final String getHeaderFieldKey(int n) {
      HttpHeaders headers = this._cacheResult.getResponseHeaders();
      return headers != null ? headers.getPropertyKey(n) : null;
   }

   @Override
   public final String getHost() {
      if (this._decodedUrl == null) {
         return null;
      } else {
         String authority = this._decodedUrl.getAuthority();
         if (authority != null) {
            int colon = authority.indexOf(58);
            return colon == -1 ? authority : authority.substring(0, colon);
         } else {
            return null;
         }
      }
   }

   @Override
   public final long getLastModified() {
      String value = this.getHeaderField("Last-Modified");
      return value != null ? DateCache.parse(value) : 0;
   }

   @Override
   public final int getPort() {
      if (this._decodedUrl == null) {
         return 80;
      }

      String authority = this._decodedUrl.getAuthority();
      if (authority != null) {
         int colon = authority.indexOf(58);
         if (colon != -1) {
            label38:
            try {
               return Integer.parseInt(authority.substring(colon + 1));
            } finally {
               break label38;
            }
         }
      }

      String scheme = this._decodedUrl.getScheme();
      return scheme != null && StringUtilities.regionMatches(scheme, true, 0, "https", 0, 5, 1701707776) ? 443 : 80;
   }

   @Override
   public final String getProtocol() {
      return this._decodedUrl != null ? this._decodedUrl.getScheme() : null;
   }

   @Override
   public final String getQuery() {
      return this._decodedUrl != null ? this._decodedUrl.getQuery() : null;
   }

   @Override
   public final String getRef() {
      return this._decodedUrl != null ? this._decodedUrl.getFragment() : null;
   }

   @Override
   public final String getRequestMethod() {
      return null;
   }

   @Override
   public final String getRequestProperty(String key) {
      return this._requestHeaders != null ? this._requestHeaders.getPropertyValue(key) : null;
   }

   @Override
   public final int getResponseCode() {
      return this._cacheResult.getStatus();
   }

   @Override
   public final String getResponseMessage() {
      return this._cacheResult.getResponseMessage();
   }

   @Override
   public final String getURL() {
      return this._url;
   }

   @Override
   public final void setRequestMethod(String method) {
   }

   @Override
   public final void setRequestProperty(String key, String value) {
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
      if (this._closed) {
         throw new Object();
      }

      if (this._inputStream == null && this._cacheResult != null) {
         this.setInputStream(this._cacheResult.getStream());
      }

      return this._inputStream;
   }

   @Override
   public final DataInputStream openDataInputStream() {
      InputStream in = this.openInputStream();
      return (DataInputStream)(!(in instanceof Object) ? new Object(in) : in);
   }

   @Override
   public final long getExpiration() {
      return this._cacheResult.getExpiration();
   }

   @Override
   public final long getDate() {
      String value = this.getHeaderField("Date");
      return value != null ? DateCache.parse(value) : 0;
   }

   @Override
   public final OutputStream openOutputStream() {
      return null;
   }

   @Override
   public final DataOutputStream openDataOutputStream() {
      return null;
   }

   public CachedHttpConnection(String url, CacheResult cacheResult, HttpHeaders requestHeaders) {
      this(url, null, null, cacheResult, requestHeaders);
   }

   public CachedHttpConnection(String url, HttpConnection connection, InputStream inputStream, CacheResult cacheResult, HttpHeaders requestHeaders) {
      this._cacheResult = cacheResult;
      this._connection = connection;
      this._url = url;
      this.setInputStream(inputStream);
      this._requestHeaders = requestHeaders;

      try {
         this._decodedUrl = (URI)(new Object(this._url));
      } finally {
         return;
      }
   }

   private final void setInputStream(InputStream in) {
      if (in != null && this._cacheResult != null) {
         HttpHeaders hdrs = this._cacheResult.getResponseHeaders();
         if (hdrs != null) {
            this._inputStream = RendererControl.getInputStreamFromContentEncoding(hdrs.getPropertyValue("Content-Encoding"), in);
            return;
         }
      }

      this._inputStream = in;
   }
}
