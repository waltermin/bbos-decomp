package net.rim.device.apps.internal.globalsearch;

final class GlobalSearchScreen$1 implements Runnable {
   private final GlobalSearchScreen this$0;

   GlobalSearchScreen$1(GlobalSearchScreen _1) {
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      this.this$0._searchString.setText(null);
      this.this$0._nameString.setText(null);
      if (this.this$0._idString != null) {
         this.this$0._idString.setText(null);
      }
   }
}
