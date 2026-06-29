package net.rim.device.apps.internal.lbs;

import net.rim.device.apps.internal.lbs.finder.FindLocationScreen;

final class LocationField$4 extends LocationField$LocationActionChoice {
   private final LocationField this$0;

   LocationField$4(LocationField this$0, int x0) {
      super(x0);
      this.this$0 = this$0;
   }

   @Override
   public final void onSelect() {
      this.this$0.selectLocation(FindLocationScreen.getFromAddressBook());
   }
}
