package net.rim.wica.runtime.comm.internal;

import javax.microedition.io.HttpConnection;
import net.rim.wica.runtime.comm.Response;

public final class ResponseImpl extends AbstractRequestResponse implements Response {
   private String _responseUri;
   private int _statusCode;
   private String _contentType;

   public final void copyHeadersFrom(HttpConnection connection) {
      if (connection != null) {
         int i = 0;

         while (true) {
            String key = connection.getHeaderFieldKey(i);
            if (key == null) {
               return;
            }

            String value = connection.getHeaderField(i);
            this.setHeader(key, value);
            i++;
         }
      }
   }

   final void setContentType(String contentType) {
      this._contentType = contentType;
   }

   public final boolean retry() {
      return this._statusCode == 503 || this._statusCode == 603 || this._statusCode == 604;
   }

   final boolean isRedirect() {
      return this._statusCode >= 300 && this._statusCode < 400 && this._statusCode != 305 && this._statusCode != 307;
   }

   final void setResponseUri(String responseUri) {
      this._responseUri = responseUri;
   }

   @Override
   public final String getResponseUri() {
      return this._responseUri;
   }

   @Override
   public final boolean isSuccessful() {
      return this._statusCode >= 200 && this._statusCode < 300;
   }

   @Override
   public final int getResponseCode() {
      return this._statusCode;
   }

   @Override
   public final String getContentType() {
      return this._contentType;
   }

   public ResponseImpl(int statusCode) {
      this._statusCode = statusCode;
   }

   ResponseImpl(int statusCode, byte[] data) {
      super(data);
      this._statusCode = statusCode;
   }

   @Override
   public final String toString() {
      StringBuffer buf = (StringBuffer)(new Object(128));
      buf.append("Response[statusCode=").append(this._statusCode);
      if (this.hasData()) {
         buf.append(",dataSize=").append(this.getData().length);
      }

      if (this.hasHeaders()) {
         buf.append(",headers=").append(super._headers);
      }

      buf.append(",contentType").append(this._contentType);
      buf.append(']');
      return buf.toString();
   }

   ResponseImpl(int statusCode, String responseUri) {
      this._responseUri = responseUri;
      this._statusCode = statusCode;
   }
}
