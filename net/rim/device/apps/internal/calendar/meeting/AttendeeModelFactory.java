package net.rim.device.apps.internal.calendar.meeting;

import net.rim.device.apps.api.framework.registration.RIMModelFactory;

class AttendeeModelFactory extends RIMModelFactory {
   @Override
   public boolean recognize(Object o) {
      return o instanceof AttendeeModel;
   }

   @Override
   public int getMinimumCount(Object context) {
      return Integer.MIN_VALUE;
   }

   @Override
   public Object createInstance(Object initialData) {
      AttendeeModel model = new AttendeeModel(initialData);
      model._attendeeType = 1;
      return model;
   }
}
