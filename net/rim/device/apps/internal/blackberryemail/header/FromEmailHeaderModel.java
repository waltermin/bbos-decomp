package net.rim.device.apps.internal.blackberryemail.header;

import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.PersistableRIMModel;

final class FromEmailHeaderModel extends EmailHeaderModel implements PersistableRIMModel {
   FromEmailHeaderModel(String[] stringPair, ContextObject contextObject) {
      super(stringPair, contextObject);
   }

   FromEmailHeaderModel(Object initialData) {
      super(initialData);
   }

   @Override
   protected final EmailHeaderModel newInstance(Object initialData) {
      return new FromEmailHeaderModel(initialData);
   }

   @Override
   public final int getHeaderType() {
      return 3;
   }
}
