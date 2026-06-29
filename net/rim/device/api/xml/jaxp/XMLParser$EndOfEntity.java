package net.rim.device.api.xml.jaxp;

class XMLParser$EndOfEntity extends XMLParser$InputReader {
   private final XMLParser this$0;

   XMLParser$EndOfEntity(XMLParser _1) {
      super(_1);
      this.this$0 = _1;
      super._returnEndOfEntity = true;
   }

   @Override
   public int nextChar() {
      return -1;
   }
}
