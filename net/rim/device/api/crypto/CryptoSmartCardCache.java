package net.rim.device.api.crypto;

import java.util.Hashtable;
import net.rim.device.api.smartcard.ReaderStatusListener;
import net.rim.device.api.smartcard.SmartCardReader;
import net.rim.device.api.system.ApplicationRegistry;

final class CryptoSmartCardCache implements ReaderStatusListener {
   private Hashtable _hashtable = new Hashtable();
   private static final long ID = 2914118616967182961L;
   private static CryptoSmartCardCache _instance;

   private CryptoSmartCardCache() {
   }

   static final CryptoSmartCardCache getInstance() {
      return _instance;
   }

   final CryptoSmartCardKeyStoreData[] getKeyStoreDataArray(SmartCardReader reader) {
      return (CryptoSmartCardKeyStoreData[])this._hashtable.get(reader);
   }

   final void setKeyStoreDataArray(SmartCardReader reader, CryptoSmartCardKeyStoreData[] data) {
      if (reader == null) {
         throw new IllegalArgumentException();
      }

      if (!this._hashtable.containsKey(reader)) {
         reader.addListener(this);
      }

      this._hashtable.put(reader, data);
   }

   @Override
   public final void readerStatus(SmartCardReader reader, int status) {
      if (status == 1 || status == 0) {
         this._hashtable.remove(reader);
      }
   }

   static {
      ApplicationRegistry registry = ApplicationRegistry.getApplicationRegistry();
      _instance = (CryptoSmartCardCache)registry.getOrWaitFor(2914118616967182961L);
      if (_instance == null) {
         _instance = new CryptoSmartCardCache();
         registry.put(2914118616967182961L, _instance);
      }
   }
}
