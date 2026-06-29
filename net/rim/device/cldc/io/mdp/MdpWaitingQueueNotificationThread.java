package net.rim.device.cldc.io.mdp;

import javax.microedition.io.Datagram;
import net.rim.device.api.io.ConnEvent;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.util.IntHashtable;
import net.rim.device.cldc.io.daemon.ProtocolDaemon;

final class MdpWaitingQueueNotificationThread extends Thread implements MdpConstants, ConnEvent {
   private int[] _statusIds = new int[16];
   private int[] _statusCodes = new int[16];
   private IntHashtable _receipts = new IntHashtable(16);
   private int _statusHead;
   private int _statusTail;
   private boolean _shutdown;
   private MdpResendThread _resendThread;

   final void kick(boolean stop, boolean kill) {
      synchronized (this._statusIds) {
         this._shutdown = kill;
         if (kill || stop) {
            this._statusTail = this._statusHead = 0;
         }

         this._statusIds.notifyAll();
         if (this._resendThread != null) {
            this._resendThread.kick(stop, kill);
            this._resendThread = null;
         }
      }
   }

   final void queueWaitingSendStatus(int dgramId, int status, MdpUtil$DatagramInfo info) {
      synchronized (this._statusIds) {
         if (!this._shutdown) {
            this._statusIds[this._statusHead] = dgramId;
            this._statusCodes[this._statusHead] = status;
            this._statusHead++;
            this._statusHead &= 15;
            if (this._statusTail == this._statusHead) {
               this._statusTail++;
               this._statusTail &= 15;
            }

            if (info != null) {
               this._receipts.put(dgramId, info);
            }

            this._statusIds.notify();
         }
      }
   }

   final void cancelResentDatagram(int id, boolean cancelCurrent) {
      if (this._resendThread != null && !this._shutdown) {
         this._resendThread.cancelWaitingDatagram(id, cancelCurrent);
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void run() {
      int id = 0;
      int code = 8;

      while (true) {
         try {
            synchronized (this._statusIds) {
               if (this._shutdown) {
                  this._receipts.clear();
                  return;
               }

               if (this._statusHead == this._statusTail) {
                  label208:
                  try {
                     this._statusIds.wait();
                  } finally {
                     break label208;
                  }
               }

               if (this._statusHead == this._statusTail) {
                  id = -1;
                  code = 8;
               } else {
                  id = this._statusIds[this._statusTail];
                  code = this._statusCodes[this._statusTail];
                  this._statusTail++;
                  this._statusTail &= 15;
               }
            }

            switch (code) {
               case 1:
               case 256:
               case 512:
                  MdpMFHUtil.getTransport().removeWaitingDatagram(id);
                  if (this._resendThread != null) {
                     this._resendThread.cancelWaitingDatagram(id, false);
                  }
                  break;
               case 16:
                  Object o = null;
                  synchronized (this._statusIds) {
                     o = this._receipts.remove(id);
                  }

                  if (o != null) {
                     Datagram d = MdpMFHUtil.getTransport()
                        .resendWaitingDatagramIfNeeded(id, ((MdpUtil$DatagramInfo)o).data, ((MdpUtil$DatagramInfo)o).offset, ((MdpUtil$DatagramInfo)o).length);
                     if (d != null) {
                        if (this._resendThread == null) {
                           this._resendThread = new MdpResendThread();
                           boolean var17 = false /* VF: Semaphore variable */;

                           label230:
                           try {
                              var17 = true;
                              ProtocolDaemon.getInstance().startThread(this._resendThread);
                              var17 = false;
                           } finally {
                              if (var17) {
                                 EventLogger.logEvent(MdpMFHUtil.GUID, 1414022514, 2);
                                 this._resendThread.kick(true, true);
                                 break label230;
                              }
                           }
                        }

                        this._resendThread.sendWaitingDatagram(d);
                     }
                  }
            }
         } finally {
            EventLogger.logEvent(MdpMFHUtil.GUID, 1313949042, 2);
            continue;
         }
      }
   }
}
