package net.rim.device.apps.internal.docview.core;

public final class ArznRichTextInfo {
   public final int _regionFont;
   public final int _textForeColor;
   public final int _textBgColor;
   public final int _textSizeIndex;
   public static final int FONTSTYLE_PLAIN = 0;
   public static final int FONTSTYLE_ITALIC = 1;
   public static final int FONTSTYLE_BOLD = 2;
   public static final int FONTSTYLE_UNDERLINE = 4;
   public static final int FONTSTYLE_STRIKETHROUGH = 8;

   ArznRichTextInfo(int fontStyle) {
      this(fontStyle, -1, -1, -1);
   }

   ArznRichTextInfo(int fontStyle, int textForeColor, int textBgColor, int textSizeIndex) {
      this._regionFont = fontStyle;
      this._textForeColor = textForeColor;
      this._textBgColor = textBgColor;
      this._textSizeIndex = textSizeIndex;
   }
}
