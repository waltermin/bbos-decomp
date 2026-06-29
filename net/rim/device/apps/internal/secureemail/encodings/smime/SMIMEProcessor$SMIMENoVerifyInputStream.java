package net.rim.device.apps.internal.secureemail.encodings.smime;

import java.io.InputStream;
import net.rim.device.api.mime.MIMEInputStream;

class SMIMEProcessor$SMIMENoVerifyInputStream extends InputStream {
   private MIMEInputStream _mimeInputStream;

   SMIMEProcessor$SMIMENoVerifyInputStream(MIMEInputStream mimeInputStream) {
      this._mimeInputStream = mimeInputStream;
   }

   @Override
   public int read() {
      return -1;
   }

   public InputStream getInnerInputStream() {
      return this._mimeInputStream;
   }
}
