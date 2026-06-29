package net.rim.device.api.xml.jaxp;

class XMLParser$EntityReader extends XMLParser$StringReader {
   private final XMLParser this$0;

   XMLParser$EntityReader(XMLParser _1, String entityName, String str, boolean isPE) {
      super(_1, str);
      this.this$0 = _1;
      this.setEntityName(entityName, isPE);
   }
}
