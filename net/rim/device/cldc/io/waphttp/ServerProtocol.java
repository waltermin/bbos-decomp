package net.rim.device.cldc.io.waphttp;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import net.rim.device.api.io.http.HttpHeaders;
import net.rim.device.api.io.http.HttpProtocolConstants;
import net.rim.device.api.io.http.HttpServerConnection;
import net.rim.device.api.io.http.PushInputStream;

public final class ServerProtocol implements HttpServerConnection, HttpProtocolConstants {
   private HttpHeaders _headers;
   private boolean _closed;
   private PushInputStream _in;

   @Override
   public final void close() {
      if (!this._closed) {
         this._closed = true;
      }
   }

   public final HttpHeaders getHeaders() {
      if (this._closed) {
         throw new Object();
      } else {
         return this._headers;
      }
   }

   @Override
   public final long getLength() {
      try {
         String value = this.getHeaders().getPropertyValue("Content-Length");
         return value == null ? -1 : Integer.parseInt(value);
      } finally {
         return -1;
      }
   }

   @Override
   public final String getRequestMethod() {
      return "POST";
   }

   @Override
   public final String getVersion() {
      return "HTTP/1.0";
   }

   @Override
   public final String getRequestURI() {
      return null;
   }

   @Override
   public final String getHeaderField(int n) {
      return this.getHeaders().getPropertyValue(n);
   }

   @Override
   public final String getHeaderField(String name) {
      return this.getHeaders().getPropertyValue(name);
   }

   @Override
   public final int getHeaderFieldInt(String name, int def) {
      if (name == null) {
         return def;
      }

      String value = this.getHeaders().getPropertyValue(name);
      return value != null ? Integer.parseInt(value) : def;
   }

   @Override
   public final String getHeaderFieldKey(int n) {
      return this.getHeaders().getPropertyKey(n);
   }

   @Override
   public final String getEncoding() {
      try {
         return this.getHeaders().getPropertyValue("Content-Encoding");
      } finally {
         ;
      }
   }

   @Override
   public final String getResponseProperty(String key) {
      return null;
   }

   @Override
   public final void setResponseProperty(String key, String value) {
   }

   @Override
   public final void setResponseCode(int code) {
   }

   @Override
   public final String getType() {
      try {
         return this.getHeaders().getPropertyValue("Content-Type");
      } finally {
         ;
      }
   }

   @Override
   public final OutputStream openOutputStream() {
      return (OutputStream)(new Object());
   }

   @Override
   public final DataOutputStream openDataOutputStream() {
      return (DataOutputStream)(new Object(this.openOutputStream()));
   }

   @Override
   public final InputStream openInputStream() {
      return this._in;
   }

   @Override
   public final DataInputStream openDataInputStream() {
      return this._in;
   }

   public ServerProtocol(HttpHeaders headers, PushInputStream in) {
      this._headers = headers;
      this._in = in;
   }
}
