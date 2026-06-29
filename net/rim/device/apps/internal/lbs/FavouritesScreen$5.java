package net.rim.device.apps.internal.lbs;

final class FavouritesScreen$5 extends LBSMenuItem {
   private final FavouritesScreen this$0;

   FavouritesScreen$5(FavouritesScreen this$0, int x0, int x1) {
      super(x0, x1);
      this.this$0 = this$0;
   }

   @Override
   public final boolean isVisible() {
      return true;
   }

   @Override
   public final void run() {
      this.this$0.addFolder();
   }
}
