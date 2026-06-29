package net.rim.device.api.util;

public final class CharacterUtilities {
   public static final int BIDI_TYPE_L;
   public static final int BIDI_TYPE_LRE;
   public static final int BIDI_TYPE_LRO;
   public static final int BIDI_TYPE_R;
   public static final int BIDI_TYPE_AL;
   public static final int BIDI_TYPE_RLE;
   public static final int BIDI_TYPE_RLO;
   public static final int BIDI_TYPE_PDF;
   public static final int BIDI_TYPE_EN;
   public static final int BIDI_TYPE_ES;
   public static final int BIDI_TYPE_ET;
   public static final int BIDI_TYPE_AN;
   public static final int BIDI_TYPE_CS;
   public static final int BIDI_TYPE_NSM;
   public static final int BIDI_TYPE_BN;
   public static final int BIDI_TYPE_B;
   public static final int BIDI_TYPE_S;
   public static final int BIDI_TYPE_WS;
   public static final int BIDI_TYPE_ON;
   public static final int BIDI_STRONG_LEFT_TO_RIGHT_TYPE_MASK;
   public static final int BIDI_STRONG_RIGHT_TO_LEFT_TYPE_MASK;
   public static final int BIDI_STRONG_TYPE_MASK;

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
