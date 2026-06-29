package net.rim.wica.runtime.messaging.internal.inbound;

final class WicletQueueManager$SuspendAllOpen extends WicletQueueManager$FlowControl {
   private WicletQueueManager$SuspendAllOpen() {
      super(null);
   }

   @Override
   final void onNewMsg(WicletQueueManager wm) {
      wm.scheduleForFlowControl();
   }

   @Override
   final void onQueueEmpty(WicletQueueManager wm) {
      wm._fcState = WicletQueueManager._SEND_ALL_OPEN;
      wm.scheduleForFlowControl();
   }

   @Override
   final void onDeactiveOrClose(WicletQueueManager wm) {
      if (!wm._bgQueue.isEmpty()) {
         wm._fcState = WicletQueueManager._SUSPEND_ALL_CLOSED;
      } else if (!wm._fgQueue.isEmpty()) {
         wm._fcState = WicletQueueManager._SUSPEND_FOREGROUND_CLOSED;
         wm.scheduleForFlowControl();
      } else {
         wm._fcState = WicletQueueManager._SEND_ALL_CLOSED;
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
      return 2;
   }

   WicletQueueManager$SuspendAllOpen(WicletQueueManager$1 x0) {
      this();
   }
}
