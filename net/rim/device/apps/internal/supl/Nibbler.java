package net.rim.device.apps.internal.supl;

public final class Nibbler {
   byte[] bitString;
   int bitReadPoint;
   int bitsHave;
   boolean isOk;

   Nibbler() {
   }

   public final void fillBitString(byte[] octets, int numOctets) {
      if (numOctets <= 0) {
         numOctets = 0;
      }

      this.bitString = octets;
      this.bitsHave = numOctets * 8;
      this.bitReadPoint = 0;
      this.isOk = true;
   }

   public final boolean getBit() {
      if (this.bitReadPoint + 1 > this.bitsHave) {
         this.bitReadPoint = this.bitsHave;
         this.isOk = false;
         return false;
      } else {
         int byteIndex = this.bitReadPoint / 8;
         int bitShift = 7 - this.bitReadPoint & 7;
         int retVal = this.bitString[byteIndex] >> bitShift;
         this.bitReadPoint++;
         return (retVal & 1) == 1;
      }
   }

   public final byte getByte() {
      return (byte)this.getBitsLarge(8);
   }

   public final int getBitsLarge(int n) {
      if (n == 0) {
         return 0;
      }

      if (this.bitReadPoint + n > this.bitsHave) {
         this.bitReadPoint = this.bitsHave;
         this.isOk = false;
         return 0;
      }

      int byteIndex = (this.bitReadPoint + n - 1) / 8;
      int bitShift = 7 - (this.bitReadPoint + n - 1) & 7;
      long retVal = (255 & this.bitString[byteIndex]) >> bitShift;
      if (n + bitShift > 8) {
         retVal |= (255 & this.bitString[byteIndex - 1]) << 8 - bitShift;
         if (n + bitShift > 16) {
            retVal |= (255 & this.bitString[byteIndex - 2]) << 16 - bitShift;
            if (n + bitShift > 24) {
               retVal |= (255 & this.bitString[byteIndex - 3]) << 24 - bitShift;
               if (n + bitShift > 32) {
                  retVal |= (255 & this.bitString[byteIndex - 4]) << 32 - bitShift;
               }
            }
         }
      }

      if (n < 32) {
         retVal &= (1 << n) - 1;
      }

      this.bitReadPoint += n;
      return (int)retVal;
   }

   public static final boolean IsBitSet(byte var, byte mask) {
      return (var & mask) == mask;
   }
}
