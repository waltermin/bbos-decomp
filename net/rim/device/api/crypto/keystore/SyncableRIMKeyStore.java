package net.rim.device.api.crypto.keystore;

import net.rim.device.api.crypto.PublicKey;
import net.rim.device.api.crypto.certificate.Certificate;
import net.rim.device.api.crypto.certificate.CertificateStatus;
import net.rim.device.api.synchronization.SyncManager;
import net.rim.device.api.system.CodeSigningKey;

public class SyncableRIMKeyStore extends PersistableRIMKeyStore {
   public SyncableRIMKeyStore(String syncName, String displayName, long id, CodeSigningKey key, PersistableRIMKeyStoreFactory factory) {
      this(syncName, displayName, id, key, factory, null);
   }

   public SyncableRIMKeyStore(String syncName, String displayName, long id, CodeSigningKey key, PersistableRIMKeyStoreFactory factory, KeyStore keyStore) {
      super(displayName, id, key, factory, keyStore);
      SyncManager.getInstance().enableSynchronization(new KeyStoreSync(((StringBuffer)(new Object())).append(syncName).append(" Key Store").toString(), this));
   }

   void set(
      AssociatedData[] associatedData,
      String label,
      byte[] privateKey,
      String privateKeyEncodingAlgorithm,
      PublicKey publicKey,
      long keyUsage,
      byte[] certificate,
      String certificateType,
      CertificateStatus certStatus,
      int uid,
      int[] hashes,
      long[] indices,
      long[] notUsed,
      KeyStoreTicket ticket
   ) throws KeyStoreCancelException {
      if (!this.checkTicket(ticket)) {
         throw new KeyStoreCancelException();
      }

      RIMKeyStoreData data = new RIMKeyStoreData(
         associatedData,
         label,
         privateKey,
         privateKeyEncodingAlgorithm,
         publicKey,
         keyUsage,
         certificate,
         certificateType,
         certStatus,
         uid,
         hashes,
         indices,
         notUsed
      );
      this.set(associatedData, data);
      data._syncObjectDirty = false;
      this.groupData(data);
   }

   void set(
      AssociatedData[] associatedData,
      String label,
      byte[] symmetricKey,
      String symmetricKeyEncodingAlgorithm,
      int uid,
      int[] hashes,
      long[] indices,
      long[] notUsed,
      KeyStoreTicket ticket
   ) throws KeyStoreCancelException {
      if (!this.checkTicket(ticket)) {
         throw new KeyStoreCancelException();
      }

      RIMKeyStoreData data = new RIMKeyStoreData(associatedData, label, symmetricKey, symmetricKeyEncodingAlgorithm, uid, hashes, indices, notUsed);
      this.set(associatedData, data);
      data._syncObjectDirty = false;
      this.groupData(data);
   }

   @Override
   protected synchronized void set(AssociatedData[] associatedData, KeyStoreData data) {
      super.set(associatedData, data);
      Certificate certificate = data.getCertificate();
      if (certificate != null && data.isPrivateKeySet()) {
         KeyStoreManager.getInstance().certificateAdded(certificate);
      }
   }

   KeyStoreTicket getTicket(byte[] hash) {
      return new SyncableRIMKeyStoreTicket(hash, this);
   }

   @Override
   public KeyStoreTicket getTicket() {
      return new SyncableRIMKeyStoreTicket(this);
   }

   @Override
   public KeyStoreTicket getTicket(String prompt) {
      return new SyncableRIMKeyStoreTicket(prompt, this);
   }

   @Override
   public boolean checkTicket(KeyStoreTicket ticket) {
      return !(ticket instanceof SyncableRIMKeyStoreTicket) ? false : ((SyncableRIMKeyStoreTicket)ticket).access(this);
   }
}
