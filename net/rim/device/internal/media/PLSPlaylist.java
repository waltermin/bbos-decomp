package net.rim.device.internal.media;

import java.io.Reader;
import java.util.Hashtable;

public final class PLSPlaylist extends Playlist {
   private Hashtable _table;
   public static final String CONTENT_TYPE;

   private PLSPlaylist() {
   }

   PLSPlaylist(Reader contentReader) {
      this._table = (Hashtable)(new Object());
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
      String url = (String)this._table.get(((StringBuffer)(new Object("File"))).append(index + 1).toString());
      if (url != null) {
         url = url.replace('\\', '/');
      }

      return url;
   }

   @Override
   public final String getTitle(int index) {
      return (String)this._table.get(((StringBuffer)(new Object("Title"))).append(index + 1).toString());
   }

   @Override
   public final int getLength(int index) {
      String value = (String)this._table.get(((StringBuffer)(new Object("Length"))).append(index + 1).toString());
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
