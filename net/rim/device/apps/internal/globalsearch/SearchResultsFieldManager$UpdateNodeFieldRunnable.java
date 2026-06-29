package net.rim.device.apps.internal.globalsearch;

final class SearchResultsFieldManager$UpdateNodeFieldRunnable implements Runnable {
   private final SearchResultsFieldManager this$0;

   private SearchResultsFieldManager$UpdateNodeFieldRunnable(SearchResultsFieldManager _1) {
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      this.this$0.doUpdateNodeField();
   }

   SearchResultsFieldManager$UpdateNodeFieldRunnable(SearchResultsFieldManager x0, SearchResultsFieldManager$1 x1) {
      this(x0);
   }
}
