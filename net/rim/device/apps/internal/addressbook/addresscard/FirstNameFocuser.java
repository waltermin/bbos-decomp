package net.rim.device.apps.internal.addressbook.addresscard;

import net.rim.device.api.ui.container.VerticalFieldManager;

final class FirstNameFocuser extends VerticalFieldManager {
   FirstNameFocuser(long style) {
      super(style);
   }

   @Override
   public final void setFocus() {
      this.getField(1).setFocus();
   }
}
