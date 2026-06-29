package net.rim.device.apps.internal.browser.ui;

import net.rim.device.api.system.Clipboard;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.apps.internal.browser.resources.BrowserResources;

public final class BrowserEditField extends EditField implements InputFormatEditField {
   private BrowserTextFilter _filter;

   public BrowserEditField(int maxNumChars, long style, String format) {
      this(null, maxNumChars, style, format);
   }

   public BrowserEditField(String initialValue, int maxNumChars, long style, String format) {
      super(null, initialValue, maxNumChars, style);
      this._filter = new BrowserTextFilter(this, format);
      this.setFilter(this._filter);
      this.setMaxSize(Math.min(this._filter.getMaxLength(), maxNumChars));
   }

   @Override
   public final boolean paste(Clipboard cb) {
      int insertPosition = this.getCursorPosition();
      String text = this.getText();
      int textLength = text.length();
      String cbText = cb.toString();
      int cbTextLength = cbText.length();
      if (!this._filter.validate(cbText, 0, cbTextLength, insertPosition)) {
         Dialog.alert(BrowserResources.getString(566));
         return true;
      } else if (insertPosition < textLength && !this._filter.validate(text, insertPosition, textLength, insertPosition + cbTextLength)) {
         Dialog.alert(BrowserResources.getString(566));
         return true;
      } else {
         super.paste(cb);
         this.autoCorrect(false);
         return true;
      }
   }

   @Override
   public final void selectionCut(Clipboard cb) {
      this.selectionCopy(cb);
      String label = this.getLabel();
      int labelLength = label != null ? this.getLabel().length() : 0;
      int start = Math.min(this.getAnchorPosition(), this.getCaretPosition()) - labelLength;
      int end = Math.max(this.getAnchorPosition(), this.getCaretPosition()) - labelLength;
      String text = this.getText();
      int length = text.length();
      if (end < length && !this._filter.validate(text, end, length, start)) {
         Dialog.alert(BrowserResources.getString(565));
      } else {
         super.selectionCut(cb);
         this.autoCorrect(true);
      }
   }

   @Override
   protected final void onFocus(int direction) {
      if (this._filter.getLiteralPrefixEnd() != -1 && direction == 1) {
         this.setCursorPosition(this._filter.getLiteralPrefixEnd() + 1);
      }

      super.onFocus(direction);
   }

   @Override
   public final int insert(String text, int context, boolean stripInvalid, boolean validateText) {
      int count = super.insert(text, context, stripInvalid, validateText);
      this.autoCorrect(false);
      return count;
   }

   @Override
   protected final boolean insert(char key, int status) {
      boolean result = super.insert(key, status);
      this.autoCorrect(false);
      return result;
   }

   @Override
   protected final int backspace(int count, int context) {
      int result = super.backspace(count, context);
      if (context != Integer.MIN_VALUE) {
         this.autoCorrect(true);
      }

      return result;
   }

   @Override
   protected final boolean backspace() {
      boolean result = super.backspace();
      this.autoCorrect(true);
      return result;
   }

   private final void autoCorrect(boolean deleting) {
      int length = this.getText().length();
      if (length <= this._filter.getLiteralPrefixEnd()) {
         this.setCursorPosition(length);
         this.initialize();
      } else {
         if (deleting) {
            int cursorPosition = length;

            while (this._filter.isLiteralAtIndex(cursorPosition) && cursorPosition > this._filter.getLiteralPrefixEnd() + 1) {
               cursorPosition--;
            }

            if (length != cursorPosition) {
               this.setCursorPosition(length);
               this.backspace(length - cursorPosition);
               return;
            }
         } else {
            int cursorPosition = length;
            StringBuffer buffer = null;

            while (this._filter.isLiteralAtIndex(cursorPosition)) {
               if (buffer == null) {
                  buffer = (StringBuffer)(new Object());
               }

               buffer.append(this._filter.getFormat()[cursorPosition]);
               cursorPosition++;
            }

            if (buffer != null) {
               this.setCursorPosition(this.getText().length());
               this.insert(buffer.toString());
            }
         }
      }
   }

   @Override
   protected final boolean keyControl(char key, int status, int time) {
      if (key == 137) {
         char format = this._filter.getFormatAtIndex(this.getCursorPosition());
         if (format == 'N' || format == 'n') {
            char altedChar = Keypad.getAltedChar(key);
            if (altedChar == '0') {
               this.keyChar(altedChar, status, time);
               return true;
            }
         }
      }

      return super.keyControl(key, status, time);
   }

   @Override
   public final void initialize() {
      String value = "";
      if (this._filter.getLiteralPrefixEnd() != -1) {
         char[] prefix = new char[this._filter.getLiteralPrefixEnd() + 1];
         System.arraycopy(this._filter.getFormat(), 0, prefix, 0, this._filter.getLiteralPrefixEnd() + 1);
         value = (String)(new Object(prefix));
         this.setText(value);
      } else {
         this.setText(value);
      }
   }

   @Override
   public final boolean validate(String text, boolean prefix) {
      return this._filter.validateString(text, 0, prefix);
   }

   @Override
   protected final void displayFieldFullMessage() {
   }
}
