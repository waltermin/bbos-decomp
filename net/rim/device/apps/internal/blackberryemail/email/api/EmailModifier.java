package net.rim.device.apps.internal.blackberryemail.email.api;

import net.rim.device.api.collection.ReadableList;
import net.rim.device.api.system.ObjectGroup;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.internal.blackberryemail.email.EmailMessageModel;
import net.rim.device.apps.internal.blackberryemail.email.EmailPayloadModel;
import net.rim.device.apps.internal.blackberryemail.email.PersistentProxyModelStore;
import net.rim.device.apps.internal.blackberryemail.email.ProxyModel;
import net.rim.device.apps.internal.blackberryemail.email.ProxyModelStore;
import net.rim.device.apps.internal.blackberryemail.email.ProxyModelStoreManager;

public final class EmailModifier {
   public static final EmailPayloadModel beginChanges(EmailMessageModel message, Object context) {
      EmailPayloadModel oldPayload = message.getPayload();
      if (ObjectGroup.isInGroup(oldPayload)) {
         message.setPayload((EmailPayloadModel)ObjectGroup.expandGroup(oldPayload));
      }

      return oldPayload;
   }

   public static final void endChanges(EmailMessageModel message, EmailPayloadModel oldPayload, Object context) {
      ContextObject contextObject = null;
      if (oldPayload != null) {
         contextObject = ContextObject.clone(context);
         contextObject.put(-967294354293097868L, oldPayload);
      } else {
         contextObject = ContextObject.castOrCreate(context);
      }

      if (message instanceof ReadableList) {
         ReadableList list = message;

         for (int i = list.size() - 1; i >= 0; i--) {
            Object element = list.getAt(i);
            if (element instanceof ProxyModel) {
               ProxyModel pm = (ProxyModel)element;
               element = pm.getObject();
               ProxyModelStore store = ProxyModelStoreManager.getProxyModelStore(pm.getRootId());
               if (store instanceof PersistentProxyModelStore) {
                  PersistentProxyModelStore ppms = (PersistentProxyModelStore)store;
                  ppms.update(element, pm);
               }
            }
         }
      }

      EmailPayloadModel payload = message.getPayload();
      payload.prepareForPersisting();
      ObjectGroup.createGroupIgnoreTooBig(payload);
      message.changeStatus(0, 0, 0, 0, true, true, false, true, contextObject);
   }
}
