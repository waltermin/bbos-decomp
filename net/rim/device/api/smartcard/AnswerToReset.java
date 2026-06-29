package net.rim.device.api.smartcard;

import net.rim.device.api.util.Arrays;
import net.rim.vm.Persistable;

public class AnswerToReset implements Persistable {
   private byte[] _atr;
   private int _historicalBytesOffset;
   private int _protocolBytesLength;
   private int _numHistBytes;
   private int _ifs;
   private int _bwt;
   private int _protocol;
   private int _supportedProtocols;
   private int _edcType;
   private byte _baudAndClockRate;
   private static int MAX_ATR_BYTE_LENGTH = 33;
   private static final int[][][] CLOCK_RATE_CONVERSION_FACTOR = new int[][][]{
      (int[][])({372, 4, -804651006, 372, 5, -805044223, 130, -804651006}),
      (int[][])({372, 5, -805044223, 130, -804651006, 1488, 16, -804651006}),
      (int[][])({558, 6, -804651005, 51, 51, 5526098, -804651006, 1116}),
      (int[][])({744, 8, 1870004480, 290219371, -1258225653, 524485, 521873416, 1637884780}),
      (int[][])({1116, 12, -804651006, 372, 4, -804651006, 372, 5}),
      (int[][])({1488, 16, -804651006, 744, 8, 1870004480, 290219371, -1258225653}),
      (int[][])({0, 0, -804650992, 0, 1, 2, 4, 8}),
      (int[][])({0, 0, -804650992, 0, 1, 2, 4, 8}),
      (int[][])({512, 5, -804651006, 768, 7, -804651006, 1024, 10}),
      (int[][])({768, 7, -804651006, 1024, 10, -804651006, 1536, 15}),
      (int[][])({1024, 10, -804651006, 1536, 15, -804651006, 2048, 20}),
      (int[][])({1536, 15, -804651006, 2048, 20, -805044213, 775162112, 774909491}),
      (int[][])({2048, 20, -805044213, 775162112, 774909491, 3420721, -805044199, 1699878656}),
      (int[][])({0, 0, -804650992, 0, 1, 2, 4, 8}),
      (int[][])({0, 0, -804650992, 0, 1, 2, 4, 8})
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
      StringBuffer data = (StringBuffer)(new Object());
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
