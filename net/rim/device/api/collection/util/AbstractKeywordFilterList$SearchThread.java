package net.rim.device.api.collection.util;

final class AbstractKeywordFilterList$SearchThread extends Thread {
   private KeywordSearcher _searcher;
   private final AbstractKeywordFilterList this$0;

   AbstractKeywordFilterList$SearchThread(AbstractKeywordFilterList _1, KeywordSearcher searcher) {
      this.this$0 = _1;
      this._searcher = searcher;

      try {
         this.setPriority(this.getPriority() - 1);
      } catch (IllegalArgumentException var4) {
      }
   }

   @Override
   public final void run() {
      while (true) {
         AbstractKeywordFilterList list = this._searcher.getList();
         synchronized (list) {
            label69: {
               synchronized (this.this$0._searchRequestLock) {
                  if (!this.this$0._currentSearchRequest.isEmpty()) {
                     if (!this.this$0._nextSearchRequest.isEmpty()) {
                        if (this.this$0._currentSearchRequest._listener != null) {
                           this.this$0._currentSearchRequest._listener.filterStarted();
                           this.this$0._currentSearchRequest._listener.filterDone(true);
                        }

                        list.filteringComplete();
                        this.swapRequests();
                     }
                     break label69;
                  }
               }

               return;
            }
         }

         synchronized (this.this$0._source) {
            try {
               Object criteria = this.this$0._currentSearchRequest._searchCriteria;
               if (criteria instanceof String[][]) {
                  this._searcher.search((String[][])criteria, this.this$0._currentSearchRequest._listener);
               } else if (criteria instanceof String[]) {
                  this._searcher.search((String[])criteria, this.this$0._currentSearchRequest._listener);
               } else {
                  this._searcher.search((String)criteria, this.this$0._currentSearchRequest._listener);
               }
            } catch (Throwable var9) {
            }
         }

         synchronized (this.this$0._searchRequestLock) {
            this.swapRequests();
         }
      }
   }

   private final void swapRequests() {
      this.this$0._currentSearchRequest.done();
      AbstractKeywordFilterList$SearchRequest tmp = this.this$0._currentSearchRequest;
      this.this$0._currentSearchRequest = this.this$0._nextSearchRequest;
      this.this$0._nextSearchRequest = tmp;
   }
}
