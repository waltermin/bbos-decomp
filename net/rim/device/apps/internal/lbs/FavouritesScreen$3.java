package net.rim.device.apps.internal.lbs;

final class FavouritesScreen$3 extends LBSMenuItem {
   private final FavouritesScreen this$0;

   FavouritesScreen$3(FavouritesScreen this$0, int x0, int x1) {
      super(x0, x1);
      this.this$0 = this$0;
   }

   @Override
   public final boolean isVisible() {
      return this.this$0.getFocusedItem() instanceof LocationSyncable;
   }

   @Override
   public final void run() {
      this.this$0.delete();
   }
}
