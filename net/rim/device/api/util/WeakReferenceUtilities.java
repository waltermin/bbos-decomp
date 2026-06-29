package net.rim.device.api.util;

import java.util.Vector;
import net.rim.vm.WeakReference;

public final class WeakReferenceUtilities {
   private WeakReferenceUtilities() {
   }

   public static final byte[] getByteArray(WeakReference wr, int size) {
      byte[] buffer = (byte[])wr.get();
      if (buffer == null) {
         buffer = new byte[size];
         wr.set(buffer);
      }

      return buffer;
   }

   public static final char[] getCharArray(WeakReference wr, int size) {
      char[] buffer = (char[])wr.get();
      if (buffer == null) {
         buffer = new char[size];
         wr.set(buffer);
      }

      return buffer;
   }

   public static final StringBuffer getStringBuffer(WeakReference wr) {
      StringBuffer buffer = (StringBuffer)wr.get();
      if (buffer == null) {
         buffer = new StringBuffer();
         wr.set(buffer);
      }

      return buffer;
   }

   public static final DataBuffer getDataBuffer(WeakReference wr, boolean bigEndianFlag) {
      DataBuffer buffer = (DataBuffer)wr.get();
      if (buffer == null) {
         buffer = new DataBuffer(bigEndianFlag);
         wr.set(buffer);
      }

      return buffer;
   }

   public static final Object[] getObjectArray(WeakReference wr, int size) {
      Object[] array = (Object[])wr.get();
      if (array == null) {
         array = new Object[size];
         wr.set(array);
      }

      return array;
   }

   public static final String[] getStringArray(WeakReference wr, int size) {
      String[] array = (String[])wr.get();
      if (array == null) {
         array = new String[size];
         wr.set(array);
      }

      return array;
   }

   public static final int incrementalWRArrayPurge(int curr, WeakReference[] wr) {
      synchronized (wr) {
         int len = wr.length;
         if (len > 0) {
            int n = len == 1 ? 1 : 2;

            for (int i = 0; i < n; i++) {
               if (++curr >= len) {
                  curr = 0;
               }

               WeakReference w = wr[curr];
               if (w == null || w.get() == null) {
                  Arrays.removeAt(wr, curr);
                  len--;
               }
            }
         }

         return curr;
      }
   }

   public static final void purge(IntHashtable ht) {
      synchronized (ht) {
         IntEnumeration keys = ht.keys();

         while (keys.hasMoreElements()) {
            int v = keys.nextElement();
            Object o = ht.get(v);
            if (o instanceof WeakReference) {
               WeakReference wr = (WeakReference)o;
               if (wr.get() == null) {
                  ht.remove(v);
               }
            }
         }
      }
   }

   public static final void purge(Vector v) {
      synchronized (v) {
         for (int i = v.size() - 1; i >= 0; i--) {
            WeakReference wr = (WeakReference)v.elementAt(i);
            if (wr.get() == null) {
               v.removeElementAt(i);
            }
         }
      }
   }
}
