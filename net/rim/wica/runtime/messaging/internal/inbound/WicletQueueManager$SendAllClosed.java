package net.rim.wica.runtime.messaging.internal.inbound;

final class WicletQueueManager$SendAllClosed extends WicletQueueManager$FlowControl {
   private WicletQueueManager$SendAllClosed() {
      super(null);
   }

   @Override
   final void onNewMsg(WicletQueueManager wm) {
      if (wm._fgQueueSize >= wm._closedFgUpper) {
         wm._fcState = WicletQueueManager._SUSPEND_FOREGROUND_CLOSED;
         wm.scheduleForFlowControl();
      }
   }

   @Override
   final void onNewBgMsg(WicletQueueManager wm) {
      if (wm._bgQueue.size() >= wm._closedBgUpper) {
         wm._fcState = WicletQueueManager._SUSPEND_ALL_CLOSED;
         wm.scheduleForFlowControl();
      }
   }

   @Override
   final void onOpen(WicletQueueManager wm) {
      wm._fcState = WicletQueueManager._SEND_ALL_OPEN;
   }

   @Override
   final void onNewThreshold(WicletQueueManager wm) {
      if (wm._bgQueue.size() >= wm._closedBgUpper) {
         wm._fcState = WicletQueueManager._SUSPEND_ALL_CLOSED;
         wm.scheduleForFlowControl();
      } else {
         if (wm._fgQueueSize >= wm._closedFgUpper) {
            wm._fcState = WicletQueueManager._SUSPEND_FOREGROUND_CLOSED;
            wm.scheduleForFlowControl();
         }
      }
   }

   @Override
   final int value() {
      return 0;
   }

   WicletQueueManager$SendAllClosed(WicletQueueManager$1 x0) {
      this();
   }
}
