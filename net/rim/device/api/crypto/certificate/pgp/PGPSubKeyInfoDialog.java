package net.rim.device.api.crypto.certificate.pgp;

import net.rim.device.api.crypto.CryptoSystemProperties;
import net.rim.device.api.crypto.PublicKey;
import net.rim.device.api.crypto.certificate.CertificateInfoDialog;
import net.rim.device.api.crypto.certificate.CertificateResources;
import net.rim.device.api.crypto.certificate.CertificateStatus;
import net.rim.device.api.crypto.certificate.CertificateUtilities;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.util.Arrays;
import net.rim.device.internal.crypto.pgp.PGPUtilities;
import net.rim.device.internal.ui.component.TitledScrollingDialog;

public class PGPSubKeyInfoDialog extends TitledScrollingDialog {
   private ResourceBundle _rb = PGPUtilities.getResourceBundle();
   private PGPCertificate _certificate;
   private byte[] _subKeyID;
   private CryptoSystemProperties _cryptoSystemProperties;
   private static final int MAX_NUM_WARNINGS;

   public PGPSubKeyInfoDialog(PGPCertificate certificate, byte[] subKeyID, CryptoSystemProperties cryptoSystemProperties, long style) {
      super(style);
      if (certificate != null && subKeyID != null) {
         this._certificate = certificate;
         this._subKeyID = subKeyID;
         this._cryptoSystemProperties = cryptoSystemProperties;
         this.setTitle(this._certificate.getSubjectFriendlyName());
      } else {
         throw new Object();
      }
   }

   @Override
   protected void populateDialog() {
      long subKeyProperties = PGPSubKeyProperties.getPGPSubKeyProperties(
         this._certificate, this._subKeyID, System.currentTimeMillis(), this._cryptoSystemProperties
      );
      String[] warnings = this.getWarnings(subKeyProperties);
      int numWarnings = warnings.length;
      int numWarningsToDisplay = Math.min(numWarnings, 2);

      for (int i = 0; i < numWarningsToDisplay; i++) {
         this.addNonScrollingText(warnings[i]);
      }

      CertificateStatus certStatus = this._certificate.getStatus(this._subKeyID);
      this.addScrollingLabelAndValue(CertificateResources.getString(2), CertificateInfoDialog.getCertificateStatusString(certStatus));
      this.addScrollingLabelAndValue(this._rb.getString(8025), CertificateInfoDialog.getTimeString(this._certificate.getNotBefore(this._subKeyID)));
      long notAfterDate = this._certificate.getNotAfter(this._subKeyID);
      if (notAfterDate == Long.MAX_VALUE) {
         this.addScrollingLabelAndValue(CertificateResources.getString(7), CertificateResources.getString(217));
      } else {
         this.addScrollingLabelAndValue(CertificateResources.getString(7), CertificateInfoDialog.getTimeString(notAfterDate));
      }

      this.addScrollingLabelAndValue(PGPUtilities.getResourceBundle().getString(8028), CertificateUtilities.getHexAsciiString(this._subKeyID));
      PublicKey publicKey = this._certificate.getPublicKey(this._subKeyID);
      if (publicKey != null) {
         this.addScrollingLabelAndValue(CertificateResources.getString(13), CertificateInfoDialog.getPublicKeyTypeString(publicKey));
      }

      this.addScrollingLabelAndValue(CertificateResources.getString(18), this.getKeyUsageString());
      super.populateDialog();
   }

   private String getKeyUsageString() {
      StringBuffer keyUsageStringBuffer = (StringBuffer)(new Object());

      for (int i = 0; i < 15; i++) {
         long currentUsage = (long)1 << i;
         if (this._certificate.queryKeyUsage(this._subKeyID, currentUsage) == 1) {
            if (keyUsageStringBuffer.length() > 0) {
               keyUsageStringBuffer.append('\n');
            }

            keyUsageStringBuffer.append(CertificateUtilities.getKeyUsageString(currentUsage));
         }
      }

      return keyUsageStringBuffer.length() > 0 ? keyUsageStringBuffer.toString() : null;
   }

   private String[] getWarnings(long propertiesSummary) {
      String[] warnings = new Object[0];
      if ((propertiesSummary & 1024) != 0) {
         Arrays.add(warnings, this._rb.getString(8033));
      }

      if ((propertiesSummary & 22) != 0) {
         Arrays.add(warnings, this._rb.getString(8039));
      }

      if ((propertiesSummary & 256) != 0) {
         Arrays.add(warnings, this._rb.getString(8035));
      }

      if ((propertiesSummary & 8) != 0) {
         Arrays.add(warnings, this._rb.getString(8037));
      }

      if ((propertiesSummary & 32) != 0) {
         Arrays.add(warnings, this._rb.getString(8038));
      }

      if ((propertiesSummary & 512) != 0) {
         Arrays.add(warnings, this._rb.getString(8040));
      }

      return warnings;
   }
}
