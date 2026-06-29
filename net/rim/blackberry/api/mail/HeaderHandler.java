package net.rim.blackberry.api.mail;

import java.util.Vector;

interface HeaderHandler {
   void addHeader(String var1, String var2, Message var3);

   void setHeader(String var1, String var2, Message var3);

   void removeHeader(String var1, Message var2);

   String[] getHeader(String var1, Message var2);

   Vector getHeaderObjects(Vector var1, Message var2);
}
