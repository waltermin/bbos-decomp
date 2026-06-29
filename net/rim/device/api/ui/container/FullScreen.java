package net.rim.device.api.ui.container;

import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.Screen;

public class FullScreen extends Screen {
   public FullScreen() {
      this(0);
   }

   public FullScreen(long style) {
      this(null, style);
   }

   public FullScreen(Manager delegate, long style) {
      super(delegate != null ? delegate : new VerticalFieldManager(validateStyleVFM(style)), validateStyle(style));
   }

   @Override
   protected void sublayout(int width, int height) {
      this.setPosition(0, 0);
      this.setExtent(width, height);
      this.setPositionDelegate(0, 0);
      this.layoutDelegate(width, height);
   }

   private static long validateStyle(long style) {
      return style & -289725711965487105L;
   }

   private static long validateStyleVFM(long style) {
      style &= 289725711965487104L;
      return style | 3458764513820540928L;
   }
}
