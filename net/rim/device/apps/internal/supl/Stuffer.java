package net.rim.device.apps.internal.supl;

final class Stuffer {
   byte[] bitString;
   int bitWritePoint;
   int bitsMax;
   boolean isOk;

   Stuffer() {
   }

   Stuffer(byte[] octets, int maxOctets) {
      this.initStuffer(octets, maxOctets);
   }

   final void initStuffer(byte[] octets, int maxOctets) {
      this.bitString = octets;
      this.bitsMax = maxOctets * 8;
      this.bitWritePoint = 0;
      this.isOk = true;

      for (int i = 0; i < maxOctets; i++) {
         octets[i] = 0;
      }
   }

   final void putBits(long bits, int n) {
      if (n != 0) {
         if (this.bitWritePoint + n > this.bitsMax) {
            this.isOk = false;
         } else {
            if (bits >> n != 0 && n != 32) {
            }

            int byteIndex = (this.bitWritePoint + n - 1) / 8;
            int bitShift = 7 - (this.bitWritePoint + n - 1) & 7;
            this.bitString[byteIndex] = (byte)(this.bitString[byteIndex] | (byte)(bits << bitShift));
            if (n + bitShift > 8) {
               this.bitString[byteIndex - 1] = (byte)(this.bitString[byteIndex - 1] | (byte)(bits >> 8 - bitShift));
               if (n + bitShift > 16) {
                  this.bitString[byteIndex - 2] = (byte)(this.bitString[byteIndex - 2] | (byte)(bits >> 16 - bitShift));
                  if (n + bitShift > 24) {
                     this.bitString[byteIndex - 3] = (byte)(this.bitString[byteIndex - 3] | (byte)(bits >> 24 - bitShift));
                     if (n + bitShift > 32) {
                        this.bitString[byteIndex - 4] = (byte)(this.bitString[byteIndex - 4] | (byte)(bits >> 32 - bitShift));
                     }
                  }
               }
            }

            this.bitWritePoint += n;
         }
      }
   }

   final void putBit(boolean bit) {
      if (this.bitWritePoint + 1 > this.bitsMax) {
         this.isOk = false;
      } else {
         if (bit) {
            int byteIndex = this.bitWritePoint / 8;
            int bitShift = 7 - this.bitWritePoint & 7;
            this.bitString[byteIndex] = (byte)(this.bitString[byteIndex] | (byte)(1 << bitShift));
         }

         this.bitWritePoint++;
      }
   }

   final int bitsWrittenCount() {
      return this.bitWritePoint;
   }
}
