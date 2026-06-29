package net.rim.device.apps.api.transmission.rim.otasync;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.component.ChoiceField;

class DeleteOnWirelessDeletesListener implements FieldChangeListener {
   private ChoiceField _deleteOnField;
   private ChoiceField _wirelessDeletesField;

   DeleteOnWirelessDeletesListener(ChoiceField deleteOnField, ChoiceField wirelessDeletesField) {
      this._deleteOnField = deleteOnField;
      this._wirelessDeletesField = wirelessDeletesField;
   }

   @Override
   public void fieldChanged(Field field, int context) {
      int deleteOnIndex = this._deleteOnField.getSelectedIndex();
      int wirelessDeletesIndex = this._wirelessDeletesField.getSelectedIndex();
      if (field == this._deleteOnField) {
         if (deleteOnIndex == 0) {
            this._wirelessDeletesField.setSelectedIndex(0);
            return;
         }
      } else if (field == this._wirelessDeletesField && wirelessDeletesIndex == 1 && deleteOnIndex == 0) {
         this._deleteOnField.setSelectedIndex(1);
      }
   }
}
