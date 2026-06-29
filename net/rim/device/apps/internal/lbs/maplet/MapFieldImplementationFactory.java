package net.rim.device.apps.internal.lbs.maplet;

import net.rim.device.api.util.Factory;

final class MapFieldImplementationFactory implements Factory {
   @Override
   public final Object createInstance(Object object) {
      return new MapFieldImplementation(object);
   }
}
