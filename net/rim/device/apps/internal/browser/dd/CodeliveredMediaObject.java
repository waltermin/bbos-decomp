package net.rim.device.apps.internal.browser.dd;

import java.io.InputStream;
import net.rim.device.api.io.IOUtilities;

final class CodeliveredMediaObject {
   private byte[] _data;
   private String _contentType;
   private String _contentID;
   private String _url;

   public CodeliveredMediaObject(InputStream inputStream, String contentType, String contentID, String url) {
      this._contentType = contentType;
      this._contentID = contentID;
      this._url = url;
      this._data = IOUtilities.streamToBytes(inputStream);
   }

   public final String getContentType() {
      return this._contentType;
   }

   public final String getContentID() {
      return this._contentID;
   }

   public final String getURL() {
      return this._url;
   }

   public final byte[] getData() {
      return this._data;
   }
}
