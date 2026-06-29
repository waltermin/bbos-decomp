package net.rim.wica.runtime.metadata.internal;

class WicletRuntimeImpl$ProcessingRequest implements Runnable {
   private int _requestType;
   private Object _context;
   private final WicletRuntimeImpl this$0;
   private static final int STARTUP_REQUEST = 1;
   private static final int ACTIVATE_REQUEST = 2;
   private static final int DEACTIVATE_REQUEST = 3;
   private static final int PROCESS_INCOMING_MESSAGE_REQUEST = 4;
   private static final int MENU_SHOW_REQUEST = 5;
   private static final int SCREEN_BACK_REQUEST = 6;
   private static final int STOP_REQUEST = 7;
   private static final int RUNNABLE = 8;

   private WicletRuntimeImpl$ProcessingRequest(WicletRuntimeImpl this$0, int requestType, Object context) {
      this.this$0 = this$0;
      this._context = context;
      this._requestType = requestType;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public void run() {
      this.this$0._app.getDataLock().acquire();
      boolean var5 = false /* VF: Semaphore variable */;

      label58: {
         try {
            label56:
            try {
               var5 = true;
               switch (this._requestType) {
                  case 0:
                     var5 = false;
                     break label58;
                  case 1:
                  default:
                     this.this$0.callEntryPoint();
                     var5 = false;
                     break label58;
                  case 2:
                     this.this$0.activateInternal();
                     var5 = false;
                     break label58;
                  case 3:
                     this.this$0.deactivateInternal();
                     var5 = false;
                     break label58;
                  case 4:
                     this.this$0._msgHandler.processMsg();
                     var5 = false;
                     break label58;
                  case 5:
                     this.this$0._uiService.showMenu(this._context);
                     var5 = false;
                     break label58;
                  case 6:
                     this.this$0._uiService.performScreenBack();
                     var5 = false;
                     break label58;
                  case 7:
                     ((WicletRuntimeImpl$StopRunnable)this._context).run();
                     var5 = false;
                     break label58;
                  case 8:
                     if (!(this._context instanceof Object)) {
                        var5 = false;
                     } else {
                        ((Runnable)this._context).run();
                        var5 = false;
                     }
                     break label58;
               }
            } catch (Throwable var8) {
               this.this$0._eventService.dispatchEvent(this, 605, 100, e);
               var5 = false;
               break label56;
            }
         } finally {
            if (var5) {
               this.this$0._app.getDataLock().release();
            }
         }

         this.this$0._app.getDataLock().release();
         return;
      }

      this.this$0._app.getDataLock().release();
   }

   WicletRuntimeImpl$ProcessingRequest(WicletRuntimeImpl x0, int x1, Object x2, WicletRuntimeImpl$1 x3) {
      this(x0, x1, x2);
   }
}
