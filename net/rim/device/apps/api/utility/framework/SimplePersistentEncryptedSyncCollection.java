package net.rim.device.apps.api.utility.framework;

import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.system.PersistentContentListener;
import net.rim.device.api.util.Comparator;

public class SimplePersistentEncryptedSyncCollection extends SimplePersistentSyncCollection implements PersistentContentListener, RecryptableCollection {
   protected SimplePersistentEncryptedSyncCollection(Comparator cmp, long persistentId) {
      super(cmp, persistentId);
   }

   @Override
   protected void commonCtorEpilogue() {
      Object ticket = PersistentContent.getTicket();
      super.commonCtorEpilogue();
      if (ticket == null) {
         new SimplePersistentEncryptedSyncCollection$SPESCWaitForUnlock(this).start();
      }

      PersistentContent.addListener(this);
   }

   protected String getContentProtectionEnabledMessage() {
      throw null;
   }

   @Override
   public void verifySorted() {
      Object ticket = PersistentContent.getTicket();
      if (ticket != null) {
         super.verifySorted();
      }
   }

   @Override
   public void endSyncCleanup() {
      Object ticket = PersistentContent.getTicket();
      if (ticket != null) {
         super.endSyncCleanup();
      }
   }

   @Override
   protected void syncTransactionStopped() {
      super.syncTransactionStopped();
   }

   @Override
   public void run() {
      Object ticket = PersistentContent.getTicket();
      if (ticket != null) {
         super.run();
      }
   }

   @Override
   public void persistentContentStateChanged(int state) {
   }

   @Override
   public void persistentContentModeChanged(int generation) {
      super._lastInsertedUpdated = null;
      RecryptableCollectionUtilities.recrypt(this, this, null, generation);
   }

   @Override
   public int getSize(Object cookie) {
      return this.size();
   }

   @Override
   public Object getElementAt(int index, Object cookie) {
      return this.getAt(index);
   }

   @Override
   public void replaceElementAt(Object oldElement, Object newElement, int index, Object cookie) {
      this.replaceAt(newElement, index);
   }

   @Override
   public void updateListeners(Object oldElement, Object newElement, Object cookie) {
      this.fireElementUpdated(this, oldElement, newElement);
   }

   @Override
   public void commit(Object cookie) {
      this.commit();
   }

   @Override
   public void reCryptStarted(Object cookie) {
   }

   @Override
   public void reCryptEnded(Object cookie) {
   }
}
