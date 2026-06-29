package javax.microedition.media.protocol;

public class ContentDescriptor {
   private String _contentType;

   public ContentDescriptor(String contentType) {
      this._contentType = contentType;
   }

   public String getContentType() {
      return this._contentType;
   }
}
