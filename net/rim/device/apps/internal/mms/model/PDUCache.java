package net.rim.device.apps.internal.mms.model;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.util.LongHashtable;
import net.rim.device.apps.internal.mms.api.MMSAttachment;
import net.rim.device.apps.internal.mms.service.MMSProtocolDataUnit;
import net.rim.vm.WeakReference;

final class PDUCache {
   private LongHashtable _hashtable;
   private static final long PDU_CACHE_GUID;
   private static PDUCache _instance;

   private static final PDUCache getInstance() {
      if (_instance == null) {
         ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
         _instance = (PDUCache)ar.get(9118645089098207847L);
         if (_instance == null) {
            synchronized (ar) {
               _instance = (PDUCache)ar.get(9118645089098207847L);
               if (_instance == null) {
                  _instance = new PDUCache();
                  ar.put(9118645089098207847L, _instance);
               }
            }
         }
      }

      return _instance;
   }

   static final MMSProtocolDataUnit get(long key, MMSAttachment attachment) {
      PDUCache cache = getInstance();
      synchronized (cache) {
         MMSProtocolDataUnit pdu = cache.get(key);
         if (pdu == null) {
            byte[] data = attachment.getData();
            pdu = new MMSProtocolDataUnit(data);
            cache.put(key, pdu);
         }

         return pdu;
      }
   }

   private final MMSProtocolDataUnit get(long key) {
      if (this._hashtable != null) {
         WeakReference ref = (WeakReference)this._hashtable.get(key);
         if (ref != null) {
            return (MMSProtocolDataUnit)ref.get();
         }
      }

      return null;
   }

   private final void put(long key, MMSProtocolDataUnit pdu) {
      if (this._hashtable == null) {
         this._hashtable = (LongHashtable)(new Object());
      }

      WeakReference ref = (WeakReference)(new Object(pdu));
      this._hashtable.put(key, ref);
   }
}
