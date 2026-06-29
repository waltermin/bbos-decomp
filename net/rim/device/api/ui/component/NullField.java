package net.rim.device.api.ui.component;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;

public class NullField extends Field {
   public NullField() {
      this(0);
   }

   public NullField(long style) {
      super(validateStyle(style));
   }

   @Override
   public int getPreferredWidth() {
      return 0;
   }

   @Override
   public int getPreferredHeight() {
      return 0;
   }

   @Override
   protected void layout(int width, int height) {
      this.setExtent(0, 0);
   }

   @Override
   protected void paint(Graphics graphics) {
   }

   private static long validateStyle(long style) {
      if ((style & 54043195528445952L) == 0) {
         style |= 18014398509481984L;
      }

      return style;
   }
}
