package net.rim.device.apps.internal.blackberryemail.header;

import net.rim.device.apps.api.framework.registration.RIMModelFactory;

final class TimeStampModelFactory extends RIMModelFactory {
   @Override
   public final Object createInstance(Object context) {
      return new TimeStampModel(context);
   }

   @Override
   public final int getMaximumCount(Object context) {
      return 1;
   }

   @Override
   public final boolean recognize(Object o) {
      return o instanceof TimeStampModel;
   }
}
