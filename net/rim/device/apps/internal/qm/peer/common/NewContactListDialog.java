package net.rim.device.apps.internal.qm.peer.common;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.component.AutoTextEditField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.apps.internal.qm.resource.QmResources;

public final class NewContactListDialog extends OkCancelDialog {
   private AutoTextEditField _nameField;
   private OkCancelDialog$OkCancelDialogScrollManager _customVfm = new OkCancelDialog$OkCancelDialogScrollManager();

   public NewContactListDialog() {
      this.addTitle(QmResources.getString(5));
      this._nameField = new AutoTextEditField(QmResources.getString(18), null, 128, 2147483648L);
      this._customVfm.add(this._nameField);
      this.add(this._customVfm);
      this.addOkCancelButtons();
   }

   @Override
   protected final boolean keyChar(char key, int status, int time) {
      if (key == '\n') {
         Field focus = this.getLeafFieldWithFocus();
         if (focus == this._nameField) {
            this.close(true);
            return true;
         }
      } else if (key == 27 && this.getLeafFieldWithFocus() == this._nameField && this._nameField.getText().trim().length() > 0) {
         this._nameField.setText("");
         return true;
      }

      return super.keyChar(key, status, time);
   }

   @Override
   public final boolean validate() {
      String name = this.getName();
      if (this.isEmpty(name)) {
         Dialog.alert(QmResources.format(11, QmResources.getString(18)));
         return false;
      } else {
         return true;
      }
   }

   public final String getName() {
      return this._nameField.getText();
   }
}
