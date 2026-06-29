package net.rim.device.apps.internal.browser.stack;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.util.Persistable;

public final class MultipartFormData extends FormData implements Persistable {
   private String _charset;
   private boolean _useWAPConventions;
   private String _boundary;
   private Object _dataEncoding;
   public static final String DEFAULT_CHARSET;
   private static final byte[] CRLF = new byte[]{13, 10};

   public MultipartFormData(String charset, boolean useWAPConventions) {
      this._charset = charset;
      this._useWAPConventions = useWAPConventions;
      if (charset == null) {
         this._charset = "iso-8859-1";
      }

      this._boundary = ((StringBuffer)(new Object("------------"))).append(System.currentTimeMillis()).toString();
   }

   public MultipartFormData(byte[] multipartData) {
      this._dataEncoding = PersistentContent.encode(multipartData, true, true);
      StringBuffer boundaryBuffer = (StringBuffer)(new Object());
      int maxBoundaryEnd = multipartData.length - 4;

      for (int i = 2; i < maxBoundaryEnd; i++) {
         byte ch = multipartData[i];
         if (ch == 13) {
            break;
         }

         boundaryBuffer.append((char)ch);
      }

      this._boundary = boundaryBuffer.toString();
   }

   @Override
   public final String getContentType() {
      return ((StringBuffer)(new Object("multipart/form-data; boundary="))).append(this._boundary).toString();
   }

   @Override
   public final String getCharset() {
      return this._charset;
   }

   private final byte[] stringToBytes(String str) {
      if (str == null) {
         return null;
      }

      try {
         return str.getBytes(this._charset);
      } finally {
         this._charset = "iso-8859-1";
         return str.getBytes();
      }
   }

   @Override
   public final void append(Object outputStream, String name, String value) {
      if (name != null && name.length() > 0) {
         this.append(
            outputStream,
            name,
            this.stringToBytes(normalizeNewlines(value)),
            this._useWAPConventions ? ((StringBuffer)(new Object("text/plain; charset="))).append(this._charset).toString() : null,
            null
         );
      }
   }

   public final void append(Object outputStream, String name, byte[] value, String contentType, String filename) {
      if (name != null && name.length() > 0) {
         try {
            OutputStream partStream = (OutputStream)outputStream;
            partStream.write(45);
            partStream.write(45);
            partStream.write(this._boundary.getBytes());
            partStream.write(CRLF);
            partStream.write("Content-Disposition: form-data; name=\"".getBytes());
            partStream.write(this.stringToBytes(name));
            partStream.write(34);
            if (filename != null) {
               partStream.write("; filename=\"".getBytes());
               if (filename.length() > 0) {
                  partStream.write(this.stringToBytes(filename));
               }

               partStream.write(34);
            }

            partStream.write(CRLF);
            if (contentType != null) {
               partStream.write("Content-Type: ".getBytes());
               partStream.write(contentType.getBytes());
               partStream.write(CRLF);
            }

            partStream.write(CRLF);
            if (value != null) {
               partStream.write(value);
            }

            partStream.write(CRLF);
         } finally {
            return;
         }
      }
   }

   private static final String normalizeNewlines(String str) {
      if (str != null && (str.indexOf(13) != -1 || str.indexOf(10) != -1)) {
         StringBuffer buffer = (StringBuffer)(new Object());
         int length = str.length();

         for (int i = 0; i < length; i++) {
            char ch = str.charAt(i);
            if (ch == '\r') {
               buffer.append(ch);
               buffer.append('\n');
               if (i + 1 < length && str.charAt(i + 1) == '\n') {
                  i++;
               }
            } else if (ch == '\n') {
               buffer.append('\r');
               buffer.append(ch);
            } else {
               buffer.append(ch);
            }
         }

         return buffer.toString();
      } else {
         return str;
      }
   }

   @Override
   public final void setData(Object data) {
      if (!(data instanceof Object)) {
         this._dataEncoding = PersistentContent.encode((byte[])data, true, true);
      } else {
         ByteArrayOutputStream baos = (ByteArrayOutputStream)data;

         label25:
         try {
            baos.write(45);
            baos.write(45);
            baos.write(this._boundary.getBytes());
            baos.write(45);
            baos.write(45);
            baos.write(CRLF);
         } finally {
            break label25;
         }

         this._dataEncoding = PersistentContent.encode(baos.toByteArray(), true, true);
      }
   }

   @Override
   public final byte[] getBytes() {
      return (byte[])PersistentContent.decode(this._dataEncoding);
   }
}
