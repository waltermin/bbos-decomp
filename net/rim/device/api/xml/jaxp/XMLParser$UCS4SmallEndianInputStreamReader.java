package net.rim.device.api.xml.jaxp;

import java.io.InputStream;

class XMLParser$UCS4SmallEndianInputStreamReader extends XMLParser$InputReader {
   private InputStream _in;
   private final XMLParser this$0;

   XMLParser$UCS4SmallEndianInputStreamReader(XMLParser _1, InputStream in) {
      super(_1);
      this.this$0 = _1;
      this._in = in;
   }

   @Override
   public int nextChar() {
      int ch1 = this._in.read();
      int ch2 = this._in.read();
      int ch3 = this._in.read();
      int ch4 = this._in.read();
      if (ch1 == -1) {
         return -1;
      }

      if (ch2 == -1) {
         this.this$0.fatalError("Unexpected end of file.");
      }

      if (ch3 == -1) {
         this.this$0.fatalError("Unexpected end of file.");
      }

      if (ch4 == -1) {
         this.this$0.fatalError("Unexpected end of file.");
      }

      super._column++;
      return ch1 + (ch2 << 8) + (ch3 << 16) + (ch4 << 24);
   }
}
