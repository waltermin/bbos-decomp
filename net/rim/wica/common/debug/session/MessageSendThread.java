package net.rim.wica.common.debug.session;

import net.rim.wica.common.debug.msgmgt.MessageSender;

public final class MessageSendThread extends StoppableLoopThread {
   private MessageSender _msgSender;

   public MessageSendThread(MessageSender msgSender) {
      this._msgSender = msgSender;
   }

   @Override
   public final void doRunLoopOnce() {
      try {
         this._msgSender.getMessageAndDispatch(0);
      } finally {
         this.onError();
         return;
      }
   }
}
