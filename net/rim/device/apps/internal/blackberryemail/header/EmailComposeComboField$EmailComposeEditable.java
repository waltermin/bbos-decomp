package net.rim.device.apps.internal.blackberryemail.header;

import net.rim.device.apps.api.addressbook.AddressBookComboField$AddressBookEditable;
import net.rim.device.internal.ui.FormatParams;
import net.rim.tid.awt.event.InputMethodEvent;

public class EmailComposeComboField$EmailComposeEditable extends AddressBookComboField$AddressBookEditable {
   private boolean _convertSpace;
   private boolean _inBackspace;
   private StringBuffer _translatedBuffer;
   private StringBuffer _imTranslatedBuffer;
   private int _imCommittedCount;
   private String _programmaticLabel;
   private final EmailComposeComboField this$0;

   public EmailComposeComboField$EmailComposeEditable(EmailComposeComboField _1, String label) {
      this(_1, label, null);
   }

   public EmailComposeComboField$EmailComposeEditable(EmailComposeComboField _1, String displayLabel, String programmaticLabel) {
      super(_1, displayLabel);
      this.this$0 = _1;
      this._programmaticLabel = programmaticLabel;
   }

   @Override
   public String getLabel() {
      return this._programmaticLabel != null ? this._programmaticLabel : super.getLabel();
   }

   public String getTranslated() {
      if (this.getInputContext().getActiveInputMethodID() == 512 && this._imTranslatedBuffer != null) {
         return this._imTranslatedBuffer.toString();
      } else if (this._translatedBuffer != null && this._translatedBuffer.length() >= this.getLabelLength()) {
         String translated = this._translatedBuffer.toString();
         return translated.substring(this.getLabelLength());
      } else {
         return this.getText();
      }
   }

   @Override
   public int inputMethodTextChanged(InputMethodEvent event) {
      if (this.getInputContext().getActiveInputMethodID() == 512) {
         StringBuffer keys = event.getOriginatingKeys();
         if (this.getText().length() == 0 && keys != null && keys.length() > 0) {
            this._imTranslatedBuffer = new StringBuffer();
            this._imCommittedCount = 0;
         }

         if (keys != null && this._imTranslatedBuffer != null) {
            this._imTranslatedBuffer.setLength(this._imCommittedCount);
            this._imTranslatedBuffer.append(keys);
            this._imCommittedCount = this._imCommittedCount + event.getOriginatingKeysCommittedCount();
            int lastIndex = 0;
            String translated = this._imTranslatedBuffer.toString();
            int index;
            if ((index = translated.indexOf(32)) > 0 && translated.indexOf(64) < 0) {
               this._imTranslatedBuffer.setCharAt(index, '@');
               lastIndex = index + 1;
            }

            while ((index = translated.indexOf(32, lastIndex)) > 0) {
               this._imTranslatedBuffer.setCharAt(index, '.');
               lastIndex = index + 1;
            }
         }
      }

      return super.inputMethodTextChanged(event);
   }

   @Override
   protected void notifyTextChanged(FormatParams aParams, boolean isInsertionOrDeletion) {
      if (this._translatedBuffer == null) {
         this._translatedBuffer = new StringBuffer();
      }

      super.notifyTextChanged(aParams, isInsertionOrDeletion);
      boolean convertSpace = this._convertSpace;
      if (!this._inBackspace) {
         this._convertSpace = true;
      }

      if (isInsertionOrDeletion) {
         int start = aParams._changedTextStart;
         if (aParams._oldLength > 0) {
            this._translatedBuffer.delete(start, start + aParams._oldLength);
         }

         if (aParams._newLength > 0) {
            this._translatedBuffer.insert(start, this.getAttributedText().getText(start, start + aParams._newLength));
         }

         if (aParams._newLength == 1 && this._translatedBuffer.charAt(start) == ' ') {
            String translated = this._translatedBuffer.toString();
            int atIndex = translated.indexOf(64, this.getLabelLength());
            if (atIndex == -1) {
               if (convertSpace) {
                  this._translatedBuffer.setCharAt(start, '@');
                  return;
               }
            } else if (convertSpace) {
               this._translatedBuffer.setCharAt(start, '.');
            }
         }
      }
   }

   @Override
   protected boolean keyChar(char key, int status, int time) {
      if (key == ' ' || key == 12288) {
         if (this.getInputContext().getActiveInputMethodID() == 512 && this._imTranslatedBuffer != null) {
            this._imTranslatedBuffer.setLength(this._imCommittedCount);
            this._imTranslatedBuffer.append(' ');
            this._imCommittedCount++;
         }

         if (this._convertSpace) {
            this._convertSpace = (status & 4) != 0 || (status & 2) == 0;
         }
      }

      return super.keyChar(key, status, time);
   }

   @Override
   protected boolean backspace() {
      if (this.getInputContext().getActiveInputMethodID() == 512) {
         if (this.getText().length() <= 1) {
            this._imTranslatedBuffer = null;
         } else if (this._imCommittedCount > 0 && this._imTranslatedBuffer != null) {
            this._imCommittedCount--;
            this._imTranslatedBuffer.setLength(this._imCommittedCount);
         }
      }

      int cursorPosition = this.getCursorPosition() + this.getLabelLength();
      if (cursorPosition > 0 && cursorPosition < this._translatedBuffer.length()) {
         char c = this._translatedBuffer.charAt(cursorPosition - 1);
         this._convertSpace = c != '@' && c != '.';
      }

      this._inBackspace = true;
      boolean ret = super.backspace();
      this._inBackspace = false;
      return ret;
   }
}
