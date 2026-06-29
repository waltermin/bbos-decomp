package net.rim.device.apps.internal.sms.message;

import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.component.SeparatorField;

final class MessageSeparatorField extends SeparatorField {
   private static final int DASHED_LINE_STIPPLE;
   private static final int SOLID_LINE_STIPPLE;

   @Override
   protected final void paint(Graphics graphics) {
      graphics.setStipple(1431655765);
      int y = this.getLinePosition();
      graphics.drawLine(0, y, this.getWidth(), y);
      graphics.setStipple(-1);
   }
}
