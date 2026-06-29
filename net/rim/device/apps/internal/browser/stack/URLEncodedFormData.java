package net.rim.device.apps.internal.browser.stack;

import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.util.Persistable;
import net.rim.device.cldc.io.utility.URIEncoder;

public final class URLEncodedFormData extends FormData implements Persistable {
   private String _charset;
   private Object _dataEncoding;
   private boolean _useWAPConventions;
   public static final String DEFAULT_CHARSET = "iso-8859-1";

   public URLEncodedFormData(String charset, boolean useWAPConventions) {
      this._charset = charset;
      if (this._charset == null) {
         this._charset = "iso-8859-1";
      }

      this._useWAPConventions = useWAPConventions;
   }

   public URLEncodedFormData(String charset, String urlEncodedFormData) {
      this._charset = charset;
      this._dataEncoding = PersistentContent.encode(urlEncodedFormData, true, true);
   }

   @Override
   public final String getContentType() {
      return "application/x-www-form-urlencoded" + (this._useWAPConventions ? "; charset=" + this._charset : "");
   }

   @Override
   public final String getCharset() {
      return this._charset;
   }

   @Override
   public final void append(Object stringBuffer, String name, String value) {
      if (name != null && name.length() > 0) {
         StringBuffer buffer = (StringBuffer)stringBuffer;
         int length = buffer.length();
         if (length > 0) {
            char character = buffer.charAt(length - 1);
            if (character != '?' && character != '&') {
               buffer.append('&');
            }
         }

         URIEncoder.encode(buffer, name, this._charset, false);
         buffer.append('=');
         if (value != null && value.length() > 0) {
            URIEncoder.encode(buffer, value, this._charset, false);
         }
      }
   }

   @Override
   public final void setData(Object data) {
      if (data instanceof StringBuffer) {
         this._dataEncoding = PersistentContent.encode(data.toString(), true, true);
      } else {
         this._dataEncoding = PersistentContent.encode((String)data, true, true);
      }
   }

   @Override
   public final byte[] getBytes() {
      return this.toString().getBytes();
   }

   @Override
   public final String toString() {
      return (String)PersistentContent.decode(this._dataEncoding);
   }
}
