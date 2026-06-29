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
   public static final int NO_REVERSE;
   public static final int REVERSE;
   public static final int ALREADY_REVERSED;
   public static final int BIDI_REORDER;
   public static final int NO_TRUNCATE_WITH_ELLIPSIS;
   public static final int TRUNCATE_WITH_ELLIPSIS_AT_START;
   public static final int TRUNCATE_WITH_ELLIPSIS_AT_END;
   public static final int TRUNCATE_WITH_NO_ELLIPSIS_AT_START;

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
