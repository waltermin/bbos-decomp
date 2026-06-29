package net.rim.device.apps.internal.api.crypto.certificate;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import net.rim.device.api.crypto.PrivateKey;
import net.rim.device.api.crypto.certificate.Certificate;
import net.rim.device.api.crypto.certificate.CertificateChainProperties;
import net.rim.device.api.crypto.keystore.KeyStore;
import net.rim.device.api.crypto.keystore.KeyStoreData;
import net.rim.device.api.crypto.keystore.TrustedKeyStore;
import net.rim.device.api.mime.MIMEOutputStream;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.ui.Field;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.transmission.rim.CMIMEParameters;
import net.rim.device.apps.api.transmission.rim.ContentPartIDGenerator;
import net.rim.device.apps.api.transmission.rim.RIMMessagingOutgoingMessage;
import net.rim.device.apps.internal.blackberryemail.unknown.UnknownMimePartModel;
import net.rim.device.internal.resource.crypto.CryptoIcons;
import net.rim.device.internal.ui.Image;
import net.rim.vm.Persistable;

public class CertificateAttachmentModel extends UnknownMimePartModel implements Persistable {
   protected String _outgoingDisplayName;
   protected Certificate[] _certificates;
   protected PrivateKey[] _privateKeys;
   public static final String STRING_NAME;
   public static final String STRING_CONTENT_DISPOSITION;
   public static final String STRING_CONTENT_DISPOSITION_ATTACHMENT_FILENAME;

   protected CertificateAttachmentModel(Object initialData) {
      super(initialData);
      ContextObject contextObject = ContextObject.castOrCreate(initialData);
      Object certificateDataObject = contextObject.get(316628257119802273L);
      if (certificateDataObject instanceof Object) {
         Certificate certificate = (Certificate)certificateDataObject;
         this._certificates = new Object[]{certificate};
      }

      Object friendlyNameObject = contextObject.get(-4886909117188079897L);
      if (friendlyNameObject instanceof Object) {
         String friendlyName = (String)friendlyNameObject;
         this.setOutgoingDisplayName(friendlyName);
      }

      if (this._certificates == null) {
         this.parseCertificatesAndPrivateKeys();
      }
   }

   @Override
   public void setData(byte[] data) {
      byte[] encoding = data;
      if (PersistentContent.isEncryptionEnabled()) {
         encoding = Arrays.copy(encoding);
      }

      super.setData(encoding);
   }

   @Override
   public byte[] getData() {
      byte[] data = super.getData();
      if (PersistentContent.isEncryptionEnabled()) {
         data = Arrays.copy(data);
      }

      return data;
   }

   @Override
   public boolean isViewable() {
      return true;
   }

   @Override
   public Field getField(Object context) {
      return new CertificateAttachmentField(this);
   }

   public Certificate[] getCertificates() {
      return this._certificates;
   }

   public PrivateKey[] getPrivateKeys() {
      return this._privateKeys;
   }

   public String getOutgoingDisplayName() {
      return this._outgoingDisplayName;
   }

   @Override
   public boolean convert(Object context, Object target) {
      if (!(target instanceof Object)) {
         if (target instanceof Object && ContextObject.getFlag(context, 54) && ContextObject.getFlag(context, 43)) {
            MIMEOutputStream outputStream = (MIMEOutputStream)target;
            MIMEOutputStream mime = outputStream.getPartOutputStream(false, this.getOutgoingMIMEEncoding());
            mime.setContentType(this.getOutgoingMIMEContentType());
            String name = (String)(new Object(this.getNameBytes()));
            mime.addContentTypeParameter("name", name);
            mime.addHeaderField(((StringBuffer)(new Object("Content-Disposition: attachment;\r\n\tfilename="))).append(name).toString());
            return this.writeToOutputStream(mime);
         } else {
            return false;
         }
      } else {
         RIMMessagingOutgoingMessage message = (RIMMessagingOutgoingMessage)target;
         ByteArrayOutputStream byteArrayOutputStream = (ByteArrayOutputStream)(new Object());
         this.writeToOutputStream(byteArrayOutputStream);
         ContextObject contextObject = (ContextObject)context;
         ContentPartIDGenerator contentPartIDGenerator = (ContentPartIDGenerator)contextObject.get(-1943436819741481055L);
         CMIMEParameters parameters = (CMIMEParameters)(new Object((DataBuffer)(new Object()), 2, 1));
         parameters.add((byte)-14, this.getNameBytes());
         parameters.addCMIMEInteger((byte)-15, contentPartIDGenerator.generateContentPartID());
         message.addAttachment(byteArrayOutputStream.toByteArray(), parameters, "application/octet-stream");
         return true;
      }
   }

   @Override
   public boolean grabDataFromField(Field field, Object context) {
      return true;
   }

   @Override
   public void receiveMore(Object context, Object moreObject) {
      super.receiveMore(context, moreObject);
      if (!this.isMoreAvailable()) {
         this.parseCertificatesAndPrivateKeys();
      }
   }

   protected void setOutgoingDisplayName(String outgoingDisplayName) {
      this._outgoingDisplayName = outgoingDisplayName;
      this.setNameBytes(((StringBuffer)(new Object())).append(outgoingDisplayName).append(this.getDefaultFileExtension()).toString().getBytes());
   }

   public Image getImage() {
      return this._certificates != null && this._certificates.length != 1 ? CryptoIcons.getImage(1) : CryptoIcons.getImage(0);
   }

   protected KeyStore getPreferredKeyStore() {
      throw null;
   }

   protected void parseCertificatesAndPrivateKeys() {
      throw null;
   }

   public String getOutgoingMIMEContentType() {
      throw null;
   }

   protected String getOutgoingMIMEEncoding() {
      throw null;
   }

   protected boolean writeToOutputStream(OutputStream _1) {
      throw null;
   }

   protected String getDefaultFileExtension() {
      throw null;
   }

   protected String getNoCertificatesLabel() {
      throw null;
   }

   protected String getRetrieveVerbDescription() {
      throw null;
   }

   public String getPublicKeyContainerString(boolean _1, boolean _2) {
      throw null;
   }

   public int getCheckState(int index) {
      if (!this.isMoreAvailable() && this._certificates != null && this._certificates[index] != null) {
         PrivateKey privateKey = null;
         if (this._privateKeys != null) {
            privateKey = this._privateKeys[index];
         }

         return !isCertificateImported(this._certificates[index], privateKey, this.getPreferredKeyStore()) ? 0 : 1;
      } else {
         return -1;
      }
   }

   public String getText(int index) {
      if (this._outgoingDisplayName != null) {
         return this._outgoingDisplayName;
      } else if (this.isMoreAvailable()) {
         return (String)(new Object(this.getNameBytes()));
      } else {
         return this._certificates != null && this._certificates[index] != null
            ? this._certificates[index].getSubjectFriendlyName()
            : this.getNoCertificatesLabel();
      }
   }

   public boolean showImportVerb(int index) {
      return this.getCheckState(index) != 1;
   }

   protected static boolean isCertificateImported(Certificate certificate, PrivateKey privateKey, KeyStore keyStore) {
      Enumeration existingKeyStoreDatas = keyStore.elements(-2038609988711824737L, certificate);
      boolean isImported = existingKeyStoreDatas.hasMoreElements();
      if (isImported && privateKey != null) {
         isImported = false;

         while (existingKeyStoreDatas.hasMoreElements()) {
            KeyStoreData keyStoreData = (KeyStoreData)existingKeyStoreDatas.nextElement();
            if (keyStoreData.isPrivateKeySet()) {
               isImported = true;
               break;
            }
         }
      }

      return isImported;
   }

   public boolean showTrustVerb(Certificate[] certificateChain, PrivateKey privateKey, KeyStore keyStore) {
      TrustedKeyStore trustedKeyStore = (TrustedKeyStore)TrustedKeyStore.getInstance();
      long properties = CertificateChainProperties.getCertificateChainProperties(certificateChain, keyStore, trustedKeyStore, System.currentTimeMillis());
      return (properties & 8) != 0 && trustedKeyStore.isAllowed(certificateChain[0]);
   }
}
