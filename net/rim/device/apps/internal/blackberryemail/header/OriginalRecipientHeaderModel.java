package net.rim.device.apps.internal.blackberryemail.header;

import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.PersistableRIMModel;

final class OriginalRecipientHeaderModel extends EmailHeaderModel implements PersistableRIMModel {
   OriginalRecipientHeaderModel(String[] stringPair, ContextObject contextObject) {
      super(stringPair, contextObject);
   }

   OriginalRecipientHeaderModel(Object initialData) {
      super(initialData);
   }

   @Override
   protected final EmailHeaderModel newInstance(Object initialData) {
      return new OriginalRecipientHeaderModel(initialData);
   }

   @Override
   public final int getHeaderType() {
      return 6;
   }
}
