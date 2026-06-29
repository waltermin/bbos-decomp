package net.rim.device.apps.internal.options;

import net.rim.device.api.ui.TextChangeListener;
import net.rim.device.api.ui.component.BasicEditField;
import net.rim.tid.awt.event.InputMethodEvent;

public class CustomDictUnitEditor$JapaneseCustomDictEditorScreen$WordFieldTextChangeListener implements TextChangeListener {
   private BasicEditField _readingField;
   private final CustomDictUnitEditor$JapaneseCustomDictEditorScreen this$1;

   public CustomDictUnitEditor$JapaneseCustomDictEditorScreen$WordFieldTextChangeListener(
      CustomDictUnitEditor$JapaneseCustomDictEditorScreen _1, BasicEditField readingField
   ) {
      this.this$1 = _1;
      this._readingField = readingField;
   }

   @Override
   public void inputMethodTextChanged(Object source, InputMethodEvent event) {
      if (source instanceof BasicEditField && ((BasicEditField)source).isFocus()) {
         if (event.getCommittedCharacterCount() != 0) {
            StringBuffer reading = event.getOriginalReading();
            if (reading != null) {
               int committedReadingCount = event.getOriginalReadingCommitedCharacterCount();
               if (committedReadingCount > 0) {
                  String commitedReading = reading.toString().substring(0, committedReadingCount);
                  this._readingField.setCursorPosition(this._readingField.getTextLength());
                  this._readingField.insert(commitedReading, 0, false, false);
               }
            }
         }
      }
   }

   @Override
   public void textValueChanged(Object source, int eventID) {
      if (source instanceof BasicEditField && ((BasicEditField)source).isFocus()) {
         BasicEditField sourceField = (BasicEditField)source;
         if (sourceField.getTextLength() == 0) {
            this._readingField.clear(0);
         }
      }
   }
}
