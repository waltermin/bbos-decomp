package net.rim.ecmascript.compiler;

public class OperandTypes {
   public static final int OP_GLOBAL_BYTE;
   public static final int OP_GLOBAL;
   public static final int OP_LOCAL;
   public static final int OP_NPARMS;
   public static final int OP_INT;
   public static final int OP_FUNCTION;
   public static final int OP_DOUBLE;
   public static final int OP_STRING;
   public static final int OP_LOCAL_WIDE_BYTE;
   public static final int OP_TEMP_WIDE;
   public static final int OP_BYTE;
   public static final int OP_TEMP;
   public static final int OP_LOCAL_WIDE;
   public static final int OP_LABEL_WIDE;
   public static final int OP_JSR;
   public static final int OP_LOCAL_BYTE;
   public static final int OP_NONE;
   public static final int OP_ID;
   public static final int OP_NPARMS_WIDE;
   public static final int OP_INVALID;
   public static final int OP_STRING_WIDE;
   public static final int OP_INT_INDEX;
   public static final int OP_LABEL;
   public static final int OP_ID_WIDE;
   public static final int OP_GLOBAL_WIDE;
   public static final int OP_GLOBAL_WIDE_BYTE;
   public static final int OP_LINENUM;
   public static final int OP_ID_BYTE;
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
