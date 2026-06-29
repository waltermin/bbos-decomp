package net.rim.device.apps.internal.sms;

import java.util.Vector;
import net.rim.device.api.collection.Collection;
import net.rim.device.api.collection.CollectionCombiner;
import net.rim.device.api.collection.ReadableList;
import net.rim.device.apps.api.framework.model.KeyUtilities;
import net.rim.device.apps.api.messaging.util.SortedCollection;

public final class MessageThread extends SortedCollection implements CollectionCombiner {
   private InclusionFilter _inclusionFilter;
   private Vector _sources;

   MessageThread(long addressHash) {
      this.initialize(-6498019436237624557L, addressHash, Storage.getLongKeyProviderAdaptor(), null);
      this._inclusionFilter = new ThreadInclusionFilter(addressHash);
      this._sources = (Vector)(new Object());
   }

   @Override
   public final void addSource(Object collection) {
      if (collection instanceof Object) {
         this.processCollectionForAddition((ReadableList)collection);
         this._sources.addElement(collection);
      }
   }

   private final void processCollectionForAddition(ReadableList list) {
      int sourceSize = list.size();

      for (int i = 0; i < sourceSize; i++) {
         Object item = list.getAt(i);
         if (this._inclusionFilter.include(item)) {
            this.insertElementAtCorrectLocation(item);
         }
      }
   }

   @Override
   public final void removeSource(Object collection) {
      this._sources.removeElement(collection);
      this.reset(null);
   }

   @Override
   public final void reset(Collection collection) {
      if (collection instanceof Object) {
         this.removeAll();
         int numberOfSources = this._sources.size();

         for (int i = 0; i < numberOfSources; i++) {
            ReadableList list = (ReadableList)this._sources.elementAt(i);
            this.processCollectionForAddition(list);
         }
      }
   }

   @Override
   public final void elementAdded(Collection collection, Object element) {
      if (this._inclusionFilter.include(element)) {
         this.insertElementAtCorrectLocation(element);
         if (!super._suspendNotification) {
            super._collectionListenerManager.fireElementAdded(this, element);
         }
      }
   }

   @Override
   public final void elementRemoved(Collection collection, Object element) {
      if (element instanceof SMSModel) {
         SMSModel model = (SMSModel)element;
         if ((model.getFlags() & 32) != 0) {
            int next = KeyUtilities.mapKeyToIndex(this, Storage.getLongKeyProviderAdaptor(), model._payload._creationDate);
            if (this.size() > next + 1) {
               Object newBeginning = this.getAt(next + 1);
               if (newBeginning instanceof SMSModel) {
                  ((SMSModel)newBeginning).setFlags(32);
               }
            }
         }
      }

      super.elementRemoved(collection, element);
   }
}
