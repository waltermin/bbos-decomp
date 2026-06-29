package net.rim.tid.im.ui;

import net.rim.device.api.i18n.Locale;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.component.TextField;

class TextFieldLookup$LookupTextField extends TextField {
   public TextFieldLookup$LookupTextField(long style) {
      super(style);
      this.setFormatFlags(2);
   }

   public TextFieldLookup$LookupTextField(String label, String initialValue, int maxNumChars, long style) {
      super(label, initialValue, maxNumChars, style);
      this.setFormatFlags(2);
   }

   public TextFieldLookup$LookupTextField(String label, String initialValue) {
      super(label, initialValue);
      this.setFormatFlags(2);
   }

   @Override
   protected int getDisplayLineCount() {
      throw new RuntimeException("cod2jar: tail call (jumpspecial)");
   }

   @Override
   protected int getLineTop(int aIndex) {
      throw new RuntimeException("cod2jar: tail call (jumpspecial)");
   }

   @Override
   protected void applyFont() {
      super.applyFont();
      Font f = this.getFont();
      int fontHeight = f.getHeight();
      int fontCorrectedHeight = fontHeight < TextFieldLookup._minFont
         ? TextFieldLookup._minFont
         : (fontHeight > TextFieldLookup._maxFont ? TextFieldLookup._maxFont : fontHeight);
      boolean changeFont = fontCorrectedHeight != fontHeight;
      if ((Locale.getDefaultInputForSystem().getCode() & -65536) == 1784741888
         && TextFieldLookup._japaneseFontFamily != null
         && !f.getFontFamily().getName().equals("BBJapanese")) {
         this.setFont(TextFieldLookup._japaneseFontFamily.getFont(f.getStyle(), fontCorrectedHeight, 0, f.getAntialiasMode(), f.getEffects()));
         changeFont = false;
      }

      if (changeFont) {
         this.setFont(f.derive(f.getStyle(), fontCorrectedHeight));
      }
   }
}
