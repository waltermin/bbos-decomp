package net.rim.wica.common.debug.session;

import net.rim.wica.common.debug.io.ByteStreamException;
import net.rim.wica.common.debug.msgmgt.MessageReceiver;

public final class MessageReceiveThread extends StoppableLoopThread {
   private MessageReceiver _msgReceiver;

   public MessageReceiveThread(MessageReceiver msgReceiver) {
      this._msgReceiver = msgReceiver;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void doRunLoopOnce() {
      boolean var4 = false /* VF: Semaphore variable */;

      ByteStreamException e;
      try {
         var4 = true;
         this._msgReceiver.getAndEnqueueMessage();
         return;
      } catch (ByteStreamException var5) {
         e = var5;
         var4 = false;
      } finally {
         if (var4) {
            this.onError();
            return;
         }
      }

      e.printStackTrace();
   }
}
