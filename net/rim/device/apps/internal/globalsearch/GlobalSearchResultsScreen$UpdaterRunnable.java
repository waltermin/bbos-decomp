package net.rim.device.apps.internal.globalsearch;

final class GlobalSearchResultsScreen$UpdaterRunnable implements Runnable {
   private final GlobalSearchResultsScreen this$0;

   GlobalSearchResultsScreen$UpdaterRunnable(GlobalSearchResultsScreen _1) {
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      this.this$0.doUpdateStatus();
   }
}
