package net.rim.tid.text;

import com.sun.cldc.i18n.j2me.TextProcessingRegistry;
import net.rim.device.api.i18n.Locale;
import net.rim.device.internal.ui.StringBufferGap;

public final class BreakIterator {
   private byte[][][] _wordlistData = (byte[][][])((byte[][])null);
   private Locale _locale;
   private String _str;
   private StringBuffer _strB;
   private StringBufferGap _strBG;
   private char[] _charArr;
   private int _dataType = -1;
   private int _iterType = -1;
   private int _currentPos = 0;
   private static final int UNDEFINED = -1;
   private static final int STR = 0;
   private static final int STRB = 1;
   private static final int STRBG = 2;
   private static final int CHARARR = 3;
   public static final int ECharacter = 0;
   public static final int EWord = 1;
   public static final int ELine = 2;
   public static final int DONE = Integer.MAX_VALUE;

   public static final BreakIterator getInstance(int aType, Locale aLocale) {
      return new BreakIterator(aType, aLocale);
   }

   public static final BreakIterator getInstance(int aType) {
      return getInstance(aType, null);
   }

   private BreakIterator(int aType, Locale aLocale) {
      this._iterType = aType;
      TextProcessingRegistry tpr = TextProcessingRegistry.getInstance();
      this._locale = aLocale != null ? aLocale : Locale.getDefault();
      int localeCode = this._locale.getCode();
      int breakingDataType = 2;
      switch (this._iterType) {
         case -1:
            break;
         case 0:
         default:
            breakingDataType = 4;
            break;
         case 1:
            breakingDataType = 5;
            break;
         case 2:
            breakingDataType = 2;
      }

      int dataID = tpr.getTextProcessingDataID(localeCode, breakingDataType);
      this._wordlistData = (byte[][][])tpr.getTextProcessingData(dataID, breakingDataType, null);
   }

   public final void setText(String strToBreak) {
      this._str = strToBreak;
      this._dataType = 0;
   }

   public final void setText(StringBuffer strToBreak) {
      this._strB = strToBreak;
      this._dataType = 1;
   }

   public final void setText(StringBufferGap strToBreak) {
      this._strBG = strToBreak;
      this._dataType = 2;
   }

   public final void setText(char[] strToBreak) {
      this._charArr = strToBreak;
      this._dataType = 3;
   }

   final StringBufferGap getText() {
      switch (this._dataType) {
         case -1:
            return null;
         case 0:
         default:
            return new StringBufferGap(this._str);
         case 1:
            return new StringBufferGap(this._strB);
         case 2:
            return this._strBG;
         case 3:
            return new StringBufferGap(new String(this._charArr));
      }
   }

   public final Locale getLocale() {
      return this._locale;
   }

   public final int getIteratorBreakingType() {
      return this._iterType;
   }

   public final int first() {
      if (this._dataType != -1 && this._iterType != -1) {
         this._currentPos = 0;
         return this._currentPos;
      } else {
         return Integer.MAX_VALUE;
      }
   }

   public final int last() {
      if (this._dataType != -1 && this._iterType != -1) {
         this._currentPos = Integer.MAX_VALUE;
         switch (this._dataType) {
            case -1:
               break;
            case 0:
            default:
               this._currentPos = this._str.length();
               break;
            case 1:
               this._currentPos = this._strB.length();
               break;
            case 2:
               this._currentPos = this._strBG.length();
               break;
            case 3:
               this._currentPos = this._charArr.length;
         }

         return this._currentPos;
      } else {
         return Integer.MAX_VALUE;
      }
   }

   public final int following(int aPos) {
      if (this._dataType != -1 && this._iterType != -1) {
         this._currentPos = Integer.MAX_VALUE;
         switch (this._dataType) {
            case -1:
               break;
            case 0:
            default:
               this._currentPos = this.scan(this._str, aPos, 1, this._iterType, this._wordlistData);
               break;
            case 1:
               this._currentPos = this.scan(this._strB, aPos, 1, this._iterType, this._wordlistData);
               break;
            case 2:
               this._currentPos = this.scan(this._strBG, aPos, 1, this._iterType, this._wordlistData);
               break;
            case 3:
               this._currentPos = this.scan(this._charArr, aPos, 1, this._iterType, this._wordlistData);
         }

         return this._currentPos;
      } else {
         return Integer.MAX_VALUE;
      }
   }

   public final int preceding(int aPos) {
      if (this._dataType != -1 && this._iterType != -1) {
         this._currentPos = Integer.MAX_VALUE;
         switch (this._dataType) {
            case -1:
               break;
            case 0:
            default:
               this._currentPos = this.scan(this._str, aPos, 0, this._iterType, this._wordlistData);
               break;
            case 1:
               this._currentPos = this.scan(this._strB, aPos, 0, this._iterType, this._wordlistData);
               break;
            case 2:
               this._currentPos = this.scan(this._strBG, aPos, 0, this._iterType, this._wordlistData);
               break;
            case 3:
               this._currentPos = this.scan(this._charArr, aPos, 0, this._iterType, this._wordlistData);
         }

         return this._currentPos;
      } else {
         return Integer.MAX_VALUE;
      }
   }

   public final int next() {
      return this.next(1);
   }

   public final int next(int aCount) {
      if (this._dataType != -1 && this._iterType != -1) {
         while (aCount-- > 0) {
            switch (this._dataType) {
               case -1:
                  break;
               case 0:
               default:
                  this._currentPos = this.scan(this._str, this._currentPos, 1, this._iterType, this._wordlistData);
                  break;
               case 1:
                  this._currentPos = this.scan(this._strB, this._currentPos, 1, this._iterType, this._wordlistData);
                  break;
               case 2:
                  this._currentPos = this.scan(this._strBG, this._currentPos, 1, this._iterType, this._wordlistData);
                  break;
               case 3:
                  this._currentPos = this.scan(this._charArr, this._currentPos, 1, this._iterType, this._wordlistData);
            }

            if (this._currentPos == Integer.MAX_VALUE) {
               break;
            }
         }

         return this._currentPos;
      } else {
         return Integer.MAX_VALUE;
      }
   }

   public final int previous() {
      return this.previous(1);
   }

   public final int previous(int aCount) {
      if (this._dataType != -1 && this._iterType != -1) {
         while (aCount-- > 0) {
            switch (this._dataType) {
               case -1:
                  break;
               case 0:
               default:
                  this._currentPos = this.scan(this._str, this._currentPos, 0, this._iterType, this._wordlistData);
                  break;
               case 1:
                  this._currentPos = this.scan(this._strB, this._currentPos, 0, this._iterType, this._wordlistData);
                  break;
               case 2:
                  this._currentPos = this.scan(this._strBG, this._currentPos, 0, this._iterType, this._wordlistData);
                  break;
               case 3:
                  this._currentPos = this.scan(this._charArr, this._currentPos, 0, this._iterType, this._wordlistData);
            }

            if (this._currentPos == Integer.MAX_VALUE) {
               break;
            }
         }

         return this._currentPos;
      } else {
         return Integer.MAX_VALUE;
      }
   }

   private final native int scan(String var1, int var2, int var3, int var4, byte[][][] var5);

   private final native int scan(StringBuffer var1, int var2, int var3, int var4, byte[][][] var5);

   private final native int scan(StringBufferGap var1, int var2, int var3, int var4, byte[][][] var5);

   private final native int scan(char[] var1, int var2, int var3, int var4, byte[][][] var5);
}
