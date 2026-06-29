package org.xml.sax;

public interface ContentHandler {
   void setDocumentLocator(Locator var1);

   void startDocument();

   void endDocument();

   void startPrefixMapping(String var1, String var2);

   void endPrefixMapping(String var1);

   void startElement(String var1, String var2, String var3, Attributes var4);

   void endElement(String var1, String var2, String var3);

   void characters(char[] var1, int var2, int var3);

   void ignorableWhitespace(char[] var1, int var2, int var3);

   void processingInstruction(String var1, String var2);

   void skippedEntity(String var1);
}
