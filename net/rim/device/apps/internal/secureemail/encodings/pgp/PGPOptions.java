package net.rim.device.apps.internal.secureemail.encodings.pgp;

import java.util.Enumeration;
import net.rim.device.api.collection.Collection;
import net.rim.device.api.crypto.certificate.Certificate;
import net.rim.device.api.crypto.certificate.pgp.PGPCertificate;
import net.rim.device.api.crypto.keystore.KeyStore;
import net.rim.device.api.crypto.keystore.KeyStoreData;
import net.rim.device.api.crypto.keystore.KeyStoreIndex;
import net.rim.device.api.synchronization.OTASyncCapableSyncItem;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.Persistable;
import net.rim.device.apps.internal.secureemail.SecureEmailOptions;
import net.rim.device.apps.internal.secureemail.SecureEmailUtilities;

public final class PGPOptions extends SecureEmailOptions implements Persistable {
   private boolean _useConventionalEncryption;
   private byte[] _fingerprint;
   private static final int CONTENT_CIPHER_BITFIELD_DEFAULT;
   private static final boolean USE_CONVENTIONAL_ENCRYPTION_DEFAULT;

   public PGPOptions() {
      this.reset();
      this.checkITPolicyConformance();
   }

   public PGPOptions(SecureEmailOptions pgpOptions) {
      this.copy(pgpOptions);
      this.checkITPolicyConformance();
   }

   @Override
   protected final void reset() {
      super.reset();
      super._contentCipherBitfield = 47;
      this._useConventionalEncryption = false;
      this._fingerprint = null;
   }

   @Override
   public final void copy(SecureEmailOptions other) {
      super.copy(other);
      PGPOptions pgpOptions = (PGPOptions)other;
      this._useConventionalEncryption = pgpOptions._useConventionalEncryption;
      this._fingerprint = pgpOptions._fingerprint;
   }

   @Override
   public final boolean equals(Object other) {
      if (this == other) {
         return true;
      }

      if (!(other instanceof PGPOptions)) {
         return false;
      }

      PGPOptions p = (PGPOptions)other;
      return super.equals(p) && this._useConventionalEncryption == p._useConventionalEncryption && Arrays.equals(this._fingerprint, p._fingerprint);
   }

   private final void checkITPolicyConformance() {
      SecureEmailUtilities utilities = PGPFactory.getInstance().getUtilities();
      if (!utilities.isCertificateAllowed(super._signingKeyStoreData, 4)) {
         super._signingKeyStoreData = null;
         super._encryptionKeyStoreData = null;
      }

      super._contentCipherBitfield = super._contentCipherBitfield & utilities.getITPolicyContentCiphers();
   }

   public final boolean getUseConventionalEncryption() {
      return this._useConventionalEncryption;
   }

   public final void setUseConventionalEncryption(boolean useConventionalEncryption) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   @Override
   public final void elementAdded(Collection collection, Object element) {
      KeyStoreData keyStoreData = (KeyStoreData)element;
      if (this.isMatchingKeyStoreData(keyStoreData, this._fingerprint)) {
         super._signingKeyStoreData = keyStoreData;
         super._encryptionKeyStoreData = keyStoreData;
         this._fingerprint = null;
         PGPFactory.getInstance().saveGlobalOptions();
      }
   }

   @Override
   public final void elementRemoved(Collection collection, Object element) {
      if (element.equals(super._signingKeyStoreData)) {
         super._signingKeyStoreData = null;
         super._encryptionKeyStoreData = null;
         PGPFactory.getInstance().saveGlobalOptions();
      }
   }

   @Override
   public final void elementUpdated(Collection collection, Object oldElement, Object newElement) {
   }

   @Override
   public final void reset(Collection collection) {
   }

   @Override
   public final void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid == 8508406279413621091L || guid == -594020114676189989L) {
         this.checkITPolicyConformance();
         PGPFactory.getInstance().saveGlobalOptions();
      }
   }

   @Override
   public final OTASyncCapableSyncItem getSyncItem() {
      return new PGPOptions$PGPOptionsSyncItem(this);
   }

   private final KeyStoreData findMatchingKeyStoreData(KeyStore keyStore, byte[] fingerprint) {
      if (keyStore != null && fingerprint != null) {
         if (!keyStore.existsIndex(3692091765934112220L)) {
            keyStore.addIndex((KeyStoreIndex)(new Object()));
         }

         Enumeration enumeration = keyStore.elements(3692091765934112220L, fingerprint);

         while (enumeration.hasMoreElements()) {
            KeyStoreData currentKeyStoreData = (KeyStoreData)enumeration.nextElement();
            if (this.checkPrivateKeyAndITPolicy(currentKeyStoreData)) {
               return currentKeyStoreData;
            }
         }

         return null;
      } else {
         return null;
      }
   }

   private final boolean isMatchingKeyStoreData(KeyStoreData keyStoreData, byte[] fingerprint) {
      if (fingerprint == null) {
         return false;
      }

      Certificate certificate = keyStoreData.getCertificate();
      return certificate instanceof Object
         && Arrays.equals(this._fingerprint, ((PGPCertificate)certificate).getFingerprint())
         && this.checkPrivateKeyAndITPolicy(keyStoreData);
   }

   private final boolean checkPrivateKeyAndITPolicy(KeyStoreData keyStoreData) {
      return keyStoreData.isPrivateKeySet() && PGPFactory.getInstance().getUtilities().isCertificateAllowed(keyStoreData, 4);
   }

   static final KeyStoreData access$100(PGPOptions x0) {
      return x0._signingKeyStoreData;
   }

   static final KeyStoreData access$200(PGPOptions x0) {
      return x0._signingKeyStoreData;
   }

   static final int access$300(PGPOptions x0) {
      return x0._contentCipherBitfield;
   }

   static final boolean access$400(PGPOptions x0) {
      return x0._showMessageDetails;
   }

   static final boolean access$500(PGPOptions x0) {
      return x0._promptProblemPersonalCerts;
   }

   static final KeyStoreData access$802(PGPOptions x0, KeyStoreData x1) {
      return x0._signingKeyStoreData = x1;
   }

   static final KeyStoreData access$902(PGPOptions x0, KeyStoreData x1) {
      return x0._encryptionKeyStoreData = x1;
   }

   static final int access$1002(PGPOptions x0, int x1) {
      return x0._contentCipherBitfield = x1;
   }

   static final boolean access$1102(PGPOptions x0, boolean x1) {
      return x0._showMessageDetails = x1;
   }

   static final boolean access$1202(PGPOptions x0, boolean x1) {
      return x0._promptProblemPersonalCerts = x1;
   }
}
