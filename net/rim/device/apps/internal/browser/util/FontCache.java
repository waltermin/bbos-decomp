package net.rim.device.apps.internal.browser.util;

import java.util.Hashtable;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.FontFamily;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.util.LongHashtable;
import net.rim.device.api.util.MathUtilities;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.internal.browser.css.CSSString;
import net.rim.vm.Array;

public final class FontCache {
   private Hashtable _families;
   private FontFamily _defaultFontFamily;
   private Hashtable _familyHeights;
   private int _defaultAntialiasMode = 1;
   private LongHashtable _cache;
   private String DEFAULT_SCALABLE_FONT = "BBMillbank";
   private int[] _mappingCacheHash;
   private String[] _mappingCacheKey;
   private String[] _mappingCacheValue;
   private String _defaultScalableFont;
   private static FontCache _instance;
   private static final int FONT_STYLES_MASK;
   private static String DEFAULT_SCALABLE_TURE_TYPE_FONT = "BBAlpha Sans";
   private static final int MAPPING_CACHE_SIZE;
   private static boolean _trueTypeCheckDone = false;
   private static boolean _trueTypeFontAvailable = false;
   private static String ARIAL = "arial";
   private static String SANS_SERIF = "sans-serif";
   private static String TIMES = "times";
   private static String SERIF = "serif";
   private static String VERDANA = "verdana";
   private static String COURIER = "courier";
   private static String MONOSPACE = "monospace";
   private static String CENTURY = "century";
   private static String COMIC = "comic";
   private static String FANTASY = "fantasy";
   private static String BB_SANS_SERIF = "BBSansSerif";
   private static String BB_SERIF = "BBSerif";
   private static String BB_SANS_SERIF_SQUARE = "BBSansSerifSquare";
   private static String BB_CONDENSED_MONOSPACE = "BBCondensedMonospace";
   private static String BB_CAPITALS = "BBCapitals";
   private static String BB_CASUAL = "BBCasual";
   private static String BB_CONDENSED = "BBCondensed";
   private static String SYSTEM = "system";
   private static String BB_ALPHA_SANS = "BBAlpha Sans";
   private static String BB_ALPHA_SERIF = "BBAlpha Serif";

   private FontCache() {
      this._cache = (LongHashtable)(new Object());
      this._families = (Hashtable)(new Object());
      this._familyHeights = (Hashtable)(new Object());
      this._mappingCacheHash = new int[5];
      this._mappingCacheKey = new Object[5];
      this._mappingCacheValue = new Object[5];
      if (isTrueTypeFontAvailable()) {
         this._defaultScalableFont = DEFAULT_SCALABLE_TURE_TYPE_FONT;
      } else {
         this._defaultScalableFont = this.DEFAULT_SCALABLE_FONT;
      }
   }

   public static final FontCache getInstance() {
      if (_instance == null) {
         _instance = new FontCache();
         _instance.resetFonts();
      }

      return _instance;
   }

   public final synchronized Font getFont(Font defaultFont, int style, int height) {
      return this.getFont(defaultFont, style, height, 0, null, null);
   }

   public final synchronized Font getFont(Font defaultFont, int style, int height, String encoding) {
      return this.getFont(defaultFont, style, height, 0, encoding, null);
   }

   public final synchronized Font getFont(Font defaultFont, int style, int height, int units, String encoding, String face) {
      FontFamily fontFamily = this.getFontFamily(defaultFont, encoding, face);
      style = this.normalizeStyle(style);
      height = this.normalizeHeight(fontFamily, height, units);
      long key = (long)fontFamily.hashCode() << 32 | style << 16 | height;
      Font font = (Font)this._cache.get(key);
      if (font == null) {
         font = fontFamily.getFont(style, height, 0, this._defaultAntialiasMode, 0);
         if (font == null) {
            font = defaultFont;
         }

         if (font == null) {
            font = Font.getDefault();
         }

         this._cache.put(key, font);
      }

      return font;
   }

   public final synchronized int getFontIndex(
      Font defaultFont, int style, int height, int units, int color, Font[] fonts, int[] colors, String encoding, String face
   ) {
      FontFamily family = this.getFontFamily(defaultFont, encoding, face);
      style = this.normalizeStyle(style);
      height = this.normalizeHeight(family, height, units);
      long key = (long)family.hashCode() << 32 | style << 16 | height;
      Font font = (Font)this._cache.get(key);
      if (font == null) {
         font = family.getFont(style, height, 0, this._defaultAntialiasMode, 0);
         if (font == null) {
            font = defaultFont;
         }

         if (font == null) {
            font = Font.getDefault();
         }

         this._cache.put(key, font);
      }

      int length = fonts.length;

      for (int index = 0; index < length; index++) {
         if (fonts[index] == font && (colors == null || colors[index] == color)) {
            return index;
         }
      }

      Array.resize(fonts, length + 1);
      fonts[length] = font;
      if (colors != null) {
         Array.resize(colors, length + 1);
         colors[length] = color;
      }

      return length;
   }

   private final Font getDefaultBrowserFont() {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aconst_null
      // 01: astore 1
      // 02: invokestatic net/rim/device/api/ui/Font.getDefault ()Lnet/rim/device/api/ui/Font;
      // 05: astore 2
      // 06: aload 2
      // 07: bipush 2
      // 09: invokevirtual net/rim/device/api/ui/Font.getHeight (I)I
      // 0c: istore 3
      // 0d: aload 2
      // 0e: invokevirtual net/rim/device/api/ui/Font.getFontFamily ()Lnet/rim/device/api/ui/FontFamily;
      // 11: astore 4
      // 13: aload 4
      // 15: invokevirtual net/rim/device/api/ui/FontFamily.getTypefaceType ()I
      // 18: getstatic net/rim/device/api/ui/FontFamily.SCALABLE_FONT I
      // 1b: if_icmpne 20
      // 1e: aload 2
      // 1f: astore 1
      // 20: aload 1
      // 21: ifnonnull 56
      // 24: invokestatic net/rim/device/api/ui/FontRegistry.getInstance ()Lnet/rim/device/api/ui/FontRegistry;
      // 27: aload 0
      // 28: getfield net/rim/device/apps/internal/browser/util/FontCache._defaultScalableFont Ljava/lang/String;
      // 2b: invokevirtual net/rim/device/api/ui/FontRegistry.getTypefaceType (Ljava/lang/String;)I
      // 2e: getstatic net/rim/device/api/ui/FontFamily.UNKNOWN_FONT I
      // 31: if_icmpeq 56
      // 34: aload 0
      // 35: getfield net/rim/device/apps/internal/browser/util/FontCache._defaultScalableFont Ljava/lang/String;
      // 38: invokestatic net/rim/device/api/ui/FontFamily.forName (Ljava/lang/String;)Lnet/rim/device/api/ui/FontFamily;
      // 3b: astore 5
      // 3d: aload 5
      // 3f: ifnull 56
      // 42: aload 5
      // 44: bipush 0
      // 45: iload 3
      // 46: bipush 2
      // 48: invokevirtual net/rim/device/api/ui/FontFamily.getFont (III)Lnet/rim/device/api/ui/Font;
      // 4b: astore 1
      // 4c: goto 56
      // 4f: astore 5
      // 51: goto 56
      // 54: astore 5
      // 56: aload 1
      // 57: ifnonnull 5c
      // 5a: aload 2
      // 5b: astore 1
      // 5c: aload 1
      // 5d: areturn
      // try (19 -> 37): 38 null
      // try (19 -> 37): 40 null
   }

   public final synchronized void resetFonts() {
      this._families.clear();
      this._familyHeights.clear();
      Font defaultFont = this.getDefaultBrowserFont();
      this._defaultFontFamily = defaultFont.getFontFamily();
      String familyName = this._defaultFontFamily.getName();
      this._families.put(familyName, this._defaultFontFamily);
      if (this._defaultFontFamily.getTypefaceType() != FontFamily.SCALABLE_FONT) {
         this._familyHeights.put(familyName, this._defaultFontFamily.getHeights());
      }

      this._defaultAntialiasMode = defaultFont.getAntialiasMode();
      this._cache.clear();

      for (int i = 0; i < 5; i++) {
         this._mappingCacheHash[i] = 0;
         this._mappingCacheKey[i] = null;
         this._mappingCacheValue[i] = null;
      }
   }

   private final synchronized int normalizeHeight(FontFamily family, int height, int units) {
      if (family.getTypefaceType() == FontFamily.SCALABLE_FONT) {
         return MathUtilities.clamp(8, Ui.convertSize(height, units, 0), 72);
      }

      int[] heights = (int[])this._familyHeights.get(StringUtilities.toLowerCase(family.getName(), 1701707776));
      int length = 0;
      int heightInPixels = Ui.convertSize(height, units, 0);
      if (heights == null || (length = heights.length) == 0) {
         return heightInPixels;
      }

      if (length == 1) {
         return heights[0];
      }

      if (heightInPixels <= heights[0]) {
         return heights[0];
      }

      if (heightInPixels >= heights[length - 1]) {
         return heights[length - 1];
      }

      int bestFontHeight = heightInPixels;

      for (int index = 0; index < length; index++) {
         int fontHeight = heights[index];
         if (fontHeight > heightInPixels) {
            break;
         }

         bestFontHeight = fontHeight;
      }

      return bestFontHeight;
   }

   private final int normalizeStyle(int style) {
      return style & 111;
   }

   public final synchronized FontFamily getFontFamily(Font param1, String param2, String param3) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: aload 1
      // 001: ifnull 00d
      // 004: aload 1
      // 005: invokevirtual net/rim/device/api/ui/Font.getFontFamily ()Lnet/rim/device/api/ui/FontFamily;
      // 008: astore 4
      // 00a: goto 013
      // 00d: aload 0
      // 00e: getfield net/rim/device/apps/internal/browser/util/FontCache._defaultFontFamily Lnet/rim/device/api/ui/FontFamily;
      // 011: astore 4
      // 013: aload 2
      // 014: ifnonnull 01e
      // 017: aload 3
      // 018: ifnonnull 01e
      // 01b: aload 4
      // 01d: areturn
      // 01e: aload 2
      // 01f: ifnull 048
      // 022: aload 3
      // 023: ifnonnull 048
      // 026: aload 2
      // 027: invokestatic com/sun/cldc/i18n/Helper.getSuggestedTypeface (Ljava/lang/String;)Ljava/lang/String;
      // 02a: astore 3
      // 02b: aload 3
      // 02c: getstatic net/rim/device/apps/internal/browser/util/FontCache.SYSTEM Ljava/lang/String;
      // 02f: ldc_w 1701707776
      // 032: invokestatic net/rim/device/api/util/StringUtilities.strEqualIgnoreCase (Ljava/lang/String;Ljava/lang/String;I)Z
      // 035: ifne 045
      // 038: aload 3
      // 039: getstatic net/rim/device/apps/internal/browser/util/FontCache.BB_CONDENSED Ljava/lang/String;
      // 03c: ldc_w 1701707776
      // 03f: invokestatic net/rim/device/api/util/StringUtilities.strEqualIgnoreCase (Ljava/lang/String;Ljava/lang/String;I)Z
      // 042: ifeq 048
      // 045: aload 4
      // 047: areturn
      // 048: aload 3
      // 049: bipush 44
      // 04b: invokevirtual java/lang/String.indexOf (I)I
      // 04e: bipush -1
      // 050: if_icmpne 083
      // 053: aload 0
      // 054: getfield net/rim/device/apps/internal/browser/util/FontCache._families Ljava/util/Hashtable;
      // 057: aload 3
      // 058: invokevirtual java/util/Hashtable.get (Ljava/lang/Object;)Ljava/lang/Object;
      // 05b: astore 5
      // 05d: aload 5
      // 05f: ifnull 068
      // 062: aload 5
      // 064: checkcast java/lang/Object
      // 067: areturn
      // 068: aload 0
      // 069: getfield net/rim/device/apps/internal/browser/util/FontCache._families Ljava/util/Hashtable;
      // 06c: aload 3
      // 06d: ldc_w 1701707776
      // 070: invokestatic net/rim/device/api/util/StringUtilities.toLowerCase (Ljava/lang/String;I)Ljava/lang/String;
      // 073: invokevirtual java/util/Hashtable.get (Ljava/lang/Object;)Ljava/lang/Object;
      // 076: astore 5
      // 078: aload 5
      // 07a: ifnull 0a0
      // 07d: aload 5
      // 07f: checkcast java/lang/Object
      // 082: areturn
      // 083: aload 0
      // 084: aload 3
      // 085: invokespecial net/rim/device/apps/internal/browser/util/FontCache.getMapping (Ljava/lang/String;)Ljava/lang/String;
      // 088: astore 5
      // 08a: aload 0
      // 08b: getfield net/rim/device/apps/internal/browser/util/FontCache._families Ljava/util/Hashtable;
      // 08e: aload 5
      // 090: invokevirtual java/util/Hashtable.get (Ljava/lang/Object;)Ljava/lang/Object;
      // 093: astore 6
      // 095: aload 6
      // 097: ifnull 0a0
      // 09a: aload 6
      // 09c: checkcast java/lang/Object
      // 09f: areturn
      // 0a0: new java/lang/Object
      // 0a3: dup
      // 0a4: aload 3
      // 0a5: bipush 44
      // 0a7: invokespecial net/rim/device/api/util/StringTokenizer.<init> (Ljava/lang/String;C)V
      // 0aa: astore 5
      // 0ac: aload 5
      // 0ae: invokevirtual net/rim/device/api/util/StringTokenizer.hasMoreTokens ()Z
      // 0b1: ifne 0b7
      // 0b4: goto 19b
      // 0b7: aload 5
      // 0b9: invokevirtual net/rim/device/api/util/StringTokenizer.nextToken ()Ljava/lang/String;
      // 0bc: astore 6
      // 0be: aload 6
      // 0c0: ifnull 0ac
      // 0c3: aload 0
      // 0c4: getfield net/rim/device/apps/internal/browser/util/FontCache._families Ljava/util/Hashtable;
      // 0c7: aload 6
      // 0c9: invokevirtual java/util/Hashtable.get (Ljava/lang/Object;)Ljava/lang/Object;
      // 0cc: astore 7
      // 0ce: aload 7
      // 0d0: ifnull 0d9
      // 0d3: aload 7
      // 0d5: checkcast java/lang/Object
      // 0d8: areturn
      // 0d9: aload 6
      // 0db: ldc_w 1701707776
      // 0de: invokestatic net/rim/device/api/util/StringUtilities.toLowerCase (Ljava/lang/String;I)Ljava/lang/String;
      // 0e1: astore 8
      // 0e3: aload 0
      // 0e4: getfield net/rim/device/apps/internal/browser/util/FontCache._families Ljava/util/Hashtable;
      // 0e7: aload 8
      // 0e9: invokevirtual java/util/Hashtable.get (Ljava/lang/Object;)Ljava/lang/Object;
      // 0ec: astore 7
      // 0ee: aload 7
      // 0f0: ifnull 0f9
      // 0f3: aload 7
      // 0f5: checkcast java/lang/Object
      // 0f8: areturn
      // 0f9: aload 0
      // 0fa: aload 6
      // 0fc: invokespecial net/rim/device/apps/internal/browser/util/FontCache.getMapping (Ljava/lang/String;)Ljava/lang/String;
      // 0ff: astore 9
      // 101: aload 0
      // 102: getfield net/rim/device/apps/internal/browser/util/FontCache._families Ljava/util/Hashtable;
      // 105: aload 9
      // 107: invokevirtual java/util/Hashtable.get (Ljava/lang/Object;)Ljava/lang/Object;
      // 10a: astore 7
      // 10c: aload 9
      // 10e: ldc_w 1701707776
      // 111: invokestatic net/rim/device/api/util/StringUtilities.toLowerCase (Ljava/lang/String;I)Ljava/lang/String;
      // 114: astore 10
      // 116: aload 7
      // 118: ifnull 12d
      // 11b: aload 0
      // 11c: getfield net/rim/device/apps/internal/browser/util/FontCache._families Ljava/util/Hashtable;
      // 11f: aload 8
      // 121: aload 7
      // 123: invokevirtual java/util/Hashtable.put (Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
      // 126: pop
      // 127: aload 7
      // 129: checkcast java/lang/Object
      // 12c: areturn
      // 12d: aconst_null
      // 12e: astore 11
      // 130: invokestatic net/rim/device/api/ui/FontRegistry.getInstance ()Lnet/rim/device/api/ui/FontRegistry;
      // 133: aload 9
      // 135: invokevirtual net/rim/device/api/ui/FontRegistry.getTypefaceType (Ljava/lang/String;)I
      // 138: getstatic net/rim/device/api/ui/FontFamily.UNKNOWN_FONT I
      // 13b: if_icmpeq 145
      // 13e: aload 9
      // 140: invokestatic net/rim/device/api/ui/FontFamily.forName (Ljava/lang/String;)Lnet/rim/device/api/ui/FontFamily;
      // 143: astore 11
      // 145: aload 11
      // 147: ifnonnull 14d
      // 14a: goto 0ac
      // 14d: aload 0
      // 14e: getfield net/rim/device/apps/internal/browser/util/FontCache._families Ljava/util/Hashtable;
      // 151: aload 8
      // 153: aload 11
      // 155: invokevirtual java/util/Hashtable.put (Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
      // 158: pop
      // 159: aload 0
      // 15a: getfield net/rim/device/apps/internal/browser/util/FontCache._families Ljava/util/Hashtable;
      // 15d: aload 11
      // 15f: invokevirtual net/rim/device/api/ui/FontFamily.getName ()Ljava/lang/String;
      // 162: aload 11
      // 164: invokevirtual java/util/Hashtable.put (Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
      // 167: pop
      // 168: aload 0
      // 169: getfield net/rim/device/apps/internal/browser/util/FontCache._families Ljava/util/Hashtable;
      // 16c: aload 9
      // 16e: aload 11
      // 170: invokevirtual java/util/Hashtable.put (Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
      // 173: pop
      // 174: aload 11
      // 176: invokevirtual net/rim/device/api/ui/FontFamily.getTypefaceType ()I
      // 179: getstatic net/rim/device/api/ui/FontFamily.SCALABLE_FONT I
      // 17c: if_icmpeq 18e
      // 17f: aload 0
      // 180: getfield net/rim/device/apps/internal/browser/util/FontCache._familyHeights Ljava/util/Hashtable;
      // 183: aload 10
      // 185: aload 11
      // 187: invokevirtual net/rim/device/api/ui/FontFamily.getHeights ()[I
      // 18a: invokevirtual java/util/Hashtable.put (Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
      // 18d: pop
      // 18e: aload 11
      // 190: areturn
      // 191: astore 11
      // 193: goto 0ac
      // 196: astore 11
      // 198: goto 0ac
      // 19b: aload 0
      // 19c: getfield net/rim/device/apps/internal/browser/util/FontCache._families Ljava/util/Hashtable;
      // 19f: aload 3
      // 1a0: ldc_w 1701707776
      // 1a3: invokestatic net/rim/device/api/util/StringUtilities.toLowerCase (Ljava/lang/String;I)Ljava/lang/String;
      // 1a6: aload 4
      // 1a8: invokevirtual java/util/Hashtable.put (Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
      // 1ab: pop
      // 1ac: aload 4
      // 1ae: areturn
      // try (138 -> 182): 183 null
      // try (138 -> 182): 185 null
   }

   private final String getMapping(String face) {
      face = face.trim();
      int hashCode = StringUtilities.hashCodeIgnoreCase(face);

      for (int i = 0; i < 5; i++) {
         if (this._mappingCacheHash[i] == hashCode && StringUtilities.strEqualIgnoreCase(this._mappingCacheKey[i], face, 1701707776)) {
            this.moveToFront(i);
            return this._mappingCacheValue[0];
         }
      }

      String result = getFontMapping(face);
      if (result == null) {
         result = face;
      }

      this._mappingCacheValue[4] = result;
      this._mappingCacheHash[4] = hashCode;
      this._mappingCacheKey[4] = face;
      this.moveToFront(4);
      return result;
   }

   public final String getDefaultFontFamily() {
      return this._defaultScalableFont;
   }

   public static final boolean isTrueTypeFontAvailable() {
      if (_trueTypeCheckDone) {
         return _trueTypeFontAvailable;
      }

      try {
         _trueTypeCheckDone = true;
         FontFamily family = FontFamily.forName(DEFAULT_SCALABLE_TURE_TYPE_FONT);
         if (family != null) {
            _trueTypeFontAvailable = true;
            return true;
         } else {
            return false;
         }
      } finally {
         ;
      }
   }

   public static final String getFontMapping(int cssFace) {
      switch (cssFace) {
         case 42:
            return BB_CASUAL;
         case 90:
            return BB_CONDENSED_MONOSPACE;
         case 123:
            if (isTrueTypeFontAvailable()) {
               return BB_ALPHA_SANS;
            }

            return BB_SANS_SERIF;
         case 127:
            if (isTrueTypeFontAvailable()) {
               return BB_ALPHA_SERIF;
            }

            return BB_SERIF;
         default:
            return null;
      }
   }

   public static final String getFontMapping(Object face) {
      int length = 0;
      char char0 = 0;
      boolean isCSS = false;
      if (face instanceof CSSString) {
         isCSS = true;
         CSSString str = (CSSString)face;
         length = str.getLength();
         if (length > 0) {
            char0 = str.charAt(0);
         }
      } else {
         String str = (String)face;
         length = str.length();
         if (length > 0) {
            char0 = str.charAt(0);
         }
      }

      switch (char0) {
         case 'A':
         case 'a':
            if (length >= 5 && startsWith(face, ARIAL, isCSS)) {
               if (isTrueTypeFontAvailable()) {
                  return BB_ALPHA_SANS;
               }

               return BB_SANS_SERIF;
            }
         case 'T':
         case 't':
            if (length >= 5 && startsWith(face, TIMES, isCSS)) {
               if (isTrueTypeFontAvailable()) {
                  return BB_ALPHA_SERIF;
               }

               return BB_SERIF;
            }
         case 'V':
         case 'v':
            if (length >= 7 && startsWith(face, VERDANA, isCSS)) {
               if (isTrueTypeFontAvailable()) {
                  return BB_ALPHA_SANS;
               }

               return BB_SANS_SERIF_SQUARE;
            }
         case 'F':
         case 'f':
            if (length >= 7 && startsWith(face, FANTASY, isCSS)) {
               return BB_CASUAL;
            }
         case 'M':
         case 'm':
            if (length >= 9 && startsWith(face, MONOSPACE, isCSS)) {
               return BB_CONDENSED_MONOSPACE;
            }
         case 'S':
         case 's':
            if (length >= 5) {
               if (startsWith(face, SERIF, isCSS)) {
                  if (isTrueTypeFontAvailable()) {
                     return BB_ALPHA_SERIF;
                  }

                  return BB_SERIF;
               } else if (startsWith(face, SANS_SERIF, isCSS)) {
                  if (isTrueTypeFontAvailable()) {
                     return BB_ALPHA_SANS;
                  }

                  return BB_SANS_SERIF;
               }
            }
         case 'C':
         case 'c':
            if (length >= 5) {
               if (startsWith(face, COURIER, isCSS)) {
                  return BB_CONDENSED_MONOSPACE;
               } else if (startsWith(face, CENTURY, isCSS)) {
                  return BB_CAPITALS;
               } else if (startsWith(face, COMIC, isCSS)) {
                  return BB_CASUAL;
               }
            }
         default:
            return null;
      }
   }

   private static final boolean startsWith(Object obj, String str, boolean isCSSStr) {
      if (isCSSStr) {
         return ((CSSString)obj).startsWith(str, true);
      }

      String thisStr = (String)obj;
      return thisStr.length() >= str.length() && StringUtilities.regionMatches(thisStr, true, 0, str, 0, str.length(), 1701707776);
   }

   private final void moveToFront(int index) {
      if (index != 0) {
         int hash = this._mappingCacheHash[index];
         String key = this._mappingCacheKey[index];
         String value = this._mappingCacheValue[index];

         for (int i = index; i > 0; i--) {
            this._mappingCacheHash[i] = this._mappingCacheHash[i - 1];
            this._mappingCacheKey[i] = this._mappingCacheKey[i - 1];
            this._mappingCacheValue[i] = this._mappingCacheValue[i - 1];
         }

         this._mappingCacheHash[0] = hash;
         this._mappingCacheKey[0] = key;
         this._mappingCacheValue[0] = value;
      }
   }
}
