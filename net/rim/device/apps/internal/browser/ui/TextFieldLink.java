package net.rim.device.apps.internal.browser.ui;

public final class TextFieldLink {
   private short _startOffset;
   private short _endOffset;
   private String _url;

   public TextFieldLink(short startOffset, String url) {
      this._startOffset = startOffset;
      this._endOffset = startOffset;
      this._url = url;
   }

   public final short getStartOffset() {
      return this._startOffset;
   }

   public final short getEndOffset() {
      return this._endOffset;
   }

   public final void setEndOffset(short endOffset) {
      this._endOffset = endOffset;
   }

   public final String getURL() {
      return this._url;
   }
}
