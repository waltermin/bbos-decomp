package net.rim.device.api.ui.component;

import net.rim.device.api.ui.Font;
import net.rim.vm.Process;

class ActiveRichTextField$StringPatternScanner implements Runnable {
   private final ActiveRichTextField this$0;

   private ActiveRichTextField$StringPatternScanner(ActiveRichTextField _1) {
      this.this$0 = _1;
   }

   @Override
   public void run() {
      Process.waitForIdle(1000);
      String text = this.this$0.getText();
      int[] offsets = this.this$0.getOffsets();
      byte[] attributes = this.this$0.getAttributes();
      Font[] fonts = this.this$0.getFonts(false);
      int[] foreColors = this.this$0.getForegroundColors();
      int[] bgColors = this.this$0.getBackgroundColors();
      ActiveRichTextField$RegionQueue rq;
      if (offsets != null) {
         rq = ActiveRichTextField.scanForActiveRegions(
            text, offsets, attributes, fonts, this.this$0.getFont(), foreColors, bgColors, this.this$0.getLabelLength(), this.this$0._patterns
         );
      } else {
         rq = ActiveRichTextField.scanForActiveRegions(text, this.this$0.getFont(), this.this$0.getLabelLength(), this.this$0._patterns);
      }

      this.this$0.setTextFromBackgroundScanner(text, offsets, attributes, fonts, foreColors, bgColors, rq);
   }

   ActiveRichTextField$StringPatternScanner(ActiveRichTextField x0, ActiveRichTextField$1 x1) {
      this(x0);
   }
}
