package javax.microedition.rms;

import java.util.Enumeration;
import java.util.Hashtable;
import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.RIMPersistentStore;
import net.rim.device.internal.proxy.Proxy;
import net.rim.device.internal.rms.RecordStoreData;
import net.rim.device.internal.rms.RecordStoreManagerProxy;
import net.rim.device.resources.Resource;
import net.rim.device.resources.Resource$Internal;
import net.rim.vm.TraceBack;

class RecordStoreManager implements RecordStoreManagerProxy {
   private static final long RECORD_STORE_ID = 5639922433554527164L;
   private static final long RECORD_STORE_LISTENER_ID = 8689535865899788676L;
   private static PersistentObject _persistentObject = RIMPersistentStore.getPersistentObject(5639922433554527164L);
   private static Hashtable _midletStores;
   private static Hashtable _activeStores;
   private static RecordStoreManager$RecordStoreManagerListener _rsml;

   private RecordStoreManager() {
   }

   static RecordStore getRecordStore(String recordStoreName, boolean createIfNecessary) {
      RecordStoreData recordStoreData;
      synchronized (_midletStores) {
         recordStoreData = (RecordStoreData)_midletStores.get(recordStoreName);
         if (recordStoreData == null) {
            if (!createIfNecessary) {
               throw new RecordStoreNotFoundException(recordStoreName);
            }

            recordStoreData = new RecordStoreData(recordStoreName);
            _midletStores.put(recordStoreName, recordStoreData);
            PersistentObject.commit(_midletStores);
         }
      }

      synchronized (_activeStores) {
         RecordStore recordStore = (RecordStore)_activeStores.get(recordStoreData);
         if (recordStore == null) {
            recordStore = new RecordStore(recordStoreData);
            _activeStores.put(recordStoreData, recordStore);
         }

         return recordStore;
      }
   }

   static RecordStore getRecordStore(String recordStoreName, String vendorName, String suiteName) {
      String midletPropertiesHashString = suiteName + vendorName;
      Hashtable allRecordStores = (Hashtable)_persistentObject.getContents();
      RecordStoreData recordStoreData;
      synchronized (allRecordStores) {
         Hashtable midletSuiteStores = (Hashtable)allRecordStores.get(midletPropertiesHashString);
         if (midletSuiteStores == null) {
            throw new RecordStoreNotFoundException();
         }

         recordStoreData = (RecordStoreData)midletSuiteStores.get(recordStoreName);
         if (recordStoreData == null) {
            throw new RecordStoreNotFoundException(recordStoreName);
         }
      }

      synchronized (_activeStores) {
         RecordStore recordStore = (RecordStore)_activeStores.get(recordStoreData);
         if (recordStore == null) {
            recordStore = new RecordStore(recordStoreData);
            _activeStores.put(recordStoreData, recordStore);
         }

         return recordStore;
      }
   }

   static void deleteRecordStore(String recordStoreName) {
      synchronized (_activeStores) {
         RecordStore recordStore = getRecordStore(recordStoreName, false);
         if (recordStore.isOpen()) {
            throw new RecordStoreException(recordStoreName);
         }

         _activeStores.remove(recordStore._recordStoreData);
         synchronized (_midletStores) {
            _midletStores.remove(recordStoreName);
            PersistentObject.commit(_midletStores);
         }
      }
   }

   static String[] getRecordStoreList() {
      String[] list = null;
      synchronized (_midletStores) {
         Enumeration keys = _midletStores.keys();
         if (_midletStores.size() > 0) {
            list = new String[_midletStores.size()];

            for (int i = 0; keys.hasMoreElements(); i++) {
               list[i] = (String)keys.nextElement();
            }
         }

         return list;
      }
   }

   static void commit() {
      _persistentObject.commit();
   }

   @Override
   public void commit(RecordStoreData recordStoreData) {
      PersistentObject.commit(recordStoreData);
   }

   static boolean checkOwner(RecordStore recordStore) {
      throw new RuntimeException("cod2jar: field: unresolved slot");
   }

   private static String generateMidletSuiteHashKey(boolean includeVendorName) {
      Resource resource = Resource$Internal.getResourceClass(TraceBack.getCallingModuleName(2));
      if (resource != null) {
         byte[] data = resource.getProperty("MIDlet-Name");
         if (data != null) {
            String midletSuiteName = new String(data, 2, data.length - 2);
            if (!includeVendorName) {
               return midletSuiteName;
            } else {
               data = resource.getProperty("MIDlet-Vendor");
               if (data != null) {
                  String midletSuiteVendor = new String(data, 2, data.length - 2);
                  return midletSuiteName + midletSuiteVendor;
               } else {
                  return midletSuiteName;
               }
            }
         } else {
            return ApplicationDescriptor.currentApplicationDescriptor().getModuleName();
         }
      } else {
         return null;
      }
   }

   @Override
   public void deleteRecordStores(String midletSuiteName, String midletSuiteVendor) {
      String midletPropertiesHashString = midletSuiteVendor == null ? midletSuiteName : midletSuiteName + midletSuiteVendor;
      synchronized (_persistentObject) {
         Hashtable allRecordStores = (Hashtable)_persistentObject.getContents();
         _rsml.deleteRecordStoresWithKey(midletPropertiesHashString, allRecordStores);
         _persistentObject.commit();
      }
   }

   @Override
   public boolean recordStoresExistForSuite(String midletSuiteName, String midletSuiteVendor) {
      String midletPropertiesHashString = midletSuiteVendor == null ? midletSuiteName : midletSuiteName + midletSuiteVendor;
      synchronized (_persistentObject) {
         Hashtable allRecordStores = (Hashtable)_persistentObject.getContents();
         return allRecordStores.containsKey(midletPropertiesHashString);
      }
   }

   @Override
   public RecordStore createRecordStore(RecordStoreData data) {
      return new RecordStore(data);
   }

   static {
      synchronized (_persistentObject) {
         if (_persistentObject.getContents() == null) {
            _persistentObject.setContents(new Hashtable(), 51);
            _persistentObject.commit();
         }
      }

      Hashtable allRecordStores = (Hashtable)_persistentObject.getContents();
      _activeStores = ApplicationRegistry.getApplicationRegistry().getHashtable(5639922433554527164L);
      boolean includeVendorNameInHashKey = true;
      String midletSuiteHashKey = generateMidletSuiteHashKey(includeVendorNameInHashKey);
      if (midletSuiteHashKey == null) {
         throw new NullPointerException();
      }

      synchronized (allRecordStores) {
         _midletStores = (Hashtable)allRecordStores.get(midletSuiteHashKey);
         if (_midletStores == null && includeVendorNameInHashKey) {
            String nameOnlyMidletSuiteHashKey = generateMidletSuiteHashKey(false);
            if (!midletSuiteHashKey.equals(nameOnlyMidletSuiteHashKey)) {
               _midletStores = (Hashtable)allRecordStores.get(nameOnlyMidletSuiteHashKey);
            }
         }

         if (_midletStores == null) {
            _midletStores = new Hashtable();
            allRecordStores.put(midletSuiteHashKey, _midletStores);
            _persistentObject.commit();
         }
      }

      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      RecordStoreManager rsm = (RecordStoreManager)ar.getOrWaitFor(6635119920104263588L);
      if (rsm == null) {
         rsm = new RecordStoreManager();
         ar.put(6635119920104263588L, rsm);
      }

      _rsml = (RecordStoreManager$RecordStoreManagerListener)ar.getOrWaitFor(8689535865899788676L);
      if (_rsml == null) {
         _rsml = new RecordStoreManager$RecordStoreManagerListener();
         ar.put(8689535865899788676L, _rsml);
         Proxy.getInstance().addGlobalEventListenerInternal(_rsml);
      }
   }
}
