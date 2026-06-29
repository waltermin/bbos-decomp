package javax.wireless.messaging;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class MessagePart {
   private byte[] _contents;
   private String _mimeType;
   private String _contentId;
   private String _contentLocation;
   private String _enc;

   public MessagePart(byte[] contents, int offset, int length, String mimeType, String contentId, String contentLocation, String enc) {
      if (mimeType != null && contentId != null && length >= 0 && offset >= 0) {
         if (contents != null) {
            if (offset + length > contents.length) {
               throw new IllegalArgumentException();
            }

            this._contents = new byte[length];
            System.arraycopy(contents, offset, this._contents, 0, length);
         }

         this._mimeType = mimeType;
         if (contentId != null && !this.isASCII(contentId)) {
            throw new IllegalArgumentException();
         }

         if (contentLocation != null && !this.isASCII(contentLocation)) {
            throw new IllegalArgumentException();
         }

         this._contentId = contentId;
         this._contentLocation = contentLocation;
         this._enc = enc;
      } else {
         throw new IllegalArgumentException();
      }
   }

   public MessagePart(byte[] contents, String mimeType, String contentId, String contentLocation, String enc) {
      if (mimeType == null || contentId == null) {
         throw new IllegalArgumentException();
      }

      if (contentId != null && !this.isASCII(contentId)) {
         throw new IllegalArgumentException();
      }

      if (contentLocation != null && !this.isASCII(contentLocation)) {
         throw new IllegalArgumentException();
      }

      if (contents != null) {
         this._contents = new byte[contents.length];
         System.arraycopy(contents, 0, this._contents, 0, contents.length);
      }

      this._mimeType = mimeType;
      this._contentId = contentId;
      this._contentLocation = contentLocation;
      this._enc = enc;
   }

   public MessagePart(InputStream is, String mimeType, String contentId, String contentLocation, String enc) {
      if (mimeType == null || contentId == null) {
         throw new IllegalArgumentException();
      }

      if (contentId != null && !this.isASCII(contentId)) {
         throw new IllegalArgumentException();
      }

      if (contentLocation != null && !this.isASCII(contentLocation)) {
         throw new IllegalArgumentException();
      }

      if (is != null) {
         this._contents = new byte[is.available()];
         is.read(this._contents);
      }

      this._mimeType = mimeType;
      this._contentId = contentId;
      this._contentLocation = contentLocation;
      this._enc = enc;
   }

   public byte[] getContent() {
      return this._contents;
   }

   public InputStream getContentAsStream() {
      return this._contents == null ? new ByteArrayInputStream(new byte[0]) : new ByteArrayInputStream(this._contents);
   }

   public String getContentID() {
      return this._contentId;
   }

   public String getContentLocation() {
      return this._contentLocation;
   }

   public String getEncoding() {
      return this._enc;
   }

   public int getLength() {
      return this._contents != null ? this._contents.length : 0;
   }

   public String getMIMEType() {
      return this._mimeType;
   }

   private boolean isASCII(String s) {
      int length = s.length();

      for (int i = 0; i < length; i++) {
         char c = s.charAt(i);
         if (c < 0 || c > 127) {
            return false;
         }
      }

      return true;
   }
}
