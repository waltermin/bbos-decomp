package net.rim.device.internal.media;

import java.io.Reader;
import java.util.Hashtable;

public final class PLSPlaylist extends Playlist {
   private Hashtable _table;
   public static final String CONTENT_TYPE = "audio/x-scpls";

   private PLSPlaylist() {
   }

   PLSPlaylist(Reader contentReader) {
      this._table = new Hashtable();
      String line = this.receiveLine(contentReader);
      if (line != null && !line.equals("[playlist]")) {
         while (true) {
            line = this.receiveLine(contentReader);
            if (line == null) {
               return;
            }

            int equalsIndex = line.indexOf(61);
            if (equalsIndex != -1) {
               String key = line.substring(0, equalsIndex);
               String value = line.substring(equalsIndex + 1);
               this._table.put(key, value);
            }
         }
      }
   }

   @Override
   public final String getUrl(int index) {
      String url = (String)this._table.get("File" + (index + 1));
      if (url != null) {
         url = url.replace('\\', '/');
      }

      return url;
   }

   @Override
   public final String getTitle(int index) {
      return (String)this._table.get("Title" + (index + 1));
   }

   @Override
   public final int getLength(int index) {
      String value = (String)this._table.get("Length" + (index + 1));
      if (value == null) {
         return -1;
      }

      try {
         return Integer.parseInt(value);
      } finally {
         ;
      }
   }

   @Override
   public final int getNumberOfItems() {
      String value = (String)this._table.get("numberofentries");
      if (value == null) {
         return 0;
      }

      try {
         return Integer.parseInt(value);
      } finally {
         ;
      }
   }
}
