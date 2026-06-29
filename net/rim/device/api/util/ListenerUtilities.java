package net.rim.device.api.util;

import java.util.Vector;
import net.rim.vm.Array;

public final class ListenerUtilities {
   private ListenerUtilities() {
   }

   public static final Vector addListener(Vector listeners, Object listener) {
      if (listener == null) {
         throw new NullPointerException();
      }

      Vector newVector;
      if (listeners != null) {
         if (listeners.contains(listener)) {
            return listeners;
         }

         newVector = CloneableVector.clone(listeners);
      } else {
         newVector = new CloneableVector(1, 2);
      }

      newVector.addElement(listener);
      return newVector;
   }

   public static final Vector fastAddListener(Vector listeners, Object listener) {
      if (listener == null) {
         throw new NullPointerException();
      }

      Vector newVector;
      if (listeners != null) {
         if (listeners.contains(listener)) {
            return listeners;
         }

         newVector = listeners;
      } else {
         newVector = new CloneableVector(1, 2);
      }

      newVector.addElement(listener);
      return newVector;
   }

   public static final Vector removeListener(Vector listeners, Object listener) {
      if (listeners != null && listeners.contains(listener)) {
         if (listeners.size() == 1) {
            return null;
         }

         Vector newVector = CloneableVector.clone(listeners);
         newVector.removeElement(listener);
         return newVector;
      } else {
         return listeners;
      }
   }

   public static final Object[] addListener(Object[] listeners, Object listener) {
      if (listener == null) {
         throw new NullPointerException();
      }

      Object[] newArray;
      int index;
      if (listeners != null) {
         if (Arrays.contains(listeners, listener)) {
            return listeners;
         }

         index = listeners.length;
         newArray = new Object[index + 1];
         System.arraycopy(listeners, 0, newArray, 0, index);
      } else {
         index = 0;
         newArray = new Object[1];
      }

      newArray[index] = listener;
      return newArray;
   }

   public static final Object[] fastAddListener(Object[] listeners, Object listener) {
      if (listener == null) {
         throw new NullPointerException();
      }

      Object[] newArray;
      int index;
      if (listeners != null) {
         if (Arrays.contains(listeners, listener)) {
            return listeners;
         }

         index = listeners.length;
         Array.resize(listeners, index + 1);
         newArray = listeners;
      } else {
         index = 0;
         newArray = new Object[1];
      }

      newArray[index] = listener;
      return newArray;
   }

   public static final Object[] removeListener(Object[] listeners, Object listener) {
      if (listeners == null) {
         return listeners;
      }

      int index = Arrays.getIndex(listeners, listener);
      if (index == -1) {
         return listeners;
      }

      int length = listeners.length;
      if (length == 1) {
         return null;
      }

      Object[] newArray = new Object[--length];
      System.arraycopy(listeners, 0, newArray, 0, index);
      System.arraycopy(listeners, index + 1, newArray, index, length - index);
      return newArray;
   }

   public static final boolean containsListener(Object[] listeners, Object listener) {
      return listeners == null ? false : Arrays.getIndex(listeners, listener) != -1;
   }
}
