package net.rim.device.api.crypto.certificate;

import net.rim.device.api.crypto.keystore.KeyStore;
import net.rim.device.api.crypto.keystore.KeyStoreResources;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.internal.ui.component.PopupDialog;

public class CertificateChoiceDialog extends PopupDialog implements FieldChangeListener {
   private VerticalFieldManager _vfm;
   private ButtonField _okButton;
   private ButtonField _cancelButton;
   private ButtonField _importSmartCardCertsButton;
   private ButtonField _displayCertButton;
   private HorizontalFieldManager _buttonManager;
   private CertificateChoiceField _certificateChoiceField;
   private KeyStore _keyStore;
   public static final int IMPORT_SMART_CARD_CERTS = 1;
   private static final ResourceBundle _smartCardRB = ResourceBundle.getBundle(7215549882295292649L, "net.rim.device.internal.resource.SmartCard");

   public CertificateChoiceDialog(
      String message, CertificateChoiceField choiceField, boolean showCancelButton, boolean importSmartCardCertsButton, KeyStore keyStore, long style
   ) {
      super(new VerticalFieldManager(281474976710656L), style);
      this._keyStore = keyStore;
      this.initialize(message, choiceField, showCancelButton, importSmartCardCertsButton);
   }

   private void initialize(String message, CertificateChoiceField choiceField, boolean showCancelButton, boolean importSmartCardCertsButton) {
      this._vfm = (VerticalFieldManager)this.getDelegate();
      this._vfm.add(new LabelField(message));
      this._vfm.add(new SeparatorField());
      this._certificateChoiceField = choiceField;
      this._vfm.add(this._certificateChoiceField);
      this._displayCertButton = new ButtonField(KeyStoreResources.getString(14));
      this._displayCertButton.setChangeListener(this);
      HorizontalFieldManager hfm = new HorizontalFieldManager(8589934592L);
      hfm.add(this._displayCertButton);
      this._vfm.add(hfm);
      this._vfm.add(new SeparatorField());
      this._buttonManager = new HorizontalFieldManager(12884901888L);
      this._okButton = new ButtonField(CommonResources.getString(117));
      this._okButton.setChangeListener(this);
      this._buttonManager.add(this._okButton);
      if (showCancelButton) {
         this._cancelButton = new ButtonField(CommonResources.getString(9042));
         this._cancelButton.setChangeListener(this);
         this._buttonManager.add(this._cancelButton);
      }

      this._vfm.add(this._buttonManager);
      if (importSmartCardCertsButton) {
         this._importSmartCardCertsButton = new ButtonField(_smartCardRB.getString(31));
         this._importSmartCardCertsButton.setChangeListener(this);
         hfm = new HorizontalFieldManager(12884901888L);
         hfm.add(this._importSmartCardCertsButton);
         this._vfm.add(hfm);
      }
   }

   public Certificate getSelectedCertificate() {
      return (Certificate)this._certificateChoiceField.getSelectedCertificate();
   }

   @Override
   public void fieldChanged(Field field, int context) {
      if (field == this._okButton) {
         this.close(0);
      } else if (field == this._cancelButton) {
         this.close(-1);
      } else if (field == this._importSmartCardCertsButton) {
         this.close(1);
      } else {
         if (field == this._displayCertButton) {
            Certificate c = (Certificate)this.getSelectedCertificate();
            if (c != null) {
               CertificateUtilities.displayCertificateDetails(c, null, this._keyStore, null, false, null, true);
            }
         }
      }
   }

   @Override
   protected boolean keyChar(char key, int status, int time) {
      if (!super.keyChar(key, status, time) && key == 27 && this.isCancelAllowed()) {
         this.close(-1);
         return true;
      } else {
         return false;
      }
   }
}
