package net.rim.device.internal.media;

import java.io.Reader;
import net.rim.device.api.io.MIMETypeAssociations;
import net.rim.device.api.util.StringUtilities;

public class Playlist {
   private boolean _crRead;

   public String getUrl(int _1) {
      throw null;
   }

   public String getTitle(int _1) {
      throw null;
   }

   public int getLength(int _1) {
      throw null;
   }

   public int getNumberOfItems() {
      throw null;
   }

   public static Playlist getPlaylist(String contentType, Reader contentReader) {
      if (contentType != null && contentReader != null) {
         contentType = MIMETypeAssociations.getNormalizedType(contentType);
         if (StringUtilities.strEqual(contentType, "audio/x-mpegurl")) {
            return new M3UPlaylist(contentReader);
         } else {
            return StringUtilities.strEqual(contentType, "audio/x-scpls") ? new PLSPlaylist(contentReader) : null;
         }
      } else {
         return null;
      }
   }

   public String receiveLine(Reader contentReader) {
      boolean anythingRead = false;
      StringBuffer result = new StringBuffer();

      try {
         boolean shouldContinue = true;

         while (shouldContinue) {
            int value = contentReader.read();
            switch (value) {
               case -1:
                  shouldContinue = false;
                  break;
               case 10:
                  anythingRead = true;
                  if (!this._crRead) {
                     shouldContinue = false;
                  }

                  this._crRead = false;
                  break;
               case 13:
                  anythingRead = true;
                  shouldContinue = false;
                  this._crRead = true;
                  break;
               default:
                  anythingRead = true;
                  this._crRead = false;
                  result.append((char)value);
            }
         }
      } finally {
         return anythingRead ? result.toString().trim() : null;
      }

      return anythingRead ? result.toString().trim() : null;
   }
}
