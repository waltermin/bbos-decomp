package net.rim.device.apps.internal.iota;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import net.rim.device.api.io.IOUtilities;
import net.rim.device.api.io.http.HttpHeaders;
import net.rim.device.api.util.StringUtilities;

public final class BodyPart {
   private HttpHeaders _headers;
   private ByteArrayOutputStream _byteStream = (ByteArrayOutputStream)(new Object());

   BodyPart(HttpHeaders headers) {
      this._headers = headers;
   }

   public final String getContentID() {
      return this._headers != null ? this._headers.getPropertyValue("Content-Id") : null;
   }

   public final byte[] getBytes() {
      byte[] bytes = this._byteStream.toByteArray();
      if (this._headers != null) {
         String contentTransferEncoding = this._headers.getPropertyValue("content-transfer-encoding");
         if (contentTransferEncoding != null && StringUtilities.strEqualIgnoreCase(contentTransferEncoding, "base64", 1701707776)) {
            try {
               return IOUtilities.streamToBytes((InputStream)(new Object((InputStream)(new Object(bytes)))));
            } finally {
               return bytes;
            }
         }
      }

      return bytes;
   }

   final void append(byte b) {
      this._byteStream.write(b);
   }

   final void append(byte[] b) {
      try {
         this._byteStream.write(b);
      } finally {
         return;
      }
   }
}
