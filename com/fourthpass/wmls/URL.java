package com.fourthpass.wmls;

public final class URL {
   public String _protocol;
   public String _host;
   public String _file;
   public String _anchor;
   public int _port = 80;
   public int _hashCode = -1;
   private static final int NO_STATE = 0;
   private static final int PROTOCOL = 1;
   private static final int HOST = 2;
   private static final int PORT = 3;
   private static final int FILE = 4;
   private static final int ANCHOR = 5;

   public URL(String protocol, String host, String file, String anchor) {
      this();
      this._host = host;
      this._file = file;
      this._anchor = anchor;
      this._protocol = protocol;
   }

   private URL() {
   }

   public static final URL parse(URL context, String strURL) {
      URL url = new URL();
      if (strURL != null && strURL.length() != 0) {
         char[] array = strURL.toCharArray();
         int length = array.length;
         int tmp = 0;
         int prev = tmp;
         int state = 0;

         while (tmp < length) {
            switch (array[tmp]) {
               case '#':
                  switch (state) {
                     case 0:
                        int var10 = 5;
                        if (tmp != prev) {
                           url._file = new String(array, prev, tmp - prev);
                        }

                        state = 5;
                        prev = ++tmp;
                        continue;
                     case 4:
                        url._file = new String(array, prev, tmp - prev);
                        state = 5;
                        prev = ++tmp;
                        continue;
                     default:
                        tmp++;
                        continue;
                  }
               case '/':
                  switch (state) {
                     case -1:
                     case 1:
                        tmp++;
                        continue;
                     case 0:
                     default:
                        state = 4;
                        tmp++;
                        continue;
                     case 2:
                        url._host = new String(array, prev, tmp - prev);
                        url._port = 80;
                        state = 4;
                        prev = tmp++;
                        continue;
                     case 3:
                        url._port = Integer.parseInt(new String(array, prev, tmp - prev));
                        prev = tmp++;
                        state = 4;
                        continue;
                     case 4:
                        tmp++;
                        continue;
                  }
               case ':':
                  switch (state) {
                     case 0:
                        int tempo = tmp++;
                        if (array[tmp] == 0 || array[tmp] != '/') {
                           continue;
                        }

                        tmp++;
                        if (array[tmp] != 0 && array[tmp] == '/') {
                           url._protocol = new String(array, prev, tempo - prev);
                           prev = ++tmp;
                           state = 2;
                           continue;
                        }

                        tmp--;
                        continue;
                     case 2:
                        url._host = new String(array, prev, tmp - prev);
                        prev = ++tmp;
                        state = 3;
                        continue;
                     case 4:
                        tmp++;
                        continue;
                     default:
                        tmp++;
                        continue;
                  }
               default:
                  tmp++;
            }
         }

         switch (state) {
            case 1:
               url._file = new String(array, prev, tmp - prev);
               break;
            case 2:
            default:
               url._host = new String(array, prev, tmp - prev);
               url._port = 80;
               url._file = "/";
               break;
            case 3:
               url._file = "/";
               url._port = Integer.parseInt(new String(array, prev, tmp - prev));
               break;
            case 4:
               url._file = new String(array, prev, tmp - prev);
               if (url._file == null) {
                  url._file = "/";
               }
               break;
            case 5:
               url._anchor = new String(array, prev, tmp - prev);
         }

         ensureRelativity(context, url);
         return url;
      } else {
         return url;
      }
   }

   public static final void ensureRelativity(URL origin, URL rel) {
      if (origin != null && rel != null) {
         if (rel._host == null) {
            rel._host = origin._host;
            rel._port = origin._port;
            rel._protocol = origin._protocol;
            if (rel._file == null) {
               rel._file = origin._file;
            } else if (rel._file.charAt(0) != '/') {
               int tmp = origin._file.lastIndexOf(47);
               if (tmp < 0) {
                  rel._file = origin._file + '/' + rel._file;
               } else {
                  rel._file = origin._file.substring(0, tmp) + '/' + rel._file;
               }
            }
         }
      }
   }

   @Override
   public final synchronized int hashCode() {
      if (this._hashCode != -1) {
         return this._hashCode;
      }

      int h = 0;
      if (this._host != null) {
         h += this._host.hashCode();
      }

      if (this._file != null) {
         h += this._file.hashCode();
      }

      if (this._anchor != null) {
         h += this._anchor.hashCode();
      }

      h += this._port;
      this._hashCode = h;
      return this._hashCode;
   }

   @Override
   public final String toString() {
      return this._protocol + "://" + this._host + ':' + this._port + this._file;
   }
}
