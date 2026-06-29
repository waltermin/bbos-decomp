package net.rim.device.apps.internal.blackberryemail.header;

import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.PersistableRIMModel;

final class SenderEmailHeaderModel extends EmailHeaderModel implements PersistableRIMModel {
   SenderEmailHeaderModel(String[] stringPair, ContextObject contextObject) {
      super(stringPair, contextObject);
   }

   SenderEmailHeaderModel(Object initialData) {
      super(initialData);
   }

   @Override
   protected final EmailHeaderModel newInstance(Object initialData) {
      return new SenderEmailHeaderModel(initialData);
   }

   @Override
   public final int getHeaderType() {
      return 4;
   }
}
