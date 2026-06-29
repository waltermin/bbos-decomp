package net.rim.device.api.crypto.keystore;

import java.util.Enumeration;
import java.util.Hashtable;
import net.rim.device.api.crypto.BlockEncryptor;
import net.rim.device.api.crypto.Key;
import net.rim.device.api.crypto.PrivateKey;
import net.rim.device.api.crypto.PublicKey;
import net.rim.device.api.crypto.SymmetricKey;
import net.rim.device.api.crypto.certificate.Certificate;
import net.rim.device.api.crypto.certificate.CertificateStatus;
import net.rim.vm.Array;

public class CombinedKeyStore implements KeyStore {
   private KeyStore[] _keyStores;
   private int _numKeyStores;
   private int _preferredKeyStore;

   public KeyStore getKeyStore(int index) {
      if (index >= 0 && index < this._keyStores.length) {
         return this._keyStores[index];
      } else {
         throw new IllegalArgumentException();
      }
   }

   public int getNumKeyStores() {
      return this._keyStores.length;
   }

   public void changePreferredKeyStore(int newPreferredKeyStore) {
      if (newPreferredKeyStore >= 0 && newPreferredKeyStore <= this._numKeyStores) {
         this._preferredKeyStore = newPreferredKeyStore;
      } else {
         throw new IllegalArgumentException();
      }
   }

   public void removeKeyStore(int index) {
      if (index >= 0 && index < this._numKeyStores) {
         KeyStore[] newKeyStoreArray = new KeyStore[this._numKeyStores - 1];
         System.arraycopy(this._keyStores, 0, newKeyStoreArray, 0, index);
         System.arraycopy(this._keyStores, index + 1, newKeyStoreArray, index, this._numKeyStores - index - 1);
         this._keyStores = newKeyStoreArray;
         this._numKeyStores--;
      } else {
         throw new IllegalArgumentException();
      }
   }

   public void addKeyStores(KeyStore[] keyStores) {
      if (keyStores == null) {
         throw new IllegalArgumentException();
      }

      int oldLength = this._numKeyStores;
      this._numKeyStores += keyStores.length;
      Array.resize(this._keyStores, this._numKeyStores);
      int i = oldLength;

      for (int j = 0; i < this._numKeyStores; j++) {
         this._keyStores[i] = keyStores[j];
         i++;
      }
   }

   @Override
   public KeyStoreData set(
      AssociatedData[] associatedData, String label, SymmetricKey symmetricKey, String symmetricKeyEncodingAlgorithm, int securityLevel, KeyStoreTicket ticket
   ) {
      return this._keyStores[this._preferredKeyStore]
         .set((BlockEncryptor[])associatedData, label, symmetricKey, symmetricKeyEncodingAlgorithm, securityLevel, ticket);
   }

   @Override
   public Enumeration elements(long index, Object associatedData) {
      return this.elements(index, associatedData, false);
   }

   @Override
   public Enumeration elements(long index, Object associatedData, boolean backingKeyStore) {
      Enumeration[] enumeration = new Enumeration[this._numKeyStores];

      for (int i = 0; i < this._numKeyStores; i++) {
         enumeration[i] = this._keyStores[i].elements(index, associatedData, backingKeyStore);
      }

      return this.getMultipleKeyStoreEnumeration(enumeration);
   }

   @Override
   public void removeKey(KeyStoreData data, KeyStoreTicket ticket) {
      this._keyStores[this._preferredKeyStore].removeKey(data, ticket);
   }

   @Override
   public Enumeration elements() {
      return this.elements(false);
   }

   @Override
   public Enumeration elements(boolean backingKeyStore) {
      Enumeration[] enumeration = new Enumeration[this._numKeyStores];

      for (int i = 0; i < this._numKeyStores; i++) {
         enumeration[i] = this._keyStores[i].elements(backingKeyStore);
      }

      return this.getMultipleKeyStoreEnumeration(enumeration);
   }

   @Override
   public Enumeration elements(long index) {
      return this.elements(index, false);
   }

   @Override
   public Enumeration elements(long index, boolean backingKeyStore) {
      Enumeration[] enumeration = new Enumeration[this._numKeyStores];

      for (int i = 0; i < this._numKeyStores; i++) {
         enumeration[i] = this._keyStores[i].elements(index, backingKeyStore);
      }

      return this.getMultipleKeyStoreEnumeration(enumeration);
   }

   @Override
   public boolean exists(long index, Object associatedData) {
      for (int i = 0; i < this._numKeyStores; i++) {
         if (this._keyStores[i].exists(index, associatedData)) {
            return true;
         }
      }

      return false;
   }

   @Override
   public boolean addIndex(KeyStoreIndex index) {
      boolean added = false;

      for (int i = 0; i < this._numKeyStores; i++) {
         if (!this._keyStores[i].existsIndex(index.getID())) {
            this._keyStores[i].addIndex(index);
            added = true;
         }
      }

      return added;
   }

   @Override
   public void addIndices(KeyStoreIndex[] indices) {
      for (int i = 0; i < this._numKeyStores; i++) {
         this._keyStores[i].addIndices(indices);
      }
   }

   @Override
   public void removeIndex(long index) {
      for (int i = 0; i < this._numKeyStores; i++) {
         this._keyStores[i].removeIndex(index);
      }
   }

   @Override
   public boolean existsIndex(long index) {
      boolean exists = true;

      for (int i = 0; i < this._numKeyStores; i++) {
         if (!this._keyStores[i].existsIndex(index)) {
            exists = false;
         }
      }

      return exists;
   }

   @Override
   public int size() {
      int size = 0;

      for (int i = 0; i < this._numKeyStores; i++) {
         size += this._keyStores[i].size();
      }

      return size;
   }

   @Override
   public KeyStoreData set(AssociatedData[] associatedData, String label, Certificate certificate, CertificateStatus certStatus, KeyStoreTicket ticket) {
      return this._keyStores[this._preferredKeyStore].set(associatedData, label, certificate, certStatus, ticket);
   }

   @Override
   public KeyStoreData set(AssociatedData[] associatedData, String label, PublicKey publicKey, long keyUsage, KeyStoreTicket ticket) {
      return this._keyStores[this._preferredKeyStore].set(associatedData, label, publicKey, keyUsage, ticket);
   }

   @Override
   public KeyStoreData set(
      AssociatedData[] associatedData,
      String label,
      PrivateKey privateKey,
      String privateKeyEncodingAlgorithm,
      int securityLevel,
      Certificate certificate,
      CertificateStatus certStatus,
      KeyStoreTicket ticket
   ) {
      return this._keyStores[this._preferredKeyStore]
         .set(associatedData, label, privateKey, privateKeyEncodingAlgorithm, securityLevel, certificate, certStatus, ticket);
   }

   @Override
   public void changePassword() {
   }

   @Override
   public KeyStoreTicket getTicket() {
      return this._keyStores[this._preferredKeyStore].getTicket();
   }

   @Override
   public KeyStoreTicket getTicket(String prompt) {
      return this._keyStores[this._preferredKeyStore].getTicket(prompt);
   }

   @Override
   public boolean checkTicket(KeyStoreTicket ticket) {
      return this._keyStores[this._preferredKeyStore].checkTicket(ticket);
   }

   @Override
   public String getName() {
      return this._keyStores[this._preferredKeyStore].getName();
   }

   @Override
   public boolean isMember(Certificate certificate) {
      for (int i = 0; i < this._numKeyStores; i++) {
         if (this._keyStores[i].isMember(certificate)) {
            return true;
         }
      }

      return false;
   }

   @Override
   public boolean isMember(byte[] certificateEncoding) {
      for (int i = 0; i < this._numKeyStores; i++) {
         if (this._keyStores[i].isMember(certificateEncoding)) {
            return true;
         }
      }

      return false;
   }

   @Override
   public boolean isMember(Key key) {
      for (int i = 0; i < this._numKeyStores; i++) {
         if (this._keyStores[i].isMember(key)) {
            return true;
         }
      }

      return false;
   }

   @Override
   public boolean isMember(KeyStoreData data) {
      for (int i = 0; i < this._numKeyStores; i++) {
         if (this._keyStores[i].isMember(data)) {
            return true;
         }
      }

      return false;
   }

   @Override
   public void addCollectionListener(Object listener) {
      for (int i = 0; i < this._numKeyStores; i++) {
         this._keyStores[i].addCollectionListener(listener);
      }
   }

   @Override
   public void removeCollectionListener(Object listener) {
      for (int i = 0; i < this._numKeyStores; i++) {
         this._keyStores[i].removeCollectionListener(listener);
      }
   }

   @Override
   public KeyStore getBackingKeyStore() {
      return this._keyStores[this._preferredKeyStore].getBackingKeyStore();
   }

   @Override
   public KeyStoreData set(
      AssociatedData[] associatedData,
      String label,
      PrivateKey privateKey,
      String privateKeyEncodingAlgorithm,
      int securityLevel,
      PublicKey publicKey,
      long keyUsage,
      KeyStoreTicket ticket
   ) {
      return this._keyStores[this._preferredKeyStore]
         .set(associatedData, label, privateKey, privateKeyEncodingAlgorithm, securityLevel, publicKey, keyUsage, ticket);
   }

   @Override
   public KeyStoreData set(
      AssociatedData[] associatedData, String label, PrivateKey privateKey, String privateKeyEncodingAlgorithm, int securityLevel, KeyStoreTicket ticket
   ) {
      return this._keyStores[this._preferredKeyStore]
         .set((BlockEncryptor[])associatedData, label, privateKey, privateKeyEncodingAlgorithm, securityLevel, ticket);
   }

   private Enumeration getMultipleKeyStoreEnumeration(Enumeration[] enumeration) {
      MultipleKeyStoreEnumeration mkse = new MultipleKeyStoreEnumeration(enumeration);
      Hashtable hashtable = new Hashtable();

      while (mkse.hasMoreElements()) {
         Object obj = mkse.nextElement();
         hashtable.put(obj, obj);
      }

      return hashtable.keys();
   }

   public CombinedKeyStore(KeyStore[] keyStores, int preferredKeyStore) {
      if (keyStores == null) {
         throw new IllegalArgumentException();
      }

      this._keyStores = keyStores;
      this._numKeyStores = this._keyStores.length;
      if (preferredKeyStore >= 0 && preferredKeyStore < this._keyStores.length) {
         this._preferredKeyStore = preferredKeyStore;
      } else {
         throw new IllegalArgumentException();
      }
   }

   public CombinedKeyStore(KeyStore[] keyStores) {
      if (keyStores == null) {
         throw new IllegalArgumentException();
      }

      this._keyStores = keyStores;
      this._numKeyStores = this._keyStores.length;
   }
}
