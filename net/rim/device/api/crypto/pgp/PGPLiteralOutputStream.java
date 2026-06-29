package net.rim.device.api.crypto.pgp;

import java.io.OutputStream;
import net.rim.device.internal.crypto.pgp.PGPUtilities;

public class PGPLiteralOutputStream extends PGPOutputStream {
   private OutputStream _internalStream;
   private OutputStream _header = this._out.getOutputStream();
   private int _lengthCount;

   public PGPLiteralOutputStream(OutputStream out) {
      this(out, 1, 0, null, 4);
   }

   public PGPLiteralOutputStream(OutputStream out, int type) {
      this(out, type, 0, null, 4);
   }

   public PGPLiteralOutputStream(OutputStream out, int type, long time) {
      this(out, type, time, null, 4);
   }

   public PGPLiteralOutputStream(OutputStream out, int type, long time, String filename) {
      this(out, type, time, filename, 4);
   }

   public PGPLiteralOutputStream(OutputStream out, int type, long time, String filename, int tagFormat) {
      super(out, tagFormat);
      this._internalStream = super._out.getOutputStream();
      if (time < 0) {
         throw new Object();
      }

      switch (type) {
         case -1:
            throw new Object();
         case 0:
         default:
            this._internalStream.write(116);
            break;
         case 1:
            this._internalStream.write(98);
      }

      this._lengthCount++;
      if (filename != null) {
         byte[] stringAsByteArray = filename.getBytes();
         int fileLength = stringAsByteArray.length;
         this._internalStream.write((byte)fileLength);
         this._internalStream.write(stringAsByteArray, 0, fileLength);
         this._lengthCount += fileLength + 1;
      } else {
         this._internalStream.write(0);
         this._lengthCount++;
      }

      byte[] timeAsByte = new byte[]{(byte)(time >> 24), (byte)(time >> 16), (byte)(time >> 8), (byte)time};
      this._internalStream.write(timeAsByte, 0, 4);
      this._lengthCount += 4;
   }

   @Override
   public void write(byte[] b, int off, int len) {
      if (b != null && off >= 0 && len >= 0 && b.length - len >= off) {
         this.updateInternal(b, off, len);
         this._internalStream.write(b, off, len);
         this._lengthCount += len;
      } else {
         throw new Object();
      }
   }

   @Override
   void update(byte[] data, int offset, int length) {
   }

   void updateInternal(byte[] data, int offset, int length) {
      if (super._pgpOut != null) {
         super._pgpOut.update(data, offset, length);
      }
   }

   @Override
   public void flush() {
      this._internalStream.flush();
   }

   @Override
   public void close() {
      this.flush();
      PGPUtilities.writeTagAndLength(this._header, 11, this._lengthCount, 4);
      this._header.close();
      this._internalStream.close();
      super.close();
   }
}
