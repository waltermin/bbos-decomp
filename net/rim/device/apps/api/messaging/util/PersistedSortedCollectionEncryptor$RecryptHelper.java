package net.rim.device.apps.api.messaging.util;

import net.rim.device.api.collection.util.BigVector;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.apps.api.utility.framework.RecryptableCollection;

class PersistedSortedCollectionEncryptor$RecryptHelper implements RecryptableCollection {
   private PersistentObject _collectionsPersistentObject;

   PersistedSortedCollectionEncryptor$RecryptHelper(PersistentObject collectionsPersistentObject) {
      this._collectionsPersistentObject = collectionsPersistentObject;
   }

   @Override
   public int getSize(Object cookie) {
      if (cookie instanceof Object[]) {
         return ((Object[])cookie).length;
      } else {
         return !(cookie instanceof BigVector) ? 0 : ((BigVector)cookie).size();
      }
   }

   @Override
   public Object getElementAt(int index, Object cookie) {
      if (cookie instanceof Object[]) {
         return ((Object[])cookie)[index];
      } else {
         return !(cookie instanceof BigVector) ? null : ((BigVector)cookie).elementAt(index);
      }
   }

   @Override
   public void replaceElementAt(Object oldElement, Object newElement, int index, Object cookie) {
      if (cookie instanceof Object[]) {
         ((Object[])cookie)[index] = newElement;
      } else {
         if (cookie instanceof BigVector) {
            ((BigVector)cookie).setElementAt(newElement, index);
         }
      }
   }

   @Override
   public void updateListeners(Object oldElement, Object newElement, Object cookie) {
   }

   @Override
   public void commit(Object cookie) {
      this._collectionsPersistentObject.commit();
   }

   @Override
   public void reCryptStarted(Object cookie) {
   }

   @Override
   public void reCryptEnded(Object cookie) {
   }
}
