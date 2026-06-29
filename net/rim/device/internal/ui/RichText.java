package net.rim.device.internal.ui;

import net.rim.device.api.i18n.Locale;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.RIMGlobalMessagePoster;
import net.rim.device.api.ui.DrawTextParam;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Ui;
import net.rim.vm.Array;

public final class RichText {
   private static Edit$Helper _helper = new Edit$Helper();
   private static Edit$BidiLineRuns _runs = new Edit$BidiLineRuns();
   private static int NO_DATA = -1;
   static long RADIO_LOGWORTHY_REPORT_REQUEST = -2816799803471967993L;
   private static int ALIGNMENT_BITS = 7;
   public static final byte DIR_LTR = 0;
   public static final byte DIR_NEUTRAL = 1;
   public static final byte DIR_RTL = 2;
   public static final String PARAG_DELIMITERS = "\n82328233";

   private RichText() {
   }

   public static final Edit$Helper calculateLengths(int width, String text, int[] offsets, byte[] attributes, Font[] fonts) {
      if (text == null || offsets == null || attributes == null || fonts == null) {
         throw new NullPointerException("NULL arguments passed");
      }

      if (width < 0) {
         throw new IllegalArgumentException("Width cannot be negative");
      }

      validateEntries(attributes, fonts);
      calculateLengths(_helper, width, text, offsets, attributes, fonts);
      return _helper;
   }

   public static final Edit$Helper calculateLengths(
      int width, int startOffset, int currentOffset, StringBufferGap text, int[] offsets, byte[] attributes, Font[] fonts, boolean measureAll
   ) {
      if (text != null && offsets != null && fonts != null) {
         if (width >= 0 && startOffset >= 0 && currentOffset >= 0 && currentOffset - startOffset <= text.length()) {
            calculateLengths(_helper, width, startOffset, currentOffset, text, offsets, attributes, fonts, measureAll ? 1 : 0);
            return _helper;
         } else {
            throw new IllegalArgumentException("Width/offsets arguments cannot be negative or exceed the text length");
         }
      } else {
         throw new NullPointerException("NULL arguments passed");
      }
   }

   private static final void validateEntries(byte[] attributes, Font[] fonts) {
      for (int i = 0; i < attributes.length; i++) {
         if (attributes[i] < 0 || attributes[i] >= fonts.length) {
            throw new IllegalArgumentException("(attribute < 0) || (fontCount <= attribute)");
         }
      }
   }

   public static final int getDefaultParagDirection() {
      int code = Locale.getDefaultForSystem().getCode() & -65536;
      return code != 1751449600 && code != 1634861056 && code != 1717633024 ? 0 : 1;
   }

   public static final int getParagraphOrdering(long fieldStyle) {
      if ((fieldStyle & 549755813888L) != 0) {
         return 2;
      } else {
         return (fieldStyle & 274877906944L) != 0 ? 3 : getDefaultParagDirection();
      }
   }

   private static final native void calculateLengths(Edit$Helper var0, int var1, String var2, int[] var3, byte[] var4, Font[] var5);

   private static final native void calculateLengths(
      Edit$Helper var0, int var1, int var2, int var3, StringBufferGap var4, int[] var5, byte[] var6, Font[] var7, int var8
   );

   public static final Edit$BidiLineRuns getBidiOrder(StringBufferGap text, int offset, int length, byte[] bidiState, boolean paragStart, int[] styleBreaks) {
      return getBidiOrder(text, offset, length, bidiState, paragStart, getDefaultParagDirection(), styleBreaks, 0, styleBreaks == null ? 0 : styleBreaks.length);
   }

   public static final Edit$BidiLineRuns getBidiOrder(
      StringBufferGap text,
      int offset,
      int length,
      byte[] bidiState,
      boolean paragStart,
      int paragDirection,
      int[] styleBreaks,
      int styleBreaksOffset,
      int styleBreaksLen
   ) {
      if (text == null) {
         throw new NullPointerException("NULL arguments passed");
      }

      if (offset >= 0
         && length >= 0
         && offset + length <= text.length()
         && styleBreaksOffset >= 0
         && styleBreaksLen >= 0
         && (styleBreaks == null || styleBreaks.length >= styleBreaksOffset + styleBreaksLen)
         && paragDirection >= 0
         && paragDirection <= 3) {
         int rc = getBidiOrder(_runs, text, offset, length, bidiState, paragDirection, paragStart, styleBreaks, styleBreaksOffset, styleBreaksLen);
         if (rc != 0) {
            _runs.ignore(true);
            sendLog(rc);
         } else {
            _runs.ignore(false);
         }

         return _runs;
      } else {
         throw new IllegalArgumentException("offset arguments cannot be negative or exceed the text length");
      }
   }

   public static final Edit$BidiLineRuns getBidiOrder(StringBuffer text, int offset, int length, byte[] bidiState, boolean paragStart, int[] styleBreaks) {
      return getBidiOrder(text, offset, length, bidiState, paragStart, getDefaultParagDirection(), styleBreaks, 0, styleBreaks == null ? 0 : styleBreaks.length);
   }

   public static final Edit$BidiLineRuns getBidiOrder(
      StringBuffer text,
      int offset,
      int length,
      byte[] bidiState,
      boolean paragStart,
      int paragDirection,
      int[] styleBreaks,
      int styleBreaksOffset,
      int styleBreaksLen
   ) {
      if (text == null) {
         throw new NullPointerException("NULL arguments passed");
      }

      if (offset >= 0
         && length >= 0
         && offset + length <= text.length()
         && styleBreaksOffset >= 0
         && styleBreaksLen >= 0
         && (styleBreaks == null || styleBreaks.length >= styleBreaksOffset + styleBreaksLen)
         && paragDirection >= 0
         && paragDirection <= 3) {
         int rc = getBidiOrder(_runs, text, offset, length, bidiState, paragDirection, paragStart, styleBreaks, styleBreaksOffset, styleBreaksLen);
         if (rc != 0) {
            _runs.ignore(true);
            sendLog(rc);
         } else {
            _runs.ignore(false);
         }

         return _runs;
      } else {
         throw new IllegalArgumentException("offset arguments cannot be negative or exceed the text length");
      }
   }

   public static final Edit$BidiLineRuns getBidiOrder(String text, int offset, int length, byte[] bidiState, boolean paragStart, int[] styleBreaks) {
      return getBidiOrder(text, offset, length, bidiState, paragStart, getDefaultParagDirection(), styleBreaks, 0, styleBreaks == null ? 0 : styleBreaks.length);
   }

   public static final Edit$BidiLineRuns getBidiOrder(
      String text,
      int offset,
      int length,
      byte[] bidiState,
      boolean paragStart,
      int paragDirection,
      int[] styleBreaks,
      int styleBreaksOffset,
      int styleBreaksLen
   ) {
      if (text == null) {
         throw new NullPointerException("NULL arguments passed");
      }

      if (offset >= 0
         && length >= 0
         && offset + length <= text.length()
         && styleBreaksOffset >= 0
         && styleBreaksLen >= 0
         && (styleBreaks == null || styleBreaks.length >= styleBreaksOffset + styleBreaksLen)
         && paragDirection >= 0
         && paragDirection <= 3) {
         int rc = getBidiOrder(_runs, text, offset, length, bidiState, paragDirection, paragStart, styleBreaks, styleBreaksOffset, styleBreaksLen);
         if (rc != 0) {
            _runs.ignore(true);
            sendLog(rc);
         } else {
            _runs.ignore(false);
         }

         return _runs;
      } else {
         throw new IllegalArgumentException("offset arguments cannot be negative or exceed the text length");
      }
   }

   private static final void sendLog(int rc) {
      Application app = Application.getApplication();
      StringBuffer debugInfo = new StringBuffer("BIDI ERROR: " + rc + "; ");

      try {
         if (app != null) {
            debugInfo.append(app.toString() + " --> " + app.getClass().getName());
         }
      } catch (Throwable thr) {
         thr.printStackTrace();
      }

      System.err.println(debugInfo.toString());
      RIMGlobalMessagePoster.postGlobalEvent(RADIO_LOGWORTHY_REPORT_REQUEST, 0, 0, debugInfo, null);
   }

   private static final native int getBidiOrder(
      Edit$BidiLineRuns var0, StringBufferGap var1, int var2, int var3, byte[] var4, int var5, boolean var6, int[] var7, int var8, int var9
   );

   private static final native int getBidiOrder(
      Edit$BidiLineRuns var0, StringBuffer var1, int var2, int var3, byte[] var4, int var5, boolean var6, int[] var7, int var8, int var9
   );

   private static final native int getBidiOrder(
      Edit$BidiLineRuns var0, String var1, int var2, int var3, byte[] var4, int var5, boolean var6, int[] var7, int var8, int var9
   );

   public static final void drawTextWithEllipses(Graphics graphics, String text, int x, int y, int width, int paragDirection, int flags) {
      if (graphics != null && text != null) {
         DrawTextParam param = Ui.getTmpDrawTextParam();
         param.iMaxAdvance = width;
         param.iDrawNonPrintableCharacters = false;
         param.iReverse = 0;
         Edit$BidiLineRuns bidiRuns = getBidiOrder(text, 0, text.length(), null, true, paragDirection, null, 0, 0);
         int bidiRunsCount = bidiRuns._runs.length;
         if (!bidiRuns.isIgnored() && bidiRunsCount > 3) {
            boolean fitsOrIsLTR = true;
            boolean lineIsRTL = paragDirection == 1 || paragDirection == 3;
            Font f = graphics.getFont();
            if (lineIsRTL) {
               int widthRequired = f.getBounds(text);
               if (widthRequired > width || (flags & 5) != 0) {
                  fitsOrIsLTR = false;
               }
            }

            if (fitsOrIsLTR) {
               int rightmostX = x + width;

               for (int i = 0; i < bidiRunsCount; i++) {
                  int runStart = bidiRuns._runs[i++];
                  int runLength = bidiRuns._runs[i++];
                  param.iReverse = bidiRuns._runs[i];
                  if (param.iReverse == 1) {
                     param.iTruncateWithEllipsis = 1;
                  } else {
                     param.iTruncateWithEllipsis = 2;
                  }

                  int xAdjust = graphics.drawText(text, runStart, runLength, x, y, param, null);
                  param.iMaxAdvance -= xAdjust;
                  x += xAdjust;
                  if (x > rightmostX) {
                     break;
                  }
               }
            } else {
               int originalX = x;
               x += width;

               for (int i = bidiRunsCount - 1; i >= 0; i--) {
                  param.iReverse = bidiRuns._runs[i--];
                  int runLength = bidiRuns._runs[i--];
                  int runStart = bidiRuns._runs[i];
                  if (param.iReverse == 1) {
                     param.iTruncateWithEllipsis = 2;
                  } else {
                     param.iTruncateWithEllipsis = 1;
                  }

                  int pieceWidth = f.measureText(text, runStart, runLength, param, null);
                  boolean breakFromLoop = false;
                  if (x - pieceWidth < originalX) {
                     breakFromLoop = true;
                     pieceWidth = x - originalX;
                  }

                  int xAdjust = graphics.drawText(text, runStart, runLength, x - pieceWidth, y, param, null);
                  param.iMaxAdvance -= xAdjust;
                  x -= xAdjust;
                  if (breakFromLoop) {
                     break;
                  }
               }
            }
         } else {
            if (bidiRunsCount == 3) {
               param.iReverse = bidiRuns._runs[2];
            }

            param.iTruncateWithEllipsis = 2;
            param.iAlignment = flags & ALIGNMENT_BITS;
            graphics.drawText(text, 0, text.length(), x, y, param, null);
         }

         Ui.returnTmpDrawTextParam(param);
      }
   }

   public static final byte getLineDirection(String s) {
      return s != null && s.length() > 0 ? getLineDirection(s, 0, s.length()) : 0;
   }

   public static final byte getLineDirection(String s, int offset, int len) {
      if (s != null && s.length() > 0) {
         int endIndex = offset + len;
         if (offset >= 0 && len >= 0 && endIndex <= s.length()) {
            for (int i = offset; i < endIndex; i++) {
               char c = s.charAt(i);
               if (isRTL(c)) {
                  return 2;
               }

               if (!isNeutral(c)) {
                  return 0;
               }
            }

            return 0;
         } else {
            throw new IllegalArgumentException();
         }
      } else {
         return 0;
      }
   }

   public static final byte getLineDirection(StringBufferGap s) {
      return s != null && s.length() > 0 ? getLineDirection(s, 0, s.length()) : 0;
   }

   public static final byte getLineDirection(StringBufferGap s, int offset, int len) {
      if (s != null && s.length() > 0) {
         int endIndex = offset + len;
         if (offset >= 0 && len >= 0 && endIndex <= s.length()) {
            for (int i = offset; i < endIndex; i++) {
               char c = s.charAt(i);
               if (isRTL(c)) {
                  return 2;
               }

               if (!isNeutral(c)) {
                  return 0;
               }
            }

            return 0;
         } else {
            throw new IllegalArgumentException();
         }
      } else {
         return 0;
      }
   }

   public static final int getRTLCount(String s, int offset, int len) {
      if (s != null && s.length() > 0) {
         int rtlCount = 0;
         int endIndex = offset + len;
         if (offset >= 0 && len >= 0 && endIndex <= s.length()) {
            for (int i = offset; i < endIndex; i++) {
               char c = s.charAt(i);
               if (isRTL(c)) {
                  rtlCount++;
               }
            }

            return rtlCount;
         } else {
            throw new IllegalArgumentException();
         }
      } else {
         return 0;
      }
   }

   public static final int getRTLCount(StringBufferGap s, int offset, int len) {
      if (s != null && s.length() > 0) {
         int rtlCount = 0;
         int endIndex = offset + len;
         if (offset >= 0 && len >= 0 && endIndex <= s.length()) {
            for (int i = offset; i < endIndex; i++) {
               char c = s.charAt(i);
               if (isRTL(c)) {
                  rtlCount++;
               }
            }

            return rtlCount;
         } else {
            throw new IllegalArgumentException();
         }
      } else {
         return 0;
      }
   }

   public static final boolean isRTL(char c) {
      return c >= 1425 && c <= 1524 || c >= 1548 && c <= 1866 || c >= 'יִ' && c <= 'ﷻ' || c >= 'ﹰ' && c <= 'ﻼ';
   }

   public static final boolean isNeutral(char c) {
      return c == '\n'
         || c >= 0 && c <= '@'
         || c >= '[' && c <= '`'
         || c >= '{' && c <= 191
         || c == 215
         || c == 247
         || c >= 768 && c <= 866
         || c >= 8192 && c <= 8334
         || c >= 8352 && c <= 8367
         || c >= 8400 && c <= 8419
         || c >= 8448 && c <= 9371
         || c >= 9472 && c <= 10174
         || c >= 12288 && c <= 12351
         || c >= 13056 && c <= 13310
         || c >= '︠' && c <= '﹫'
         || c >= '\ufeff' && c <= '＠'
         || c >= '［' && c <= '｀'
         || c >= '｛' && c <= '､'
         || c >= '\ufff9' && c <= '\uffff';
   }

   public static final int[] getParagraphOffsets(StringBufferGap s, int offset, int endIndex) {
      int[] offsets = new int[]{offset};
      if (s != null && s.length() > 0) {
         int oIndex = 0;
         int index = offset;
         int newIndex = 0;
         int i = offset;

         while (i < endIndex) {
            char c = s.charAt(i);
            switch (c) {
               case '\n':
               case '\u2028':
               case '\u2029':
                  newIndex = i;
                  int b = getLineDirection(s, index, newIndex - index) << 24;
                  offsets[oIndex] |= b;
                  if (offsets.length == ++oIndex) {
                     Array.resize(offsets, oIndex + 5);
                  }

                  index = newIndex + 1;
                  offsets[oIndex] = index;
               default:
                  i++;
            }
         }

         if (offsets.length != oIndex + 1) {
            Array.resize(offsets, oIndex + 1);
         }

         i = getLineDirection(s, index, endIndex - index) << 24;
         offsets[oIndex] |= i;
      }

      return offsets;
   }
}
