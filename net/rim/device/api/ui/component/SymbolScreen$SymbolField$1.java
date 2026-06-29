package net.rim.device.api.ui.component;

class SymbolScreen$SymbolField$1 implements Runnable {
   private final SymbolScreen$SymbolField this$1;

   SymbolScreen$SymbolField$1(SymbolScreen$SymbolField _1) {
      this.this$1 = _1;
   }

   @Override
   public void run() {
      this.this$1.gotoPage(this.this$1.this$0._pendingPageNumber - 1, false);
      this.this$1.this$0._pageTimer = -1;
   }
}
