package net.rim.device.api.crypto.tls;

import java.util.Enumeration;
import java.util.Hashtable;
import net.rim.device.api.crypto.keystore.KeyStoreData;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.RIMPersistentStore;
import net.rim.device.api.util.Persistable;

public class TLSDeviceOptionStore implements Persistable {
   private Hashtable _defaultClientCerts = (Hashtable)(new Object());
   private Object _defaultCert;
   private static final long ID = -5479247544345008286L;
   private static final String DEFAULT = "DEFAULT";
   private static PersistentObject _persist;

   private TLSDeviceOptionStore() {
   }

   public static TLSDeviceOptionStore getOptions() {
      _persist = RIMPersistentStore.getPersistentObject(-5479247544345008286L);
      synchronized (_persist) {
         if (_persist.getContents() == null) {
            _persist.setContents(new TLSDeviceOptionStore(), 4801362);
            _persist.commit();
         }
      }

      return (TLSDeviceOptionStore)_persist.getContents();
   }

   public KeyStoreData getDefaultClientCert() {
      return (KeyStoreData)this._defaultCert;
   }

   public KeyStoreData getClientCert(String hostname) {
      return (KeyStoreData)this._defaultClientCerts.get(hostname);
   }

   public Enumeration getCurrentHostnames() {
      return this._defaultClientCerts.keys();
   }

   public int getCurrentSize() {
      return this._defaultClientCerts.size();
   }

   public void setDefaultCert(KeyStoreData data) {
      this._defaultCert = data;
      _persist.commit();
   }

   public void addDefaultCert(String hostname, KeyStoreData data) {
      this._defaultClientCerts.put(hostname, data);
      _persist.commit();
   }

   public void removeDefaultCert(String hostname) {
      this._defaultClientCerts.remove(hostname);
      _persist.commit();
   }
}
