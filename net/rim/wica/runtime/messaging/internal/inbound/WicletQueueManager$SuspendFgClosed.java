package net.rim.wica.runtime.messaging.internal.inbound;

final class WicletQueueManager$SuspendFgClosed extends WicletQueueManager$FlowControl {
   private WicletQueueManager$SuspendFgClosed() {
      super(null);
   }

   @Override
   final void onNewMsg(WicletQueueManager wm) {
      wm.scheduleForFlowControl();
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
      if (wm._fgQueue.isEmpty()) {
         wm._fcState = WicletQueueManager._SEND_ALL_OPEN;
      } else {
         wm._fcState = WicletQueueManager._SUSPEND_ALL_OPEN;
      }

      wm.scheduleForFlowControl();
   }

   @Override
   final void onNewThreshold(WicletQueueManager wm) {
      if (wm._bgQueue.size() >= wm._closedBgUpper) {
         wm._fcState = WicletQueueManager._SUSPEND_ALL_CLOSED;
         wm.scheduleForFlowControl();
      }
   }

   @Override
   final void onStart(WicletQueueManager wm) {
      if (wm._fgQueue.isEmpty()) {
         wm._fcState = WicletQueueManager._SEND_ALL_OPEN;
         wm.scheduleForFlowControl();
      }
   }

   @Override
   final int value() {
      return 1;
   }

   WicletQueueManager$SuspendFgClosed(WicletQueueManager$1 x0) {
      this();
   }
}
