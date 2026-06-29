package net.rim.device.api.xml.jaxp;

import com.sun.cldc.i18n.Helper;
import java.io.InputStream;
import java.io.Reader;

class XMLParser$UTF8InputStreamReader extends XMLParser$InputReader {
   private InputStream _in;
   boolean _havePeek;
   int _peek;
   Reader _reader;
   private final XMLParser this$0;

   XMLParser$UTF8InputStreamReader(XMLParser _1, InputStream in) {
      super(_1);
      this.this$0 = _1;
      this._in = in;
   }

   @Override
   void setEncoding(String enc) {
      try {
         this._reader = Helper.getStreamReader(this._in, enc);
      } finally {
         return;
      }
   }

   @Override
   public int nextChar() {
      if (this._havePeek) {
         this._havePeek = false;
         return this._peek;
      }

      if (this._reader != null) {
         return this._reader.read();
      }

      int c0 = this._in.read();
      super._column++;
      if (c0 <= 127) {
         return c0;
      }

      switch (c0 & 248) {
         case 192:
         case 200:
         case 208:
         case 216: {
            int c1 = this._in.read();
            super._column++;
            if ((c1 & 192) == 128) {
               return ((c0 & 31) << 6) + (c1 & 63);
            }
            break;
         }
         case 224:
         case 232:
         case 248: {
            int c1 = this._in.read();
            super._column++;
            if ((c1 & 192) == 128) {
               int c2x = this._in.read();
               super._column++;
               if ((c2x & 192) == 128) {
                  return ((c0 & 15) << 12) + ((c1 & 63) << 6) + (c2x & 63);
               }
            }
            break;
         }
         case 240: {
            c0 &= 7;
            int c1 = this._in.read() & 63;
            super._column++;
            int c2 = this._in.read() & 63;
            super._column++;
            int z = this._in.read() & 63;
            super._column++;
            int v = (c0 << 2) + (c1 >> 4) - 1;
            int w = c1 & 15;
            int x = c2 >> 4;
            int y = c2 & 15;
            this._peek = (char)(56320 + (y << 6) + z);
            this._havePeek = true;
            return (char)(55296 + (v << 6) + (w << 2) + x);
         }
      }

      this.this$0.fatalError("Malformed UTF-8.");
      return -1;
   }
}
