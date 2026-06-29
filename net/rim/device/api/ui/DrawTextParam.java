package net.rim.device.api.ui;

public final class DrawTextParam {
   public int iMaxAdvance;
   public int iMaxCharacters;
   public int iTracking;
   public int iReverse;
   public int iTruncateWithEllipsis;
   public int iStartOffset;
   public int iEndOffset;
   public int iAlignment;
   public DrawTextParam$AdvancedDrawTextParam iAdvancedParam;
   public boolean iDrawNonPrintableCharacters;
   public boolean iUnderlineToBounds;
   public boolean iShaping;
   public boolean iPasswordMode;
   public static final int NO_REVERSE = 0;
   public static final int REVERSE = 1;
   public static final int ALREADY_REVERSED = 2;
   public static final int BIDI_REORDER = 3;
   public static final int NO_TRUNCATE_WITH_ELLIPSIS = 0;
   public static final int TRUNCATE_WITH_ELLIPSIS_AT_START = 1;
   public static final int TRUNCATE_WITH_ELLIPSIS_AT_END = 2;
   public static final int TRUNCATE_WITH_NO_ELLIPSIS_AT_START = 3;

   public DrawTextParam(int maxAdvance) {
      this.reset();
      this.iMaxAdvance = maxAdvance;
   }

   public DrawTextParam() {
      this(Integer.MAX_VALUE);
   }

   public final void reset() {
      this.iMaxAdvance = Integer.MAX_VALUE;
      this.iMaxCharacters = Integer.MAX_VALUE;
      this.iTracking = 0;
      this.iReverse = 3;
      this.iTruncateWithEllipsis = 0;
      this.iStartOffset = 0;
      this.iEndOffset = Integer.MAX_VALUE;
      this.iAlignment = 0;
      this.iDrawNonPrintableCharacters = true;
      this.iUnderlineToBounds = true;
      this.iShaping = true;
      this.iPasswordMode = false;
      if (this.iAdvancedParam != null) {
         this.iAdvancedParam.reset();
      }
   }

   public final int getStartOffset() {
      return this.iStartOffset;
   }

   public final int getEndOffset() {
      return this.iEndOffset;
   }
}
