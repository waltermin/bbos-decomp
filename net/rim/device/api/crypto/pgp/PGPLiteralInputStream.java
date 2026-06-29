package net.rim.device.api.crypto.pgp;

import java.io.InputStream;

public final class PGPLiteralInputStream extends PGPInputStream {
   private int _type;
   private String _filename;
   private long _date;

   PGPLiteralInputStream(InputStream input) throws PGPEncodingException {
      super(input, null);
      byte firstByte = (byte)super._input.read();
      if (firstByte == 116) {
         this._type = 0;
      } else {
         if (firstByte != 98) {
            throw new PGPEncodingException(((StringBuffer)(new Object("Lit:"))).append(firstByte).toString());
         }

         this._type = 1;
      }

      byte stringLength = (byte)super._input.read();
      int length = stringLength & 255;
      byte[] stringAsBytes = new byte[length];
      super._input.read(stringAsBytes);
      if (length != 0) {
         this._filename = (String)(new Object(stringAsBytes));
      }

      this._date = (super._input.read() & 255) << 24;
      this._date = this._date + ((super._input.read() & 255) << 16);
      this._date = this._date + ((super._input.read() & 255) << 8);
      this._date = this._date + (super._input.read() & 255);
   }

   @Override
   public final int read(byte[] b, int off, int len) {
      if (b != null && off >= 0 && len >= 0 && b.length - len >= off) {
         return super._input.read(b, off, len);
      } else {
         throw new Object();
      }
   }

   @Override
   public final int available() {
      return super._input.available();
   }

   @Override
   public final long skip(long n) {
      return super._input.skip(n);
   }

   public final int getDataType() {
      return this._type;
   }

   public final String getFileName() {
      return this._filename;
   }

   public final long getDate() {
      return this._date;
   }

   @Override
   public final String getType() {
      return "Literal";
   }
}
