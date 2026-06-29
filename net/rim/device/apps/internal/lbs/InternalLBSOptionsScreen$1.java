package net.rim.device.apps.internal.lbs;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.component.BasicEditField;
import net.rim.device.api.ui.component.Dialog;

final class InternalLBSOptionsScreen$1 extends MenuItem {
   private final InternalLBSOptionsScreen this$0;

   InternalLBSOptionsScreen$1(InternalLBSOptionsScreen this$0, String x0, int x1, int x2) {
      super(x0, x1, x2);
      this.this$0 = this$0;
   }

   @Override
   public final void run() {
      Field field = this.this$0.getLeafFieldWithFocus();
      String whichServer = "";
      long whichKey = 0;
      BasicEditField edit = null;
      if (field == this.this$0._lbsServerField) {
         whichServer = "LBS Server";
         whichKey = -7064416726417485961L;
         edit = this.this$0._lbsServerField;
      } else if (field == this.this$0._locatorServerField) {
         whichServer = "Location Server";
         whichKey = 6933732722635403673L;
         edit = this.this$0._locatorServerField;
      } else if (field == this.this$0._directionsServerField) {
         whichServer = "Directions Server";
         whichKey = -254277793043409026L;
         edit = this.this$0._directionsServerField;
      } else if (field == this.this$0._poiServerField) {
         whichServer = "POI Server";
         whichKey = 3589376987760903020L;
         edit = this.this$0._poiServerField;
      }

      if (edit != null && Dialog.ask(3, "Set default URL for " + whichServer) == 4) {
         boolean useCustomURLs = LBSOptions.getBoolean(-6271428560607580713L, false);
         LBSOptions.setBoolean(-6271428560607580713L, false);
         boolean customURLsInUse = this.this$0._useCustomULRs.getChecked();
         boolean wasCustomURLdirty = this.this$0._useCustomULRs.isDirty();
         String defValue = LBSOptions.getURL(whichKey);
         LBSOptions.setBoolean(-6271428560607580713L, useCustomURLs);
         edit.setText(defValue);
         edit.setDirty(true);
         this.this$0.invalidate();
         this.this$0._useCustomULRs.setChecked(customURLsInUse);
         this.this$0._useCustomULRs.setDirty(wasCustomURLdirty);
      }
   }
}
