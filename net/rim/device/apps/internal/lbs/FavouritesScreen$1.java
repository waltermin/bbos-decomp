package net.rim.device.apps.internal.lbs;

final class FavouritesScreen$1 extends LBSMenuItem {
   private final FavouritesScreen this$0;

   FavouritesScreen$1(FavouritesScreen this$0, int x0, int x1) {
      super(x0, x1);
      this.this$0 = this$0;
   }

   @Override
   public final boolean isVisible() {
      Object item = this.this$0.getFocusedItem();
      return this.this$0._showOnlyFolders && item instanceof Object || item instanceof LocationSyncable;
   }

   @Override
   public final void run() {
      if (this.this$0._showOnlyFolders) {
         this.this$0.selectFolder();
      } else {
         this.this$0.selectLocation();
      }

      this.this$0.close();
   }
}
