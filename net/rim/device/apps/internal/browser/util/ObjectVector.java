package net.rim.device.apps.internal.browser.util;

import java.util.Vector;

public final class ObjectVector extends Vector {
   public ObjectVector() {
   }

   public ObjectVector(int initialCapacity) {
   }

   public final Object[] getArray() {
      return super.elementData;
   }
}
