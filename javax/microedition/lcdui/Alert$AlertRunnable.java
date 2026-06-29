package javax.microedition.lcdui;

class Alert$AlertRunnable implements Runnable {
   private final Alert this$0;

   Alert$AlertRunnable(Alert _1) {
      this.this$0 = _1;
   }

   @Override
   public void run() {
      this.this$0.dismiss();
   }
}
