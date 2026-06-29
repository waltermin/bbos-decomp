package net.rim.device.apps.internal.qm.peer.common;

import net.rim.device.api.ui.component.AutoTextEditField;
import net.rim.device.apps.internal.qm.resource.QmResources;

public final class RenameContactListDialog extends OkCancelDialog {
   AutoTextEditField _nameField;
   private OkCancelDialog$OkCancelDialogScrollManager _customVfm = new OkCancelDialog$OkCancelDialogScrollManager();

   public RenameContactListDialog(String oldContactListName) {
      this.addTitle(QmResources.getString(57));
      this._nameField = QmThemeFactory.createThemedAutoTextEditField(null, oldContactListName, 128, 10737418240L);
      this._customVfm.add(this._nameField);
      this.add(this._customVfm);
      this.addOkCancelButtons();
      this._nameField.setFocus();
   }

   public final String getName() {
      return this._nameField.getText().trim();
   }

   @Override
   protected final boolean keyChar(char key, int status, int time) {
      if (key == '\n' && this.getLeafFieldWithFocus() == this._nameField) {
         this.close(true);
         return true;
      } else if (key == 27 && this.getLeafFieldWithFocus() == this._nameField && this._nameField.getText().length() > 0) {
         this._nameField.setText("");
         return true;
      } else {
         return super.keyChar(key, status, time);
      }
   }

   @Override
   public final boolean validate() {
      String name = this.getName();
      if (this.isEmpty(name)) {
         QmThemedDialog.alert(QmResources.format(11, ""));
         return false;
      } else {
         return true;
      }
   }
}
