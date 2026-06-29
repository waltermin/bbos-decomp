package net.rim.wica.runtime.ui;

import net.rim.device.apps.api.ribbon.EntryPointDescriptor;
import net.rim.device.apps.api.utility.props.CharProps;
import net.rim.device.apps.api.utility.props.IntegerProps;
import net.rim.device.apps.api.utility.props.ObjectProps;
import net.rim.device.apps.api.utility.props.StringProps;

class HomeScreenUtilities$HomeScreenEntryWrapper implements EntryPointDescriptor, Runnable, CharProps, IntegerProps, StringProps, ObjectProps {
   private HomeScreenEntry _entry;

   HomeScreenUtilities$HomeScreenEntryWrapper(HomeScreenEntry entry) {
      this._entry = entry;
   }

   @Override
   public void run() {
      this._entry.run();
   }

   @Override
   public char get(long propID, char defaultReturned) {
      return defaultReturned;
   }

   @Override
   public Integer get(long propID, Integer defaultReturned) {
      return propID == 6 ? this._entry.getEntryDefaultPosition() : defaultReturned;
   }

   @Override
   public Object get(long propID, Object defaultReturned) {
      if (propID == 4) {
         return this._entry.getEntryBitmap();
      } else {
         return propID == 10 ? this._entry.getEntryBitmapFocus() : defaultReturned;
      }
   }

   @Override
   public String get(long propID, String defaultReturned) {
      if (propID == 1) {
         return this._entry.getEntryId();
      } else {
         return propID == 3 ? this._entry.getEntryDescription() : defaultReturned;
      }
   }

   @Override
   public void set(long propID, char valueToSet) {
   }

   @Override
   public void set(long propID, Integer valueToSet) {
   }

   @Override
   public void set(long propID, Object valueToSet) {
   }

   @Override
   public void set(long propID, String valueToSet) {
   }
}
