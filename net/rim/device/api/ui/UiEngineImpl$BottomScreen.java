package net.rim.device.api.ui;

final class UiEngineImpl$BottomScreen extends Screen {
   public UiEngineImpl$BottomScreen() {
      super(new UiEngineImpl$BottomScreen$BottomScreenManager());
   }

   @Override
   protected final void sublayout(int width, int height) {
      this.setPosition(0, 0);
      this.setExtent(width, height);
   }

   @Override
   protected final void applyFont() {
   }

   @Override
   protected final void paint(Graphics graphics) {
   }
}
