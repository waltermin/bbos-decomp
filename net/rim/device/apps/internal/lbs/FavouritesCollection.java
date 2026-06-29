package net.rim.device.apps.internal.lbs;

import net.rim.device.api.collection.LongKeyProviderAdaptor;
import net.rim.device.apps.api.messaging.FolderHierarchies;
import net.rim.device.apps.api.messaging.util.PersistedSortedCollection;
import net.rim.vm.Array;

final class FavouritesCollection extends PersistedSortedCollection {
   @Override
   public final boolean initialize(long applicationFamily, long folderId, LongKeyProviderAdaptor longKeyProviderAdaptor, Object context) {
      return this.initialize(
         applicationFamily, folderId, new FavouritesCollection$FavouritesLongKeyProviderAdaptorComparator(this, longKeyProviderAdaptor), context
      );
   }

   @Override
   protected final void traverseItemsToInitialize(Object context) {
   }

   @Override
   protected final void insertElementAtCorrectLocation(Object message) {
      if (super._useBigVector) {
         super.insertElementAtCorrectLocation(message);
      } else {
         int previousSize = super._messagesAsArray.length;
         Array.resize(super._messagesAsArray, previousSize + 1);
         super._messagesAsArray[previousSize] = message;
         if (previousSize >= 64) {
            this.checkForPromotion();
         }
      }
   }

   @Override
   public final int getIndex(Object obj) {
      synchronized (FolderHierarchies.getLockObject()) {
         if (super._useBigVector) {
            for (int i = 0; i < super._messagesAsBigVector.size(); i++) {
               if (super._messagesAsBigVector.elementAt(i).equals(obj)) {
                  return i;
               }
            }
         } else {
            for (int i = 0; i < super._messagesAsArray.length; i++) {
               if (super._messagesAsArray[i] != null && super._messagesAsArray[i].equals(obj)) {
                  return i;
               }
            }
         }

         return -1;
      }
   }
}
