package net.rim.device.apps.internal.sms;

class SetupMenuEntryPoint$1 implements Runnable {
   private final SetupMenuEntryPoint this$0;

   SetupMenuEntryPoint$1(SetupMenuEntryPoint _1) {
      this.this$0 = _1;
   }

   @Override
   public void run() {
      SIMATEventLogger.logDebug(18);
      SIMATPopupScreen s = new SIMATPopupScreen(this.this$0._stk, this.this$0._title);
      s.selectMenu(this.this$0._items, this.this$0._ids, this.this$0._helpAvailable);
   }
}
