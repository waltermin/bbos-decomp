package net.rim.device.internal.system;

public final class LEDUtilities implements LEDConstants {
   private LEDUtilities() {
   }

   static final int getFlagColour(int bitNumber) {
      return bitNumber > LEDConstants.flagColourMap.length - 1 ? 0 : LEDConstants.flagColourMap[bitNumber];
   }

   static final int countSetBits(int flag) {
      int numSetBits = 0;

      for (int i = 32; i >= 0; i--) {
         if ((flag & 1) == 1) {
            numSetBits++;
         }

         flag >>= 1;
      }

      return numSetBits;
   }

   static final int stateToColours(int flag) {
      if ((flag & 64) != 0) {
         return 4;
      }

      switch (flag) {
         case 15:
         case 32:
         case 33:
         case 34:
         case 35:
         case 36:
         case 37:
         case 38:
         case 39:
         case 40:
         case 41:
         case 42:
         case 43:
         case 44:
         case 45:
         case 46:
         case 47:
            return 0;
         case 16:
            return 2;
         case 17:
            return 6;
         case 18:
            return 3;
         case 19:
            return 7;
         case 20:
            return 0;
         case 21:
            return 4;
         case 22:
            return 1;
         case 23:
            return 5;
         case 24:
            return 2;
         case 25:
            return 6;
         case 26:
            return 3;
         case 27:
            return 7;
         case 28:
            return 8;
         case 29:
            return 4;
         case 30:
            return 1;
         case 31:
         default:
            return 5;
         case 48:
         case 49:
         case 56:
         case 57:
            return 6;
         case 50:
         case 51:
         case 58:
         case 59:
            return 7;
         case 52:
         case 53:
         case 61:
            return 4;
         case 54:
         case 55:
         case 62:
         case 63:
            return 5;
         case 60:
            return 12;
      }
   }
}
