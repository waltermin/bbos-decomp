package net.rim.device.apps.internal.addressbook.addresscard;

import net.rim.device.api.ui.component.AutoTextEditField;
import net.rim.device.api.ui.component.EditField;

class PersonalNameField extends AutoTextEditField {
   private boolean _removeAllOnFirstKey;
   private YomiField _peerYomiField;

   public PersonalNameField() {
   }

   public PersonalNameField(String label, String initialValue, int maxNumChars, long style) {
      super(label, initialValue, maxNumChars, style);
   }

   public void setRemoveAllOnFirstKey(boolean removeAllOnFirstKey) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   public void setPeerYomiField(YomiField peerYomiField) {
      this._peerYomiField = peerYomiField;
      if (peerYomiField != null) {
         this._peerYomiField.addTextChangeListener(new PersonalNameField$1(this));
      }
   }

   @Override
   public void focusChangeNotify(int action) {
      super.focusChangeNotify(action);
      if (this._removeAllOnFirstKey) {
         if (action == 1) {
            this.setSelection(this.getAttributedText().length(), true, this.getLabelLength());
            if (this._peerYomiField != null) {
               this._peerYomiField
                  .setSelection(this._peerYomiField.getTextLength() + this._peerYomiField.getLabelLength(), true, this._peerYomiField.getLabelLength());
               return;
            }
         } else if (action == 3) {
            this.removeSelection(this);
            this.removeSelection(this._peerYomiField);
         }
      }
   }

   private void removeSelection(EditField editField) {
      editField.select(true);
      editField.select(false);
   }

   private void handleYomiFieldTextChange(Object source) {
      if (source instanceof EditField && ((EditField)source).isFocus()) {
         if (this._removeAllOnFirstKey) {
            this._removeAllOnFirstKey = false;
         }
      }
   }

   @Override
   public int processKeyEvent(int event, char key, int keycode, int time) {
      if (this._removeAllOnFirstKey) {
         this._removeAllOnFirstKey = false;
         this.clear(0);
      }

      return super.processKeyEvent(event, key, keycode, time);
   }
}
