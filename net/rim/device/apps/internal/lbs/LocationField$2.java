package net.rim.device.apps.internal.lbs;

import java.util.Calendar;
import net.rim.device.apps.internal.lbs.finder.FindLocationScreen;

final class LocationField$2 extends LocationField$LocationActionChoice {
   private final LocationField this$0;

   LocationField$2(LocationField this$0, int x0) {
      super(x0);
      this.this$0 = this$0;
   }

   @Override
   public final void onSelect() {
      Location location = FindLocationScreen.getGPSLocation();
      if (location != null) {
         location._label = ((StringBuffer)(new Object("Here @ "))).append(LocationField._timeFormat.format(Calendar.getInstance())).toString();
      }

      this.this$0.selectLocation(location);
   }
}
