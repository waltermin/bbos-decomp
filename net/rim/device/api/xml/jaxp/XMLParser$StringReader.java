package net.rim.device.api.xml.jaxp;

class XMLParser$StringReader extends XMLParser$InputReader {
   private String _str;
   private int _index;
   private final XMLParser this$0;

   XMLParser$StringReader(XMLParser _1, String str) {
      super(_1);
      this.this$0 = _1;
      this._str = str;
      this._index = 0;
   }

   @Override
   public int nextChar() {
      return this._index < this._str.length() ? this._str.charAt(this._index++) : -1;
   }
}
