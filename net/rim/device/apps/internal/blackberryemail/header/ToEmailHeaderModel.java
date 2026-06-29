package net.rim.device.apps.internal.blackberryemail.header;

import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.PersistableRIMModel;

final class ToEmailHeaderModel extends EmailHeaderModel implements PersistableRIMModel {
   ToEmailHeaderModel(String[] stringPair, ContextObject contextObject) {
      super(stringPair, contextObject);
   }

   ToEmailHeaderModel(Object initialData) {
      super(initialData);
   }

   @Override
   protected final EmailHeaderModel newInstance(Object initialData) {
      return new ToEmailHeaderModel(initialData);
   }

   @Override
   public final int getHeaderType() {
      return 0;
   }
}
