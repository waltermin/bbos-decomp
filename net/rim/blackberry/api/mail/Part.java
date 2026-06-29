package net.rim.blackberry.api.mail;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;

public interface Part {
   String ATTACHMENT;
   String INLINE;

   void addHeader(String var1, String var2);

   void removeHeader(String var1);

   void setHeader(String var1, String var2);

   String[] getHeader(String var1);

   Enumeration getAllHeaders();

   void setContent(Object var1);

   Object getContent();

   String getContentType();

   boolean isMimeType(String var1);

   InputStream getInputStream();

   void writeTo(OutputStream var1);

   int getSize();
}
