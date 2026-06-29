package net.rim.device.apps.internal.blackberryemail.email.options;

import net.rim.device.apps.api.framework.registration.RIMModelFactory;

final class EmailImportanceOptionModelFactory extends RIMModelFactory {
   @Override
   public final Object createInstance(Object context) {
      return new EmailImportanceOptionModel(context);
   }

   @Override
   public final int getMaximumCount(Object context) {
      return 1;
   }

   @Override
   public final boolean recognize(Object o) {
      return o instanceof EmailImportanceOptionModel;
   }
}
