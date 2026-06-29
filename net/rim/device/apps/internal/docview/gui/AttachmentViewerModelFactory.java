package net.rim.device.apps.internal.docview.gui;

import net.rim.device.apps.api.framework.registration.RIMModelFactory;
import net.rim.device.apps.internal.blackberryemail.email.ProxyModel;

final class AttachmentViewerModelFactory extends RIMModelFactory {
   private DocViewProxyModelStore _proxyModelStore = DocViewProxyModelStore.getInstance();

   @Override
   public final Object createInstance(Object context) {
      int handle = this._proxyModelStore.add(new DocViewAttachmentViewerModel(context));
      return new Object(this._proxyModelStore.getId(), handle);
   }

   @Override
   public final boolean recognize(Object o) {
      if (o instanceof Object) {
         o = ((ProxyModel)o).getObject();
      }

      return o instanceof DocViewAttachmentViewerModel;
   }
}
