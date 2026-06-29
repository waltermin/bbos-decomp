package net.rim.device.api.xml.jaxp;

class XMLParser$SetDTDBody extends XMLParser$InputReader {
   StringBuffer _oldDTDBody;
   private final XMLParser this$0;

   XMLParser$SetDTDBody(XMLParser _1, StringBuffer oldDTDBody) {
      super(_1);
      this.this$0 = _1;
      this._oldDTDBody = oldDTDBody;
   }

   @Override
   public int nextChar() {
      this.this$0._dtdBody = this._oldDTDBody;
      return -1;
   }
}
