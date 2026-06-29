package net.rim.device.apps.internal.lbs;

import net.rim.device.apps.internal.lbs.finder.FindAddress;
import net.rim.device.apps.internal.lbs.resources.LBSResources;

final class LocationField$3 extends LocationField$LocationActionChoice {
   private final LocationField this$0;

   LocationField$3(LocationField this$0, int x0) {
      super(x0);
      this.this$0 = this$0;
   }

   @Override
   public final void onSelect() {
      FindAddress find = new FindAddress(LBSResources.getString(325), false);
      this.this$0.selectLocation((Location)find.invoke(this.this$0._model));
   }
}
