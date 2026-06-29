package net.rim.wica.runtime.ui.internal.component;

import net.rim.device.api.ui.component.ButtonField;
import net.rim.wica.runtime.ui.internal.Focusable;

final class PagingController$NavigagionButtonField extends ButtonField implements Focusable {
   public PagingController$NavigagionButtonField(String text) {
      super(text, 65536);
   }

   @Override
   public final int moveFocus(int amount, int status, int time) {
      throw new RuntimeException("cod2jar: tail call (jumpspecial)");
   }

   @Override
   public final boolean trackwheelClick(int status, int time) {
      return (status & 1) == 0 ? super.trackwheelClick(status, time) : false;
   }
}
