package net.rim.wica.common.debug.msgmgt;

import java.util.Vector;
import net.rim.wica.common.debug.protocol.messages.IMessageEnvelope;

public final class MessageQueue {
   private Vector _dataQueue = (Vector)(new Object());
   public static final int WAIT_FOREVER;

   public final synchronized IMessageEnvelope get(int timeout) {
      IMessageEnvelope msg = null;

      while (this._dataQueue.size() == 0) {
         try {
            this.wait(timeout);
         } finally {
            ;
         }
      }

      msg = (IMessageEnvelope)this._dataQueue.elementAt(0);
      this._dataQueue.removeElementAt(0);
      return msg;
   }

   public final synchronized void add(IMessageEnvelope msg) {
      this._dataQueue.addElement(msg);
      this.notifyAll();
   }
}
