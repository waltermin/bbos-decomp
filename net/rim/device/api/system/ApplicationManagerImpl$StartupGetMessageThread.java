package net.rim.device.api.system;

import net.rim.vm.Message;
import net.rim.vm.MessageQueue;

class ApplicationManagerImpl$StartupGetMessageThread extends Thread {
   private MessageQueue _messageQueue = new MessageQueue();
   private boolean _done;

   @Override
   public void run() {
      Message message = new Message();
      boolean havePowerUp = false;
      boolean msgQueueOverflowed = false;
      synchronized (this._messageQueue) {
         this._messageQueue.setMaxCapacity(256);

         while (true) {
            if (message.get()) {
               switch (message.getDevice()) {
                  case 0:
                  case 2:
                  case 3:
                  case 5:
                  case 26:
                  case 27:
                     break;
                  case 1:
                     if (message.getEvent() == 260) {
                        havePowerUp = true;
                     }
                  default:
                     if (!this._messageQueue.enqueue(message)) {
                        msgQueueOverflowed = true;
                     }
                     break;
                  case 14:
                     if (message.getEvent() == 3585 && !this._messageQueue.enqueue(message)) {
                        msgQueueOverflowed = true;
                     }
               }
            }

            synchronized (this) {
               if (this._done) {
                  if (!havePowerUp || msgQueueOverflowed) {
                     message = new Message(1, 260);
                     if ((!havePowerUp || !this._messageQueue.alreadyPresent(message)) && !this._messageQueue.enqueue(message)) {
                        msgQueueOverflowed = true;
                     }
                  }

                  if (msgQueueOverflowed) {
                     ApplicationProcess.logMessageQueueOverflow(this._messageQueue, "Startup OS Message Queue Overflow");
                  }

                  this.notify();
                  return;
               }
            }
         }
      }
   }

   private synchronized MessageQueue getMessages() {
      this._done = true;
      Message.abortGet(this);

      try {
         this.wait();
      } catch (InterruptedException var2) {
      }

      return this._messageQueue;
   }
}
