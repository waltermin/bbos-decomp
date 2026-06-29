package net.rim.device.apps.internal.browser.ui;

import net.rim.device.api.util.IntComparator;

class TextFlowData$RegionComparator implements IntComparator {
   private final TextFlowData this$0;

   TextFlowData$RegionComparator(TextFlowData _1) {
      this.this$0 = _1;
   }

   @Override
   public int compare(int o1, int o2) {
      int y1 = this.this$0._regions[o1].getOffsetYTop();
      int y2 = this.this$0._regions[o2].getOffsetYTop();
      if (y1 == y2) {
         if (o1 == o2) {
            return 0;
         } else {
            return o1 < o2 ? -1 : 1;
         }
      } else {
         return y1 > y2 ? 1 : -1;
      }
   }
}
