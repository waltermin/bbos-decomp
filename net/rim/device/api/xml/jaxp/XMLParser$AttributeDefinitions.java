package net.rim.device.api.xml.jaxp;

import java.util.Enumeration;
import java.util.Hashtable;

class XMLParser$AttributeDefinitions {
   private Hashtable _definitions;
   private final XMLParser this$0;

   XMLParser$AttributeDefinitions(XMLParser _1) {
      this.this$0 = _1;
      this._definitions = new Hashtable();
   }

   public void addAttribute(String qName, String localName, String prefixName, String type, boolean isCData, String defaultValue) {
      XMLParser$AttributeDefinitions$Value a = new XMLParser$AttributeDefinitions$Value(this);
      a.qName = qName;
      a.localName = localName;
      a.prefixName = prefixName;
      a.defaultValue = defaultValue;
      a.type = type;
      a.isCData = isCData;
      a.isDefaultNamespace = localName.equals(this.this$0.XMLNS);
      a.isNamespace = prefixName.equals(this.this$0.XMLNS);
      this.this$0.putIgnoreDuplicates(this._definitions, qName, a);
   }

   public XMLParser$AttributeDefinitions$Value getValue(String attName) {
      return (XMLParser$AttributeDefinitions$Value)this._definitions.get(attName);
   }

   public Enumeration values() {
      return this._definitions.elements();
   }
}
