package net.rim.blackberry.api.browser;

import java.io.ByteArrayOutputStream;
import net.rim.device.apps.internal.browser.stack.MultipartFormData;

public final class MultipartPostData extends PostData {
   private ByteArrayOutputStream _baos;
   public static final String DEFAULT_CHARSET = "iso-8859-1";

   public MultipartPostData(String charset, boolean useWAPConventions) {
      super._formData = new MultipartFormData(charset, useWAPConventions);
      this._baos = new ByteArrayOutputStream();
   }

   public MultipartPostData(byte[] multipartData) {
      super._formData = new MultipartFormData(multipartData);
   }

   @Override
   public final String getContentType() {
      return ((MultipartFormData)super._formData).getContentType();
   }

   @Override
   public final void append(String name, String value) {
      if (this._baos != null) {
         ((MultipartFormData)super._formData).append(this._baos, name, value);
      }
   }

   @Override
   public final void setData(Object data) {
      MultipartFormData formData = new MultipartFormData(!(data instanceof ByteArrayOutputStream) ? (byte[])data : ((ByteArrayOutputStream)data).toByteArray());
      if (data instanceof ByteArrayOutputStream) {
         formData.setData(data);
      }

      super._formData = formData;
      this._baos = null;
   }

   @Override
   public final byte[] getBytes() {
      if (this._baos != null) {
         ((MultipartFormData)super._formData).setData(this._baos);
         this._baos = null;
      }

      return ((MultipartFormData)super._formData).getBytes();
   }

   @Override
   public final int size() {
      if (this._baos != null) {
         return this._baos.size();
      }

      byte[] data = ((MultipartFormData)super._formData).getBytes();
      return data != null ? data.length : 0;
   }
}
