package net.rim.blackberry.api.browser;

import com.sun.cldc.i18n.Helper;
import net.rim.device.apps.internal.browser.stack.FormData;
import net.rim.device.apps.internal.browser.stack.URLEncodedFormData;

public final class URLEncodedPostData extends PostData {
   private StringBuffer _buffer;
   public static final String DEFAULT_CHARSET = "iso-8859-1";

   private final void init() {
      this._buffer = (StringBuffer)(new Object());
      this.setData(this._buffer);
   }

   public URLEncodedPostData(String charset, boolean useWAPConventions) {
      charset = this.checkCharset(charset);
      super._formData = (FormData)(new Object(charset, useWAPConventions));
      this.init();
   }

   public URLEncodedPostData(String charset, String urlEncodedFormData) {
      charset = this.checkCharset(charset);
      super._formData = (FormData)(new Object(charset, urlEncodedFormData));
      this.init();
   }

   private final String checkCharset(String charset) {
      return charset != null && Helper.isSupportedEncoding(charset) ? charset : "iso-8859-1";
   }

   @Override
   public final String getContentType() {
      return ((URLEncodedFormData)super._formData).getContentType();
   }

   public final String getCharset() {
      return ((URLEncodedFormData)super._formData).getCharset();
   }

   @Override
   public final void append(String name, String value) {
      ((URLEncodedFormData)super._formData).append(this._buffer, name, value);
      this.setData(this._buffer);
   }

   @Override
   public final void setData(Object data) {
      if (!(data instanceof Object)) {
         this._buffer = (StringBuffer)data;
      } else {
         String s = (String)data;
         this._buffer = (StringBuffer)(new Object(s));
      }

      ((URLEncodedFormData)super._formData).setData(data);
   }

   @Override
   public final byte[] getBytes() {
      return ((URLEncodedFormData)super._formData).getBytes();
   }

   @Override
   public final String toString() {
      return super._formData.toString();
   }

   @Override
   public final int size() {
      return this._buffer.length();
   }
}
