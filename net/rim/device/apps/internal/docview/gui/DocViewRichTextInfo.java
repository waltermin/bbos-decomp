package net.rim.device.apps.internal.docview.gui;

public final class DocViewRichTextInfo {
   public int _iStartOffset;
   public final int _regionFont;
   public final int _textBgColor;
   public final int _textSize;
   public int _textForeColor;
   public int _iLength;
   int _hyperlinkIndex = -1;

   DocViewRichTextInfo(int startOffset, int length, int regionFont, int textForeColor, int textBgColor, int textSize) {
      this._iStartOffset = startOffset;
      this._iLength = length;
      this._regionFont = regionFont;
      this._textForeColor = textForeColor;
      this._textBgColor = textBgColor;
      this._textSize = textSize;
   }

   final DocViewRichTextInfo cloneObject() {
      DocViewRichTextInfo cloneObj = new DocViewRichTextInfo(
         this._iStartOffset, this._iLength, this._regionFont, this._textForeColor, this._textBgColor, this._textSize
      );
      cloneObj._hyperlinkIndex = this._hyperlinkIndex;
      return cloneObj;
   }

   final boolean identical(DocViewRichTextInfo otherFormatInfo) {
      return this._regionFont == otherFormatInfo._regionFont
         && this._textForeColor == otherFormatInfo._textForeColor
         && this._textBgColor == otherFormatInfo._textBgColor
         && this._hyperlinkIndex == otherFormatInfo._hyperlinkIndex
         && this._textSize == otherFormatInfo._textSize;
   }
}
