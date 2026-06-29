package net.rim.device.internal.ui;

import net.rim.device.api.ui.XYRect;
import net.rim.tid.text.AttributedString;
import net.rim.tid.text.TextHitInfo;

public class ArticInterface {
   public static final int ALLOW_LINE_BREAK_ON_NON_BREAK_SPACE;
   public static final int LINE_BREAK_CJK_AS_ALPHABETIC;
   public static final int FORBID_ILLEGAL_LINE_BREAK;
   private static final int KMoveNext;
   private static final int KMovePrev;
   private static final int KMoveRight;
   private static final int KMoveLeft;
   private static final int KMoveDown;
   private static final int KMoveUp;
   public static ArticInterface$Layout _layout = new ArticInterface$Layout();

   private ArticInterface() {
   }

   public static ArticInterface$Layout Format(
      int aWidth,
      int aTextStart,
      int aOldLength,
      int aNewLength,
      int aCursor,
      boolean aCursorLeadingEdge,
      int aAnchor,
      AttributedString aText,
      ArticInterface$Line aLine,
      int aLineStart,
      int aLineTop,
      int aMaxLines,
      boolean aTextUnchanged,
      int aFlags
   ) {
      if (aText == null) {
         throw new IllegalArgumentException("ArticInterface.Format: aText == null");
      }

      if (aLine == null) {
         throw new IllegalArgumentException("ArticInterface.Format: aLine == null");
      }

      if (aTextStart < 0 || aTextStart > aText.length()) {
         throw new IllegalArgumentException("ArticInterface.Format: aTextStart (" + aTextStart + ") out of range 0...aText.length() (" + aText.length() + ")");
      }

      if (aOldLength < 0) {
         throw new IllegalArgumentException("ArticInterface.Format: aOldLength < 0");
      }

      if (aNewLength < 0) {
         throw new IllegalArgumentException("ArticInterface.Format: aNewLength < 0");
      }

      Format(
         _layout,
         aWidth,
         aTextStart,
         aOldLength,
         aNewLength,
         aCursor,
         aCursorLeadingEdge,
         aAnchor,
         aText,
         aLine,
         aLineStart,
         aLineTop,
         aMaxLines,
         aTextUnchanged,
         aFlags
      );
      return _layout;
   }

   private static native void Format(
      ArticInterface$Layout var0,
      int var1,
      int var2,
      int var3,
      int var4,
      int var5,
      boolean var6,
      int var7,
      AttributedString var8,
      ArticInterface$Line var9,
      int var10,
      int var11,
      int var12,
      boolean var13,
      int var14
   );

   public static native void DocPosToCaret(XYRect var0, AttributedString var1, ArticInterface$Line var2, int var3, int var4, int var5, boolean var6);

   public static native void PointToDocPos(
      TextHitInfo var0, XYRect var1, AttributedString var2, ArticInterface$Line var3, int var4, int var5, int var6, int var7
   );

   private static native void DocPosMove(int var0, TextHitInfo var1, XYRect var2, AttributedString var3, ArticInterface$Line var4, int var5, int var6);

   public static void DocPosNext(TextHitInfo aDocPos, XYRect aCaret, AttributedString aText, ArticInterface$Line aLine, int aLineStart, int aLineTop) {
      DocPosMove(0, aDocPos, aCaret, aText, aLine, aLineStart, aLineTop);
   }

   public static void DocPosPrev(TextHitInfo aDocPos, XYRect aCaret, AttributedString aText, ArticInterface$Line aLine, int aLineStart, int aLineTop) {
      DocPosMove(1, aDocPos, aCaret, aText, aLine, aLineStart, aLineTop);
   }

   public static void DocPosLeft(TextHitInfo aDocPos, XYRect aCaret, AttributedString aText, ArticInterface$Line aLine, int aLineStart, int aLineTop) {
      DocPosMove(3, aDocPos, aCaret, aText, aLine, aLineStart, aLineTop);
   }

   public static void DocPosRight(TextHitInfo aDocPos, XYRect aCaret, AttributedString aText, ArticInterface$Line aLine, int aLineStart, int aLineTop) {
      DocPosMove(2, aDocPos, aCaret, aText, aLine, aLineStart, aLineTop);
   }

   public static void DocPosDown(TextHitInfo aDocPos, XYRect aCaret, AttributedString aText, ArticInterface$Line aLine, int aLineStart, int aLineTop) {
      DocPosMove(4, aDocPos, aCaret, aText, aLine, aLineStart, aLineTop);
   }

   public static void DocPosUp(TextHitInfo aDocPos, XYRect aCaret, AttributedString aText, ArticInterface$Line aLine, int aLineStart, int aLineTop) {
      DocPosMove(5, aDocPos, aCaret, aText, aLine, aLineStart, aLineTop);
   }

   public static native int AdjustDocPos(StringBufferGap var0, TextHitInfo var1, int var2);
}
