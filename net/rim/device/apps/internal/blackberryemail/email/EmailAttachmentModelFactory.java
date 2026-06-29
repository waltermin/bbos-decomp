package net.rim.device.apps.internal.blackberryemail.email;

import net.rim.device.apps.api.framework.registration.RIMModelFactory;

public class EmailAttachmentModelFactory extends RIMModelFactory {
   @Override
   public Object createInstance(Object initialData) {
      return null;
   }

   @Override
   public boolean recognize(Object o) {
      return o instanceof AbstractEmailFileAttachment;
   }

   @Override
   public int getMinimumCount(Object context) {
      return Integer.MIN_VALUE;
   }
}
