package org.xml.sax;

public interface XMLReader {
   boolean getFeature(String var1);

   void setFeature(String var1, boolean var2);

   Object getProperty(String var1);

   void setProperty(String var1, Object var2);

   void setEntityResolver(EntityResolver var1);

   EntityResolver getEntityResolver();

   void setDTDHandler(DTDHandler var1);

   DTDHandler getDTDHandler();

   void setContentHandler(ContentHandler var1);

   ContentHandler getContentHandler();

   void setErrorHandler(ErrorHandler var1);

   ErrorHandler getErrorHandler();

   void parse(InputSource var1);

   void parse(String var1);
}
