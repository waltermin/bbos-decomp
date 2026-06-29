package net.rim.device.apps.internal.mms.options;

import net.rim.device.api.ui.component.NumericChoiceField;

final class MaximumVoiceNoteRecordSizeOptionField extends NumericChoiceField implements MMSOptionsScreen$Saveable {
   MaximumVoiceNoteRecordSizeOptionField() {
      super("Max Record Size: ", 0, 500, 1, MMSClientServiceBook.getMaxVoiceNoteRecordSize() / 1000);
   }

   @Override
   public final void saveOption() {
      if (this.isDirty()) {
         MMSClientServiceBook.setMaxVoiceNoteRecordSize(this.getSelectedIndex() * 1000);
      }
   }
}
