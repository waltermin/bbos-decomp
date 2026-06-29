package net.rim.device.api.browser.util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import javax.microedition.io.HttpConnection;
import net.rim.device.api.io.http.HttpDateParser;
import net.rim.device.api.io.http.HttpHeaders;
import net.rim.device.api.io.http.HttpProtocolConstants;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.api.utility.general.URI;

public final class StaticHttpConnection implements HttpConnection, HttpProtocolConstants {
   private InputStream _inputStream;
   private boolean _closed;
   private String _url;
   private URI _decodedUrl;
   private HttpHeaders _responseHeaders;

   @Override
   public final void close() {
      if (this._inputStream != null) {
         this._inputStream.close();
         this._inputStream = null;
      }

      this._closed = true;
   }

   @Override
   public final long getExpiration() {
      String value = this.getHeaderField("Expires");
      return value != null ? HttpDateParser.parse(value) : 0;
   }

   @Override
   public final String getFile() {
      return this._decodedUrl != null ? this._decodedUrl.getFileName() : null;
   }

   @Override
   public final String getHeaderField(int n) {
      return this._responseHeaders.getPropertyValue(n);
   }

   @Override
   public final String getHeaderField(String name) {
      return this._responseHeaders.getPropertyValue(name);
   }

   @Override
   public final long getHeaderFieldDate(String name, long def) {
      String value = this._responseHeaders.getPropertyValue(name);

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
   public final int getHeaderFieldInt(String name, int def) {
      String value = this._responseHeaders.getPropertyValue(name);

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
   public final String getHeaderFieldKey(int n) {
      return this._responseHeaders.getPropertyKey(n);
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
      return value != null ? HttpDateParser.parse(value) : 0;
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
      return null;
   }

   @Override
   public final int getResponseCode() {
      return 200;
   }

   @Override
   public final String getResponseMessage() {
      return "OK";
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
      } else {
         return this._inputStream;
      }
   }

   @Override
   public final DataInputStream openDataInputStream() {
      InputStream in = this.openInputStream();
      return (DataInputStream)(!(in instanceof Object) ? new Object(in) : in);
   }

   @Override
   public final long getDate() {
      String value = this.getHeaderField("Date");
      return value != null ? HttpDateParser.parse(value) : 0;
   }

   @Override
   public final OutputStream openOutputStream() {
      return null;
   }

   @Override
   public final DataOutputStream openDataOutputStream() {
      return null;
   }

   public StaticHttpConnection(String url, byte[] data, HttpHeaders responseHeaders) {
      this._inputStream = (InputStream)(new Object(data));
      this._responseHeaders = responseHeaders;
      this._url = url;

      try {
         this._decodedUrl = (URI)(new Object(this._url));
      } finally {
         return;
      }
   }
}
