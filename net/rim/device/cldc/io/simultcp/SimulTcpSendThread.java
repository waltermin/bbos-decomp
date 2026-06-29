package net.rim.device.cldc.io.simultcp;

import java.util.Random;
import java.util.Timer;
import net.rim.device.internal.system.RadioInternal;

final class SimulTcpSendThread extends Thread implements SimulTcpConstants {
   private Protocol protocol;
   private Object trigger = new Object();
   private int bytesSent;
   private long lingerPeriod;
   private boolean stopSending;
   private boolean closeSocket = true;
   private int sequenceNumber;
   private LingerTimer closeTimerTask;
   private Timer closeTimer;

   SimulTcpSendThread(Protocol connection) {
      Random temp = new Random();
      this.sequenceNumber = temp.nextInt();
      this.protocol = connection;
      SimulTcpProcess.getInstance().invokeAndWait(new SimulTcpSendThread$1(this));
   }

   public final synchronized void stopSending(boolean pCloseSocket, boolean allowLinger) {
      this.stopSending = true;
      this.closeSocket = pCloseSocket;
      if (this.lingerPeriod > 0 && allowLinger) {
         this.closeTimerTask = new LingerTimer(this, pCloseSocket);
         this.closeTimer.schedule(this.closeTimerTask, this.lingerPeriod);
      } else {
         this.notifyAll();
         synchronized (this.trigger) {
            this.trigger.notifyAll();
         }
      }
   }

   public final void setLingerPeriod(int period) {
      this.lingerPeriod = period;
   }

   public final long getLingerPeriod() {
      return this.lingerPeriod;
   }

   public final synchronized void confirmSend(long numberSent, int seqN) {
      if (seqN == this.sequenceNumber) {
         this.bytesSent = (int)(this.bytesSent + numberSent);
         this.sequenceNumber = (int)(this.sequenceNumber + numberSent);
      }

      synchronized (this.trigger) {
         this.trigger.notifyAll();
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void run() {
      byte[] sendArray = new byte[this.protocol.maxPayloadSize];

      while (!this.stopSending) {
         synchronized (this) {
            while (!this.protocol._dataWaitingToBeSent) {
               label171:
               try {
                  this.wait();
               } finally {
                  break label171;
               }

               if (this.checkStop()) {
                  this.closeTimer.cancel();
                  return;
               }
            }
         }

         int sendLength = this.protocol.sendBuffer.getLength();
         int loadSize = 0;
         this.bytesSent = 0;

         while (this.bytesSent != sendLength && !this.stopSending) {
            loadSize = (sendLength - this.bytesSent) % this.protocol.maxPayloadSize;
            if (loadSize == 0) {
               loadSize = this.protocol.maxPayloadSize;
            }

            this.protocol.sendBuffer.fillArray(sendArray, loadSize);
            this.protocol.tcpOutput(sendArray, 0, loadSize, this.sequenceNumber);
            long startTime = System.currentTimeMillis();
            synchronized (this.trigger) {
               boolean var15 = false /* VF: Semaphore variable */;

               try {
                  var15 = true;
                  this.trigger.wait(this.protocol.maxRetransmit);
                  var15 = false;
               } finally {
                  if (var15) {
                     throw new RuntimeException();
                  }
               }
            }

            if (System.currentTimeMillis() - startTime >= this.protocol.maxRetransmit) {
               this.protocol.throwIOException = true;
               this.stopSending = true;
            }

            if (this.checkStop()) {
               this.closeTimer.cancel();
               return;
            }
         }

         this.protocol.sendBuffer.clear();
         synchronized (this) {
            this.protocol._dataWaitingToBeSent = false;
            this.notifyAll();
         }
      }

      this.checkStop();
   }

   private final boolean checkStop() {
      if (this.stopSending) {
         if (this.protocol.socketID != -1) {
            RadioInternal.simulTCPCommand(2, this.protocol.socketID, 0, 0, 0);
         }

         if (this.closeSocket) {
            if (this.protocol.socketID != -1) {
               RadioInternal.simulTCPCommand(1, this.protocol.socketID, 0, 0, 0);
            }

            this.protocol.socketID = -1;
         }

         this.protocol.sendBuffer.clear();
         synchronized (this) {
            this.protocol._dataWaitingToBeSent = false;
            this.notifyAll();
         }

         return this.closeSocket;
      } else {
         return false;
      }
   }
}
