package net.rim.device.api.util;

public final class CharacterUtilities {
   public static final int BIDI_TYPE_L = 1;
   public static final int BIDI_TYPE_LRE = 2;
   public static final int BIDI_TYPE_LRO = 4;
   public static final int BIDI_TYPE_R = 8;
   public static final int BIDI_TYPE_AL = 16;
   public static final int BIDI_TYPE_RLE = 32;
   public static final int BIDI_TYPE_RLO = 64;
   public static final int BIDI_TYPE_PDF = 128;
   public static final int BIDI_TYPE_EN = 256;
   public static final int BIDI_TYPE_ES = 512;
   public static final int BIDI_TYPE_ET = 1024;
   public static final int BIDI_TYPE_AN = 2048;
   public static final int BIDI_TYPE_CS = 4096;
   public static final int BIDI_TYPE_NSM = 8192;
   public static final int BIDI_TYPE_BN = 16384;
   public static final int BIDI_TYPE_B = 32768;
   public static final int BIDI_TYPE_S = 65536;
   public static final int BIDI_TYPE_WS = 131072;
   public static final int BIDI_TYPE_ON = 262144;
   public static final int BIDI_STRONG_LEFT_TO_RIGHT_TYPE_MASK = 7;
   public static final int BIDI_STRONG_RIGHT_TO_LEFT_TYPE_MASK = 120;
   public static final int BIDI_STRONG_TYPE_MASK = 127;

   private CharacterUtilities() {
   }

   public static final native char foldFullWidth(char var0);

   public static final native char getOriginal(char var0);

   public static final native boolean isLowerCase(char var0);

   public static final native boolean isUpperCase(char var0);

   public static final native boolean isSymbol(char var0);

   public static final native boolean isPunctuation(char var0);

   public static final native boolean isLetter(char var0);

   public static final native boolean isISOControl(char var0);

   public static final native boolean isSpaceChar(char var0);

   public static final native boolean isDigit(char var0);

   public static final native char toLowerCase(char var0);

   public static final native char toUpperCase(char var0);

   public static final native char toLowerCase(char var0, int var1);

   public static final native char toUpperCase(char var0, int var1);

   public static final native boolean isPrintable(char var0);

   public static final native int getBidiType(char var0);

   public static final native boolean isHan(char var0);
}
