package net.rim.device.apps.internal.mms.ui;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import javax.microedition.io.HttpConnection;
import net.rim.device.api.io.IOCancelledException;
import net.rim.device.api.io.http.HttpDateParser;
import net.rim.device.api.io.http.HttpHeaders;
import net.rim.device.api.io.http.HttpProtocolConstants;
import net.rim.device.apps.internal.mms.api.MMSAttachment;
import net.rim.device.internal.io.file.FileUtilities;

final class AttachmentHttpConnection implements HttpConnection, HttpProtocolConstants {
   private MMSAttachment _attachment;
   private HttpHeaders _requestHeaders;
   private HttpHeaders _responseHeaders;
   private InputStream _inputStream;
   private boolean _closed;

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
      return 0;
   }

   @Override
   public final String getFile() {
      return this._attachment.getName();
   }

   @Override
   public final String getHeaderField(int n) {
      return this._responseHeaders != null ? this._responseHeaders.getPropertyValue(n) : null;
   }

   @Override
   public final String getHeaderField(String name) {
      return this._responseHeaders != null ? this._responseHeaders.getPropertyValue(name) : null;
   }

   @Override
   public final long getHeaderFieldDate(String name, long def) {
      if (this._responseHeaders != null) {
         String value = this._responseHeaders.getPropertyValue(name);

         try {
            if (value != null) {
               return HttpDateParser.parse(value);
            }
         } finally {
            return def;
         }
      }

      return def;
   }

   @Override
   public final int getHeaderFieldInt(String name, int def) {
      if (this._responseHeaders != null) {
         String value = this._responseHeaders.getPropertyValue(name);

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
      return this._responseHeaders != null ? this._responseHeaders.getPropertyKey(n) : null;
   }

   @Override
   public final String getHost() {
      return null;
   }

   @Override
   public final long getLastModified() {
      String value = this.getHeaderField("Last-Modified");
      return value != null ? HttpDateParser.parse(value) : 0;
   }

   @Override
   public final int getPort() {
      return 80;
   }

   @Override
   public final String getProtocol() {
      return null;
   }

   @Override
   public final String getQuery() {
      return null;
   }

   @Override
   public final String getRef() {
      return null;
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
      return 200;
   }

   @Override
   public final String getResponseMessage() {
      return null;
   }

   @Override
   public final String getURL() {
      String name = this._attachment.getName();
      return FileUtilities.makeFileURL(name);
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
   public final InputStream openInputStream() throws IOCancelledException {
      if (this._closed) {
         throw new IOCancelledException();
      }

      if (this._inputStream == null) {
         this._inputStream = new ByteArrayInputStream(this._attachment.getData());
      }

      return this._inputStream;
   }

   @Override
   public final DataInputStream openDataInputStream() {
      InputStream in = this.openInputStream();
      return !(in instanceof DataInputStream) ? new DataInputStream(in) : (DataInputStream)in;
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

   public AttachmentHttpConnection(MMSAttachment attachment, HttpHeaders requestHeaders, HttpHeaders responseHeaders) {
      this._attachment = attachment;
      this._requestHeaders = requestHeaders;
      this._responseHeaders = responseHeaders;
   }
}
