package net.rim.device.internal.ui;

import net.rim.device.api.ui.container.VerticalFieldManager;

public class VerticalFieldManager3 extends VerticalFieldManager {
   private static final int MAX_EXTENT = 1073741823;

   public VerticalFieldManager3() {
      super(0);
   }

   public VerticalFieldManager3(long style) {
      super(style);
   }

   @Override
   protected void sublayout(int maxWidth, int maxHeight) {
      super.sublayout(Math.min(maxWidth, this.getPreferredWidth()), maxHeight);
   }
}
