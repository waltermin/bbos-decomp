package net.rim.device.cldc.io.mdp;

import java.util.Vector;
import javax.microedition.io.Datagram;
import net.rim.device.api.io.DatagramBase;
import net.rim.device.api.system.EventLogger;

final class MdpResendThread extends Thread {
   private Vector _datagrams = new Vector(3, 2);
   private Datagram _currentDatagram;
   private boolean _shutdown;

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   final void cancelWaitingDatagram(int datagramId, boolean cancelCurrentDatagram) {
      synchronized (this._datagrams) {
         boolean var9 = false /* VF: Semaphore variable */;

         try {
            var9 = true;

            for (int e = this._datagrams.size() - 1; e >= 0; e--) {
               Object d = this._datagrams.elementAt(e);
               if (d instanceof DatagramBase && ((DatagramBase)d).getDatagramId() == datagramId) {
                  this._datagrams.removeElementAt(e);
                  var9 = false;
                  return;
               }
            }

            if (cancelCurrentDatagram) {
               if (!(this._currentDatagram instanceof DatagramBase)) {
                  var9 = false;
               } else if (((DatagramBase)this._currentDatagram).getDatagramId() == datagramId) {
                  MdpMFHUtil.getTransport().superCancel(this._currentDatagram);
                  var9 = false;
               } else {
                  var9 = false;
               }
            } else {
               var9 = false;
            }
         } finally {
            if (var9) {
               EventLogger.logEvent(MdpMFHUtil.GUID, 1397834610, 2);
               return;
            }
         }
      }
   }

   final void sendWaitingDatagram(Datagram d) {
      synchronized (this._datagrams) {
         if (!this._shutdown) {
            this._datagrams.addElement(d);
            this._datagrams.notifyAll();
         }
      }
   }

   final void kick(boolean stop, boolean kill) {
      synchronized (this._datagrams) {
         this._shutdown = kill;
         if (stop || kill) {
            this._datagrams.removeAllElements();
         }

         this._datagrams.notifyAll();
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void run() {
      while (true) {
         boolean var10 = false /* VF: Semaphore variable */;
         boolean var16 = false /* VF: Semaphore variable */;

         label215: {
            label214: {
               label213: {
                  try {
                     label211:
                     try {
                        var16 = true;
                        var10 = true;
                        synchronized (this._datagrams) {
                           if (this._shutdown) {
                              var10 = false;
                              var16 = false;
                              break label215;
                           }

                           if (this._datagrams.isEmpty()) {
                              label193:
                              try {
                                 this._datagrams.wait();
                              } finally {
                                 break label193;
                              }
                           }

                           if (this._datagrams.isEmpty()) {
                              var10 = false;
                              var16 = false;
                              break label214;
                           }

                           this._currentDatagram = (Datagram)this._datagrams.firstElement();
                           this._datagrams.removeElementAt(0);
                        }

                        boolean var22 = false /* VF: Semaphore variable */;

                        try {
                           var22 = true;
                           EventLogger.logEvent(MdpMFHUtil.GUID, 1415082871, 0);
                           MdpMFHUtil.getTransport().superSendInternal(this._currentDatagram);
                           var10 = false;
                           var16 = false;
                           var22 = false;
                           break label213;
                        } finally {
                           if (var22) {
                              EventLogger.logEvent(MdpMFHUtil.GUID, 1397838706, 0);
                              var10 = false;
                              var16 = false;
                              break label213;
                           }
                        }
                     } finally {
                        if (var16) {
                           EventLogger.logEvent(MdpMFHUtil.GUID, 1397838706, 0);
                           var10 = false;
                           break label211;
                        }
                     }
                  } finally {
                     if (var10) {
                        this._currentDatagram = null;
                     }
                  }

                  this._currentDatagram = null;
                  continue;
               }

               this._currentDatagram = null;
               continue;
            }

            this._currentDatagram = null;
            continue;
         }

         this._currentDatagram = null;
         this._currentDatagram = null;
         return;
      }
   }
}
