package net.rim.device.apps.internal.lbs;

import net.rim.device.apps.api.messaging.util.SimpleFolder;

final class FavouritesScreen$4 extends LBSMenuItem {
   private final FavouritesScreen this$0;

   FavouritesScreen$4(FavouritesScreen this$0, int x0, int x1) {
      super(x0, x1);
      this.this$0 = this$0;
   }

   @Override
   public final boolean isVisible() {
      Object folder = this.this$0.getFocusedItem();
      return folder instanceof SimpleFolder && folder != this.this$0._root || this.this$0.getFocusedItem() instanceof LocationSyncable;
   }

   @Override
   public final void run() {
      this.this$0.moveFavourite();
   }
}
