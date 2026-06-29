package net.rim.device.api.crypto.certificate.pgp;

import java.util.Hashtable;
import net.rim.device.api.collection.CollectionListener;
import net.rim.device.api.crypto.CryptoSystemProperties;
import net.rim.device.api.crypto.certificate.Certificate;
import net.rim.device.api.crypto.certificate.CertificateInfoDialog;
import net.rim.device.api.crypto.certificate.CertificateResources;
import net.rim.device.api.crypto.certificate.CertificateUtilities;
import net.rim.device.api.crypto.keystore.CertificateStatusManagerTicket;
import net.rim.device.api.crypto.keystore.KeyStore;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.component.BitmapField;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.util.Arrays;
import net.rim.device.internal.crypto.pgp.PGPUtilities;
import net.rim.device.internal.ui.component.BackgroundDialog;

public class PGPCertificateInfoDialog extends CertificateInfoDialog implements CollectionListener {
   private ResourceBundle _rb = PGPUtilities.getResourceBundle();

   public PGPCertificateInfoDialog(
      Certificate certificate,
      Certificate[] certificatePool,
      KeyStore keyStore,
      CryptoSystemProperties cryptoSystemProperties,
      boolean allowFetchStatus,
      CertificateStatusManagerTicket ticket,
      long style
   ) {
      this(certificate, certificatePool, keyStore, cryptoSystemProperties, allowFetchStatus, ticket, null, style);
   }

   protected PGPCertificateInfoDialog(
      Certificate certificate,
      Certificate[] certificatePool,
      KeyStore keyStore,
      CryptoSystemProperties cryptoSystemProperties,
      boolean allowFetchStatus,
      CertificateStatusManagerTicket ticket,
      Hashtable alreadyViewedCertificates,
      long style
   ) {
      super(certificate, certificatePool, keyStore, cryptoSystemProperties, allowFetchStatus, ticket, alreadyViewedCertificates, style);
   }

   @Override
   protected String[] getWarnings(long propertiesSummary) {
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

      if ((propertiesSummary & 2048) != 0) {
         Arrays.add(warnings, this._rb.getString(8046));
      }

      if ((propertiesSummary & 512) != 0) {
         Arrays.add(warnings, this._rb.getString(8040));
      }

      return warnings;
   }

   @Override
   protected String getKeyUsageString() {
      PGPCertificate pgpCertificate = (PGPCertificate)super._certificate;
      byte[] keyID = pgpCertificate.getKeyID();
      StringBuffer keyUsageStringBuffer = (StringBuffer)(new Object());

      for (int i = 0; i < 15; i++) {
         long currentUsage = (long)1 << i;
         if (pgpCertificate.queryKeyUsage(keyID, currentUsage) == 1) {
            if (keyUsageStringBuffer.length() > 0) {
               keyUsageStringBuffer.append('\n');
            }

            keyUsageStringBuffer.append(CertificateUtilities.getKeyUsageString(currentUsage));
         }
      }

      return keyUsageStringBuffer.length() > 0 ? keyUsageStringBuffer.toString() : null;
   }

   @Override
   protected void addFields() {
      PGPCertificate pgpCertificate = (PGPCertificate)super._certificate;
      this.addStatusFields();
      this.addTrustFields();
      this.addScrollingLabelAndValue(this._rb.getString(8025), CertificateInfoDialog.getTimeString(super._certificate.getNotBefore()));
      long notAfterDate = super._certificate.getNotAfter();
      if (notAfterDate == Long.MAX_VALUE) {
         this.addScrollingLabelAndValue(CertificateResources.getString(7), CertificateResources.getString(217));
      } else {
         this.addScrollingLabelAndValue(CertificateResources.getString(7), CertificateInfoDialog.getTimeString(notAfterDate));
      }

      String[] userIDs = pgpCertificate.getUserIDs();
      int numUserIDs = userIDs == null ? 0 : userIDs.length;

      for (int i = 0; i < numUserIDs; i++) {
         this.addScrollingLabelAndValue(this._rb.getString(8041), userIDs[i]);
      }

      byte[] parentKeyID = pgpCertificate.getKeyID();
      this.addScrollingLabelAndValue(this._rb.getString(29), CertificateUtilities.getHexAsciiString(parentKeyID));
      if (super._keyStore != null) {
         super._keyStore.addIndex(new PGPKeyIDKeyStoreIndex());
      }

      this.addPublicKeyTypeFields();
      int numCertificateChains = super._certificateChains.length;
      Hashtable buttonsAdded = (Hashtable)(new Object(numCertificateChains));

      for (int i = 0; i < numCertificateChains; i++) {
         Certificate[] currentCertificateChain = super._certificateChains[i];
         if (currentCertificateChain.length > 1) {
            Certificate currentSignerCertificate = currentCertificateChain[1];
            if (!buttonsAdded.containsKey(currentSignerCertificate) && !super._alreadyViewedCertificates.containsKey(currentSignerCertificate)) {
               buttonsAdded.put(currentSignerCertificate, currentSignerCertificate);
               this.addScrollingLabelAndValue(this._rb.getString(8043), currentSignerCertificate.getSubjectFriendlyName());
               ButtonField viewCertField = (ButtonField)(new Object(this._rb.getString(8042), 12884901888L));
               viewCertField.setChangeListener(this);
               viewCertField.setCookie(currentSignerCertificate);
               this.addScrollingField(viewCertField);
            }
         }
      }

      this.addScrollingLabelAndValue(CertificateResources.getString(18), this.getKeyUsageString());
      byte[][] subKeyIDs = pgpCertificate.getSubKeyIDs();
      int subKeyIDsLength = subKeyIDs == null ? 0 : subKeyIDs.length;

      for (int i = 0; i < subKeyIDsLength; i++) {
         this.addScrollingLabelAndValue(this._rb.getString(8028), CertificateUtilities.getHexAsciiString(subKeyIDs[i]));
         ButtonField viewButton = (ButtonField)(new Object(this._rb.getString(8029), 12884967424L));
         viewButton.setCookie(subKeyIDs[i]);
         viewButton.setChangeListener(this);
         this.addScrollingField(viewButton);
      }

      Bitmap[] userImages = pgpCertificate.getUserImages();
      int numUserImages = userImages == null ? 0 : userImages.length;

      for (int i = 0; i < numUserImages; i++) {
         LabelField photoLabelField = (LabelField)(new Object(this._rb.getString(8044), 36028797018964032L));
         photoLabelField.setFont(super._boldFont);
         this.addScrollingField(photoLabelField);
         BitmapField photoField = (BitmapField)(new Object(userImages[i], 18014411394383872L));
         this.addScrollingField(photoField);
      }

      this.addCustomDisplayFields();
      String fingerprint = CertificateUtilities.getHexAsciiString(pgpCertificate.getFingerprint());
      this.addScrollingLabelAndValue(this._rb.getString(8045), fingerprint);
   }

   @Override
   protected boolean handleFieldChanged(Field field, int context) {
      if (super.handleFieldChanged(field, context)) {
         return true;
      } else {
         Object cookie = field.getCookie();
         if (field instanceof Object && cookie instanceof byte[]) {
            byte[] subKeyID = (byte[])cookie;
            PGPSubKeyInfoDialog subKeyDialog = new PGPSubKeyInfoDialog((PGPCertificate)super._certificate, subKeyID, super._cryptoSystemProperties, 134217728);
            BackgroundDialog.show(subKeyDialog);
            return true;
         } else {
            return false;
         }
      }
   }

   @Override
   protected CertificateInfoDialog createNewDialog(
      Certificate certificate,
      Certificate[] certificatePool,
      KeyStore keyStore,
      CryptoSystemProperties cryptoSystemProperties,
      boolean allowFetchStatus,
      CertificateStatusManagerTicket ticket,
      Hashtable alreadyViewedCertificates,
      long style
   ) {
      return new PGPCertificateInfoDialog(
         certificate, certificatePool, keyStore, cryptoSystemProperties, allowFetchStatus, ticket, alreadyViewedCertificates, style
      );
   }
}
