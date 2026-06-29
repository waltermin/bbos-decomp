package org.xml.sax;

public interface ErrorHandler {
   void warning(SAXParseException var1);

   void error(SAXParseException var1);

   void fatalError(SAXParseException var1);
}
