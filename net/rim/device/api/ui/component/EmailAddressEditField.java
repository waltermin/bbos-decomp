package net.rim.device.api.ui.component;

import net.rim.device.api.ui.text.EmailAddressTextFilter;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.internal.ui.FormatParams;

public class EmailAddressEditField extends EditField {
   private boolean _convertSpace = true;
   private boolean _inBackspace;

   public EmailAddressEditField(String label, String initialValue) {
      super(label, initialValue, 1000000, 2147483648L);
      this.commonInit();
   }

   public EmailAddressEditField(String label, String initialValue, int maxNumChars) {
      super(label, initialValue, maxNumChars, 2147483648L);
      this.commonInit();
   }

   public EmailAddressEditField(String label, String initialValue, int maxNumChars, long style) {
      super(label, initialValue, maxNumChars, style);
      this.commonInit();
   }

   private void commonInit() {
      this.setFilter(new EmailAddressTextFilter());
      this.setNonSpellCheckable(true);
      this.setAllowUnicodeInput(false);
   }

   @Override
   protected boolean backspace() {
      int cursorPosition = this.getCursorPosition();
      if (cursorPosition > 0) {
         char c = this.getTextAbstractString().charAt(cursorPosition - 1);
         this._convertSpace = c != '@' && c != '.';
      }

      this._inBackspace = true;
      boolean ret = super.backspace();
      this._inBackspace = false;
      return ret;
   }

   @Override
   protected boolean keyChar(char key, int status, int time) {
      if (key == ' ' && this._convertSpace) {
         this._convertSpace = (status & 4) != 0 || (status & 2) == 0;
      }

      return super.keyChar(key, status, time);
   }

   @Override
   protected int insert(String text, int context) {
      if (StringUtilities.startsWithIgnoreCase(text, "mailto:", 1701707776)) {
         int beginIndex = text.indexOf(58) + 1;
         int endIndex = text.length();

         while (beginIndex < endIndex && text.charAt(beginIndex) == '/') {
            beginIndex++;
         }

         while (beginIndex < endIndex && text.charAt(endIndex - 1) == '/') {
            endIndex--;
         }

         text = text.substring(beginIndex, endIndex);
      }

      return super.insert(text, context);
   }

   @Override
   protected boolean insert(char key, int status) {
      return super.insert(key, status);
   }

   @Override
   protected void notifyTextChanged(FormatParams aParams, boolean aIsInsertionOrDeletion) {
      super.notifyTextChanged(aParams, aIsInsertionOrDeletion);
      boolean convertSpace = this._convertSpace;
      if (!this._inBackspace) {
         this._convertSpace = true;
      }

      int start = aParams._changedTextStart;
      if (aIsInsertionOrDeletion && aParams._newLength == 1 && super._text.charAt(start) == ' ') {
         int atIndex = super._text.getText().indexOf('@', this.getLabelLength(), super._text.length());
         if (atIndex == -1) {
            if (convertSpace) {
               super._text.replace(start, start + 1, '@');
               return;
            }
         } else if (convertSpace) {
            super._text.replace(start, start + 1, '.');
         }
      }
   }
}
