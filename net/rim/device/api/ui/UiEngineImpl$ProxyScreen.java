package net.rim.device.api.ui;

import net.rim.device.api.system.Display;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.internal.ui.BackingStore;

class UiEngineImpl$ProxyScreen extends Screen {
   Screen _screen;

   public UiEngineImpl$ProxyScreen(Screen screen) {
      super(new VerticalFieldManager(), !screen.isTransparent() && !screen.isTransparentBorder() ? 0 : 68719476736L);
      this._screen = screen;
      this.setAcceptsInput(this._screen.acceptsInput());
   }

   public Screen getWrappedScreen() {
      return this._screen;
   }

   public void updateInvalid() {
      BackingStore backingStore = this._screen.getBackingStore();
      if (backingStore != null) {
         XYRect totalDirty = Ui.getTmpXYRect();
         backingStore.getTotalDirty(totalDirty);
         XYRect extent = this._screen.getExtent();
         totalDirty.translate(extent.x, extent.y);
         this.getInvalid().unionNoEmpty(totalDirty);
         Ui.returnTmpXYRect(totalDirty);
      }
   }

   @Override
   public boolean isGlobal() {
      return this._screen.isGlobal();
   }

   @Override
   protected void paint(Graphics graphics) {
      BackingStore backingStore = this._screen.getBackingStore();
      if (backingStore != null && !this.shouldNotPaint()) {
         int overIndentX = this._screen.getExtent().x - graphics.getTranslateX();
         int overIndentY = this._screen.getExtent().y - graphics.getTranslateY();
         if (overIndentX <= 0 && overIndentY <= 0) {
            backingStore.paint(graphics, 0, 0);
         } else {
            graphics.pushContext(graphics.getClippingRect(), overIndentX, overIndentY);
            backingStore.paint(graphics, 0, 0);
            graphics.popContext();
         }
      }
   }

   private boolean shouldNotPaint() {
      return !this._screen.isGlobal()
         && this._screen.getExtent().width == Display.getWidth()
         && this._screen.getExtent().height == Display.getHeight()
         && !this._screen.isBackingStoreUpdated();
   }

   @Override
   protected void paintBackground(Graphics graphics) {
   }

   @Override
   protected void sublayout(int width, int height) {
      XYRect wrappedExtent = this._screen.getExtent();
      this.setExtent(wrappedExtent.width, wrappedExtent.height);
      this.setPosition(wrappedExtent.x, wrappedExtent.y);
   }
}
