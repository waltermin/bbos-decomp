package net.rim.device.api.math;

public final class VecMath {
   private static final int ONE = 65536;
   public static final int[] IDENTITY_3X3 = new int[]{
      65536,
      0,
      0,
      0,
      65536,
      0,
      0,
      0,
      65536,
      -804650983,
      1,
      100,
      101,
      102,
      103,
      104,
      105,
      106,
      107,
      108,
      109,
      126,
      135,
      156,
      157,
      160,
      163,
      164,
      172,
      177,
      189,
      203,
      213,
      225,
      226,
      -804650992
   };
   private static final int[] tempMatrix = new int[9];

   private VecMath() {
   }

   public static final native long multiplyPoint(int[] var0, int var1, int var2, int var3);

   public static final native void transformPoints(int[] var0, int var1, int[] var2, int[] var3, int[] var4, int[] var5);

   public static final native void transformPoints32(int[] var0, int var1, int[] var2, int[] var3, int[] var4, int[] var5);

   public static final native void transformPoints32(int[] var0, int var1, int[] var2, int[] var3, int[] var4, int[] var5, int var6);

   public static final boolean isIdentity(int[] source, int index) {
      return source[index] == 65536
         && source[index + 1] == 0
         && source[index + 2] == 0
         && source[index + 3] == 0
         && source[index + 4] == 65536
         && source[index + 5] == 0
         && source[index + 6] == 0
         && source[index + 7] == 0
         && source[index + 8] == 65536;
   }

   public static final boolean isTranslation(int[] source, int index) {
      return source[index] == 65536
         && source[index + 1] == 0
         && source[index + 3] == 0
         && source[index + 4] == 65536
         && source[index + 6] == 0
         && source[index + 7] == 0
         && source[index + 8] == 65536;
   }

   public static final native void rotateMatrix(int[] var0, int var1, int var2, int var3, int var4, int[] var5);

   public static final native void translate(int[] var0, int var1, int var2, int var3, int[] var4);

   public static final native void scale(int[] var0, int var1, int var2, int var3, int[] var4);

   public static final native void skew(int[] var0, int var1, int var2, int var3, int[] var4);

   public static final native boolean intersects(int var0, int var1, int var2, int var3, int var4, int var5, int var6, int var7);

   public static final void multiply3x3(int[] a, int aStartIndex, int[] b, int bStartIndex) {
      multiply3x3(a, aStartIndex, b, bStartIndex, tempMatrix, 0);
      System.arraycopy(tempMatrix, 0, b, bStartIndex, 9);
   }

   public static final native void multiply3x3(int[] var0, int var1, int[] var2, int var3, int[] var4, int var5);

   public static final native void multiply3x3Affine(int[] var0, int var1, int[] var2, int var3, int[] var4, int var5);

   public static final native void pointMultiply3x3(int[] var0, int var1, int var2, int var3, int[] var4, int var5);

   public static final void copyIdentity3x3(int[] dest, int startIndex) {
      System.arraycopy(IDENTITY_3X3, 0, dest, startIndex, 9);
   }

   public static final void invert2x2Mat(int a, int b, int c, int d, int[] dest, int offset) {
      int determinant = Fixed32.mul(a, d) - Fixed32.mul(b, c);
      if (determinant == 0) {
         dest[offset++] = a;
         dest[offset++] = b;
         dest[offset++] = c;
         dest[offset++] = d;
      } else {
         dest[offset++] = Fixed32.div(d, determinant);
         dest[offset++] = Fixed32.div(-b, determinant);
         dest[offset++] = Fixed32.div(-c, determinant);
         dest[offset++] = Fixed32.div(a, determinant);
      }
   }
}
