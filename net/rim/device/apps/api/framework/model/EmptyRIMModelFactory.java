package net.rim.device.apps.api.framework.model;

import net.rim.device.apps.api.framework.registration.RIMModelFactory;

class EmptyRIMModelFactory extends RIMModelFactory {
   @Override
   public boolean recognize(Object o) {
      return true;
   }

   @Override
   public Object createInstance(Object initialData) {
      return null;
   }
}
