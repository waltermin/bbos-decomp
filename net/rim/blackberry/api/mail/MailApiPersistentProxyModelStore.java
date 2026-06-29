package net.rim.blackberry.api.mail;

import net.rim.device.api.collection.Collection;
import net.rim.device.api.collection.ReadableList;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.RIMPersistentStore;
import net.rim.device.api.util.IntEnumeration;
import net.rim.device.api.util.IntHashtable;
import net.rim.device.api.util.IntVector;
import net.rim.device.api.util.Persistable;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.internal.blackberryemail.email.EmailMessageModelImpl;
import net.rim.device.apps.internal.blackberryemail.email.PersistentProxyModelStore;
import net.rim.device.apps.internal.blackberryemail.email.ProxyModel;
import net.rim.vm.WeakReference;

final class MailApiPersistentProxyModelStore extends PersistentProxyModelStore implements Persistable {
   public static final long PROXY_MODEL_STORE_ID = -7104206253406502952L;

   public static final MailApiPersistentProxyModelStore getInstance() {
      PersistentObject po = RIMPersistentStore.getPersistentObject(-7104206253406502952L);
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      synchronized (ar) {
         synchronized (po) {
            if (po.getContents() == null) {
               po.setContents(new MailApiPersistentProxyModelStore(-7104206253406502952L, true), 51);
               po.commit();
            }

            IntHashtable h = (IntHashtable)ar.getOrWaitFor(-7104206253406502952L);
            if (null == h) {
               h = new IntHashtable();
               ar.put(-7104206253406502952L, h);
            }
         }
      }

      return (MailApiPersistentProxyModelStore)po.getContents();
   }

   private MailApiPersistentProxyModelStore(long id, boolean group) {
      super(id, group);
   }

   @Override
   public final synchronized int add(Object o) {
      IntHashtable transientRefs = ApplicationRegistry.getApplicationRegistry().getIntHashtable(-7104206253406502952L);
      if (transientRefs == null) {
         throw new IllegalStateException("MailApi: TransientRefs table does not exist");
      }

      if (transientRefs.contains(o)) {
         IntEnumeration e = transientRefs.keys();

         while (e.hasMoreElements()) {
            int key = e.nextElement();
            if (transientRefs.get(key).equals(o)) {
               return key;
            }
         }

         throw new IllegalStateException("AbstractProxyModelStore: add() - illegal state!");
      } else if (super._data.contains(o)) {
         IntEnumeration e = super._data.keys();

         while (e.hasMoreElements()) {
            int key = e.nextElement();
            if (super._data.get(key).equals(o)) {
               return key;
            }
         }

         throw new IllegalStateException("AbstractProxyModelStore: add() - illegal state!");
      } else {
         transientRefs.put(++super._index, new WeakReference(o));
         PersistentObject.commit(this);
         return super._index;
      }
   }

   @Override
   public final Object get(int objectHandle) {
      IntHashtable transientRefs = ApplicationRegistry.getApplicationRegistry().getIntHashtable(-7104206253406502952L);
      Object o = transientRefs.get(objectHandle);
      if (o instanceof WeakReference) {
         WeakReference wr = (WeakReference)o;
         o = wr.get();
      }

      if (o == null) {
         o = super._data.get(objectHandle);
      }

      return o;
   }

   @Override
   public final synchronized Object delete(int handle) {
      return super.delete(handle);
   }

   @Override
   public final void reset(Collection c) {
      this.reset(c, null);
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void reset(Collection c, Object hint) {
      if (!ContextObject.getFlag(hint, 62)) {
         try {
            synchronized (this) {
               int index = -1;
               IntVector handles = new IntVector();
               ReadableList rl = (ReadableList)c;

               for (int i = rl.size() - 1; i >= 0; i--) {
                  Object item = rl.getAt(i);
                  if (item instanceof EmailMessageModelImpl) {
                     EmailMessageModelImpl m = (EmailMessageModelImpl)item;
                     if (m.getAttachmentCount() != 0) {
                        for (int j = m.size() - 1; j >= 0; j--) {
                           Object o = m.getAt(j);
                           if (o instanceof ProxyModel) {
                              ProxyModel pm = (ProxyModel)o;
                              if (pm.getRootId() == super._rootId) {
                                 int handle = pm.getObjectHandle();
                                 handles.addElement(handle);
                                 index = Math.max(handle, index);
                              }
                           }
                        }
                     }
                  }
               }

               int[] keys = new int[super._data.size()];
               super._data.keysToArray(keys);

               for (int i = keys.length - 1; i >= 0; i--) {
                  if (!handles.contains(keys[i])) {
                     this.delete(keys[i]);
                  }
               }

               super._index = index;
               PersistentObject.commit(this);
            }
         } catch (Throwable var18) {
            EventLogger.logEvent(DefaultService.EVENT_LOGGER_GUID, e.toString().getBytes(), 2);
            return;
         }
      }
   }

   @Override
   protected final synchronized void collectionEvent(Collection collection, Object element, int delta) {
      IntHashtable transientRefs = ApplicationRegistry.getApplicationRegistry().getIntHashtable(-7104206253406502952L);
      if (element instanceof ReadableList) {
         ReadableList l = (ReadableList)element;
         int size = l.size();

         for (int i = 0; i < size; i++) {
            Object o = l.getAt(i);
            if (o instanceof ProxyModel) {
               ProxyModel pm = (ProxyModel)o;
               if (pm.getRootId() == super._rootId) {
                  int handle = pm.getObjectHandle();
                  if (!this.makeRefHard(handle, delta)) {
                     this.refCount(handle, delta);
                  }
               }
            }
         }
      }

      IntEnumeration keys = transientRefs.keys();

      while (keys.hasMoreElements()) {
         int key = keys.nextElement();
         Object o = transientRefs.get(key);
         if (o instanceof WeakReference) {
            WeakReference wr = (WeakReference)o;
            if (wr.get() == null) {
               transientRefs.remove(key);
               super._refCounts.remove(key);
               super._data.remove(key);
            }
         }
      }

      PersistentObject.commit(this);
   }

   final boolean makeRefHard(int handle, int delta) {
      boolean retVal = false;
      IntHashtable transientRefs = ApplicationRegistry.getApplicationRegistry().getIntHashtable(-7104206253406502952L);
      Object oo = transientRefs.get(handle);
      if (delta > 0 && oo instanceof WeakReference) {
         WeakReference wr = (WeakReference)oo;
         Object hardref = wr.get();
         transientRefs.remove(handle);
         super._data.put(handle, hardref);
         super._refCounts.put(handle, delta);
         retVal = true;
      }

      return retVal;
   }

   final void removeWeakRef(int handle) {
      this.makeRefHard(handle, 1);
      super._refCounts.put(handle, 0);
   }
}
