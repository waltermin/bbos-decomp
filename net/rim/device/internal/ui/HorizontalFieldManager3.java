package net.rim.device.internal.ui;

import net.rim.device.api.ui.container.HorizontalFieldManager;

public class HorizontalFieldManager3 extends HorizontalFieldManager {
   private static final int MAX_EXTENT = 1073741823;

   public HorizontalFieldManager3() {
      super(0);
   }

   public HorizontalFieldManager3(long style) {
      super(style);
   }

   @Override
   protected void sublayout(int maxWidth, int maxHeight) {
      super.sublayout(maxWidth, Math.min(maxHeight, this.getPreferredHeight()));
   }
}
