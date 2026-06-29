package net.rim.device.internal.ui;

public final class ArticInterface$Layout {
   public int _totalLength;
   public int _boundsLeft;
   public int _boundsTop;
   public int _boundsRight;
   public int _boundsBottom;
   public int _oldBoundsLeft;
   public int _oldBoundsTop;
   public int _oldBoundsRight;
   public int _oldBoundsBottom;
   public int _caretLeft;
   public int _caretTop;
   public int _caretRight;
   public int _caretBottom;
   public int _textStart;
   public int _oldLines;
   public int _unformattedStart;
   public int _unformattedLength;
   public int[] _lineData = new int[0];
   public int[] _runData = new int[0];
   public byte[] _bidi = new byte[0];
   private static final int KTextLengthOffset;
   private static final int KSkippedCharactersOffset;
   private static final int KLayoutRunsOffset;
   private static final int KOriginXOffset;
   private static final int KOriginYOffset;
   private static final int KBoundsLeftOffset;
   private static final int KBoundsTopOffset;
   private static final int KBoundsRightOffset;
   private static final int KBoundsBottomOffset;
   private static final int KBidiBytesOffset;
   private static final int KFlagsOffset;
   private static final int KLineElements;
   private static final int KRunElements;

   public final int lines() {
      return this._lineData.length / 11;
   }

   public final int runs() {
      return this._runData.length / 5;
   }

   public final int textLength(int aLine) {
      return this._lineData[aLine * 11 + 0] + this._lineData[aLine * 11 + 1];
   }

   public final int pixelLength(int aLine) {
      return this._lineData[aLine * 11 + 7] - this._lineData[aLine * 11 + 5];
   }

   public final int pixelHeight(int aLine) {
      return this._lineData[aLine * 11 + 8] - this._lineData[aLine * 11 + 6];
   }

   public final int boundsWidth() {
      return this._boundsRight - this._boundsLeft;
   }
}
