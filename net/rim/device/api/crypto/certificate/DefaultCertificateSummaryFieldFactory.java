package net.rim.device.api.crypto.certificate;

import net.rim.device.api.crypto.keystore.DeviceKeyStore;
import net.rim.device.api.crypto.keystore.KeyStore;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.internal.ui.container.VerticalIndentFieldManager;

public class DefaultCertificateSummaryFieldFactory extends CertificateSummaryFieldFactory {
   @Override
   public Field createCertificateSummaryPageField(Certificate certificate) {
      VerticalIndentFieldManager manager = (VerticalIndentFieldManager)(new Object());
      Font boldFont = Font.getDefault();
      boldFont = boldFont.derive(boldFont.getStyle() | 1);
      LabelField subjectLabelField = (LabelField)(new Object(CertificateResources.getString(3)));
      subjectLabelField.setFont(boldFont);
      manager.add(subjectLabelField);
      RichTextField subjectField = (RichTextField)(new Object(CertificateUtilities.formatDistinguishedName(certificate.getSubject(), '\n')));
      manager.add(subjectField, 12);
      LabelField issuerLabelField = (LabelField)(new Object(CertificateResources.getString(9)));
      issuerLabelField.setFont(boldFont);
      manager.add(issuerLabelField);
      RichTextField issuerField = (RichTextField)(new Object(CertificateUtilities.formatDistinguishedName(certificate.getIssuer(), '\n')));
      manager.add(issuerField, 12);
      return manager;
   }

   @Override
   protected String getType() {
      return "Default";
   }

   @Override
   public KeyStore getKeyStore() {
      return DeviceKeyStore.getInstance();
   }
}
