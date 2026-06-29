package net.rim.device.apps.internal.browser.multipart;

import java.io.InputStream;

final class MimeMultipartParser$PartInputStream extends InputStream {
   private MimeMultipartParser _parser;

   public MimeMultipartParser$PartInputStream(MimeMultipartParser parser) {
      this._parser = parser;
   }

   @Override
   public final int read() {
      return this._parser.read();
   }
}
