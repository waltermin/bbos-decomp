package net.rim.device.api.ui.container;

class DialogFieldManager$1 extends VerticalFieldManager {
   private final DialogFieldManager this$0;

   DialogFieldManager$1(DialogFieldManager _1, long x0) {
      super(x0);
      this.this$0 = _1;
   }

   @Override
   protected void sublayout(int maxWidth, int maxHeight) {
      super.sublayout(maxWidth, maxHeight);
      this.setVerticalQuantization(1);
   }
}
