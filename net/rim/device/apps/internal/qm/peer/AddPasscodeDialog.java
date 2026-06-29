package net.rim.device.apps.internal.qm.peer;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.component.AutoTextEditField;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.PopupScreen;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.apps.internal.qm.peer.common.FixedHeightManager;
import net.rim.device.apps.internal.qm.resource.QmResources;

final class AddPasscodeDialog extends PopupScreen implements FieldChangeListener {
   ButtonField _okButton;
   ButtonField _cancelButton;
   AutoTextEditField _question;
   AutoTextEditField _answer;
   boolean _result;

   AddPasscodeDialog() {
      super((Manager)(new Object(562949953421312L)));
      FixedHeightManager fhm = new FixedHeightManager(3);
      HorizontalFieldManager hfm = (HorizontalFieldManager)(new Object(12884901888L));
      this._okButton = (ButtonField)(new Object(QmResources.getString(51), 12884901888L));
      this._okButton.setChangeListener(this);
      this._cancelButton = (ButtonField)(new Object(CommonResources.getString(9042), 12884901888L));
      this._cancelButton.setChangeListener(this);
      hfm.add(this._okButton);
      hfm.add(this._cancelButton);
      this._question = (AutoTextEditField)(new Object(PeerResources.getString(900), "", 1000000, 2147483648L));
      this._answer = (AutoTextEditField)(new Object(PeerResources.getString(901), "", 1000000, 2147483648L));
      fhm.addField(this._question);
      fhm.addField(this._answer);
      LabelField lf = (LabelField)(new Object(PeerResources.getString(902)));
      this.add(lf);
      this.add((Field)(new Object()));
      this.add(fhm);
      this.add(hfm);
   }

   final boolean doModal() {
      Ui.getUiEngine().pushModalScreen(this);
      return this._result;
   }

   private final void close(boolean result) {
      this._result = result;
      this.close();
   }

   public final String getQuestion() {
      return this._question.getText().trim();
   }

   public final String getAnswer() {
      return this._answer.getText().trim();
   }

   @Override
   public final void fieldChanged(Field field, int context) {
      if (field == this._okButton) {
         if (this.getQuestion().length() > 0 && this.getAnswer().length() > 0) {
            this.close(true);
         } else {
            Dialog.inform(PeerResources.getString(2051));
         }
      } else {
         if (field == this._cancelButton) {
            this.close(false);
         }
      }
   }

   @Override
   protected final boolean keyChar(char key, int status, int time) {
      Field field = this.getLeafFieldWithFocus();
      switch (key) {
         case '\n':
            if (this.getQuestion().length() > 0 && this.getAnswer().length() > 0) {
               this.close(true);
               return true;
            }

            if (field == this._question) {
               this._answer.setFocus();
            }

            return true;
         case '\u001b':
            if (field == this._answer && this.getAnswer().length() > 0) {
               this._answer.setText("");
               return true;
            } else {
               if (field == this._question && this.getQuestion().length() > 0) {
                  this._question.setText("");
                  return true;
               }

               this.close(false);
               return true;
            }
         default:
            return super.keyChar(key, status, time);
      }
   }
}
