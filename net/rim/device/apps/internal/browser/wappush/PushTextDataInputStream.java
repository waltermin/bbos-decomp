package net.rim.device.apps.internal.browser.wappush;

import java.io.DataInputStream;
import java.io.InputStream;

final class PushTextDataInputStream extends DataInputStream {
   private InputStream _rawContentStream;

   public PushTextDataInputStream(InputStream in, InputStream rawContentStream) {
      super(in);
      this._rawContentStream = rawContentStream;
   }

   public final InputStream getRawContentStream() {
      return this._rawContentStream;
   }
}
