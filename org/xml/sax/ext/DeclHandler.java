package org.xml.sax.ext;

public interface DeclHandler {
   void elementDecl(String var1, String var2);

   void attributeDecl(String var1, String var2, String var3, String var4, String var5);

   void internalEntityDecl(String var1, String var2);

   void externalEntityDecl(String var1, String var2, String var3);
}
