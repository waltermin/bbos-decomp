package net.rim.device.cldc.io.tcp;

import net.rim.device.api.system.EventLogger;
import net.rim.device.internal.io.tcp.Deque;
import net.rim.device.internal.io.tcp.TcpConstants;
import net.rim.device.internal.io.tcp.TcpDatagramBase;

class TcpIOThread extends Thread implements TcpConstants {
   Protocol _protocol;
   boolean _shutDownRequired;
   long GUID;
   int _waitTag;
   Object _sendLock = new Object();
   Deque _receiveRequestDeque = (Deque)(new Object());
   int _sendRequestsPendingSize;
   Object _trigger = new Object();

   TcpIOThread(Protocol protocol) {
      this.init(protocol);
      this.GUID = -8230387478786502228L;
      EventLogger.register(this.GUID, "net.rim.tcpreceivethread", 2);
   }

   void init(Protocol protocol) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public void run() {
      TcpDatagramBase tcpDatagram = null;

      while (!this._shutDownRequired) {
         while (this._sendRequestsPendingSize == 0 && this._receiveRequestDeque.getSize() == 0 && !this._shutDownRequired) {
            synchronized (this._trigger) {
               try {
                  this._trigger.wait();
               } finally {
                  continue;
               }
            }
         }

         while (!this._shutDownRequired && (this._sendRequestsPendingSize != 0 || this._receiveRequestDeque.getSize() != 0)) {
            if (this._receiveRequestDeque.getSize() > 0) {
               for (int ackCount = 0; ackCount < 10; ackCount++) {
                  synchronized (this._receiveRequestDeque) {
                     try {
                        tcpDatagram = (TcpDatagramBase)this._receiveRequestDeque.dequeueHead();
                     } finally {
                        ;
                     }
                  }

                  if (tcpDatagram == null) {
                     break;
                  }

                  try {
                     label666:
                     try {
                        this._protocol.tcpInput(tcpDatagram);
                     } finally {
                        break label666;
                     }
                  } finally {
                     tcpDatagram = null;
                  }

                  if (this._receiveRequestDeque.getSize() <= 0) {
                     break;
                  }

                  synchronized (this._receiveRequestDeque) {
                     boolean var29 = false /* VF: Semaphore variable */;

                     try {
                        var29 = true;
                        tcpDatagram = (TcpDatagramBase)this._receiveRequestDeque.getHead();
                        if (tcpDatagram != null) {
                           if ((tcpDatagram._tcpProps._flags & 16) != 0) {
                              if (tcpDatagram.getLength() == 0) {
                                 var29 = false;
                              } else {
                                 var29 = false;
                                 break;
                              }
                           } else {
                              var29 = false;
                              break;
                           }
                        } else {
                           var29 = false;
                           break;
                        }
                     } finally {
                        if (var29) {
                           tcpDatagram = null;
                           break;
                        }
                     }
                  }
               }
            }

            if (this._sendRequestsPendingSize != 0) {
               boolean sendCompleted = false;

               label694:
               try {
                  sendCompleted = this._protocol.tcpOutput();
               } finally {
                  break label694;
               }

               if (!this._protocol.shouldWeSendAPacket(sendCompleted) && sendCompleted) {
                  synchronized (this._sendLock) {
                     this._sendRequestsPendingSize--;
                  }
               }
            }
         }
      }

      this._protocol.shutDownConnection();
   }

   void abort() {
      synchronized (this._sendLock) {
         this._sendRequestsPendingSize = 0;
         this._sendLock.notifyAll();
      }

      synchronized (this._receiveRequestDeque) {
         this._receiveRequestDeque.purge();
         this._receiveRequestDeque.notifyAll();
      }

      this.requestShutDown();
   }

   void requestShutDown() {
      this._shutDownRequired = true;
      synchronized (this._trigger) {
         this._trigger.notifyAll();
      }
   }

   void addSendRequest() {
      if (!this._shutDownRequired) {
         synchronized (this._sendLock) {
            if (this._sendRequestsPendingSize == 0) {
               this._sendRequestsPendingSize = 1;
            }
         }

         synchronized (this._trigger) {
            this._trigger.notifyAll();
         }
      }
   }

   void addReceiveRequest(TcpDatagramBase tcpDatagram) {
      if (!this._shutDownRequired) {
         synchronized (this._receiveRequestDeque) {
            this._receiveRequestDeque.enqueueTail(tcpDatagram);
         }

         synchronized (this._trigger) {
            this._trigger.notifyAll();
         }
      }
   }
}
