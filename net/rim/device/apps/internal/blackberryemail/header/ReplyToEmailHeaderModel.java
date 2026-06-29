package net.rim.device.apps.internal.blackberryemail.header;

import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.PersistableRIMModel;

class ReplyToEmailHeaderModel extends EmailHeaderModel implements PersistableRIMModel {
   ReplyToEmailHeaderModel(String[] stringPair, ContextObject contextObject) {
      super(stringPair, contextObject);
   }

   ReplyToEmailHeaderModel(Object initialData) {
      super(initialData);
   }

   @Override
   protected EmailHeaderModel newInstance(Object initialData) {
      return new ReplyToEmailHeaderModel(initialData);
   }

   @Override
   public int getHeaderType() {
      return 5;
   }
}
