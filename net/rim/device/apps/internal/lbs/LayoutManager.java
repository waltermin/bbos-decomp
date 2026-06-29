package net.rim.device.apps.internal.lbs;

import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Manager;

final class LayoutManager extends Manager {
   public LayoutManager() {
      super(562949953421312L);
   }

   @Override
   protected final void sublayout(int width, int height) {
      boolean bannerAdded = this.getField(0).getClass().getName().startsWith("net.rim.device.apps.internal.ribbon.banners.");
      int ix = bannerAdded ? 1 : 0;
      int maxHeight = Display.getHeight();
      if (bannerAdded) {
         this.layoutChild(this.getField(0), width, this.getField(0).getPreferredHeight());
      }

      this.layoutChild(this.getField(ix), width, maxHeight - this.getField(ix + 1).getPreferredHeight() - this.getField(0).getPreferredHeight());
      int h = this.getField(ix + 1).getPreferredHeight();
      if (bannerAdded && h == maxHeight) {
         h -= this.getField(0).getPreferredHeight();
      }

      this.layoutChild(this.getField(ix + 1), width, h);
      this.setVirtualExtent(width, maxHeight);
      int y = 0;
      if (bannerAdded) {
         y = this.getField(0).getHeight();
         this.setPositionChild(this.getField(0), 0, 0);
      }

      this.setPositionChild(this.getField(ix), 0, y);
      this.setPositionChild(this.getField(ix + 1), 0, maxHeight - this.getField(ix + 1).getHeight());
      this.setExtent(width, maxHeight);
   }
}
