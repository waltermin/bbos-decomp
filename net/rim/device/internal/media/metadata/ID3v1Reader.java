package net.rim.device.internal.media.metadata;

import com.sun.cldc.i18n.Helper;
import javax.microedition.media.control.MetaDataControl;
import net.rim.device.api.i18n.Locale;
import net.rim.device.api.i18n.ResourceBundle;

public final class ID3v1Reader {
   private static final int ID3V1_TITLE_FIELD_LENGTH = 30;
   private static final int ID3V1_ARTIST_FIELD_LENGTH = 30;
   private static final int ID3V1_ALBUM_FIELD_LENGTH = 30;
   private static final int ID3V1_YEAR_FIELD_LENGTH = 4;
   private static final int ID3V1_COMMENT_FIELD_LENGTH = 30;
   private static final int ID3V1_TITLE_FIELD_OFFSET = 0;
   private static final int ID3V1_ARTIST_FIELD_OFFSET = 30;
   private static final int ID3V1_ALBUM_FIELD_OFFSET = 60;
   private static final int ID3V1_YEAR_FIELD_OFFSET = 90;
   private static final int ID3V1_COMMENT_FIELD_OFFSET = 94;
   private static final int ID3V1_GENRE_FIELD_OFFSET = 124;
   private static final int ID3V1_1_TRACK_NUMBER_FIELD_OFFSET = 123;
   private static final int MAX_GENRE_CODE = 147;

   private ID3v1Reader() {
   }

   public static final MetaDataControl readTag(byte[] tag) {
      if (tag.length != 125) {
         throw new IllegalArgumentException();
      }

      MetaDataControlImpl metaData = new MetaDataControlImpl();
      String encoding = Helper.getSuggestedEncoding(Locale.getDefault().getCode());
      metaData.put("title", readID3v1Field(tag, 0, 30, encoding));
      metaData.put("author", readID3v1Field(tag, 30, 30, encoding));
      metaData.put("album", readID3v1Field(tag, 60, 30, encoding));
      metaData.put("date", readID3v1Field(tag, 90, 4, encoding));
      metaData.put("comment", readID3v1Field(tag, 94, 30, encoding));

      label39:
      try {
         metaData.put("genre", genreCodeToString(tag[124] & 255));
      } finally {
         break label39;
      }

      if (tag[123] != 0 && tag[122] == 0) {
         metaData.put("track number", String.valueOf(tag[123] & 255));
      }

      return metaData.size() > 0 ? metaData : null;
   }

   private static final String readID3v1Field(byte[] tag, int offset, int maxFieldLength, String encoding) {
      int byteLength = 0;

      while (byteLength < maxFieldLength && tag[offset + byteLength] != 0) {
         byteLength++;
      }

      String str = null;
      if (byteLength > 0) {
         try {
            str = new String(tag, offset, byteLength, encoding).trim();
            if (str.length() > 0) {
               return str;
            }
         } finally {
            return null;
         }
      }

      return null;
   }

   static final String genreCodeToString(int genreCode) {
      if (genreCode >= 0 && genreCode <= 147) {
         return ResourceBundle.getBundle(7712504258262123478L, "net.rim.device.internal.resource.Genre").getString(genreCode);
      } else {
         throw new IllegalArgumentException();
      }
   }
}
