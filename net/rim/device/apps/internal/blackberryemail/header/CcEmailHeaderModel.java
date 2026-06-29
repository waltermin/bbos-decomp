package net.rim.device.apps.internal.blackberryemail.header;

import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.PersistableRIMModel;

final class CcEmailHeaderModel extends EmailHeaderModel implements PersistableRIMModel {
   CcEmailHeaderModel(String[] stringPair, ContextObject contextObject) {
      super(stringPair, contextObject);
   }

   CcEmailHeaderModel(Object initialData) {
      super(initialData);
   }

   @Override
   protected final EmailHeaderModel newInstance(Object initialData) {
      return new CcEmailHeaderModel(initialData);
   }

   @Override
   public final int getHeaderType() {
      return 1;
   }
}
