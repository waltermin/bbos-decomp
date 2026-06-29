package net.rim.device.api.util;

import java.util.Enumeration;
import java.util.NoSuchElementException;

public final class EmptyEnumeration implements Enumeration {
   @Override
   public final boolean hasMoreElements() {
      return false;
   }

   @Override
   public final Object nextElement() {
      throw new NoSuchElementException();
   }
}
