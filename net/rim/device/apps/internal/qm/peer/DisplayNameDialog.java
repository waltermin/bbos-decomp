package net.rim.device.apps.internal.qm.peer;

import net.rim.device.api.ui.component.AutoTextEditField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.util.StringPattern;
import net.rim.device.api.util.StringPatternContainer;
import net.rim.device.apps.internal.qm.peer.common.OkCancelDialog;
import net.rim.device.apps.internal.qm.peer.common.OkCancelDialog$OkCancelDialogScrollManager;
import net.rim.device.apps.internal.qm.resource.QmResources;
import net.rim.device.apps.internal.smileys.Smileys;

final class DisplayNameDialog extends OkCancelDialog {
   private RichTextField _promptField;
   private AutoTextEditField _nameField;
   private OkCancelDialog$OkCancelDialogScrollManager _customVfm = new OkCancelDialog$OkCancelDialogScrollManager();
   static final int MAX_DISPLAY_NAME_LENGTH = 200;

   DisplayNameDialog(String title, String message, String oldDisplayName) {
      this.addTitle(title);
      if (!this.isEmpty(message)) {
         this._promptField = (RichTextField)(new Object(message));
         this.add(this._promptField);
      }

      StringPattern[] smileys = new Object[]{Smileys.getSmileyFacility()};
      StringPatternContainer patterns = (StringPatternContainer)(new Object(smileys));
      this._nameField = (AutoTextEditField)(new Object(null, oldDisplayName, 200, 10747904000L, patterns));
      this._customVfm.add(this._nameField);
      this.add(this._customVfm);
      this.addOkCancelButtons();
      this._nameField.setFocus();
   }

   @Override
   public final boolean validate() {
      String name = this._nameField.getText();
      String alertMessage = null;
      if (this.isEmpty(name)) {
         alertMessage = QmResources.getString(24);
      } else if (name.indexOf(44) != -1) {
         alertMessage = PeerResources.getString(2054);
      } else if (name.indexOf(58) != -1) {
         alertMessage = PeerResources.getString(2058);
      }

      if (alertMessage != null) {
         Dialog.alert(alertMessage);
         return false;
      } else {
         return true;
      }
   }

   final String getName() {
      return this._nameField.getText().trim();
   }

   @Override
   protected final boolean keyChar(char key, int status, int time) {
      switch (key) {
         case '\n':
            if (this.getLeafFieldWithFocus() == this._nameField) {
               this.close(true);
               return true;
            }
            break;
         case '\u001b':
            if (this.getLeafFieldWithFocus() == this._nameField && this._nameField.getText().length() > 0) {
               this._nameField.setText("");
               return true;
            }
      }

      return super.keyChar(key, status, time);
   }
}
