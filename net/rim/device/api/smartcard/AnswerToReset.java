package net.rim.device.api.smartcard;

import net.rim.device.api.util.Arrays;
import net.rim.vm.Persistable;

public class AnswerToReset implements Persistable {
   private byte[] _atr;
   private int _historicalBytesOffset;
   private int _protocolBytesLength;
   private int _numHistBytes;
   private int _ifs = 32;
   private int _bwt = 0;
   private int _protocol = 1;
   private int _supportedProtocols = 0;
   private int _edcType = 1;
   private byte _baudAndClockRate = 1;
   private static int MAX_ATR_BYTE_LENGTH = 33;
   private static final int[][] CLOCK_RATE_CONVERSION_FACTOR = new int[][]{
      {372, 4, -804651006, 372, 5, -805044223, 130, -804651006},
      {372, 5, -805044223, 130, -804651006, 1488, 16, -804651006},
      {558, 6, -804651005, 51, 51, 5526098, -804651006, 1116},
      {744, 8, 1870004480, 290219371, -1258225653, 524485, 521873416, 1637884780},
      {1116, 12, -804651006, 372, 4, -804651006, 372, 5},
      {1488, 16, -804651006, 744, 8, 1870004480, 290219371, -1258225653},
      {0, 0, -804650992, 0, 1, 2, 4, 8},
      {0, 0, -804650992, 0, 1, 2, 4, 8},
      {512, 5, -804651006, 768, 7, -804651006, 1024, 10},
      {768, 7, -804651006, 1024, 10, -804651006, 1536, 15},
      {1024, 10, -804651006, 1536, 15, -804651006, 2048, 20},
      {1536, 15, -804651006, 2048, 20, -805044213, 775162112, 774909491},
      {2048, 20, -805044213, 775162112, 774909491, 3420721, -805044199, 1699878656},
      {0, 0, -804650992, 0, 1, 2, 4, 8},
      {0, 0, -804650992, 0, 1, 2, 4, 8}
   };
   private static final int[] BAUD_RATE_CONVERSION_FACTOR = new int[]{
      0,
      1,
      2,
      4,
      8,
      16,
      32,
      0,
      12,
      20,
      0,
      0,
      0,
      0,
      0,
      0,
      -804651006,
      512,
      5,
      -804651006,
      768,
      7,
      -804651006,
      1024,
      10,
      -804651006,
      1536,
      15,
      -804651006,
      2048,
      20,
      -805044213,
      775162112,
      774909491,
      3420721,
      -805044199,
      1699878656,
      1918985587,
      1226860643,
      1867325550,
      1852795252,
      1685343264,
      46,
      -804651006,
      558,
      6,
      -804651005,
      51,
      51,
      5526098,
      -804651006,
      1116,
      12,
      -804651006,
      372,
      4,
      -804651006,
      372,
      5,
      -805044223,
      130,
      -804651006,
      1488,
      16
   };
   public static final int PROTOCOL_T0 = 1;
   public static final int PROTOCOL_T1 = 2;
   public static final int EDC_LRC = 1;
   public static final int EDC_CRC = 2;

   public byte[] getBytes() {
      return Arrays.copy(this._atr);
   }

   public AnswerToReset(byte[] atr) {
      if (atr == null || atr.length > MAX_ATR_BYTE_LENGTH || atr.length < 2) {
         throw new IllegalArgumentException(" The ATR is null or has an incorrect length ");
      }

      if (atr[0] != 59 && atr[0] != 63) {
         throw new IllegalArgumentException(" The TS byte of the ATR was not 0x3b or 0x3f ");
      }

      this._protocolBytesLength = 1;
      int tdOffset = 1;
      int nextTdOffset = 1;
      int round = 1;
      boolean gotIFS = false;
      boolean t1Set = false;
      boolean t0Set = false;
      boolean gotBWI = false;
      boolean gotEDC = false;
      int fi = 372;
      int f = 4;
      int di = 1;
      float etu = (float)false;
      int bwi = 4;
      boolean tdPresent = false;

      do {
         if ((atr[tdOffset] & 16) != 0) {
            nextTdOffset++;
            this._protocolBytesLength++;
            if (round == 1) {
               int indexF = atr[nextTdOffset] >>> 4 & 15;
               int indexD = atr[nextTdOffset] & 15;
               this._baudAndClockRate = atr[nextTdOffset];
               fi = CLOCK_RATE_CONVERSION_FACTOR[indexF][0];
               f = CLOCK_RATE_CONVERSION_FACTOR[indexF][1];
               di = BAUD_RATE_CONVERSION_FACTOR[indexD];
               if (di == 0) {
                  throw new IllegalArgumentException(" Error calculating the baud rate");
               }

               if (f == 0 || fi == 0) {
                  throw new IllegalArgumentException(" Error calculating the clock rate");
               }

               etu = fi / di * (1 / f);
               int temp1 = (int)(1093664768 * etu);
               int temp2 = (int)((long)(multBy2(bwi) * 960) * 372 / f);
               this._bwt = temp1 + temp2;
            }

            if (round == 2) {
               this._protocol = atr[nextTdOffset] & 15;
            }

            if (round > 2 && t1Set && !gotIFS) {
               this._ifs = atr[nextTdOffset] & 255;
               gotIFS = true;
            }
         }

         if ((atr[tdOffset] & 32) != 0) {
            nextTdOffset++;
            this._protocolBytesLength++;
            if (round > 2 && t1Set && !gotBWI) {
               bwi = atr[nextTdOffset] >> 4 & 15;
               int temp1 = (int)(1093664768 * etu);
               int temp2 = (int)((long)(multBy2(bwi) * 960) * 372 / f);
               this._bwt = temp1 + temp2;
               gotBWI = true;
            }
         }

         if ((atr[tdOffset] & 64) != 0) {
            nextTdOffset++;
            this._protocolBytesLength++;
            if (round > 2 && t1Set && !gotEDC) {
               this._edcType = (atr[nextTdOffset] & 1) == 1 ? 2 : 1;
               gotEDC = true;
            }
         }

         if ((atr[tdOffset] & 128) != 0) {
            tdOffset = ++nextTdOffset;
            tdPresent = true;
            this._protocolBytesLength++;
            if ((atr[tdOffset] & 15) == 1 && !t1Set && !t0Set) {
               t1Set = true;
               this._protocol = 2;
            } else if ((atr[tdOffset] & 15) == 0 && !t1Set) {
               t0Set = true;
               this._protocol = 1;
            }

            if ((atr[tdOffset] & 15) == 1) {
               this._supportedProtocols |= 2;
            }

            if ((atr[tdOffset] & 15) == 0) {
               this._supportedProtocols |= 1;
            }

            round++;
         } else {
            tdPresent = false;
         }
      } while (tdPresent);

      if (this._supportedProtocols == 0) {
         this._supportedProtocols = 1;
      }

      this._historicalBytesOffset = this._protocolBytesLength + 1;
      this._numHistBytes = atr[1] & 15;
      if (this._protocol == 2) {
         if (atr.length != 1 + this._protocolBytesLength + this._numHistBytes + 1) {
            throw new IllegalArgumentException("The ATR provided has an incorrect length");
         }

         byte tck = atr[1 + this._protocolBytesLength + this._numHistBytes];

         for (int i = atr.length - 2; i > 0; i--) {
            tck ^= atr[i];
         }

         if (tck != 0) {
            throw new IllegalArgumentException("TCK ATR check is invalid");
         }
      } else if (atr.length != 1 + this._protocolBytesLength + this._numHistBytes && atr.length != 1 + this._protocolBytesLength + this._numHistBytes + 1) {
         throw new IllegalArgumentException("The ATR provided has an incorrect length");
      }

      this._atr = atr;
   }

   public byte[] getProtocolBytes() {
      if (this._atr != null && this._atr.length >= 2 && this._protocolBytesLength > 0) {
         byte[] data = new byte[this._protocolBytesLength];
         System.arraycopy(this._atr, 1, data, 0, data.length);
         return data;
      } else {
         return null;
      }
   }

   public byte[] getHistoricalBytes() {
      return this._atr != null && this._atr.length >= 2 && this._numHistBytes > 0
         ? Arrays.copy(this._atr, this._historicalBytesOffset, this._numHistBytes)
         : null;
   }

   @Override
   public boolean equals(Object other) {
      if (this == other) {
         return true;
      }

      if (!(other instanceof AnswerToReset)) {
         return false;
      }

      AnswerToReset atr = (AnswerToReset)other;
      return Arrays.equals(atr._atr, this._atr);
   }

   @Override
   public String toString() {
      StringBuffer data = new StringBuffer();
      if (this._atr != null) {
         int length = this._atr.length;

         for (int i = 0; i < length; i++) {
            data.append(Integer.toHexString(this._atr[i] & 255));
            if (i != length - 1) {
               data.append(' ');
            }
         }
      }

      return data.toString();
   }

   public int getIFS() {
      return this._ifs;
   }

   public int getBWT() {
      return this._bwt;
   }

   public int getProtocol() {
      return this._protocol;
   }

   public int getSupportedProtocols() {
      return this._supportedProtocols;
   }

   public int getEdcType() {
      return this._edcType;
   }

   public byte getBaudAndClockRate() {
      return this._baudAndClockRate;
   }

   private static int multBy2(int a) {
      int multOf2 = 2;

      for (int i = 1; i < a; i++) {
         multOf2 *= 2;
      }

      return multOf2;
   }
}
