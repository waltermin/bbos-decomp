package net.rim.device.api.system;

import net.rim.device.api.util.LongIntHashtable;
import net.rim.device.api.util.Persistable;
import net.rim.vm.Array;
import net.rim.vm.Persistence;
import net.rim.vm.TraceBack;

public final class RIMPersistentStore implements Persistable {
   private static final int IT_POLICY_PERSISTENT_STORE_ACCESS;

   private RIMPersistentStore() {
   }

   public static final PersistentObject getPersistentObject(long key) {
      Object[] objects = (Object[])Persistence.getRoot();
      if (objects == null) {
         synchronized (Persistence.getSynchObject()) {
            if (Persistence.getRoot() == null) {
               objects = new Object[]{new LongIntHashtable(89)};
               Persistence.commit(objects, true);
               Persistence.setRoot(objects);
            }
         }

         objects = (Object[])Persistence.getRoot();
      }

      synchronized (objects) {
         LongIntHashtable keys = (LongIntHashtable)objects[0];
         PersistentObject obj = null;
         int index = keys.get(key);
         if (index != -1) {
            obj = (PersistentObject)objects[index];
         }

         if (obj != null) {
            return obj;
         }

         if (index == -1) {
            index = objects.length;
            Array.resize(objects, index + 1);
            keys.put(key, index);
         }

         obj = new PersistentObject();
         objects[index] = obj;
         Persistence.commit(objects, false);
         return obj;
      }
   }

   public static final void destroyPersistentObject(long key) {
      destroyPersistentObject(key, TraceBack.getCallingModule(0));
   }

   static final void destroyPersistentObject(long key, int callingModule) {
      Object[] objects = (Object[])Persistence.getRoot();
      if (objects != null) {
         synchronized (objects) {
            LongIntHashtable keys = (LongIntHashtable)objects[0];
            int index = keys.get(key);
            if (index != -1) {
               PersistentObject obj = (PersistentObject)objects[index];
               if (obj != null) {
                  obj.getControlledAccess().assertReplacePermission(callingModule);
                  objects[index] = null;
                  if (index == objects.length - 1) {
                     Array.resize(objects, index);
                     keys.remove(key);
                  }

                  Persistence.commit(objects, false);
               }
            }
         }
      }
   }

   public static final Object getSynchObject() {
      return Persistence.getSynchObject();
   }
}
