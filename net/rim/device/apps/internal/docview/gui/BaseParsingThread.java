package net.rim.device.apps.internal.docview.gui;

class BaseParsingThread implements Runnable {
   protected DocViewParser _coreData;
   protected int _consecutiveBlockCount;
   protected DocViewParserObj _notifyObj;
   private static final long PARSE_TIMEOUT = 15000L;

   BaseParsingThread(DocViewParser coreData, int consecutiveBlockCount, DocViewParserObj notifyObj) {
      if (coreData != null && consecutiveBlockCount > 0) {
         this._coreData = coreData;
         this._consecutiveBlockCount = consecutiveBlockCount;
         this._notifyObj = notifyObj;
      } else {
         throw new Object("Illegal values for parsing parameters");
      }
   }

   void doStart() {
      ((Thread)(new Object(this))).start();
   }

   protected final synchronized void notifyWaitingThreads() {
      this.notifyAll();
   }

   final synchronized void waitForContinueSignal(long timeout) {
      try {
         this.wait(timeout);
      } finally {
         return;
      }
   }

   protected boolean onStartLoop() {
      return true;
   }

   protected void onStop(boolean success) {
   }

   protected void onEndLoop(boolean success, int succeededBlockCount) {
   }

   @Override
   public final void run() {
      int succeededBlockCount = 0;
      boolean bStop = false;
      boolean bSucceed = false;
      boolean init = false;
      boolean notifyThreads = true;

      while (true) {
         synchronized (this._coreData.getParsingData()) {
            if (!init) {
               init = this.onStartLoop();
            }

            if (notifyThreads) {
               new BaseParsingThread$1(this).start();
               notifyThreads = false;
            }

            label107:
            try {
               this._coreData.getParsingData().wait(15000);
            } finally {
               break label107;
            }

            byte parseStatus = this._coreData.getLastParsingStatus();
            switch (parseStatus) {
               case 4:
                  if (this._coreData.getParsingData().getStopFlag() != 1) {
                     break;
                  }
               case 0:
                  bSucceed = true;
                  succeededBlockCount++;
                  if (this._notifyObj != null && parseStatus != 4 && succeededBlockCount != this._consecutiveBlockCount) {
                     this._notifyObj.parseHasSucceeded(succeededBlockCount, false);
                  }

                  if (succeededBlockCount != this._consecutiveBlockCount && parseStatus != 4) {
                     notifyThreads = true;
                  } else {
                     bStop = true;
                  }
                  break;
               default:
                  bStop = true;
            }

            if (bStop) {
               if (bSucceed) {
                  if (this._notifyObj != null) {
                     this._notifyObj.parseHasSucceeded(succeededBlockCount, true);
                  }

                  this.onStop(true);
               } else {
                  this.onStop(false);
                  if (this._notifyObj != null) {
                     this._notifyObj.parseHasFailed(this._coreData.getLastParsingStatus());
                  }
               }

               return;
            }

            this.onEndLoop(bSucceed, succeededBlockCount);
            if (bSucceed) {
               bSucceed = false;
            }
         }
      }
   }
}
