package net.rim.wica.runtime.messaging.internal.inbound;

final class WicletQueueManager$SuspendAllClosed extends WicletQueueManager$FlowControl {
   private WicletQueueManager$SuspendAllClosed() {
      super(null);
   }

   @Override
   final void onNewMsg(WicletQueueManager wm) {
      wm.scheduleForFlowControl();
   }

   @Override
   final void onNewBgMsg(WicletQueueManager wm) {
      wm.scheduleForFlowControl();
   }

   @Override
   final void onQueueEmpty(WicletQueueManager wm) {
      if (wm._fgQueue.isEmpty()) {
         wm._fcState = WicletQueueManager._SEND_ALL_CLOSED;
      } else {
         wm._fcState = WicletQueueManager._SUSPEND_FOREGROUND_CLOSED;
      }

      wm.scheduleForFlowControl();
   }

   @Override
   final void onOpen(WicletQueueManager wm) {
      if (!wm._fgQueue.isEmpty()) {
         wm._fcState = WicletQueueManager._SUSPEND_ALL_OPEN;
      } else {
         wm._fcState = WicletQueueManager._SEND_ALL_OPEN;
         wm.scheduleForFlowControl();
      }
   }

   @Override
   final void onStart(WicletQueueManager wm) {
      if (wm._bgQueue.isEmpty()) {
         if (wm._fgQueue.isEmpty()) {
            wm._fcState = WicletQueueManager._SEND_ALL_CLOSED;
         } else {
            wm._fcState = WicletQueueManager._SUSPEND_FOREGROUND_CLOSED;
         }

         wm.scheduleForFlowControl();
      }
   }

   @Override
   final int value() {
      return 2;
   }

   WicletQueueManager$SuspendAllClosed(WicletQueueManager$1 x0) {
      this();
   }
}
