package net.rim.device.apps.api.utility.framework;

import net.rim.device.api.ui.Manager;
import net.rim.device.internal.ui.component.Scrollbar;

class ModelScreen$ScrollbarManager extends Manager {
   private Scrollbar _scrollbar = (Scrollbar)(new Object());
   private Manager _content;

   public ModelScreen$ScrollbarManager(long style) {
      super(3459362648146051072L);
      this._content = new ModelScreen$ScrollbarManager$1(this, validateVFMStyle(style));
      this.add(this._content);
      this.add(this._scrollbar);
      this._scrollbar.setClient(this._content);
   }

   public Manager getContentManager() {
      return this._content;
   }

   private static long validateVFMStyle(long style) {
      style &= 288230376151711744L;
      return style | 4035506741100675072L;
   }

   @Override
   protected void sublayout(int width, int height) {
      this.setExtent(width, height);
      this.setVirtualExtent(width, height);
      int remainingWidth = width - this._scrollbar.getPreferredWidth();
      this.layoutChild(this._content, remainingWidth, height);
      this.setPositionChild(this._content, 0, 0);
      this.layoutChild(this._scrollbar, width, height);
      this.setPositionChild(this._scrollbar, remainingWidth, 0);
   }

   static void access$100(ModelScreen$ScrollbarManager x0, int x1, int x2, int x3, int x4) {
      x0.invalidate(x1, x2, x3, x4);
   }
}
