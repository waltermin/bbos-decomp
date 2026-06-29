package net.rim.device.apps.internal.lbs;

import net.rim.device.api.util.Factory;

final class LocationField$LocationFieldFactory implements Factory {
   @Override
   public final Object createInstance(Object initialData) {
      return new LocationField(initialData);
   }
}
