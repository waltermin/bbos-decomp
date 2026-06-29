package net.rim.device.apps.internal.browser.debug;

import java.io.InputStream;

final class PushbackInputStream {
   InputStream _stream;
   int _pushBack = -1;

   PushbackInputStream(InputStream stream) {
      this._stream = stream;
   }

   final int read() {
      if (this._pushBack != -1) {
         int retVal = this._pushBack;
         this._pushBack = -1;
         return retVal;
      } else {
         return this._stream.read();
      }
   }

   public final int readMBInt() {
      int result = 0;

      int i;
      do {
         i = this.read();
         if (i == -1) {
            return -1;
         }

         result = result << 7 | i & 127;
      } while ((i & 128) != 0);

      return result;
   }

   final int available() {
      return this._stream.available();
   }

   final void close() {
      this._stream.close();
   }

   final void unread(int value) {
      this._pushBack = value;
   }
}
