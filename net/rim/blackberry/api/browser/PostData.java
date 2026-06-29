package net.rim.blackberry.api.browser;

import net.rim.device.apps.internal.browser.stack.FormData;

public class PostData {
   FormData _formData;
   public static final int ENCTYPE_URLENCODED = 0;
   public static final int ENCTYPE_MULTIPART_FORMDATA = 1;

   protected PostData() {
   }

   public String getContentType() {
      throw null;
   }

   public void append(String _1, String _2) {
      throw null;
   }

   public void setData(Object _1) {
      throw null;
   }

   public byte[] getBytes() {
      throw null;
   }

   public int size() {
      throw null;
   }
}
