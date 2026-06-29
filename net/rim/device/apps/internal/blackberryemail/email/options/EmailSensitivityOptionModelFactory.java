package net.rim.device.apps.internal.blackberryemail.email.options;

import net.rim.device.apps.api.framework.registration.RIMModelFactory;

final class EmailSensitivityOptionModelFactory extends RIMModelFactory {
   @Override
   public final Object createInstance(Object context) {
      return new EmailSensitivityOptionModel(context);
   }

   @Override
   public final boolean recognize(Object o) {
      return o instanceof EmailSensitivityOptionModel;
   }

   @Override
   public final int getMaximumCount(Object context) {
      return 1;
   }
}
