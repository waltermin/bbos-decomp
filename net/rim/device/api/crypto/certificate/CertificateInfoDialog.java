package net.rim.device.api.crypto.certificate;

import java.util.Enumeration;
import java.util.Hashtable;
import net.rim.device.api.collection.Collection;
import net.rim.device.api.collection.CollectionListener;
import net.rim.device.api.crypto.CryptoSystemProperties;
import net.rim.device.api.crypto.DigestFactory;
import net.rim.device.api.crypto.InvalidCryptoSystemException;
import net.rim.device.api.crypto.PublicKey;
import net.rim.device.api.crypto.certificate.status.CertificateStatusRequest;
import net.rim.device.api.crypto.keystore.CertificateStatusManager;
import net.rim.device.api.crypto.keystore.CertificateStatusManagerTicket;
import net.rim.device.api.crypto.keystore.KeyStore;
import net.rim.device.api.crypto.keystore.KeyStoreData;
import net.rim.device.api.crypto.keystore.RevocationReason;
import net.rim.device.api.crypto.keystore.TrustedKeyStore;
import net.rim.device.api.i18n.DateFormat;
import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.system.Application;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.util.Arrays;
import net.rim.device.internal.resource.crypto.CryptoIndicatorImages;
import net.rim.device.internal.ui.component.BackgroundDialog;
import net.rim.device.internal.ui.component.ImageField;
import net.rim.device.internal.ui.component.TitledScrollingDialog;

public class CertificateInfoDialog extends TitledScrollingDialog implements CollectionListener {
   private ButtonField _fetchStatus;
   private RichTextField _certStatusField;
   private ImageField _certificateIconField;
   protected KeyStore _keyStore;
   protected Certificate[] _certificatePool;
   protected CryptoSystemProperties _cryptoSystemProperties;
   private CertificateStatusManager _certificateStatusManager;
   private CertificateStatusManagerTicket _ticket;
   protected Certificate _certificate;
   protected Certificate[][][] _certificateChains;
   protected Certificate[] _bestCertificateChain;
   protected long _bestCertificateChainProperties;
   private boolean _allowFetchStatus;
   protected Hashtable _alreadyViewedCertificates;
   private static final int MAX_NUM_WARNINGS;

   protected String[] getWarnings(long propertiesSummary) {
      String[] warnings = new Object[0];
      if ((propertiesSummary & 1024) != 0) {
         Arrays.add(warnings, CertificateResources.getString(23));
      }

      if ((propertiesSummary & 22) != 0) {
         Arrays.add(warnings, CertificateResources.getString(222));
      }

      if ((propertiesSummary & 256) != 0) {
         Arrays.add(warnings, CertificateResources.getString(219));
      }

      if ((propertiesSummary & 8) != 0) {
         Arrays.add(warnings, CertificateResources.getString(24));
      }

      if ((propertiesSummary & 32) != 0) {
         Arrays.add(warnings, CertificateResources.getString(220));
      }

      if ((propertiesSummary & 2048) != 0) {
         Arrays.add(warnings, CertificateResources.getString(225));
      }

      if ((propertiesSummary & 512) != 0) {
         Arrays.add(warnings, CertificateResources.getString(221));
      }

      return warnings;
   }

   protected void buildCertificateChains() {
      this._certificateChains = CertificateUtilities.buildCertificateChains(this._certificate, this._certificatePool, this._keyStore);
      long[] properties = CertificateChainProperties.getCertificateChainProperties(
         this._certificateChains, this._keyStore, TrustedKeyStore.getInstance(), System.currentTimeMillis(), this._cryptoSystemProperties
      );
      int bestCertificateChainIndex = CertificateChainProperties.selectBestCertificateChain(properties);
      this._bestCertificateChain = this._certificateChains[bestCertificateChainIndex];
      this._bestCertificateChainProperties = properties[bestCertificateChainIndex];
   }

   protected void addFields() {
      if (this._certificate.isRoot()) {
         Boolean displayRoot = (Boolean)this._certificate.getInformation(-334528756150594391L, null, null);
         if (displayRoot == null || displayRoot) {
            this.addNonScrollingText(CertificateResources.getString(20));
         }
      }

      if (this._certificate.isCA()) {
         Boolean displayCA = (Boolean)this._certificate.getInformation(-2021910959928808912L, null, null);
         if (displayCA == null || displayCA) {
            this.addNonScrollingText(CertificateResources.getString(30));
         }
      }

      if (this._keyStore != null) {
         this._keyStore.addIndex(new CertificateKeyStoreIndex());
         Enumeration keyStoreElements = this._keyStore.elements(-2038609988711824737L, this._certificate);
         if (keyStoreElements.hasMoreElements()) {
            KeyStoreData keyStoreData = (KeyStoreData)keyStoreElements.nextElement();
            if (keyStoreData.isPrivateKeySet()) {
               if (keyStoreData.getAssociatedData(-4699629744920546763L) != null) {
                  this.addNonScrollingText(CertificateResources.getString(223));
               } else {
                  this.addNonScrollingText(CertificateResources.getString(224));
               }
            }
         }
      }

      this.addStatusFields();
      this.addTrustFields();
      long notBeforeDate = this._certificate.getNotBefore();
      if (notBeforeDate > System.currentTimeMillis()) {
         this.addScrollingLabelAndValue(CertificateResources.getString(6), getTimeString(notBeforeDate));
      } else {
         this.addScrollingLabelAndValue(CertificateResources.getString(7), getTimeString(this._certificate.getNotAfter()));
      }

      this.addScrollingLabelAndValue(CertificateResources.getString(8), this._certificate.getType());
      this.addPublicKeyTypeFields();
      this.addScrollingLabelAndValue(CertificateResources.getString(3), CertificateUtilities.formatDistinguishedName(this._certificate.getSubject(), '\n'));
      DistinguishedName issuer = this._certificate.getIssuer();
      this.addScrollingLabelAndValue(CertificateResources.getString(9), CertificateUtilities.formatDistinguishedName(issuer, '\n'));
      byte[] serialNumber = this._certificate.getSerialNumber();
      if (serialNumber != null) {
         this.addScrollingLabelAndValue(CertificateResources.getString(29), CertificateUtilities.getHexAsciiString(serialNumber));
      }

      this.addScrollingLabelAndValue(CertificateResources.getString(18), this.getKeyUsageString());
      this.addCustomDisplayFields();

      label150:
      try {
         String sha1Thumbprint = CertificateUtilities.calculateThumbprint(this._certificate, DigestFactory.getInstance("SHA1"));
         this.addScrollingLabelAndValue(CertificateResources.getString(14), sha1Thumbprint);
      } finally {
         break label150;
      }

      label147:
      try {
         String md5Thumbprint = CertificateUtilities.calculateThumbprint(this._certificate, DigestFactory.getInstance("MD5"));
         this.addScrollingLabelAndValue(CertificateResources.getString(15), md5Thumbprint);
      } finally {
         break label147;
      }

      if (!this._certificate.isRoot()) {
         int numCertificateChains = this._certificateChains.length;
         Hashtable buttonsAdded = (Hashtable)(new Object(numCertificateChains));

         for (int i = 0; i < numCertificateChains; i++) {
            Certificate[] currentCertificateChain = this._certificateChains[i];
            if (currentCertificateChain.length > 1) {
               Certificate currentIssuerCertificate = currentCertificateChain[1];
               if (!buttonsAdded.containsKey(currentIssuerCertificate) && !this._alreadyViewedCertificates.containsKey(currentIssuerCertificate)) {
                  buttonsAdded.put(currentIssuerCertificate, currentIssuerCertificate);
                  ButtonField viewCertField = (ButtonField)(new Object(CertificateResources.getString(17), 12884901888L));
                  viewCertField.setChangeListener(this);
                  viewCertField.setCookie(currentIssuerCertificate);
                  this.addScrollingField(viewCertField);
               }
            }
         }
      }
   }

   protected void addStatusFields() {
      this._certStatusField = this.addScrollingLabelAndValue(CertificateResources.getString(2), getCertificateStatusString(this._certificate.getStatus()));
      if (this._allowFetchStatus
         && (this._bestCertificateChainProperties & 1024) == 0
         && CertificateStatusProviderFacade.queryStatusAvailability(this._bestCertificateChain, true)) {
         this._fetchStatus = (ButtonField)(new Object(CertificateResources.getString(31), 12884901888L));
         this._fetchStatus.setChangeListener(this);
         this.addScrollingField(this._fetchStatus);
      }
   }

   protected void addTrustFields() {
      String trustStatusString = null;
      if ((this._bestCertificateChainProperties & 8) != 0) {
         trustStatusString = CertificateResources.getString(28);
      } else if (TrustedKeyStore.getInstance().isMember(this._certificate)) {
         trustStatusString = CertificateResources.getString(26);
      } else {
         trustStatusString = CertificateResources.getString(27);
      }

      this.addScrollingLabelAndValue(CertificateResources.getString(25), trustStatusString);
   }

   protected void addPublicKeyTypeFields() {
      try {
         PublicKey publicKey = this._certificate.getPublicKey();
         if (publicKey != null) {
            this.addScrollingLabelAndValue(CertificateResources.getString(13), getPublicKeyTypeString(publicKey));
            return;
         }
      } catch (InvalidCryptoSystemException var2) {
      }
   }

   protected void addCustomDisplayFields() {
      CertificateDisplayField[] customFields = this._certificate.getCustomDisplayFields();
      if (customFields != null) {
         for (CertificateDisplayField currentField : customFields) {
            if (currentField != null) {
               if (currentField.isFieldPresent()) {
                  this.addScrollingField(currentField.getField());
               } else {
                  this.addScrollingLabelAndValue(currentField.getLabel(), currentField.getValue());
               }
            }
         }
      }
   }

   protected String getKeyUsageString() {
      StringBuffer keyUsageStringBuffer = (StringBuffer)(new Object());

      for (int i = 0; i < 15; i++) {
         long currentUsage = (long)1 << i;
         if (this._certificate.queryKeyUsage(currentUsage) == 1) {
            if (keyUsageStringBuffer.length() > 0) {
               keyUsageStringBuffer.append('\n');
            }

            keyUsageStringBuffer.append(CertificateUtilities.getKeyUsageString(currentUsage));
         }
      }

      return keyUsageStringBuffer.length() > 0 ? keyUsageStringBuffer.toString() : null;
   }

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
      return new CertificateInfoDialog(
         certificate, certificatePool, keyStore, cryptoSystemProperties, allowFetchStatus, ticket, alreadyViewedCertificates, style
      );
   }

   @Override
   public void reset(Collection collection) {
   }

   @Override
   public void elementAdded(Collection collection, Object element) {
      if (collection == this._certificateStatusManager && element.equals(this._certificate)) {
         this.handleCertificateStatus();
      }
   }

   @Override
   public void elementUpdated(Collection collection, Object oldElement, Object newElement) {
      if (collection == this._certificateStatusManager && newElement.equals(this._certificate)) {
         this.handleCertificateStatus();
      }
   }

   @Override
   public void elementRemoved(Collection collection, Object element) {
   }

   public static String getPublicKeyTypeString(PublicKey param0) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aload 0
      // 01: invokeinterface net/rim/device/api/crypto/PublicKey.getCryptoSystem ()Lnet/rim/device/api/crypto/CryptoSystem; 1
      // 06: astore 1
      // 07: aload 1
      // 08: invokeinterface net/rim/device/api/crypto/CryptoSystem.getAlgorithm ()Ljava/lang/String; 1
      // 0d: astore 2
      // 0e: aload 1
      // 0f: invokeinterface net/rim/device/api/crypto/CryptoSystem.getBitLength ()I 1
      // 14: istore 3
      // 15: new java/lang/Object
      // 18: dup
      // 19: aload 2
      // 1a: invokespecial java/lang/StringBuffer.<init> (Ljava/lang/String;)V
      // 1d: bipush 32
      // 1f: invokevirtual java/lang/StringBuffer.append (C)Ljava/lang/StringBuffer;
      // 22: iload 3
      // 23: invokevirtual java/lang/StringBuffer.append (I)Ljava/lang/StringBuffer;
      // 26: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 29: astore 2
      // 2a: aload 2
      // 2b: areturn
      // 2c: astore 3
      // 2d: aload 2
      // 2e: areturn
      // 2f: astore 3
      // 30: aload 2
      // 31: areturn
      // try (6 -> 19): 21 null
      // try (6 -> 19): 24 null
   }

   private void updateIcon(long propertiesSummary) {
      int icon = CertificateIconProvider.getCertificateIcon(propertiesSummary);
      synchronized (Application.getEventLock()) {
         this._certificateIconField.setImage(CryptoIndicatorImages.getImage(icon));
      }
   }

   public static String getTimeString(long date) {
      return DateFormat.getInstance(45).formatLocal(date);
   }

   protected CertificateInfoDialog(
      Certificate certificate,
      Certificate[] certificatePool,
      KeyStore keyStore,
      CryptoSystemProperties cryptoSystemProperties,
      boolean allowFetchStatus,
      CertificateStatusManagerTicket ticket,
      Hashtable alreadyViewedCertificates,
      long style
   ) {
      super(style);
      if (certificate == null) {
         throw new Object();
      }

      this._certificate = certificate;
      this._certificatePool = certificatePool;
      this._keyStore = keyStore;
      this._cryptoSystemProperties = cryptoSystemProperties;
      this._allowFetchStatus = allowFetchStatus && CertificateStatusProviderFacade.available();
      this._ticket = ticket;
      this._alreadyViewedCertificates = (Hashtable)(alreadyViewedCertificates != null ? alreadyViewedCertificates : new Object());
      this._certStatusField = null;
      this._fetchStatus = null;
      HorizontalFieldManager titleManager = (HorizontalFieldManager)(new Object(1152921504606846976L));
      this._certificateIconField = CryptoIndicatorImages.getImageField(0);
      titleManager.add(this._certificateIconField);
      String title = null;
      if (this._keyStore != null) {
         this._keyStore.addIndex(new CertificateKeyStoreIndex());
         Enumeration keyStoreElements = this._keyStore.elements(-2038609988711824737L, this._certificate);
         if (keyStoreElements.hasMoreElements()) {
            KeyStoreData keyStoreData = (KeyStoreData)keyStoreElements.nextElement();
            title = keyStoreData.getLabel();
         }
      }

      if (title == null || title.length() == 0) {
         title = this._certificate.getSubjectFriendlyName();
      }

      LabelField friendlyNameField = (LabelField)(new Object(title, 64));
      friendlyNameField.setFont(super._boldFont);
      titleManager.add(friendlyNameField);
      this.setTitle(titleManager);
   }

   @Override
   protected boolean handleFieldChanged(Field field, int context) {
      if (super.handleFieldChanged(field, context)) {
         return true;
      } else if (field == this._fetchStatus) {
         CertificateStatusRequest request = new CertificateStatusRequest(this._bestCertificateChain, true, this._keyStore, this._ticket, null);
         CertificateStatusProviderFacade.requestCertificateStatus(request, null, true, false);
         return true;
      } else {
         Object issuerCert = field.getCookie();
         if (issuerCert instanceof Certificate) {
            this._alreadyViewedCertificates.put(this._certificate, this._certificate);
            CertificateInfoDialog newDialog = this.createNewDialog(
               (Certificate)issuerCert,
               this._certificatePool,
               this._keyStore,
               this._cryptoSystemProperties,
               this._allowFetchStatus,
               this._ticket,
               this._alreadyViewedCertificates,
               this.getStyle()
            );
            BackgroundDialog.show(newDialog);
            this._alreadyViewedCertificates.remove(this._certificate);
            return true;
         } else {
            return false;
         }
      }
   }

   public CertificateInfoDialog(
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

   @Override
   protected void onUiEngineAttached(boolean attached) {
      if (attached) {
         this._certificateStatusManager = CertificateStatusManager.getInstance();
         this._certificateStatusManager.addCollectionListener(new Object(this));
         super.onUiEngineAttached(attached);
      } else {
         super.onUiEngineAttached(attached);
         this._certificateStatusManager.removeCollectionListener(this);
      }
   }

   public static String getCertificateStatusString(CertificateStatus certStatus) {
      String certStatusString = null;
      long certStatusDate = 0;
      String revocationReason = null;
      if (certStatus != null) {
         int statusInt = certStatus.getStatus();
         switch (statusInt) {
            case -2:
               break;
            case -1:
               certStatusString = CertificateResources.getString(12);
               certStatusDate = certStatus.getProducedAtTime();
               break;
            case 0:
            default:
               certStatusString = CertificateResources.getString(10);
               certStatusDate = certStatus.getProducedAtTime();
               break;
            case 1:
               certStatusString = CertificateResources.getString(11);
               certStatusDate = certStatus.getRevocationTime();
               revocationReason = RevocationReason.getRevocationReason(certStatus.getRevocationReason());
         }
      } else {
         certStatusString = CertificateResources.getString(12);
      }

      if (certStatusDate > 0) {
         certStatusString = MessageFormat.format(CertificateResources.getString(19), new Object[]{certStatusString, getTimeString(certStatusDate)});
      }

      if (revocationReason != null) {
         certStatusString = ((StringBuffer)(new Object())).append(certStatusString).append('\n').append(revocationReason).toString();
      }

      return certStatusString;
   }

   private void updateWarningFields(long propertiesSummary) {
      this.deleteAllNonScrollingFields();
      String[] warnings = this.getWarnings(propertiesSummary);
      int numWarnings = warnings == null ? 0 : warnings.length;
      int numWarningsToDisplay = Math.min(numWarnings, 2);

      for (int i = 0; i < numWarningsToDisplay; i++) {
         this.addNonScrollingText(warnings[i]);
      }
   }

   private void handleCertificateStatus() {
      this.buildCertificateChains();
      this.updateIcon(this._bestCertificateChainProperties);
      this.updateWarningFields(this._bestCertificateChainProperties);
      String certStatusString = getCertificateStatusString(this._certificate.getStatus());
      if (certStatusString != null) {
         synchronized (Application.getEventLock()) {
            this._certStatusField.setText(certStatusString);
         }
      }

      if ((this._bestCertificateChainProperties & 1024) != 0 && this._fetchStatus != null) {
         Field newFocusField = null;
         if (super._scrollingRegion.getFieldWithFocus() == this._fetchStatus) {
            int i = this._fetchStatus.getIndex() + 1;

            do {
               newFocusField = super._scrollingRegion.getField(i++);
            } while (!newFocusField.isFocusable());
         }

         synchronized (Application.getEventLock()) {
            if (newFocusField != null) {
               newFocusField.setFocus();
            }

            super._scrollingRegion.delete(this._fetchStatus);
         }

         this._fetchStatus = null;
      }
   }

   @Override
   protected void populateDialog() {
      this.buildCertificateChains();
      this.updateIcon(this._bestCertificateChainProperties);
      this.updateWarningFields(this._bestCertificateChainProperties);
      this.addFields();
      super.populateDialog();
   }
}
