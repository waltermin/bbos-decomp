package net.rim.device.api.xml.jaxp;

class XMLParser$CharacterEntityReader extends XMLParser$InputReader {
   private int _ch;
   private final XMLParser this$0;

   XMLParser$CharacterEntityReader(XMLParser _1, int ch) {
      super(_1);
      this.this$0 = _1;
      this._ch = ch;
   }

   @Override
   public int nextChar() {
      int ch = this._ch;
      this._ch = -1;
      return ch;
   }

   @Override
   boolean isEntity() {
      return true;
   }
}
