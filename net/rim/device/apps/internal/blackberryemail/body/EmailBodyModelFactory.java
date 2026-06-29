package net.rim.device.apps.internal.blackberryemail.body;

import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.SyncBuffer;
import net.rim.device.apps.api.framework.registration.RIMModelFactory;

final class EmailBodyModelFactory extends RIMModelFactory {
   @Override
   public final Object createInstance(Object initialData) {
      EmailBodyModelImpl model = null;
      String text = null;
      if (ContextObject.getFlag(initialData, 19)) {
         SyncBuffer syncBuffer = (SyncBuffer)ContextObject.get(initialData, 255);
         if (syncBuffer == null) {
            return null;
         }
      } else {
         model = new EmailBodyModelImpl(initialData);
      }

      if (text != null && text.length() > 0) {
         model = new EmailBodyModelImpl(null);
         model.setText(text);
         return model;
      }

      if (text == null && ContextObject.getFlag(initialData, 20)) {
         model = new EmailBodyModelImpl(null);
      }

      return model;
   }

   @Override
   public final boolean recognize(Object object) {
      if (object instanceof EmailBodyModelImpl) {
         return true;
      }

      if (ContextObject.getFlag(object, 19)) {
         SyncBuffer syncBuffer = (SyncBuffer)ContextObject.get(object, 255);
         if (syncBuffer == null) {
            return false;
         }
      }

      return false;
   }
}
