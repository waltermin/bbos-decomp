package net.rim.device.api.crypto.certificate.pgp;

import net.rim.device.api.crypto.certificate.Certificate;
import net.rim.device.api.crypto.certificate.CertificateSummaryFieldFactory;
import net.rim.device.api.crypto.certificate.CertificateUtilities;
import net.rim.device.api.crypto.keystore.KeyStore;
import net.rim.device.api.crypto.keystore.PGPKeyStore;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.internal.crypto.pgp.PGPUtilities;
import net.rim.device.internal.ui.container.VerticalIndentFieldManager;

public class PGPCertificateSummaryFieldFactory extends CertificateSummaryFieldFactory {
   protected ButtonField _viewButton;
   protected ButtonField _importButton;
   private ResourceBundle _rb = PGPUtilities.getResourceBundle();

   @Override
   public Field createCertificateSummaryPageField(Certificate certificate) {
      VerticalIndentFieldManager manager = new VerticalIndentFieldManager();
      String[] userIDs = ((PGPCertificate)certificate).getUserIDs();
      Font boldFont = Font.getDefault();
      boldFont = boldFont.derive(boldFont.getStyle() | 1);
      LabelField userLabelField = new LabelField(this._rb.getString(8041));
      userLabelField.setFont(boldFont);
      manager.add(userLabelField);

      for (int i = 0; i < userIDs.length; i++) {
         manager.add(new RichTextField(userIDs[i]), 12);
      }

      LabelField fingerLabelField = new LabelField(this._rb.getString(8045));
      fingerLabelField.setFont(boldFont);
      manager.add(fingerLabelField);
      RichTextField fingerField = new RichTextField(CertificateUtilities.getHexAsciiString(((PGPCertificate)certificate).getFingerprint()));
      manager.add(fingerField, 12);
      return manager;
   }

   @Override
   protected String getType() {
      return "PGP";
   }

   @Override
   public KeyStore getKeyStore() {
      return PGPKeyStore.getInstance();
   }
}
