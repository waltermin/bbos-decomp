package net.rim.device.api.xml.jaxp;

import java.io.Reader;

class XMLParser$ReaderReader extends XMLParser$InputReader {
   private Reader _in;
   private final XMLParser this$0;

   XMLParser$ReaderReader(XMLParser _1, Reader in) {
      super(_1);
      this.this$0 = _1;
      this._in = in;
   }

   @Override
   public int nextChar() {
      super._column++;
      return this._in.read();
   }
}
