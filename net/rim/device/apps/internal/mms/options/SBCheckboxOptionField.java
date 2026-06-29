package net.rim.device.apps.internal.mms.options;

final class SBCheckboxOptionField extends CheckboxOptionField {
   SBCheckboxOptionField(String label, int flag, boolean invert) {
      super(label, (MMSClientServiceBook.getDefaultOptionFlags() & flag) != 0, flag, invert);
   }

   @Override
   protected final void saveFlag(int flag, boolean value) {
      int flags = MMSClientServiceBook.getDefaultOptionFlags();
      if (value) {
         flags |= flag;
      } else {
         flags &= ~flag;
      }

      MMSClientServiceBook.setDefaultOptionFlags(flags);
   }
}
