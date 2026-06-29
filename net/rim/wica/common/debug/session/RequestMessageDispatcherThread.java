package net.rim.wica.common.debug.session;

public final class RequestMessageDispatcherThread extends StoppableLoopThread {
   RequestMessageDispatcher _messageDispatcher;

   public RequestMessageDispatcherThread(RequestMessageDispatcher messageDispatcher) {
      this._messageDispatcher = messageDispatcher;
   }

   @Override
   protected final void doRunLoopOnce() {
      this._messageDispatcher.processNextMessage();
   }
}
