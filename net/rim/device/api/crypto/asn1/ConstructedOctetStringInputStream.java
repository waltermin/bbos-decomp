package net.rim.device.api.crypto.asn1;

import net.rim.device.api.io.SharedInputStream;

final class ConstructedOctetStringInputStream extends SharedInputStream {
   private ASN1Field _field;
   private SharedInputStream _currentInputStream;

   public ConstructedOctetStringInputStream(SharedInputStream input) {
      this(input, -1);
   }

   public ConstructedOctetStringInputStream(SharedInputStream input, int length) {
      super(input);
      if (length >= 0) {
         this.setLength(length);
      }

      this._field = new ASN1Field(input, input.getCurrentPosition(), length);
      super.setCurrentPosition(this._field.getStartPosition());
      this.updateCurrentInputStream();
   }

   @Override
   public final int read() {
      return !this.updateStreams() ? -1 : this._currentInputStream.read();
   }

   @Override
   public final int read(byte[] b, int off, int len) {
      if (b != null && off >= 0 && len >= 0 && b.length - len >= off) {
         int lengthLeft = len;
         byte[] buffer = new byte[len];
         int position = 0;

         while (lengthLeft > 0) {
            if (!this.updateStreams()) {
               position = -1;
               break;
            }

            int streamRead = this._currentInputStream.read(buffer, position, lengthLeft);
            if (streamRead == lengthLeft) {
               position += lengthLeft;
               lengthLeft = 0;
            } else {
               lengthLeft -= streamRead;
               position += streamRead;
            }
         }

         if (position == -1) {
            if (lengthLeft >= len) {
               return -1;
            }

            position = len - lengthLeft;
         }

         System.arraycopy(buffer, 0, b, off, position);
         return position;
      } else {
         throw new IllegalArgumentException();
      }
   }

   @Override
   public final int available() {
      int available = this._currentInputStream.available();
      int currentPosition = this._currentInputStream.getCurrentPosition();
      ASN1Field currentField = this._field;
      this._currentInputStream.skip(this._currentInputStream.available());

      while (this.updateStreams()) {
         available += this._currentInputStream.available();
         this._currentInputStream.skip(this._currentInputStream.available());
      }

      try {
         this._field = currentField;
         super.setCurrentPosition(this._field.getStartPosition());
         this.updateCurrentInputStream();
         this._currentInputStream.setCurrentPosition(currentPosition);
         return available;
      } catch (ASN1EncodingException e) {
         throw new RuntimeException();
      }
   }

   @Override
   public final long skip(long n) {
      long lengthLeft = n;

      while (lengthLeft > 0 && this.updateStreams()) {
         long skipLen = this._currentInputStream.skip(lengthLeft);
         if (skipLen == 0) {
            break;
         }

         if (skipLen == lengthLeft) {
            lengthLeft = 0;
         } else {
            lengthLeft -= skipLen;
         }
      }

      return n - lengthLeft;
   }

   private final boolean updateStreams() {
      try {
         if (this._currentInputStream.available() == 0 && this._currentInputStream.peek() == -1) {
            int currentPosition = this.getCurrentPosition();
            int fieldLength = this._field.getFieldLength();
            this.setCurrentPosition(currentPosition + fieldLength);
            if (this.peek() == -1) {
               this.setCurrentPosition(currentPosition);
               return false;
            }

            this.setCurrentPosition(this._field.getStartPosition() + fieldLength);
            if ((this.peek() & 31) != 4) {
               return false;
            }

            ASN1Field newField = new ASN1Field(this._field, super.readInputStream());
            this._field = newField;
            this.updateCurrentInputStream();
         }

         return true;
      } catch (ASN1EncodingException var6) {
         return false;
      } finally {
         ;
      }
   }

   private final void updateCurrentInputStream() {
      SharedInputStream fieldInputStream = (SharedInputStream)this._field.getInputStream();
      if ((this._field.getTag() & 32) != 0) {
         this._currentInputStream = new ConstructedOctetStringInputStream(fieldInputStream);
      } else {
         this._currentInputStream = new SharedInputStream(fieldInputStream);
         this._currentInputStream.setLength(this._field.getValueLength());
      }
   }
}
