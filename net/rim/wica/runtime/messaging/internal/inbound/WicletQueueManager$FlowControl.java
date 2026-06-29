package net.rim.wica.runtime.messaging.internal.inbound;

class WicletQueueManager$FlowControl {
   private WicletQueueManager$FlowControl() {
   }

   int value() {
      return -1;
   }

   void onNewMsg(WicletQueueManager wm) {
   }

   void onNewBgMsg(WicletQueueManager wm) {
   }

   void onQueueEmpty(WicletQueueManager wm) {
   }

   void onOpen(WicletQueueManager wm) {
   }

   void onDeactiveOrClose(WicletQueueManager wm) {
   }

   void onNewThreshold(WicletQueueManager wm) {
   }

   void onStart(WicletQueueManager wm) {
   }

   void invalidEvent() {
      throw new RuntimeException("Invalid event");
   }

   WicletQueueManager$FlowControl(WicletQueueManager$1 x0) {
      this();
   }
}
