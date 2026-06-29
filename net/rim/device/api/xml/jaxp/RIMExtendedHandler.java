package net.rim.device.api.xml.jaxp;

import org.xml.sax.Attributes;

public interface RIMExtendedHandler {
   void startDTD();

   void endDTD(String var1, String var2, String var3, String var4);

   void comment(char[] var1, int var2, int var3);

   void cdataSection(char[] var1, int var2, int var3);

   void startEntityReference(String var1, String var2, String var3);

   void endEntityReference(String var1);

   void entityDecl(String var1, String var2);

   void defaultAttribute(String var1, String var2, String var3);

   void startAndEndElement(String var1, String var2, String var3, Attributes var4);
}
