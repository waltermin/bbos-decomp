package net.rim.device.api.listener;

import net.rim.device.api.system.ApplicationManager;

class EventListenerManager$EventNotificationsThread extends Thread {
   private final EventListenerManager this$0;

   private EventListenerManager$EventNotificationsThread(EventListenerManager _1) {
      this.this$0 = _1;
   }

   @Override
   public void run() {
      ApplicationManager applicationManager = ApplicationManager.getApplicationManager();

      while (applicationManager.inStartup()) {
         try {
            Thread.sleep(10000);
         } catch (InterruptedException var8) {
         }
      }

      while (true) {
         EventListenerManager$EventNotification notification;
         synchronized (this.this$0._eventNotifications) {
            if (this.this$0._eventNotifications.size() == 0) {
               this.this$0._eventNotificationsThread = null;
               this.this$0._eventNotificationsThreadRunning = false;
               return;
            }

            notification = (EventListenerManager$EventNotification)this.this$0._eventNotifications.elementAt(0);
            this.this$0._eventNotifications.removeElementAt(0);
         }

         notification.run();
         Thread thread = notification.getThread();
         if (thread != null) {
            int time = 0;
            int delta = 0;

            while (thread.isAlive()) {
               try {
                  if (time < 60000) {
                     delta += 10;
                     time += delta;
                  }

                  Thread.sleep(time);
               } catch (InterruptedException var7) {
               }
            }
         }
      }
   }

   EventListenerManager$EventNotificationsThread(EventListenerManager x0, EventListenerManager$1 x1) {
      this(x0);
   }
}
