package net.rim.device.apps.internal.sms.ui;

import net.rim.device.api.system.Clipboard;
import net.rim.device.api.ui.component.ActiveAutoTextEditField;

public final class SMSEditField extends ActiveAutoTextEditField {
   private SMSFilter _filter;

   public SMSEditField(String label, String initialValue, int maxNumChars, long style, int messageCoding) {
      super(label, initialValue, maxNumChars, style | 8388608);
      this.updateTextFilter(messageCoding);
   }

   public final void updateTextFilter(int messageCoding) {
      this._filter = new SMSFilter(true, messageCoding);
      this.setFilter(this._filter);
   }

   public final int getEncodedLength() {
      return this._filter.getEncodedLength(this.getTextAbstractString());
   }

   @Override
   protected final boolean insert(char key, int status) {
      if (this.getMaxSize() - this.getEncodedLength() < this._filter.getEncodedLength(key)) {
         this.displayFieldFullMessage();
         return false;
      } else {
         return super.insert(key, status);
      }
   }

   @Override
   public final int insert(String text, int context, boolean stripInvalid, boolean validateText) {
      if (this.getMaxSize() - this.getEncodedLength() < this._filter.getEncodedLength(text)) {
         this.displayFieldFullMessage();
         return 0;
      } else {
         return super.insert(text, context, stripInvalid, validateText);
      }
   }

   @Override
   public final boolean paste(Clipboard cb) {
      int currEncodedLength = this.getEncodedLength();
      int maxSize = this.getMaxSize();
      if (currEncodedLength == maxSize) {
         this.displayFieldFullMessage();
         return false;
      }

      String cbString = cb.toString();
      Object originalContents = null;
      if (this._filter.getEncodedLength(cbString) + currEncodedLength > maxSize) {
         int currIndex = 0;
         int lastValidIndex = 0;

         while (currEncodedLength <= maxSize) {
            lastValidIndex = currIndex;
            currEncodedLength += this._filter.getEncodedLength(cbString.charAt(currIndex));
            currIndex++;
         }

         String newString = cbString.substring(0, lastValidIndex);
         if (newString.length() == 0) {
            this.displayFieldFullMessage();
            return false;
         }

         originalContents = cb.get();
         cb.put(newString);
      }

      boolean retVal = super.paste(cb);
      if (originalContents != null) {
         cb.put(originalContents);
      }

      return retVal;
   }
}
