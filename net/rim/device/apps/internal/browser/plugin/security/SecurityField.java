package net.rim.device.apps.internal.browser.plugin.security;

import net.rim.device.api.crypto.certificate.Certificate;
import net.rim.device.api.crypto.certificate.CertificateImporterFactory;
import net.rim.device.api.crypto.certificate.CertificateSummaryFieldFactory;
import net.rim.device.api.crypto.certificate.CertificateUtilities;
import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.i18n.ResourceBundleFamily;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.internal.i18n.CommonResource;
import net.rim.device.internal.ui.container.VerticalIndentFieldManager;

public final class SecurityField extends VerticalIndentFieldManager implements FieldChangeListener {
   private Certificate _certificate;
   private CertificateSummaryFieldFactory _factory;
   private ButtonField _viewButton;
   private ButtonField _importButton;
   private String _contentType;
   private static ResourceBundleFamily _rb = ResourceBundle.getBundle(1626664884155368124L, "net.rim.device.apps.internal.resource.SecurityPlugin");

   public SecurityField(long flags, byte[] data, String contentType) {
      super(flags | 35184372088832L | 281474976710656L);
      this._contentType = contentType;
      if (data != null) {
         this._certificate = CertificateUtilities.readCertificateFile(this.getCertificateTypeFromContentType(), data);
         if (this._certificate != null) {
            Font boldFont = Font.getDefault();
            boldFont = boldFont.derive(boldFont.getStyle() | 1);
            String certificateType = this._certificate.getType();
            RichTextField textField;
            if (StringUtilities.strEqualIgnoreCase(certificateType, "PGP")) {
               textField = (RichTextField)(new Object(_rb.getString(4)));
            } else {
               textField = (RichTextField)(new Object(MessageFormat.format(_rb.getString(6), new Object[]{certificateType})));
            }

            this.add(textField);
            this._factory = CertificateSummaryFieldFactory.getCertificateSummaryFieldFactory(this._certificate);
            Field certSummaryField = this._factory.createCertificateSummaryPageField(this._certificate);
            this.add(certSummaryField);
            HorizontalFieldManager hfm = (HorizontalFieldManager)(new Object(12884901888L));
            this._viewButton = (ButtonField)(new Object(CommonResource.getString(10015), 65536));
            this._viewButton.setChangeListener(this);
            hfm.add(this._viewButton);
            if (!this._factory.getKeyStore().isMember(this._certificate)) {
               this._importButton = (ButtonField)(new Object(_rb.getString(1), 65536));
               this._importButton.setChangeListener(this);
               hfm.add(this._importButton);
            }

            this.add(hfm);
         }
      }

      if (this._certificate == null) {
         RichTextField textField = (RichTextField)(new Object(_rb.getString(2)));
         RichTextField textField2 = (RichTextField)(new Object(_rb.getString(3)));
         this.add(textField);
         this.add(textField2);
      }
   }

   private final String getCertificateTypeFromContentType() {
      if (this._contentType == null) {
         return null;
      } else if (this._contentType.indexOf("x509") != -1) {
         return "X509";
      } else if (this._contentType.indexOf("wap") != -1) {
         return "WTLS";
      } else if (this._contentType.indexOf("pgp") != -1) {
         return "PGP";
      } else {
         return this._contentType.indexOf("octet") != -1 ? "PGP" : null;
      }
   }

   @Override
   public final void fieldChanged(Field field, int context) {
      if (field == this._viewButton) {
         CertificateUtilities.displayCertificateDetails(this._certificate);
      } else {
         if (field == this._importButton
            && CertificateImporterFactory.importCertificate(
               this._certificate, null, this._certificate.getSubjectFriendlyName(), this._factory.getKeyStore(), null
            )) {
            this._importButton.getManager().delete(this._importButton);
         }
      }
   }
}
