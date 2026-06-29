package net.rim.device.apps.internal.qm.peer;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.component.AutoTextEditField;
import net.rim.device.api.ui.component.RadioButtonField;
import net.rim.device.api.ui.component.RadioButtonGroup;
import net.rim.device.api.util.Arrays;
import net.rim.device.apps.internal.qm.peer.common.OkCancelDialog;
import net.rim.device.apps.internal.qm.peer.common.OkCancelDialog$OkCancelDialogScrollManager;
import net.rim.device.apps.internal.qm.peer.common.QmRadioButton;
import net.rim.device.apps.internal.qm.resource.QmResources;

final class UserStatusDialog extends OkCancelDialog {
   private PeerApplication _application = PeerApplication.getInstance();
   private RadioButtonGroup _radioButtonGroup;
   private RadioButtonField[] _radioButtons = new RadioButtonField[0];
   private AutoTextEditField _messageField;
   private OkCancelDialog$OkCancelDialogScrollManager _customVfm = new OkCancelDialog$OkCancelDialogScrollManager();

   UserStatusDialog() {
      this.addTitle(QmResources.getString(76));
      this._radioButtonGroup = new RadioButtonGroup();
      this.addButton(PeerResources.getString(2033));
      this.addButton(PeerResources.getString(2034));
      if (this._application.isUserAvailable()) {
         this._radioButtonGroup.setSelectedIndex(0);
         this._radioButtons[1].setFocus();
      } else {
         this._radioButtonGroup.setSelectedIndex(1);
         this._radioButtons[0].setFocus();
      }

      this._messageField = new AutoTextEditField(QmResources.getString(44) + ' ', this._application.getCustomStatusMessage(), 252, 2147483648L);
      this._messageField.setChangeListener(this);
      this._customVfm.add(this._messageField);
      this.add(this._customVfm);
      this.addOkCancelButtons();
   }

   private final void addButton(String label) {
      RadioButtonField field = new QmRadioButton(label);
      field.setChangeListener(this);
      this._radioButtonGroup.add(field);
      Arrays.add(this._radioButtons, field);
      this.add(field);
   }

   final boolean isAvailable() {
      return this._radioButtonGroup.getSelectedIndex() == 0;
   }

   final String getMessage() {
      return this._messageField.getText().trim();
   }

   @Override
   public final void fieldChanged(Field field, int context) {
      if (this.isDisplayed()) {
         super.fieldChanged(field, context);
         if (field instanceof RadioButtonField) {
            this._messageField.setFocus();
            this._messageField.setCursorPosition(this._messageField.getTextLength());
         }
      }
   }

   @Override
   protected final boolean keyChar(char key, int status, int time) {
      Field focusField = this.getLeafFieldWithFocus();
      if (focusField == this._messageField) {
         if (key == '\n') {
            this.close(true);
            return true;
         }

         if (key == 27 && this._messageField.getText().length() > 0) {
            this._messageField.setText("");
            return true;
         }
      }

      return super.keyChar(key, status, time);
   }
}
