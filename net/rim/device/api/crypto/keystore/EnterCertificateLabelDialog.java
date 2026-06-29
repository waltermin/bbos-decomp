package net.rim.device.api.crypto.keystore;

import net.rim.device.api.crypto.certificate.Certificate;
import net.rim.device.api.crypto.certificate.CertificateUtilities;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.component.BasicEditField;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.internal.i18n.CommonResource;
import net.rim.device.internal.ui.component.SimpleOKCancelInputDialog;

final class EnterCertificateLabelDialog extends SimpleOKCancelInputDialog {
   private ButtonField _viewButton;
   private Certificate _certificate;
   private KeyStore _keyStore;

   public EnterCertificateLabelDialog(String dialogPrompt, Certificate certificate, KeyStore keyStore, String defaultLabel, long style) {
      super(11, dialogPrompt, 1, 1000000, style);
      this._certificate = certificate;
      this._keyStore = keyStore;
      this.setText(defaultLabel);
      if (certificate != null) {
         this._viewButton = (ButtonField)(new Object(CommonResource.getString(10015), 65536));
         this._viewButton.setChangeListener(this);
         Manager buttonFieldManager = this.getButtonFieldManager();
         buttonFieldManager.add(this._viewButton);
      }

      this.setCancelAllowed(true);
   }

   private final boolean view() {
      CertificateUtilities.displayCertificateDetails(this._certificate, this._keyStore);
      return true;
   }

   @Override
   public final void fieldChanged(Field field, int context) {
      if (field == this._viewButton) {
         this.view();
      } else {
         super.fieldChanged(field, context);
      }
   }

   @Override
   public final boolean navigationClick(int status, int time) {
      Field field = this.getDelegate().getLeafFieldWithFocus();
      return field == this._viewButton ? this.view() : super.navigationClick(status, time);
   }

   @Override
   protected final boolean keyChar(char key, int status, int time) {
      if (key == '\n') {
         Field field = this.getDelegate().getLeafFieldWithFocus();
         if (field == this._viewButton) {
            return this.view();
         }
      }

      return super.keyChar(key, status, time);
   }

   @Override
   public final boolean accept() {
      BasicEditField editField = this.getEditField();
      editField.setText(this.getText().trim());
      return super.accept();
   }
}
