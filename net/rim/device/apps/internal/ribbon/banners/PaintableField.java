package net.rim.device.apps.internal.ribbon.banners;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.apps.api.ribbon.SimpleRibbonComponent;

final class PaintableField extends Field implements BannerField {
   SimpleRibbonComponent _paintable;

   public PaintableField(SimpleRibbonComponent paintable) {
      this._paintable = paintable;
   }

   @Override
   protected final void applyTheme() {
      super.applyTheme();
      this._paintable.applyTheme();
   }

   @Override
   public final int getPreferredHeight() {
      return this._paintable.getComponentHeight();
   }

   @Override
   public final int getPreferredWidth() {
      return this._paintable.getComponentWidth();
   }

   @Override
   protected final void layout(int width, int height) {
      width = this._paintable.getComponentWidth();
      height = this._paintable.getComponentHeight();
      this._paintable.setDimensionsAvailable(width, height);
      this.setExtent(width, height);
   }

   @Override
   protected final void paint(Graphics graphics) {
      this._paintable.paintComponent(graphics, 0, 0, this.getWidth(), this.getHeight(), null);
   }

   @Override
   public final String getTitle() {
      return null;
   }

   @Override
   public final void setTitle(String title) {
   }

   @Override
   public final void bannerInvalidate() {
      this.invalidate();
   }
}
