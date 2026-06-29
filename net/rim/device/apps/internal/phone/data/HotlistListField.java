package net.rim.device.apps.internal.phone.data;

import net.rim.device.api.ui.component.ListField;

final class HotlistListField extends ListField {
   public HotlistListField(int numEntries, long style) {
      super(numEntries, style);
   }

   @Override
   protected final void onFocus(int direction) {
      super.onFocus(0);
   }
}
