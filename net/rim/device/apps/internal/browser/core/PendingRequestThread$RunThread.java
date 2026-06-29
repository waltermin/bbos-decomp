package net.rim.device.apps.internal.browser.core;

import net.rim.device.api.servicebook.ServiceRouting;
import net.rim.device.api.system.EventLogger;
import net.rim.device.apps.internal.browser.options.BrowserConfigRecord;
import net.rim.device.apps.internal.browser.page.PageModel;
import net.rim.device.apps.internal.browser.stack.FetchRequest;
import net.rim.vm.Process;

final class PendingRequestThread$RunThread extends Thread {
   private Object _ticket;
   private final PendingRequestThread this$0;

   public PendingRequestThread$RunThread(PendingRequestThread _1, Object ticket) {
      this.this$0 = _1;
      this._ticket = ticket;
   }

   @Override
   public final void run() {
      Process.killProcessIfThisThreadDies(true);
      EventLogger.logEvent(1907089860548946979L, 1347572850, 5);
      BrowserImpl browserImpl = BrowserDaemonRegistry.getInstance();
      PageModel firstSkippedPageModel = null;
      PageModel pageModel = null;
      PendingRequestListener listener = null;
      FetchRequest fetchRequest = null;
      int index = 0;

      while (true) {
         pageModel = null;
         listener = null;
         fetchRequest = null;
         synchronized (this.this$0._store._pendingRequests) {
            if (this.this$0._store._pendingRequests.size() == 0) {
               label194:
               try {
                  this.this$0._store._pendingRequests.wait(30000);
               } finally {
                  break label194;
               }
            }

            if (this.this$0._store._pendingRequests.size() == 0 || !browserImpl.getInCoverage() && !ServiceRouting.getInstance().isSerialBypassActive()) {
               if (this._ticket != null) {
                  this._ticket = null;
               }

               this.this$0._pendingRequestThread = null;
               return;
            }

            if (index >= this.this$0._store._pendingRequests.size()) {
               index = 0;
            }

            pageModel = (PageModel)this.this$0._store._pendingRequests.elementAt(index);
            fetchRequest = new FetchRequest(pageModel);
            BrowserConfigRecord browserConfig = fetchRequest.getBrowserConfigRecord();
            if (browserConfig == null) {
               BrowserSession session = BrowserSession.getCurrentSession();
               if (session != null) {
                  browserConfig = session.getConfig();
               }
            }

            boolean makeRequest = false;
            if (browserConfig != null) {
               String transportCID = browserConfig.getPropertyAsString(3);
               if (browserImpl.isCoverageSufficient(transportCID)
                  || transportCID.equals(BrowserConfigRecord.IPPP_SERVICE_CID)
                     && ServiceRouting.getInstance().isServiceRoutable(browserConfig.getPropertyAsString(4), -1)) {
                  makeRequest = true;
               }
            }

            if (!makeRequest) {
               if (pageModel == firstSkippedPageModel) {
                  if (this._ticket != null) {
                     this._ticket = null;
                  }

                  this.this$0._pendingRequestThread = null;
                  return;
               }

               if (firstSkippedPageModel == null) {
                  firstSkippedPageModel = pageModel;
               }

               index++;
               continue;
            }

            if (pageModel == firstSkippedPageModel) {
               firstSkippedPageModel = null;
            }

            listener = (PendingRequestListener)this.this$0._store._pendingRequestListeners.elementAt(index);
            this.this$0._store._pendingRequests.removeElementAt(index);
            this.this$0._store._pendingRequestListeners.removeElementAt(index);
         }

         pageModel.changeStatus(1);
         browserImpl.processRequest(fetchRequest, false, listener == null);
         if (listener != null) {
            listener.notify(pageModel);
         }

         this.this$0._persistentObject.commit();
         synchronized (this.this$0._store._pendingRequests) {
            synchronized (this.this$0._store._savedPendingRequests) {
               if (this.this$0._store._pendingRequests.size() == 0
                  && this.this$0._store._savedPendingRequests.size() == 0
                  && browserImpl.getBrowserState() == 1) {
                  browserImpl.changeBrowserState(0);
               }
            }
         }
      }
   }
}
