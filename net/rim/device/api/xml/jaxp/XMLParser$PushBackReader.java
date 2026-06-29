package net.rim.device.api.xml.jaxp;

class XMLParser$PushBackReader extends XMLParser$StringReader {
   private final XMLParser this$0;

   XMLParser$PushBackReader(XMLParser _1, String str) {
      super(_1, str);
      this.this$0 = _1;
   }

   @Override
   void setLastRead() {
      super._next.setLastRead();
   }
}
