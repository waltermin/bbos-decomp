package net.rim.plazmic.internal.contentpreview.device.apps;

import net.rim.plazmic.internal.contentpreview.service.ServiceException;

final class ControlPanelClient$ControlPanelClientTask implements Runnable {
   private final ControlPanelClient this$0;

   private ControlPanelClient$ControlPanelClientTask(ControlPanelClient _1) {
      this.this$0 = _1;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void run() {
      synchronized (this) {
         while (true) {
            boolean var8 = false /* VF: Semaphore variable */;

            try {
               var8 = true;
               if (this.this$0._otherMessages.isEmpty()) {
                  if (this.this$0._lastTime != null) {
                     this.this$0._packet.setData(this.this$0._lastTime, 0, this.this$0._lastTime.length);
                     this.this$0._connection.send(this.this$0._packet);
                     this.this$0._lastTime = null;
                     var8 = false;
                  } else {
                     var8 = false;
                  }
                  break;
               }

               byte[] message = (byte[])this.this$0._otherMessages.elementAt(0);
               this.this$0._otherMessages.removeElementAt(0);
               this.this$0._packet.setData(message, 0, message.length);
               this.this$0._connection.send(this.this$0._packet);
            } finally {
               if (var8) {
                  try {
                     this.this$0._threadService.stopService(false);
                  } catch (ServiceException var9) {
                  }
                  break;
               }
            }
         }
      }
   }

   ControlPanelClient$ControlPanelClientTask(ControlPanelClient x0, ControlPanelClient$1 x1) {
      this(x0);
   }
}
