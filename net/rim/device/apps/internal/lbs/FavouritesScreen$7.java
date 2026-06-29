package net.rim.device.apps.internal.lbs;

final class FavouritesScreen$7 extends LBSMenuItem {
   private final FavouritesScreen this$0;

   FavouritesScreen$7(FavouritesScreen this$0, int x0, int x1) {
      super(x0, x1);
      this.this$0 = this$0;
   }

   @Override
   public final boolean isVisible() {
      Object folder = this.this$0.getFocusedItem();
      return folder instanceof Object && folder != this.this$0._root;
   }

   @Override
   public final void run() {
      this.this$0.renameFolder();
   }
}
