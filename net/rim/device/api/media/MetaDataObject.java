package net.rim.device.api.media;

public class MetaDataObject {
   private String _mimeType;
   private String _filename;
   private String _url;
   private int _pictureType = -1;
   private String _description;
   private byte[] _data;
   public static final int PICTURE_TYPE_UNDEFINED = -1;
   public static final int PICTURE_TYPE_OTHER = 0;
   public static final int PICTURE_TYPE_32x32_FILE_ICON = 1;
   public static final int PICTURE_TYPE_OTHER_FILE_ICON = 2;
   public static final int PICTURE_TYPE_COVER_FRONT = 3;
   public static final int PICTURE_TYPE_COVER_BACK = 4;
   public static final int PICTURE_TYPE_LEAFLET_PAGE = 5;
   public static final int PICTURE_TYPE_MEDIA = 6;
   public static final int PICTURE_TYPE_LEAD_ARTIST = 7;
   public static final int PICTURE_TYPE_ARTIST = 8;
   public static final int PICTURE_TYPE_CONDUCTOR = 9;
   public static final int PICTURE_TYPE_BAND = 10;
   public static final int PICTURE_TYPE_COMPOSER = 11;
   public static final int PICTURE_TYPE_LYRICIST = 12;
   public static final int PICTURE_TYPE_RECORDING_LOCATION = 13;
   public static final int PICTURE_TYPE_DURING_RECORDING = 14;
   public static final int PICTURE_TYPE_DURING_PERFORMANCE = 15;
   public static final int PICTURE_TYPE_VIDEO_SCREEN_CAPTURE = 16;
   public static final int PICTURE_TYPE_A_BRIGHT_COLORED_FISH = 17;
   public static final int PICTURE_TYPE_ILLUSTRATION = 18;
   public static final int PICTURE_TYPE_BAND_LOGOTYPE = 19;
   public static final int PICTURE_TYPE_PUBLISHER_LOGOTYPE = 20;

   public String getMIMEType() {
      return this._mimeType;
   }

   public void setMIMEType(String mimeType) {
      this._mimeType = mimeType;
   }

   public String getFilename() {
      return this._filename;
   }

   public void setFilename(String filename) {
      this._filename = filename;
   }

   public String getURL() {
      return this._url;
   }

   public void setURL(String url) {
      this._url = url;
   }

   public int getPictureType() {
      return this._pictureType;
   }

   public void setPictureType(int pictureType) {
      this._pictureType = pictureType;
   }

   public String getDescription() {
      return this._description;
   }

   public void setDescription(String description) {
      this._description = description;
   }

   public byte[] getData() {
      return this._data;
   }

   public void setData(byte[] data) {
      this._data = data;
   }
}
