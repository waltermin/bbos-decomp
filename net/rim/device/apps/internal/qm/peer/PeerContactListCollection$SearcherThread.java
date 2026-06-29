package net.rim.device.apps.internal.qm.peer;

final class PeerContactListCollection$SearcherThread extends Thread {
   private final PeerContactListCollection this$0;

   PeerContactListCollection$SearcherThread(PeerContactListCollection _1) {
      this.this$0 = _1;
      this.setPriority(this.getPriority() - 1);
   }

   @Override
   public final void run() {
      boolean searchDone = false;

      while (true) {
         synchronized (this.this$0._searchData) {
            if (this.this$0._searchData._nextAvailable) {
               this.this$0._searchData._current = this.this$0._searchData._next;
               this.this$0._searchData._currentAvailable = true;
               this.this$0._searchData._next = null;
               this.this$0._searchData._nextAvailable = false;
            }
         }

         if (this.this$0._searchData._currentAvailable) {
            searchDone = this.this$0.update(this.this$0._searchData._current);
            this.this$0._searchData._current = null;
            this.this$0._searchData._currentAvailable = false;
         } else if (searchDone) {
            boolean notify = false;
            synchronized (this.this$0._searchData) {
               if (!this.this$0._searchData._nextAvailable) {
                  this.this$0._searcher = null;
                  notify = true;
               }
            }

            if (notify) {
               PeerContactListCollection.access$000(this.this$0).fireReset(null);
               return;
            }
         }
      }
   }
}
