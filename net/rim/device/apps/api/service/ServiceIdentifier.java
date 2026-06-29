package net.rim.device.apps.api.service;

import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.synchronization.UIDGenerator;
import net.rim.device.api.util.CRC16;
import net.rim.device.api.util.CRC32;
import net.rim.device.api.util.Persistable;

public class ServiceIdentifier implements Persistable {
   Object _key;
   protected long _uniqueServiceId;
   long _expiredTime = -1;
   private boolean _defaultService = false;
   private boolean _systemDefault = false;
   static final long DEFAULT_SERVICE_SET = 158775118060600435L;
   public static final long SERVICE_EVENT_ADDED = -860845403685493259L;
   public static final long SERVICE_EVENT_CHANGED = 8478935834746748823L;
   public static final long SERVICE_EVENT_REMOVED = -7853136852381124900L;

   ServiceIdentifier() {
   }

   public ServiceIdentifier(Object key) {
      this._key = key;
      this._uniqueServiceId = createServiceID(key);
   }

   public static long createServiceID(Object key) {
      long id = 0;
      if (!(key instanceof ServiceRecord)) {
         if (key instanceof String) {
            String keyString = (String)key;
            byte[] keyBytes = keyString.getBytes();
            id = UIDGenerator.makeLUID(CRC32.update(-1, keyBytes), CRC32.update(-1, keyBytes));
         }
      } else {
         ServiceRecord sr = (ServiceRecord)key;
         if (sr.getUserId() == -1) {
            int hashUID = sr.getUidHash();
            int hashName = sr.getNameHash();
            long highPart = hashUID & 4294967295L;
            long lowPart = hashName & 4294967295L;
            id = highPart << 32 | lowPart;
         } else {
            int hashUID = CRC16.update(65535, sr.getUid().getBytes());
            int hashDSID = CRC16.update(65535, sr.getDataSourceId().getBytes());
            int hashUserID = CRC32.update(-1, sr.getUserId());
            long highPart = hashUID & 65535;
            long middlePart = hashDSID & 65535;
            long lowPart = hashUserID & 4294967295L;
            id = highPart << 48 | middlePart << 32 | lowPart;
         }
      }

      if (id == 0) {
         id = UIDGenerator.makeLUID(UIDGenerator.getUID(), UIDGenerator.getUID());
      }

      return id;
   }

   protected void setSystemDefault(boolean systemDefault) {
      this._systemDefault = systemDefault;
   }

   public boolean isSystemDefault() {
      return this._systemDefault;
   }

   public long getUniqueServiceID() {
      if (this._uniqueServiceId == 0) {
         this._uniqueServiceId = createServiceID(this._key);
      }

      return this._uniqueServiceId;
   }

   public Object getServiceKey() {
      return this._key;
   }

   public void setServiceKey(Object newKey) {
      this._key = newKey;
      this._uniqueServiceId = createServiceID(newKey);
   }

   public void changeServiceKey(Object newKey) {
      this.setServiceKey(newKey);
   }

   public void deleteService() {
   }

   void setDefaultService(boolean defaultService) {
      this._defaultService = defaultService;
   }

   public boolean isDefaultService() {
      return this._defaultService;
   }

   public boolean isSecureService() {
      boolean result = false;
      if (this._key instanceof ServiceRecord) {
         ServiceRecord sr = (ServiceRecord)this._key;
         result = sr.isSecureService();
      }

      return result;
   }

   @Override
   public String toString() {
      return this._key.toString();
   }
}
