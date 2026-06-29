package net.rim.device.internal.ui;

import net.rim.device.api.ui.XYRect;

public class FormatParams {
   public int _changedTextStart;
   public int _oldLength;
   public int _newLength;
   public int _cursorOffset;
   public boolean _moveCursor;
   public boolean _isBackspace;
   public boolean _isFormatComplete = true;
   int _nextStartPosToFormat;
   int _formatOldLength;
   int _formatNewLength;
   boolean _formatTextUnchanged = false;
   int _linesToFormatCount;
   int _formatFlags;
   public int _layoutWidth = 0;
   public int _lineCount = 1;
   public ArticInterface$Line _lineList;
   public ArticInterface$LineInfo _cursorLineInfo = new ArticInterface$LineInfo();
   public XYRect _invalidRect = new XYRect();

   public void init(int aStart, int aLength, int aNewLength, int aCursorOffset, boolean aMoveCursor, ArticInterface$Line lineList) {
      this._changedTextStart = aStart;
      this._oldLength = aLength;
      this._newLength = aNewLength;
      this._cursorOffset = aCursorOffset;
      this._moveCursor = aMoveCursor;
      this._lineList = lineList;
   }

   public void initCursorLine(ArticInterface$Line cursorLine, int cursorLineStart, int cursorLineTop) {
      this._cursorLineInfo._line = cursorLine;
      this._cursorLineInfo._start = cursorLineStart;
      this._cursorLineInfo._top = cursorLineTop;
   }

   public int getNextStartPosToFormat() {
      return this._nextStartPosToFormat;
   }

   public int getDelta() {
      return this._newLength - this._oldLength;
   }

   public void computeParamsAfterTextChanged(boolean mergeFormats, int linesToFormat) {
      if (!this._isFormatComplete && mergeFormats) {
         int addFormatLengthFromLeft = this._changedTextStart > this._nextStartPosToFormat ? this._changedTextStart - this._nextStartPosToFormat : 0;
         int addFormatLengthFromRight = this._nextStartPosToFormat + this._formatOldLength > this._changedTextStart + this._oldLength
            ? this._nextStartPosToFormat + this._formatOldLength - (this._changedTextStart + this._oldLength)
            : 0;
         this._nextStartPosToFormat = this._changedTextStart - addFormatLengthFromLeft;
         this._formatOldLength = this._oldLength + addFormatLengthFromLeft + addFormatLengthFromRight;
         this._formatNewLength = this._newLength + addFormatLengthFromLeft + addFormatLengthFromRight;
      } else {
         this._formatOldLength = this._oldLength;
         this._formatNewLength = this._newLength;
         this._nextStartPosToFormat = this._changedTextStart;
      }

      this._formatTextUnchanged = false;
      this._isFormatComplete = false;
      this._linesToFormatCount = linesToFormat;
      this._layoutWidth = 0;
   }

   public void setFormatFlags(int formatFlags) {
      this._formatFlags = formatFlags;
   }
}
