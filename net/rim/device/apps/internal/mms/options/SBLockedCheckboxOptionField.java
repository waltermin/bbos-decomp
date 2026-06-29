package net.rim.device.apps.internal.mms.options;

final class SBLockedCheckboxOptionField extends CheckboxOptionField {
   SBLockedCheckboxOptionField(String label, int flag, boolean invert) {
      super(label, (MMSClientServiceBook.getLockedOptionsFlags() & flag) != 0, flag, invert);
   }

   @Override
   protected final void saveFlag(int flag, boolean value) {
      int flags = MMSClientServiceBook.getLockedOptionsFlags();
      if (value) {
         flags |= flag;
      } else {
         flags &= ~flag;
      }

      MMSClientServiceBook.setLockedOptionsFlags(flags);
   }
}
