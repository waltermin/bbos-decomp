package net.rim.device.apps.internal.qm.peer;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.component.ActiveAutoTextEditField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.util.StringPattern;
import net.rim.device.api.util.StringPatternContainer;
import net.rim.device.apps.internal.qm.peer.common.OkCancelDialog;
import net.rim.device.apps.internal.qm.resource.QmResources;
import net.rim.device.apps.internal.smileys.Smileys;

final class RenameContactDialog extends OkCancelDialog {
   EditField _nameField;

   RenameContactDialog(PeerContact contact) {
      this.addTitle(QmResources.getString(58));
      StringPattern[] smileys = new StringPattern[]{Smileys.getSmileyFacility()};
      StringPatternContainer patterns = new StringPatternContainer(smileys);
      this._nameField = new ActiveAutoTextEditField("", contact.getDisplayName(), 1000000, 10737418240L, patterns);
      HorizontalFieldManager hfm = new HorizontalFieldManager(1125899906842624L);
      hfm.add(this._nameField);
      this.add(hfm);
      this.addOkCancelButtons();
      this._nameField.setFocus();
   }

   final String getName() {
      return this._nameField.getText().trim();
   }

   @Override
   protected final boolean keyChar(char key, int status, int time) {
      if (key == '\n') {
         this.close(true);
         return true;
      } else {
         Field field = this.getLeafFieldWithFocus();
         if (key == 27 && field == this._nameField && this.getName().length() > 0) {
            this._nameField.setText("");
            return true;
         } else {
            return super.keyChar(key, status, time);
         }
      }
   }

   @Override
   public final boolean validate() {
      String name = this.getName();
      if (this.isEmpty(name)) {
         Dialog.alert(QmResources.format(11, ""));
         return false;
      }

      if (name != null) {
         if (name.indexOf(44) != -1) {
            Dialog.alert(PeerResources.getString(2054));
            return false;
         }

         if (name.indexOf(58) != -1) {
            Dialog.alert(PeerResources.getString(2058));
            return false;
         }
      }

      return true;
   }
}
