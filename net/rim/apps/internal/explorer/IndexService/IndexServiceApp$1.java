package net.rim.apps.internal.explorer.IndexService;

final class IndexServiceApp$1 implements Runnable {
   private final IndexServiceApp this$0;

   IndexServiceApp$1(IndexServiceApp _1) {
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      FileIndexServiceImpl service = new FileIndexServiceImpl();
      service.init();
   }
}
