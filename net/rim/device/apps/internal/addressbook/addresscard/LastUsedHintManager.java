package net.rim.device.apps.internal.addressbook.addresscard;

import net.rim.device.api.collection.util.BigLongVector;
import net.rim.device.api.synchronization.SyncObject;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.RIMPersistentStore;
import net.rim.device.api.util.IntIntHashtable;

public final class LastUsedHintManager {
   private static final long UID_MASK = -4294967296L;
   private static final long KEY_MASK = -256L;
   private static final long ADDRESSBOOK_LAST_USED_HINTS = -894877559931875652L;
   private static BigLongVector _hints;
   private static final long ADDRESSBOOK_LAST_USED_HINT_TYPE = -3259947950905852759L;
   private static IntIntHashtable _lastHintType;

   private LastUsedHintManager() {
   }

   private static final long getKey(int uid, int hintType) {
      return (long)uid << 32 | (hintType & 16777215) << 8;
   }

   public static final int get(int uid, int hintType) {
      long key = getKey(uid, hintType);
      synchronized (_hints) {
         int index = _hints.binarySearch(key);
         if (index < 0) {
            index = -index - 1;
         }

         if (index < _hints.size()) {
            long entry = _hints.elementAt(index);
            if ((entry & -256) == key) {
               return (int)(entry & 255);
            }
         }

         return -1;
      }
   }

   public static final int getLastHintType(int uid) {
      synchronized (_lastHintType) {
         return _lastHintType.get(uid);
      }
   }

   private static final void setLastHintType(int uid, int hintType) {
      synchronized (_lastHintType) {
         _lastHintType.put(uid, hintType);
         PersistentObject.commit(_lastHintType);
      }
   }

   public static final void put(int uid, int hintType, int hint, boolean setLastHintType) {
      if (uid != -1) {
         long key = getKey(uid, hintType);
         long value = key | hint & 0xFF;
         if (setLastHintType) {
            setLastHintType(uid, hintType);
         }

         synchronized (_hints) {
            int index = _hints.binarySearch(key);
            if (index < 0) {
               index = -index - 1;
            }

            if (index < _hints.size()) {
               long entry = _hints.elementAt(index);
               if ((entry & -256) == key) {
                  if (entry != value) {
                     _hints.setElementAt(value, index);
                     PersistentObject.commit(_hints);
                  }

                  return;
               }
            }

            _hints.insertElementAt(value, index);
            PersistentObject.commit(_hints);
         }
      }
   }

   public static final void update(Object oldObject, Object newObject) {
      int oldUid = -1;
      int newUid = -1;
      if (oldObject instanceof Object) {
         oldUid = ((SyncObject)oldObject).getUID();
      }

      if (newObject instanceof Object) {
         newUid = ((SyncObject)newObject).getUID();
      }

      if (oldUid != -1 && newUid != -1 && oldUid == newUid && oldObject != newObject) {
         remove(oldUid);
      } else {
         if (oldUid != -1) {
            remove(oldUid);
         }
      }
   }

   public static final void remove(int uid) {
      long key = getKey(uid, 0);
      synchronized (_hints) {
         int index = _hints.binarySearch(key);
         if (index < 0) {
            index = -index - 1;
         }

         boolean anyRemoved;
         for (anyRemoved = false; index < _hints.size() && (_hints.elementAt(index) & -4294967296L) == key; anyRemoved = true) {
            _hints.removeElementAt(index);
         }

         if (anyRemoved) {
            PersistentObject.commit(_hints);
         }
      }
   }

   public static final void remove(Object object) {
      if (object instanceof Object) {
         remove(((SyncObject)object).getUID());
      }
   }

   public static final void removeAll() {
      synchronized (_hints) {
         _hints.removeAll();
         PersistentObject.commit(_hints);
      }
   }

   static {
      PersistentObject persistentObject = RIMPersistentStore.getPersistentObject(-894877559931875652L);
      _hints = (BigLongVector)persistentObject.getContents();
      if (_hints == null) {
         synchronized (persistentObject) {
            _hints = (BigLongVector)persistentObject.getContents();
            if (_hints == null) {
               _hints = (BigLongVector)(new Object());
               persistentObject.setContents(_hints, 51);
               persistentObject.commit();
            }
         }
      }

      persistentObject = RIMPersistentStore.getPersistentObject(-3259947950905852759L);
      _lastHintType = (IntIntHashtable)persistentObject.getContents();
      if (_lastHintType == null) {
         synchronized (persistentObject) {
            _lastHintType = (IntIntHashtable)persistentObject.getContents();
            if (_lastHintType == null) {
               _lastHintType = (IntIntHashtable)(new Object());
               persistentObject.setContents(_lastHintType, 51);
               persistentObject.commit();
            }
         }
      }
   }
}
