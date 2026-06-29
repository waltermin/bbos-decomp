package org.xml.sax.ext;

public interface LexicalHandler {
   void startDTD(String var1, String var2, String var3);

   void endDTD();

   void startEntity(String var1);

   void endEntity(String var1);

   void startCDATA();

   void endCDATA();

   void comment(char[] var1, int var2, int var3);
}
