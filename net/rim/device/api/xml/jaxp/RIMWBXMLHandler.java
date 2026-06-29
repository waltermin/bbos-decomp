package net.rim.device.api.xml.jaxp;

import org.xml.sax.Attributes;

public interface RIMWBXMLHandler {
   void startElement(int var1, String var2, String var3, String var4, Attributes var5);

   void endElement(int var1, String var2, String var3, String var4);
}
