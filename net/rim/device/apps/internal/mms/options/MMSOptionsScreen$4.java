package net.rim.device.apps.internal.mms.options;

class MMSOptionsScreen$4 extends HomeAwayOptionField {
   private final MMSOptionsScreen this$0;

   MMSOptionsScreen$4(MMSOptionsScreen _1, int x0, String x1, int x2) {
      super(x0, x1, x2);
      this.this$0 = _1;
   }

   @Override
   protected void saveMode(int mode) {
      MMSClientServiceBook.setDefaultReceptionMode(mode);
   }
}
