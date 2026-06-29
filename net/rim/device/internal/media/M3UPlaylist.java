package net.rim.device.internal.media;

import java.io.ByteArrayOutputStream;
import java.io.Reader;
import java.io.Writer;
import javax.microedition.io.InputConnection;
import net.rim.device.api.browser.util.StaticHttpConnection;
import net.rim.device.api.io.http.HttpHeaders;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.NumberUtilities;

public final class M3UPlaylist extends Playlist {
   private String[] _urls = new Object[0];
   private String[] _titles = new Object[0];
   private int[] _lengths = new int[0];
   private static final String UTF8 = "UTF-8";
   private static final String CHARSET_UTF8 = ";charset=UTF-8";
   public static final String CONTENT_TYPE = "audio/x-mpegurl";
   private static final String FORMAT_DESCRIPTOR = "#EXTM3U";
   private static final String RECORD_MARKER = "#EXTINF";
   private static final char NEW_LINE = '\n';

   public M3UPlaylist() {
   }

   M3UPlaylist(Reader contentReader) {
      String line = this.receiveLine(contentReader);
      if (line != null) {
         boolean extended = false;
         if (line.equals("#EXTM3U")) {
            extended = true;
         } else {
            this.addUrl(line, null, -1);
         }

         int length = -1;
         String title = null;

         while (true) {
            line = this.receiveLine(contentReader);
            if (line == null) {
               return;
            }

            if (line.length() != 0) {
               if (extended) {
                  if (line.startsWith("#EXTINF")) {
                     int colonIndex = line.indexOf(58);
                     int commaIndex = line.indexOf(44);
                     if (colonIndex != -1 && commaIndex != -1) {
                        try {
                           length = NumberUtilities.parseInt(line, colonIndex + 1, commaIndex, 10);
                        } finally {
                           ;
                        }

                        title = line.substring(commaIndex + 1);
                     } else {
                        length = -1;
                        title = null;
                     }
                  } else {
                     this.addUrl(line, title, length);
                  }
               } else {
                  this.addUrl(line, null, -1);
               }
            }
         }
      }
   }

   private final String getString(int index, String[] array) {
      return index >= 0 && index < array.length ? array[index] : null;
   }

   private final int getInt(int index, int[] array, int def) {
      return index >= 0 && index < array.length ? array[index] : def;
   }

   @Override
   public final String getUrl(int index) {
      return this.getString(index, this._urls);
   }

   @Override
   public final String getTitle(int index) {
      return this.getString(index, this._titles);
   }

   @Override
   public final int getLength(int index) {
      return this.getInt(index, this._lengths, -1);
   }

   @Override
   public final int getNumberOfItems() {
      return this._urls.length;
   }

   public final void addUrl(String url, String title, int length) {
      Arrays.add(this._urls, url != null ? url.replace('\\', '/') : url);
      Arrays.add(this._titles, title);
      Arrays.add(this._lengths, length);
   }

   public final InputConnection openConnection(String url) {
      return new StaticHttpConnection(url, this.getBytes(), this.getHeaders());
   }

   private final HttpHeaders getHeaders() {
      HttpHeaders headers = new HttpHeaders();
      headers.addProperty("content-type", "audio/x-mpegurl;charset=UTF-8");
      return headers;
   }

   private final byte[] getBytes() {
      ByteArrayOutputStream baos = (ByteArrayOutputStream)(new Object());
      Writer writer = (Writer)(new Object(baos, "UTF-8"));
      writer.write("#EXTM3U");
      writer.write(10);
      String temp = null;

      for (int i = 0; i < this._urls.length; i++) {
         writer.write("#EXTINF");
         writer.write(58);
         writer.write(this._lengths[i]);
         writer.write(44);
         temp = this._titles[i];
         if (temp != null) {
            writer.write(this._titles[i]);
         }

         writer.write(10);
         temp = this._urls[i];
         if (temp != null) {
            writer.write(this._urls[i]);
         }

         writer.write(10);
      }

      writer.flush();
      return baos.toByteArray();
   }
}
