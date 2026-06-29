package net.rim.device.apps.internal.ribbon.launcher;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.util.IntHashtable;
import net.rim.device.api.util.LongHashtable;

final class RibbonApiProxy$EntryPointDescriptionStore {
   IntHashtable _moduleLookup = (IntHashtable)(new Object());

   private RibbonApiProxy$EntryPointDescriptionStore() {
   }

   public final Bitmap getBitmap(int moduleHandle, int index, long property) {
      LongHashtable[] array = (Object[])this._moduleLookup.get(moduleHandle);
      if (array != null && index < array.length) {
         LongHashtable lht = array[index];
         return (Bitmap)(lht == null ? null : lht.get(property));
      } else {
         return null;
      }
   }

   public final String getString(int moduleHandle, int index, long property) {
      LongHashtable[] array = (Object[])this._moduleLookup.get(moduleHandle);
      if (array != null && index < array.length) {
         LongHashtable lht = array[index];
         return (String)(lht == null ? null : lht.get(property));
      } else {
         return null;
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final void put(int moduleHandle, int index, long property, Object object) {
      LongHashtable lht = null;
      LongHashtable[] array = (Object[])this._moduleLookup.get(moduleHandle);
      if (array == null) {
         array = new Object[index + 1];
      } else {
         boolean var10 = false /* VF: Semaphore variable */;

         label34:
         try {
            var10 = true;
            lht = array[index];
            var10 = false;
         } finally {
            if (var10) {
               array = new Object[index + 1];
               break label34;
            }
         }
      }

      if (lht == null) {
         lht = (LongHashtable)(new Object());
      }

      if (object != null) {
         lht.put(property, object);
      } else {
         lht.remove(property);
      }

      array[index] = lht;
      this._moduleLookup.put(moduleHandle, array);
   }

   public final void putBitmap(int moduleHandle, int index, long property, Bitmap icon) {
      this.put(moduleHandle, index, property, icon);
   }

   public final void putString(int moduleHandle, int index, long property, String string) {
      this.put(moduleHandle, index, property, string);
   }

   RibbonApiProxy$EntryPointDescriptionStore(RibbonApiProxy$1 x0) {
      this();
   }
}
