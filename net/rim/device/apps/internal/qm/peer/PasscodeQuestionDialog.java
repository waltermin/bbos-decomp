package net.rim.device.apps.internal.qm.peer;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.component.AutoTextEditField;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.PopupScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.apps.internal.qm.peer.common.FixedHeightManager;
import net.rim.device.apps.internal.qm.resource.QmResources;

final class PasscodeQuestionDialog extends PopupScreen implements FieldChangeListener {
   String _passcodeQuestion;
   ButtonField _okButton;
   ButtonField _cancelButton;
   AutoTextEditField _answer;
   boolean _result;

   PasscodeQuestionDialog(String question) {
      super(new VerticalFieldManager(562949953421312L));
      this._passcodeQuestion = question;
      this.createDialog();
   }

   final void createDialog() {
      FixedHeightManager fhm = null;
      if (this._passcodeQuestion == null) {
         fhm = new FixedHeightManager(2);
      } else {
         fhm = new FixedHeightManager(4);
      }

      HorizontalFieldManager hfm = new HorizontalFieldManager(12884901888L);
      this._okButton = new ButtonField(QmResources.getString(51), 12884901888L);
      this._okButton.setChangeListener(this);
      this._cancelButton = new ButtonField(CommonResources.getString(9042), 12884901888L);
      this._cancelButton.setChangeListener(this);
      hfm.add(this._okButton);
      hfm.add(this._cancelButton);
      if (this._passcodeQuestion != null) {
         RichTextField rtf = new RichTextField(this._passcodeQuestion);
         fhm.addField(rtf);
      }

      this._answer = new AutoTextEditField(PeerResources.getString(901), "", 1000000, 2147483648L);
      fhm.addField(this._answer);
      LabelField lf = new LabelField(PeerResources.getString(904));
      this.add(lf);
      this.add(new SeparatorField());
      this.add(fhm);
      this.add(hfm);
      this._answer.setFocus();
   }

   final boolean doModal() {
      Ui.getUiEngine().pushModalScreen(this);
      return this._result;
   }

   private final void close(boolean result) {
      this._result = result;
      this.close();
   }

   public final String getAnswer() {
      return this._answer.getText().trim();
   }

   @Override
   public final void fieldChanged(Field field, int context) {
      if (field == this._okButton) {
         if (this.getAnswer().length() > 0) {
            this.close(true);
         } else {
            this.close(false);
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
      if (key == '\n' && field == this._answer) {
         if (this.getAnswer().length() > 0) {
            this.close(true);
            return true;
         }
      } else if (key == 27) {
         if (field == this._answer && this.getAnswer().length() > 0) {
            this._answer.setText("");
            return true;
         }

         this.close(false);
         return true;
      }

      return super.keyChar(key, status, time);
   }
}
