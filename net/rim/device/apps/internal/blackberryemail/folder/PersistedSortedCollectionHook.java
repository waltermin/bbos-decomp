package net.rim.device.apps.internal.blackberryemail.folder;

import net.rim.device.apps.api.messaging.FolderHierarchies;
import net.rim.device.apps.api.messaging.util.PersistedSortedCollection;

class PersistedSortedCollectionHook extends PersistedSortedCollection {
   private long _luid;
   private EmailHierarchy _hierarchy;

   PersistedSortedCollectionHook(long luid) {
      this._luid = luid;
   }

   private EmailHierarchy getHierarchy() {
      if (this._hierarchy == null) {
         this._hierarchy = EmailHierarchy.getEmailHierarchyForFolder(this._luid);
      }

      return this._hierarchy;
   }

   @Override
   public void add(Object message) {
      this.getHierarchy().addTrackingInfo(message);
      super.add(message);
   }

   @Override
   public void addMessageWithoutCommittingFolder(Object message) {
      this.getHierarchy().addTrackingInfo(message);
      super.addMessageWithoutCommittingFolder(message);
   }

   @Override
   public void remove(Object message, boolean forceNotification) {
      this.getHierarchy().removeTrackingInfo(message);
      super.remove(message, forceNotification);
   }

   @Override
   public void removeAll() {
      synchronized (FolderHierarchies.getLockObject()) {
         EmailHierarchy hierarchy = this.getHierarchy();

         for (int i = this.size() - 1; i >= 0; i--) {
            hierarchy.removeTrackingInfo(this.getAt(i), false);
         }

         super.removeAll();
      }
   }
}
