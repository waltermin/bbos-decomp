package net.rim.device.apps.api.service;

import java.util.Enumeration;
import java.util.Hashtable;
import net.rim.device.api.servicebook.ServiceBook;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.RIMGlobalMessagePoster;
import net.rim.device.api.system.RIMPersistentStore;
import net.rim.device.api.util.Factory;
import net.rim.device.api.util.LongEnumeration;
import net.rim.device.api.util.LongHashtable;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.internal.proxy.Proxy;
import net.rim.vm.Array;

public class MultiServiceManager implements GlobalEventListener {
   private Hashtable _serviceStore;
   private PersistentObject _persistentServiceStore;
   private Hashtable _serviceFactories = (Hashtable)(new Object());
   protected static final long ID = 5421400767263226689L;
   protected static final long PERSISTANT_STORE_ID = 6664804295181405943L;
   protected static final long PERSISTANT_EXPIRED_STORE_ID = 3614653646137588814L;
   public static final long DEFAULT_SERVICE_SET = 158775118060600435L;
   private static final long DEFAULT_SERVICE_KEY = -2793225263839058177L;
   private static final long DEFAULT_SERVICE_KEY_SET_BY_USER = 4251133684997842836L;
   private static MultiServiceManager _instance;

   public synchronized ServiceIdentifier[] getAllServices(String CID) {
      ServiceIdentifier[] result = new ServiceIdentifier[0];
      Object o = this._serviceStore.get(CID);
      if (o instanceof Object) {
         LongHashtable ht = (LongHashtable)o;
         if (ht.size() > 0) {
            result = new ServiceIdentifier[ht.size()];
            LongEnumeration keys = ht.keys();
            int count = 0;

            while (keys.hasMoreElements()) {
               long key = keys.nextElement();
               if (key != -2793225263839058177L && key != 4251133684997842836L) {
                  ServiceIdentifier service = (ServiceIdentifier)ht.get(key);
                  result[count++] = service;
               } else {
                  Array.resize(result, result.length - 1);
               }
            }
         }
      }

      return result;
   }

   public synchronized ServiceIdentifier setDefaultService(String CID, ServiceIdentifier service) {
      return (ServiceIdentifier)this.setDefaultService(CID, service, false);
   }

   public ServiceIdentifier setDefaultService(String CID, ServiceIdentifier service, boolean setByUser) {
      ServiceIdentifier oldService = null;
      ServiceIdentifier oldServiceSetByUser = null;
      synchronized (this._serviceStore) {
         Object o = this._serviceStore.get(CID);
         if (o instanceof Object) {
            LongHashtable ht = (LongHashtable)o;
            if (ht.containsKey(service.getUniqueServiceID())) {
               service.setDefaultService(true);
               oldService = (ServiceIdentifier)ht.get(-2793225263839058177L);
               oldServiceSetByUser = (ServiceIdentifier)ht.get(4251133684997842836L);
               if (setByUser) {
                  ht.put(4251133684997842836L, service);
               } else {
                  ht.put(-2793225263839058177L, service);
               }

               if (oldService != null) {
                  oldService.setDefaultService(false);
               }

               if (oldServiceSetByUser != null) {
                  oldServiceSetByUser.setDefaultService(false);
               }

               RIMGlobalMessagePoster.postGlobalEvent(8288627527798139133L, 0, 0, CID, service);
            }
         }

         this.commitChanges();
      }

      RIMGlobalMessagePoster.postGlobalEvent(158775118060600435L, 0, 0, CID, service);
      return oldService;
   }

   public synchronized ServiceIdentifier getDefaultService(String CID) {
      Object o = this._serviceStore.get(CID);
      ServiceIdentifier result = null;
      if (o instanceof Object) {
         LongHashtable ht = (LongHashtable)o;
         Object userSetDefaultService = ht.get(4251133684997842836L);
         if (userSetDefaultService instanceof ServiceIdentifier) {
            return (ServiceIdentifier)userSetDefaultService;
         }

         Object defaultService = ht.get(-2793225263839058177L);
         if (defaultService instanceof ServiceIdentifier) {
            result = (ServiceIdentifier)defaultService;
         }
      }

      return result;
   }

   public ServiceIdentifier findServiceByID(String CID, long serviceID) {
      Object o = this._serviceStore.get(CID);
      ServiceIdentifier result = null;
      if (o instanceof Object) {
         LongHashtable ht = (LongHashtable)o;
         result = (ServiceIdentifier)ht.get(serviceID);
      }

      return result;
   }

   public ServiceIdentifier findServiceByKey(String CID, Object key) {
      LongHashtable ht = (LongHashtable)this._serviceStore.get(CID);
      return (ServiceIdentifier)this.findServiceInTable(ht, key, -1);
   }

   public synchronized void registerCID(String CID, Factory serviceFactory) {
      if (serviceFactory != null) {
         this._serviceFactories.put(CID, serviceFactory);
         if (!this._serviceStore.containsKey(CID)) {
            this._serviceStore.put(CID, new Object());
            this.reloadFromExistingServices(CID);
            this.fixUpDefaultService(CID);
         }
      }
   }

   public synchronized void deRegisterCID(String CID) {
      if (this._serviceFactories.containsKey(CID)) {
         this._serviceFactories.remove(CID);
         if (this._serviceStore.containsKey(CID)) {
            this._serviceStore.remove(CID);
            this.commitChanges();
         }
      }
   }

   public synchronized ServiceIdentifier addNewService(String cid, Object key, boolean systemDefault) {
      ServiceIdentifier result = this.addService(cid, key);
      result.setSystemDefault(systemDefault);
      this.fixUpDefaultService(cid);
      RIMGlobalMessagePoster.postGlobalEvent(-860845403685493259L, 0, 0, key, null);
      return result;
   }

   public synchronized ServiceRecord[] getAllServiceRecords(String cid) {
      ServiceRecord[] records = new Object[0];
      ServiceBook sb = ServiceBook.getSB();

      for (ServiceRecord sr : sb.findRecordsByType(0)) {
         String contentType = StringUtilities.toUpperCase(sr.getCid(), 1701707776);
         if (contentType.equalsIgnoreCase(cid)) {
            Array.resize(records, records.length + 1);
            records[records.length - 1] = sr;
         }
      }

      return records;
   }

   @Override
   public synchronized void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if ((guid == -4220058463650496006L || guid == 8288627527798139133L || guid == 2522898683889177438L) && object0 instanceof Object) {
         ServiceRecord sr = (ServiceRecord)object0;
         String cid = sr != null && sr.getCid() != null ? sr.getCid() : null;
         if (cid != null && this._serviceFactories.containsKey(cid)) {
            if (sr.getType() == 2 || sr.getType() == 6) {
               guid = 2522898683889177438L;
            }

            this.handleServiceChange(sr, cid, guid == 2522898683889177438L);
         }
      }
   }

   MultiServiceManager() {
      this._persistentServiceStore = RIMPersistentStore.getPersistentObject(6664804295181405943L);

      label26:
      try {
         this._serviceStore = (Hashtable)this._persistentServiceStore.getContents();
      } finally {
         break label26;
      }

      if (this._serviceStore == null) {
         this._serviceStore = (Hashtable)(new Object());
         this._persistentServiceStore.setContents(this._serviceStore, 51);
         this._persistentServiceStore.commit();
      }

      Proxy.getInstance().addGlobalEventListener(this);
   }

   public static MultiServiceManager getInstance() {
      if (_instance == null) {
         ApplicationRegistry reg = ApplicationRegistry.getApplicationRegistry();
         synchronized (reg) {
            _instance = (MultiServiceManager)reg.getOrWaitFor(5421400767263226689L);
            if (_instance == null) {
               _instance = new MultiServiceManager();
               reg.put(5421400767263226689L, _instance);
            }
         }
      }

      return _instance;
   }

   private void fixUpDefaultService(String CID) {
      ServiceIdentifier firstInsecure = null;
      ServiceIdentifier service = null;
      ServiceIdentifier currentDefaultService = null;
      Object o = this._serviceStore.get(CID);
      if (o instanceof Object) {
         LongHashtable ht = (LongHashtable)o;
         Object userSetDefaultService = ht.get(4251133684997842836L);
         if (userSetDefaultService instanceof ServiceIdentifier) {
            return;
         }

         Object defaultService = ht.get(-2793225263839058177L);
         if (defaultService instanceof ServiceIdentifier) {
            service = (ServiceIdentifier)defaultService;
            currentDefaultService = (ServiceIdentifier)defaultService;
         }

         boolean needToScan = service == null || service != null && !service.isSecureService();
         if (needToScan) {
            ServiceIdentifier var12 = null;
            Enumeration elements = ht.elements();

            while (elements.hasMoreElements()) {
               Object serviceObject = elements.nextElement();
               if (serviceObject instanceof ServiceIdentifier) {
                  var12 = (ServiceIdentifier)serviceObject;
                  if (var12.isSecureService()) {
                     this.setDefaultService(CID, var12);
                     return;
                  }

                  if (firstInsecure == null || !(firstInsecure.getServiceKey() instanceof Object)) {
                     firstInsecure = var12;
                  }
               }
            }

            if ((currentDefaultService == null || currentDefaultService.isSystemDefault()) && firstInsecure != null) {
               this.setDefaultService(CID, firstInsecure);
            }
         }
      }
   }

   private ServiceIdentifier handleServiceChange(ServiceRecord newServiceRecord, String cid, boolean remove) {
      ServiceIdentifier result = null;
      if (this._serviceStore.containsKey(cid)) {
         long eventToBrodcast = -1;
         ServiceRecord oldServiceKey = null;
         ServiceRecord newServiceKey = null;
         LongHashtable serviceTable = (LongHashtable)this._serviceStore.get(cid);
         long newServiceID = ServiceIdentifier.createServiceID(newServiceRecord);
         result = (ServiceIdentifier)serviceTable.get(newServiceID);
         LongEnumeration keys = serviceTable.keys();

         while (keys.hasMoreElements()) {
            long currentServiceID = keys.nextElement();
            ServiceIdentifier serviceIdentifier = (ServiceIdentifier)serviceTable.get(currentServiceID);
            Object o = serviceIdentifier.getServiceKey();
            if (o instanceof Object) {
               ServiceRecord currentServiceRecord = (ServiceRecord)o;
               String cidOfNewServiceRecord = newServiceRecord.getCid() != null ? newServiceRecord.getCid() : null;
               if (cidOfNewServiceRecord != null
                  && StringUtilities.strEqualIgnoreCase(cidOfNewServiceRecord, cid, 1701707776)
                  && (currentServiceRecord == newServiceRecord || currentServiceRecord.isDuplicate(newServiceRecord, -1, null, null, null, null, -1))) {
                  boolean deleteChange = false;
                  result = (ServiceIdentifier)serviceTable.get(currentServiceID);
                  serviceTable.remove(currentServiceID);
                  if (!remove) {
                     if (newServiceID == currentServiceID) {
                        result.changeServiceKey(newServiceRecord);
                        serviceTable.put(newServiceID, result);
                        eventToBrodcast = 8478935834746748823L;
                     } else {
                        deleteChange = true;
                     }
                  }

                  if (remove || deleteChange) {
                     result.deleteService();
                     ServiceIdentifier defaultServiceByUser = (ServiceIdentifier)serviceTable.get(4251133684997842836L);
                     if (result == defaultServiceByUser) {
                        serviceTable.remove(4251133684997842836L);
                     }

                     ServiceIdentifier defaultService = (ServiceIdentifier)serviceTable.get(-2793225263839058177L);
                     if (result == defaultService) {
                        serviceTable.remove(-2793225263839058177L);
                     }

                     result = null;
                     eventToBrodcast = -7853136852381124900L;
                  }

                  this.commitChanges();
                  oldServiceKey = currentServiceRecord;
                  newServiceKey = newServiceRecord;
                  break;
               }
            }
         }

         if (result == null && !remove) {
            result = this.addService(cid, newServiceRecord);
            newServiceKey = newServiceRecord;
            eventToBrodcast = -860845403685493259L;
         }

         this.fixUpDefaultService(cid);
         if (eventToBrodcast != -1) {
            int newID = newServiceKey != null ? newServiceKey.getId() : 0;
            int oldID = oldServiceKey != null ? oldServiceKey.getId() : 0;
            RIMGlobalMessagePoster.postGlobalEvent(eventToBrodcast, newID, oldID, newServiceKey, oldServiceKey);
         }
      }

      return result;
   }

   private ServiceIdentifier addService(String cid, Object key) {
      Factory serviceCreator = (Factory)this._serviceFactories.get(cid);
      ServiceIdentifier result = (ServiceIdentifier)serviceCreator.createInstance(key);
      LongHashtable ht = (LongHashtable)this._serviceStore.get(cid);
      result.setServiceKey(key);
      ht.put(result.getUniqueServiceID(), result);
      this.commitChanges();
      return result;
   }

   private void commitChanges() {
      this._persistentServiceStore.commit();
   }

   private void reloadFromExistingServices(String cid) {
      ServiceBook sb = ServiceBook.getSB();
      ServiceRecord[] srs = sb.findRecordsByCid(cid);

      for (int i = 0; i < srs.length; i++) {
         ServiceRecord sr = srs[i];
         String srcid = sr.getCid() != null ? sr.getCid() : null;
         if (srcid != null) {
            this.handleServiceChange(sr, srcid, false);
         }
      }
   }

   private Object findServiceInTable(LongHashtable ht, Object key, int serviceRecordType) {
      Object result = null;
      if (ht != null) {
         synchronized (ht) {
            long id = ServiceIdentifier.createServiceID(key);
            result = ht.get(id);
            if (result == null) {
               Enumeration values = ht.elements();

               while (values.hasMoreElements() && result == null) {
                  Object o = values.nextElement();
                  if (o instanceof ServiceIdentifier) {
                     ServiceIdentifier service = (ServiceIdentifier)o;
                     Object serviceKey = service.getServiceKey();
                     if (serviceKey instanceof Object) {
                        ServiceRecord sr1 = (ServiceRecord)serviceKey;
                        if (key instanceof Object) {
                           ServiceRecord sr2 = (ServiceRecord)key;
                           if (sr2.isDuplicate(sr1, serviceRecordType, null, null, null, null, -1)) {
                              result = o;
                           }
                        }
                     }
                  }
               }
            }
         }
      }

      return result;
   }
}
