package net.rim.blackberry.api.mail;

import net.rim.device.apps.api.framework.registration.RIMModelFactory;

final class MailApiAttachmentViewerModelFactory extends RIMModelFactory {
   @Override
   public final Object createInstance(Object context) {
      MailApiAttachmentViewerModel maavm = new MailApiAttachmentViewerModel(context);
      MailApiPersistentProxyModelStore store = MailApiPersistentProxyModelStore.getInstance();
      synchronized (store) {
         int handle = store.add(maavm);
         store.removeWeakRef(handle);
         return new Object(store.getId(), handle);
      }
   }

   @Override
   public final boolean recognize(Object o) {
      return o instanceof MailApiAttachmentViewerModel;
   }
}
