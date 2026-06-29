package net.rim.device.apps.api.utility.lowMemory;

import java.util.Vector;
import net.rim.device.api.collection.Collection;
import net.rim.device.api.collection.CollectionLock;
import net.rim.device.api.collection.ReadableList;
import net.rim.device.api.lowmemory.LowMemoryListener;
import net.rim.device.api.lowmemory.LowMemoryManager;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.apps.api.framework.model.KeyProvider;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.vm.Process;

public final class PurgeManager implements LowMemoryListener {
   private long[] _purgeCollectionKeys = new long[1];
   private Vector _collections = (Vector)(new Object());
   private long[] _sort = new long[1];
   private static final long KEY;
   public static final long MIN_PURGE_IDLE_TIME;
   public static final long MAX_PURGE_TIME;
   public static final long MAX_PURGE_COUNT;
   private static PurgeManager _instance;

   public static final PurgeManager getInstance() {
      return _instance;
   }

   private PurgeManager() {
   }

   public final void addCollection(Collection collection) {
      if (collection instanceof Object) {
         synchronized (CollectionLock.getGlobalLock()) {
            if (this._collections.size() == 0) {
               LowMemoryManager.addLowMemoryListener(this);
            }

            if (this._collections.indexOf(collection) == -1) {
               this._collections.addElement(collection);
            }
         }
      }
   }

   public final void removeCollection(Collection collection) {
      if (collection instanceof Object) {
         synchronized (CollectionLock.getGlobalLock()) {
            this._collections.removeElement(collection);
            if (this._collections.size() == 0) {
               LowMemoryManager.removeLowMemoryListener(this);
            }
         }
      }
   }

   public final boolean purge(int purgeType, long threshold) {
      for (int i = this._collections.size() - 1; i >= 0; i--) {
         if (!this.purgeCollection((ReadableList)this._collections.elementAt(i), purgeType, threshold)) {
            return false;
         }
      }

      return true;
   }

   public final boolean purgeCollection(ReadableList collection, int purgeType, long threshold) {
      synchronized (CollectionLock.getGlobalLock()) {
         long idleTime = Process.ensureMinimumIdleTime(30);
         if (idleTime <= 0) {
            return false;
         }

         int size = collection.size();
         boolean forceResume = false;
         int indexOfFirstItemToDelete = -1;

         for (int i = 0; i < size; i++) {
            Object o = collection.getAt(i);
            if (o instanceof KeyProvider) {
               ((KeyProvider)o).getKeys(null, this._purgeCollectionKeys, 0, -7628247220259263034L);
               if (this._purgeCollectionKeys[0] > threshold) {
                  break;
               }

               indexOfFirstItemToDelete = i;
               if (i >= 50) {
                  forceResume = true;
                  break;
               }
            }
         }

         long startTime = System.currentTimeMillis();

         for (int i = indexOfFirstItemToDelete; i >= 0; i--) {
            Object o = collection.getAt(i);
            if (o instanceof PurgeProvider) {
               PurgeProvider p = (PurgeProvider)o;
               if (p.canPurge(purgeType)) {
                  p.purge(purgeType);
               }
            }

            idleTime = Process.ensureMinimumIdleTime(idleTime);
            if (idleTime <= 0) {
               return false;
            }

            if (System.currentTimeMillis() - startTime > 5000) {
               return false;
            }
         }

         return !forceResume;
      }
   }

   private final RIMModel getPurgeCandidate(ReadableList collection) {
      int size = collection.size();

      for (int i = 0; i < size; i++) {
         Object o = collection.getAt(i);
         if (o instanceof RIMModel) {
            RIMModel m = (RIMModel)o;
            if (m instanceof PurgeProvider) {
               PurgeProvider p = (PurgeProvider)m;
               if (p.canPurge(0)) {
                  p.getKeys(null, this._sort, 0, -7628247220259263034L);
                  return m;
               }
            }
         }
      }

      return null;
   }

   @Override
   public final boolean freeStaleObject(int priority) {
      if (priority != 1) {
         return false;
      }

      synchronized (CollectionLock.getGlobalLock()) {
         int num = this._collections.size();
         if (num == 0) {
            return false;
         }

         RIMModel theCandidate = null;
         long theSort = Long.MAX_VALUE;

         for (int i = 0; i < num; i++) {
            RIMModel candidate = this.getPurgeCandidate((ReadableList)this._collections.elementAt(i));
            if (candidate != null && this._sort[0] < theSort) {
               theSort = this._sort[0];
               theCandidate = candidate;
            }
         }

         if (theCandidate == null) {
            return false;
         }

         ((PurgeProvider)theCandidate).purge(0);
         return true;
      }
   }

   static {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      _instance = (PurgeManager)ar.getOrWaitFor(8720984902929879083L);
      if (_instance == null) {
         _instance = new PurgeManager();
         ar.put(8720984902929879083L, _instance);
      }
   }
}
