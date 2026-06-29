package net.rim.device.apps.internal.mms.options;

import net.rim.device.api.ui.component.NumericChoiceField;

final class MaximumVoiceNoteRecordTimeOptionField extends NumericChoiceField implements MMSOptionsScreen$Saveable {
   MaximumVoiceNoteRecordTimeOptionField() {
      super("Max Record Time: ", 0, 600, 1, MMSClientServiceBook.getMaxVoiceNoteRecordTime());
   }

   @Override
   public final void saveOption() {
      if (this.isDirty()) {
         MMSClientServiceBook.setMaxVoiceNoteRecordTime(this.getSelectedIndex());
      }
   }
}
