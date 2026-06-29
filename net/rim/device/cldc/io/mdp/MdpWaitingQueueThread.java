package net.rim.device.cldc.io.mdp;

import java.util.Vector;
import javax.microedition.io.Datagram;
import net.rim.device.api.io.ConnEvent;
import net.rim.device.api.io.DatagramAddressBase;
import net.rim.device.api.io.DatagramBase;
import net.rim.device.api.io.DatagramStatusListener;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.util.Arrays;

final class MdpWaitingQueueThread extends Thread implements MdpConstants, ConnEvent {
   private Vector _datagrams = (Vector)(new Object(4, 5));
   private boolean _shutdown;
   private long _timer;
   private long _minDatagramAckTimeout = Long.MAX_VALUE;
   private final byte[] _rcArrayEmpty = new byte[16];
   private int _overflowSize;
   private static final int MDP_WAITING_QUEUE_MAX_RETRIES;
   private static final int MDP_WAITING_QUEUE_MAX_BACKOFF;
   private static final int MDP_WAITING_QUEUE_DEF_BACKOFF_MAX;
   private static final int MDP_WAITING_QUEUE_MAX_DATAGRAMS_NUMBER;

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void run() {
      MdpWaitingQueueThread$MdpTxDatagram datagram = null;
      long currentTime = 0;
      long maxTime = 0;
      long endTime = 0;
      boolean timerExpired = false;
      boolean maxAttempts = false;
      int size = 0;

      while (true) {
         synchronized (this._datagrams) {
            if (this._shutdown) {
               return;
            }

            if (this._datagrams.isEmpty()) {
               label674:
               try {
                  this._datagrams.wait();
               } finally {
                  break label674;
               }
            }

            size = this._datagrams.size();
         }

         currentTime = System.currentTimeMillis();
         long minTimeout = Long.MAX_VALUE;
         int reference = 0;
         DatagramStatusListener listener = null;
         DatagramAddressBase addressBase = null;
         boolean overflowDatagram = false;
         boolean var51 = false /* VF: Semaphore variable */;

         try {
            label726:
            try {
               var51 = true;
               int t = 0;

               while (t < size) {
                  synchronized (this._datagrams) {
                     datagram = (MdpWaitingQueueThread$MdpTxDatagram)this._datagrams.elementAt(t);
                     if (datagram == null) {
                        t++;
                        continue;
                     }

                     maxTime = datagram.maxTime;
                     endTime = datagram.endTime;
                     timerExpired = endTime - currentTime <= 0;
                     maxAttempts = timerExpired
                        && (maxTime > 0 && currentTime >= maxTime || endTime > 0 && ++datagram.retries >= 9 || datagram.completeRetries >= 3);
                     if (size - this._overflowSize > 20) {
                        if (!datagram.overflowDatagram) {
                           EventLogger.logEvent(MdpMFHUtil.GUID, 1415073125, 3);
                           this._overflowSize++;
                           datagram.overflowDatagram = true;
                        }

                        datagram.datagram = null;
                        datagram.addressBase = null;
                     }

                     if (maxAttempts || maxTime <= 0) {
                        EventLogger.logEvent(MdpMFHUtil.GUID, 1415082615, 0);
                        if (datagram.datagram != null && datagram.endTime == 0) {
                           label694:
                           try {
                              MdpMFHUtil.getTransport().cancelResentDatagramIfPossible(datagram.reference, false);
                           } finally {
                              break label694;
                           }
                        }

                        this._datagrams.removeElementAt(t);
                        size--;
                        if (datagram.overflowDatagram && --this._overflowSize < 0) {
                           this._overflowSize = 0;
                        }

                        if (!maxAttempts) {
                           continue;
                        }
                     }

                     listener = datagram.listener;
                     addressBase = datagram.addressBase;
                     reference = datagram.reference;
                     overflowDatagram = datagram.overflowDatagram;
                  }

                  if (timerExpired) {
                     if (maxAttempts) {
                        EventLogger.logEvent(MdpMFHUtil.GUID, 1413836129, 2);
                        MdpMFHUtil.getTransport().xmitDgslEvent(listener, reference, 8321, null);
                        continue;
                     }

                     if (endTime > 0 && !overflowDatagram) {
                        EventLogger.logEvent(MdpMFHUtil.GUID, 1415074647, 0);
                        if (addressBase != null) {
                           MdpMFHUtil.getTransport().sendStatusInternal(addressBase.getSubAddressBase(), reference, false, false);
                        }

                        synchronized (this._datagrams) {
                           datagram.datagramAckTimeout = Math.min((datagram.datagramAckTimeout >> 1) + (datagram.datagramAckTimeout << 1), 600000);
                           datagram.endTime = Math.min(datagram.maxTime, currentTime + datagram.datagramAckTimeout);
                        }
                     }
                  }

                  minTimeout = Math.min(
                     minTimeout,
                     Math.min(
                        Math.min(maxTime - currentTime, endTime - currentTime > 0 ? endTime - currentTime : maxTime - currentTime), datagram.datagramAckTimeout
                     )
                  );
                  t++;
               }

               var51 = false;
            } finally {
               if (var51) {
                  EventLogger.logEvent(MdpMFHUtil.GUID, 1464943986, 0);
                  break label726;
               }
            }
         } finally {
            datagram = null;
            listener = null;
            addressBase = null;
            overflowDatagram = false;
            int var88 = false;
         }

         if (minTimeout > 0) {
            synchronized (this._datagrams) {
               if (this._datagrams.size() <= 5 && this._datagrams.capacity() > 5) {
                  this._datagrams.trimToSize();
               }

               if (!this._shutdown && !this._datagrams.isEmpty()) {
                  this._minDatagramAckTimeout = minTimeout;
                  this._timer = this._minDatagramAckTimeout;

                  try {
                     this._datagrams.wait(this._timer);
                  } finally {
                     continue;
                  }
               }
            }
         }
      }
   }

   final void kick(boolean stop, boolean kill) {
      synchronized (this._datagrams) {
         this._shutdown = kill;
         if (stop || kill) {
            this._datagrams.removeAllElements();
            this._timer = 0;
            this._minDatagramAckTimeout = Integer.MAX_VALUE;
            this._overflowSize = 0;
         }

         this._datagrams.notifyAll();
      }
   }

   final boolean isRunning() {
      synchronized (this._datagrams) {
         return !this._shutdown;
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   final void addDatagram(
      Datagram datagram, DatagramStatusListener listener, DatagramAddressBase addressBase, int reference, int retriesCount, int datagramAckTimeout
   ) {
      EventLogger.logEvent(MdpMFHUtil.GUID, 1415078263, 4);
      synchronized (this._datagrams) {
         if (!this._shutdown && datagramAckTimeout > 0 && datagram != null && addressBase != null) {
            boolean var19 = false /* VF: Semaphore variable */;

            try {
               var19 = true;
               MdpWaitingQueueThread$MdpTxDatagram t = null;
               byte completeRetries = 0;
               int i = this.findDatagram(reference);
               if (i >= 0) {
                  t = (MdpWaitingQueueThread$MdpTxDatagram)this._datagrams.elementAt(i);
                  if (t != null) {
                     completeRetries = ++t.completeRetries;
                     if (t.completeRetries >= 3) {
                        t.maxTime = 0;
                        t.listener = listener;
                        this._datagrams.notifyAll();
                        var19 = false;
                        return;
                     }
                  }
               }

               if (t == null) {
                  t = new MdpWaitingQueueThread$MdpTxDatagram(null);
               }

               t.reference = reference;
               t.retries = retriesCount;
               DatagramBase dg = (DatagramBase)(new Object());
               if (!(datagram instanceof Object)) {
                  dg.setData(datagram.getData(), datagram.getOffset(), datagram.getLength());
                  dg.setAddress(datagram.getAddress());
               } else {
                  DatagramBase temp = (DatagramBase)datagram;
                  boolean var23 = false /* VF: Semaphore variable */;

                  label158:
                  try {
                     var23 = true;
                     dg.copy(temp);
                     var23 = false;
                  } finally {
                     if (var23) {
                        temp.copyFlagsInto(dg);
                        dg.setData(temp.getData(), temp.getOffset(), temp.getLength());
                        break label158;
                     }
                  }

                  dg.setDatagramId(reference);
                  dg.setDatagramStatusListener(listener);
                  dg.setAddressBase(addressBase);
               }

               t.datagram = dg;
               t.listener = listener;
               t.addressBase = addressBase;
               t.completeRetries = completeRetries;
               t.datagramAckTimeout = datagramAckTimeout;
               if (i >= 0 && t.overflowDatagram) {
                  t.overflowDatagram = false;
                  this._overflowSize--;
               }

               long time = System.currentTimeMillis();
               t.maxTime = time + 720000;
               t.endTime = time + t.datagramAckTimeout;
               EventLogger.logEvent(MdpMFHUtil.GUID, 1415078263, 0);
               if (i >= 0) {
                  this._datagrams.setElementAt(t, i);
               } else {
                  this._datagrams.addElement(t);
               }

               int size = this.getRealSize();
               if (this._timer <= t.datagramAckTimeout && size != 1) {
                  if (i >= 0) {
                     var19 = false;
                     return;
                  }

                  if (size <= 20) {
                     var19 = false;
                     return;
                  }
               }

               this._datagrams.notifyAll();
               var19 = false;
            } finally {
               if (var19) {
                  EventLogger.logEvent(MdpMFHUtil.GUID, 1415078263, 2);
                  return;
               }
            }
         }
      }
   }

   final Datagram resendDatagramIfNeeded(int reference, byte[] dataArray, int offset, int length) {
      EventLogger.logEvent(MdpMFHUtil.GUID, 1415082871, 4);
      if (dataArray != null
         && offset + length <= dataArray.length
         && length < this._rcArrayEmpty.length
         && Arrays.equals(this._rcArrayEmpty, 0, dataArray, offset, length)) {
         synchronized (this._datagrams) {
            int i = this.findDatagram(reference);
            if (i >= 0) {
               MdpWaitingQueueThread$MdpTxDatagram d = (MdpWaitingQueueThread$MdpTxDatagram)this._datagrams.elementAt(i);
               if (d.maxTime > 0) {
                  d.endTime = 0;
                  d.maxTime = System.currentTimeMillis() + 720000;
                  return d.datagram;
               }
            }

            return null;
         }
      } else {
         return null;
      }
   }

   private final int getRealSize() {
      return this._datagrams.size() - this._overflowSize;
   }

   private final int findDatagram(int reference) {
      int i;
      for (i = this._datagrams.size() - 1; i >= 0; i--) {
         MdpWaitingQueueThread$MdpTxDatagram d = (MdpWaitingQueueThread$MdpTxDatagram)this._datagrams.elementAt(i);
         if (d != null && d.reference == reference) {
            return i;
         }
      }

      return i;
   }

   final Datagram removeDatagram(int reference) {
      EventLogger.logEvent(MdpMFHUtil.GUID, 1415082615, 4);
      synchronized (this._datagrams) {
         int i = this.findDatagram(reference);
         Datagram datagram = null;
         if (i >= 0) {
            MdpWaitingQueueThread$MdpTxDatagram d = (MdpWaitingQueueThread$MdpTxDatagram)this._datagrams.elementAt(i);
            d.maxTime = 0;
            datagram = d.datagram;
            this._datagrams.notifyAll();
         }

         return datagram;
      }
   }

   final boolean isEmpty() {
      synchronized (this._datagrams) {
         return this._datagrams.size() == 0;
      }
   }
}
