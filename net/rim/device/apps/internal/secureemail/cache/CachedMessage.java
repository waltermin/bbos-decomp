package net.rim.device.apps.internal.secureemail.cache;

import java.util.Enumeration;
import net.rim.device.api.crypto.certificate.Certificate;
import net.rim.device.api.crypto.certificate.CertificateKeyStoreIndex;
import net.rim.device.api.crypto.keystore.KeyStore;
import net.rim.device.api.crypto.keystore.KeyStoreData;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.ui.Manager;
import net.rim.device.apps.internal.blackberryemail.email.EmailMessageModel;
import net.rim.device.apps.internal.secureemail.SecureEmailFactory;

public class CachedMessage extends CachedManager {
   protected SecureEmailFactory _secureEmailFactory;
   private boolean _isSigned;
   private boolean _isEncrypted;
   private boolean _passwordRequiredForAccess;
   private boolean _isBodyTruncated;
   private CachedErrorField _cachedErrorField;
   private CachedAttachmentsField _cachedAttachmentsField;
   private boolean _doNotCache;

   public CachedMessage() {
      this(null);
   }

   public CachedMessage(SecureEmailFactory secureEmailFactory) {
      this._secureEmailFactory = secureEmailFactory;
   }

   @Override
   public void setEmailMessageModel(EmailMessageModel emailMessageModel, ServiceRecord serviceRecord) {
      super.setEmailMessageModel(emailMessageModel, serviceRecord);
      if (this._cachedErrorField != null) {
         this._cachedErrorField.setEmailMessageModel(emailMessageModel, serviceRecord);
      }

      if (this._cachedAttachmentsField != null) {
         this._cachedAttachmentsField.setEmailMessageModel(emailMessageModel, serviceRecord);
      }
   }

   public void setEncrypted(boolean isEncrypted) {
      this._isEncrypted = isEncrypted;
   }

   public void setSigned(boolean isSigned) {
      this._isSigned = isSigned;
   }

   public void addDecryptionCertificate(Certificate decryptionCertificate, KeyStore preferredKeyStore) {
      if (!this._passwordRequiredForAccess && decryptionCertificate != null) {
         preferredKeyStore.addIndex(new CertificateKeyStoreIndex());
         Enumeration enumeration = preferredKeyStore.elements(-2038609988711824737L, decryptionCertificate);

         while (enumeration.hasMoreElements()) {
            KeyStoreData keyStoreData = (KeyStoreData)enumeration.nextElement();
            if (keyStoreData.isPrivateKeySet() && keyStoreData.getSecurityLevel() == 2) {
               this._passwordRequiredForAccess = true;
               return;
            }
         }
      }
   }

   public void setBodyTruncated(boolean isBodyTruncated) {
      this._isBodyTruncated = isBodyTruncated;
   }

   public boolean isEncrypted() {
      return this._isEncrypted;
   }

   public boolean isSigned() {
      return this._isSigned;
   }

   public boolean isPasswordRequiredForAccess() {
      return this._passwordRequiredForAccess;
   }

   public boolean isBodyTruncated() {
      return this._isBodyTruncated;
   }

   public boolean doNotCache() {
      return this._doNotCache;
   }

   public void setDoNotCache() {
      this._doNotCache = true;
   }

   @Override
   public void fillManager(Manager manager, Object context) {
      if (this._cachedErrorField != null) {
         this._cachedErrorField.fillManager(manager, context);
      } else {
         super.fillManager(manager, context);
      }
   }

   @Override
   public boolean hasHeaderFields() {
      return this._cachedAttachmentsField != null || super.hasHeaderFields();
   }

   @Override
   public void fillManagerHeader(Manager manager, Object context) {
      if (this._cachedAttachmentsField != null) {
         this._cachedAttachmentsField.fillManagerHeader(manager, context);
      }

      super.fillManagerHeader(manager, context);
   }

   @Override
   public boolean hasFooterFields() {
      return this._cachedAttachmentsField != null || super.hasFooterFields();
   }

   @Override
   public void fillManagerFooter(Manager manager, Object context) {
      if (this._cachedAttachmentsField != null) {
         this._cachedAttachmentsField.fillManagerFooter(manager, context);
      }

      super.fillManagerFooter(manager, context);
   }

   public void setErrorString(String errorString) {
      this._cachedErrorField = new CachedErrorField(errorString);
      this.setDoNotCache();
   }

   public void addAttachment(Object attachment) {
      if (this._cachedAttachmentsField == null) {
         this._cachedAttachmentsField = new CachedAttachmentsField();
      }

      this._cachedAttachmentsField.addAttachment(attachment);
   }

   @Override
   public CachedMessage getCachedMessage() {
      return this;
   }
}
