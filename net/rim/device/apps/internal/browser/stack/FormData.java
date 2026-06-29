package net.rim.device.apps.internal.browser.stack;

import net.rim.device.api.util.Persistable;

public class FormData implements Persistable {
   public static final int ENCTYPE_URLENCODED;
   public static final int ENCTYPE_MULTIPART_FORMDATA;

   protected FormData() {
   }

   public String getContentType() {
      throw null;
   }

   public String getCharset() {
      throw null;
   }

   public void append(Object _1, String _2, String _3) {
      throw null;
   }

   public void setData(Object _1) {
      throw null;
   }

   public byte[] getBytes() {
      throw null;
   }
}
