package net.rim.device.api.crypto.tls.wtls20;

import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.RIMPersistentStore;
import net.rim.device.api.util.Persistable;

class WTLSCipherSuiteStorage implements Persistable {
   private byte[] _encryptionElements = WTLSCipherSuites.getDefaultEncryptionAlgorithms();
   private byte[] _macElements = WTLSCipherSuites.getDefaultMACAlgorithms();
   private byte[][] _keyExElements = WTLSCipherSuites.getDefaultKeyExchangeAlgorithms();
   private static final long ID = 7393491993781461490L;
   private static PersistentObject _persist;

   private WTLSCipherSuiteStorage() {
   }

   public static WTLSCipherSuiteStorage getInstance() {
      _persist = RIMPersistentStore.getPersistentObject(7393491993781461490L);
      WTLSCipherSuiteStorage store = null;
      synchronized (_persist) {
         if (!(_persist.getContents() instanceof WTLSCipherSuiteStorage)) {
            store = new WTLSCipherSuiteStorage();
            _persist.setContents(store, 4801362);
            _persist.commit();
         } else {
            store = (WTLSCipherSuiteStorage)_persist.getContents();
         }

         return store;
      }
   }

   public byte[] getEncryptionElements() {
      return this._encryptionElements;
   }

   public void setEncryptionElements(byte[] data) {
      this._encryptionElements = data;
      _persist.commit();
   }

   public byte[] getMACElements() {
      return this._macElements;
   }

   public void setMACElements(byte[] data) {
      this._macElements = data;
      _persist.commit();
   }

   public byte[][] getKeyExchangeElements() {
      return this._keyExElements;
   }

   public void setKeyExchangeElements(byte[][] data) {
      this._keyExElements = data;
      _persist.commit();
   }
}
