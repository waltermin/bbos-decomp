package net.rim.device.apps.internal.blackberryemail.header;

import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.PersistableRIMModel;

final class BccEmailHeaderModel extends EmailHeaderModel implements PersistableRIMModel {
   BccEmailHeaderModel(String[] stringPair, ContextObject contextObject) {
      super(stringPair, contextObject);
   }

   BccEmailHeaderModel(Object initialData) {
      super(initialData);
   }

   @Override
   protected final EmailHeaderModel newInstance(Object initialData) {
      return new BccEmailHeaderModel(initialData);
   }

   @Override
   public final int getHeaderType() {
      return 2;
   }
}
