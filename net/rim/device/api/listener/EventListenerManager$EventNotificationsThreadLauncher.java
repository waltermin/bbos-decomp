package net.rim.device.api.listener;

import net.rim.vm.Process;

class EventListenerManager$EventNotificationsThreadLauncher implements Runnable {
   private final EventListenerManager this$0;

   private EventListenerManager$EventNotificationsThreadLauncher(EventListenerManager _1) {
      this.this$0 = _1;
   }

   @Override
   public void run() {
      if (Process.currentProcess().getProcessId() != this.this$0._proxyProcessID) {
         this.this$0._proxy.invokeLater(this);
      } else {
         this.this$0._eventNotificationsThread = new EventListenerManager$EventNotificationsThread(this.this$0, null);
         this.this$0._eventNotificationsThread.start();
      }
   }

   EventListenerManager$EventNotificationsThreadLauncher(EventListenerManager x0, EventListenerManager$1 x1) {
      this(x0);
   }
}
