package net.rim.device.apps.api.messaging;

import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.RIMPersistentStore;
import net.rim.device.api.util.IntHashtable;

public final class MessageLookups {
   public static final long CMIME_REFERENCE_IDENTIFIER_LOOKUP = -4420850319371185992L;
   public static final long PIN_CMIME_REFERENCE_IDENTIFIER_LOOKUP = 1844977059971836508L;
   public static final long GME_REFERENCE_IDENTIFIER_LOOKUP = 431630751329425149L;
   public static final long SMS_REFERENCE_IDENTIFIER_LOOKUP = -6051701886797080507L;
   public static final long VOICE_REFERENCE_IDENTIFIER_LOOKUP = -7579072715623987642L;
   public static final long IMPLUS_REFERENCE_IDENTIFIER_LOOKUP = 2623838111545834320L;
   public static final long CMIME_ORIGINAL_REFERENCE_IDENTIFIER_LOOKUP = 4530015158237739359L;
   private static final long MESSAGE_LOOKUPS_PERSISTENT_STORE = 2428421339051139361L;

   private MessageLookups() {
   }

   private static final IntHashtable getLookupTable(long lookupTable, boolean create) {
      IntHashtable hashtable = null;
      PersistentObject persistentObject = RIMPersistentStore.getPersistentObject(2428421339051139361L + lookupTable);
      synchronized (persistentObject) {
         hashtable = (IntHashtable)persistentObject.getContents();
         if (hashtable == null && create) {
            hashtable = new IntHashtable();
            persistentObject.setContents(hashtable, 51);
            persistentObject.commit();
         }

         return hashtable;
      }
   }

   public static final void put(long lookupTable, int lookupTag, Object item) {
      PersistentObject.commit(internalPutWithoutCommit(lookupTable, lookupTag, item));
   }

   public static final void putWithoutCommit(long lookupTable, int lookupTag, Object item) {
      internalPutWithoutCommit(lookupTable, lookupTag, item);
   }

   private static final Object internalPutWithoutCommit(long lookupTable, int lookupTag, Object item) {
      IntHashtable hashtable = getLookupTable(lookupTable, true);
      synchronized (hashtable) {
         hashtable.put(lookupTag, item);
         return hashtable;
      }
   }

   public static final Object get(long lookupTable, int lookupTag) {
      IntHashtable hashtable = getLookupTable(lookupTable, false);
      Object result = null;
      if (hashtable != null) {
         synchronized (hashtable) {
            result = hashtable.get(lookupTag);
         }
      }

      return result;
   }

   public static final Object remove(long lookupTable, int lookupTag) {
      IntHashtable hashtable = getLookupTable(lookupTable, false);
      Object result = null;
      if (hashtable != null) {
         synchronized (hashtable) {
            result = hashtable.remove(lookupTag);
            if (result != null) {
               PersistentObject.commit(hashtable);
            }
         }
      }

      return result;
   }

   public static final void commit(long lookupTable) {
      IntHashtable hashtable = getLookupTable(lookupTable, false);
      if (hashtable != null) {
         PersistentObject.commit(hashtable);
      }
   }
}
