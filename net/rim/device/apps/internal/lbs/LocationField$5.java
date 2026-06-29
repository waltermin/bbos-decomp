package net.rim.device.apps.internal.lbs;

final class LocationField$5 extends LocationField$LocationActionChoice {
   private final LocationField this$0;

   LocationField$5(LocationField this$0, int x0) {
      super(x0);
      this.this$0 = this$0;
   }

   @Override
   public final void onSelect() {
      this.this$0.selectLocation(new FavouritesScreen().getSelectedLocation());
   }
}
