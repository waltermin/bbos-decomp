package net.rim.device.apps.internal.docview.gui;

import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.RIMPersistentStore;
import net.rim.device.api.util.Persistable;
import net.rim.device.apps.internal.blackberryemail.email.PersistentProxyModelStore;

final class DocViewProxyModelStore extends PersistentProxyModelStore implements Persistable {
   private static final long ID = 330074175348706624L;

   private DocViewProxyModelStore(long rootId) {
      super(rootId, true);
   }

   static final DocViewProxyModelStore getInstance() {
      PersistentObject po = RIMPersistentStore.getPersistentObject(330074175348706624L);
      synchronized (po) {
         if (po.getContents() == null) {
            po.setContents(new DocViewProxyModelStore(330074175348706624L), 51);
            po.commit();
         }
      }

      return (DocViewProxyModelStore)po.getContents();
   }
}
