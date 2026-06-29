package net.rim.device.apps.internal.addressbook.addresscard;

import net.rim.device.api.ui.TextChangeListener;
import net.rim.device.api.ui.component.EditField;
import net.rim.tid.awt.event.InputMethodEvent;

public final class YomiFieldTextChangeListener implements TextChangeListener {
   private EditField _yomiField;

   public YomiFieldTextChangeListener(EditField yomiField) {
      this._yomiField = yomiField;
   }

   @Override
   public final void inputMethodTextChanged(Object source, InputMethodEvent event) {
      if (source instanceof EditField && ((EditField)source).isFocus()) {
         if (event.getCommittedCharacterCount() != 0) {
            StringBuffer yomi = event.getAlternativeIdeographicReading();
            if (yomi != null) {
               int committedYomiCount = event.getAlternativeReadingCommittedCharacterCount();
               if (committedYomiCount > 0) {
                  String commitedYomi = yomi.toString().substring(0, committedYomiCount);
                  this._yomiField.setCursorPosition(this._yomiField.getTextLength());
                  this._yomiField.insert(commitedYomi, 0, false, false);
               }
            }
         }
      }
   }

   @Override
   public final void textValueChanged(Object source, int eventID) {
      if (source instanceof EditField && ((EditField)source).isFocus()) {
         EditField sourceField = (EditField)source;
         if (sourceField.getTextLength() == 0) {
            this._yomiField.clear(0);
         }
      }
   }
}
