package net.rim.ecmascript.compiler;

public class OperandTypes {
   public static final int OP_GLOBAL_BYTE = 0;
   public static final int OP_GLOBAL = 1;
   public static final int OP_LOCAL = 2;
   public static final int OP_NPARMS = 3;
   public static final int OP_INT = 4;
   public static final int OP_FUNCTION = 5;
   public static final int OP_DOUBLE = 6;
   public static final int OP_STRING = 7;
   public static final int OP_LOCAL_WIDE_BYTE = 8;
   public static final int OP_TEMP_WIDE = 9;
   public static final int OP_BYTE = 10;
   public static final int OP_TEMP = 11;
   public static final int OP_LOCAL_WIDE = 12;
   public static final int OP_LABEL_WIDE = 13;
   public static final int OP_JSR = 14;
   public static final int OP_LOCAL_BYTE = 15;
   public static final int OP_NONE = 16;
   public static final int OP_ID = 17;
   public static final int OP_NPARMS_WIDE = 18;
   public static final int OP_INVALID = 19;
   public static final int OP_STRING_WIDE = 20;
   public static final int OP_INT_INDEX = 21;
   public static final int OP_LABEL = 22;
   public static final int OP_ID_WIDE = 23;
   public static final int OP_GLOBAL_WIDE = 24;
   public static final int OP_GLOBAL_WIDE_BYTE = 25;
   public static final int OP_LINENUM = 26;
   public static final int OP_ID_BYTE = 27;
   private static final byte[] _table = new byte[]{
      16,
      16,
      16,
      16,
      16,
      18,
      3,
      18,
      3,
      18,
      19,
      16,
      16,
      16,
      16,
      16,
      16,
      16,
      16,
      16,
      16,
      23,
      23,
      16,
      16,
      16,
      16,
      16,
      16,
      16,
      16,
      16,
      16,
      16,
      17,
      23,
      23,
      2,
      12,
      1,
      24,
      23,
      2,
      12,
      1,
      24,
      11,
      9,
      13,
      22,
      13,
      22,
      13,
      22,
      13,
      22,
      13,
      22,
      13,
      22,
      13,
      22,
      13,
      22,
      13,
      22,
      13,
      22,
      13,
      22,
      13,
      22,
      13,
      22,
      13,
      22,
      16,
      16,
      27,
      15,
      8,
      0,
      25,
      16,
      13,
      22,
      14,
      16,
      16,
      16,
      16,
      5,
      16,
      16,
      16,
      16,
      6,
      21,
      4,
      10,
      16,
      16,
      16,
      16,
      7,
      20,
      16,
      16,
      16,
      16,
      13,
      16,
      16,
      16,
      17,
      23,
      23,
      23,
      2,
      12,
      1,
      24,
      11,
      9,
      9,
      16,
      16,
      16,
      11,
      16,
      16,
      16,
      16,
      16,
      16,
      16,
      23,
      16,
      16,
      26,
      16,
      19,
      19,
      3,
      18,
      0
   };

   public static int get(int op) {
      return _table[op & 0xFF];
   }
}
