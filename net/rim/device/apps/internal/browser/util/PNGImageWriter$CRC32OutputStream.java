package net.rim.device.apps.internal.browser.util;

import java.io.DataOutputStream;
import java.io.OutputStream;
import net.rim.device.api.util.CRC32;

class PNGImageWriter$CRC32OutputStream extends DataOutputStream {
   private int _crcValue = -1;

   public PNGImageWriter$CRC32OutputStream(OutputStream out) {
      super(out);
   }

   @Override
   public void write(int b) {
      this._crcValue = CRC32.update(this._crcValue, b);
      super.out.write(b);
   }

   @Override
   public void write(byte[] b, int off, int len) {
      this._crcValue = CRC32.update(this._crcValue, b, off, len);
      super.out.write(b, off, len);
   }

   public void writeCRC() {
      this.writeInt(~this._crcValue);
      this.clearCRC();
   }

   public void clearCRC() {
      this._crcValue = -1;
   }
}
