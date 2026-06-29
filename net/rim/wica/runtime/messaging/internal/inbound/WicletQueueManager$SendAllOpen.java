package net.rim.wica.runtime.messaging.internal.inbound;

final class WicletQueueManager$SendAllOpen extends WicletQueueManager$FlowControl {
   private WicletQueueManager$SendAllOpen() {
      super(null);
   }

   @Override
   final void onNewMsg(WicletQueueManager wm) {
      if (wm._fgQueue.size() >= wm._openUpper) {
         wm._fcState = WicletQueueManager._SUSPEND_ALL_OPEN;
         wm.scheduleForFlowControl();
      }
   }

   @Override
   final void onDeactiveOrClose(WicletQueueManager wm) {
      if (wm._bgQueue.size() >= wm._closedBgUpper) {
         wm._fcState = WicletQueueManager._SUSPEND_ALL_CLOSED;
         wm.scheduleForFlowControl();
      } else if (wm._fgQueue.size() >= wm._closedFgUpper) {
         wm._fcState = WicletQueueManager._SUSPEND_FOREGROUND_CLOSED;
         wm.scheduleForFlowControl();
      } else {
         wm._fcState = WicletQueueManager._SEND_ALL_CLOSED;
      }
   }

   @Override
   final void onNewThreshold(WicletQueueManager wm) {
      if (wm._fgQueue.size() >= wm._openUpper) {
         wm._fcState = WicletQueueManager._SUSPEND_ALL_OPEN;
         wm.scheduleForFlowControl();
      }
   }

   @Override
   final int value() {
      return 0;
   }

   WicletQueueManager$SendAllOpen(WicletQueueManager$1 x0) {
      this();
   }
}
