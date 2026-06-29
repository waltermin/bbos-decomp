package net.rim.device.apps.internal.qm.peer;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.component.AutoTextEditField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.apps.internal.qm.peer.common.FixedHeightManager;
import net.rim.device.apps.internal.qm.peer.common.OkCancelDialog;
import net.rim.device.apps.internal.qm.resource.QmResources;

final class ContactAddMessageDialog extends OkCancelDialog {
   private AutoTextEditField _messageField;

   ContactAddMessageDialog() {
      this.addTitle(QmResources.getString(7));
      String invitation = PeerResources.format(67, PeerApplication.getSession().getDisplayName());
      this._messageField = (AutoTextEditField)(new Object("", invitation, Math.max(200, invitation.length()), 2147483648L));
      FixedHeightManager fhf = new FixedHeightManager(this._messageField, 2);
      this.add((Field)(new Object(QmResources.getString(44))));
      this.add(fhf);
      this.addOkCancelButtons();
      this.addPasteButton(this._messageField);
      super._okField.setFocus();
   }

   final String getMessage() {
      return this._messageField.getText().trim();
   }

   @Override
   protected final boolean keyChar(char key, int status, int time) {
      switch (key) {
         case '\n':
            if (this.isMessageFieldFocused()) {
               super._okField.setFocus();
               return true;
            }
            break;
         case '\u001b':
            if (this.isMessageFieldFocused()) {
               this._messageField.setText("");
               return true;
            }
      }

      return super.keyChar(key, status, time);
   }

   private final boolean isMessageFieldFocused() {
      return this.getLeafFieldWithFocus() == this._messageField && this._messageField.getText().length() > 0;
   }

   @Override
   public final boolean validate() {
      if (this.isEmpty(this.getMessage())) {
         Dialog.alert(QmResources.format(11, QmResources.getString(44)));
         return false;
      } else {
         return true;
      }
   }
}
