package net.rim.device.apps.api.reminders;

import net.rim.device.apps.api.framework.registration.RIMModelFactory;

public class ReminderModelFactory extends RIMModelFactory {
   public static final long ID;

   @Override
   public Object createInstance(Object initialData) {
      boolean absolute = false;
      if (initialData instanceof Object) {
         absolute = initialData == 2;
      }

      return this.createInstance(absolute);
   }

   protected ReminderModel createInstance(boolean _1) {
      throw null;
   }

   @Override
   public int getMaximumCount(Object context) {
      return 1;
   }

   @Override
   public boolean recognize(Object o) {
      return o instanceof ReminderModel;
   }
}
