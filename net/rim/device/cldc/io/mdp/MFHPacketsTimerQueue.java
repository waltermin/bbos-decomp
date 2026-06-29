package net.rim.device.cldc.io.mdp;

import net.rim.device.api.system.EventLogger;
import net.rim.device.api.util.Arrays;
import net.rim.vm.Array;

final class MFHPacketsTimerQueue extends Thread implements MdpConstants {
   private boolean _shutdown;
   MFHRetransmissionConfiguration _config;
   private int _currentDatagramId = 0;
   private long _minTimeout = Long.MAX_VALUE;
   private long _timer = Long.MAX_VALUE;
   private int[] _packetsTrackRetries = new int[0];
   private long[] _packetsTrackTime = new long[0];

   final void setup(int datagramId, int maxSequence, MFHRetransmissionConfiguration config) {
      this._currentDatagramId = datagramId;
      this._config = config;
      this._minTimeout = this._timer = Long.MAX_VALUE;
      this.setQueue(datagramId, maxSequence);
   }

   final void shutdown() {
      synchronized (this) {
         this._shutdown = true;
         this._config = null;
         this.notifyAll();
      }
   }

   @Override
   public final void run() {
      long currentTime = 0;
      long endTime = 0;
      int size = 0;
      long minTimeout = Long.MAX_VALUE;

      while (true) {
         synchronized (this._packetsTrackTime) {
            if (this._shutdown) {
               return;
            }

            if (this._minTimeout == Long.MAX_VALUE) {
               label227:
               try {
                  this._packetsTrackTime.wait();
               } finally {
                  break label227;
               }
            }

            size = this._packetsTrackTime.length;
         }

         currentTime = System.currentTimeMillis();
         minTimeout = Long.MAX_VALUE;

         label249:
         try {
            for (int i = 0; i < size; i++) {
               synchronized (this._packetsTrackTime) {
                  if (this._packetsTrackTime[i] == Long.MAX_VALUE) {
                     continue;
                  }

                  endTime = this._packetsTrackTime[i] - currentTime;
               }

               if (endTime <= 0) {
                  EventLogger.logEvent(MdpMFHUtil.GUID, 1414817144, 4);
                  if (i < size - 1) {
                     MdpMFHUtil.getTransport().queueSendStatus(this._currentDatagramId | i << 8, 1024, null);
                  } else {
                     MdpMFHUtil.getTransport().queueSendStatus(this._currentDatagramId, 2048, null);
                  }

                  this.cancelPacket(this._currentDatagramId, i);
               } else {
                  minTimeout = Math.min(minTimeout, endTime);
               }
            }
         } finally {
            break label249;
         }

         synchronized (this._packetsTrackTime) {
            if (minTimeout <= 0 || minTimeout >= Long.MAX_VALUE) {
               this._minTimeout = minTimeout;
            } else if (!this._shutdown) {
               this._minTimeout = minTimeout;
               this._timer = this._minTimeout;

               try {
                  this._packetsTrackTime.wait(this._timer);
               } finally {
                  continue;
               }
            }
         }
      }
   }

   private final long calculateTimer(int retriesCount, long suggestedTimer) {
      long timer = 0;
      if (retriesCount < -this._config._maxImmediatePacketRetry) {
         timer = this._config._maxRelayPacketRTT;
      } else if (retriesCount < 0) {
         timer = this._config._lostPacketTimeout;
      } else {
         timer = Math.min(
            this._config._maxPacketTimeout, (this._config._lostPacketTimeout << 1) * (1 << (Math.min(this._config._maxBackoffPacketRetry, retriesCount) >> 1))
         );
      }

      return suggestedTimer > 0 && suggestedTimer < timer ? suggestedTimer : timer;
   }

   private final void setQueue(int reference, int maxSequence) {
      if (maxSequence > 127) {
         maxSequence = 127;
      }

      this.initPackets(reference, maxSequence);
   }

   final void addPacket(int reference, int sequence, long suggestedTimer) {
      synchronized (this._packetsTrackTime) {
         if (sequence >= 0 && sequence < this._packetsTrackTime.length - 1) {
            if (this._packetsTrackRetries[sequence] + 1 <= this._config._maxBackoffPacketRetry) {
               EventLogger.logEvent(MdpMFHUtil.GUID, 1414820724, 5);
               long pTimer = this.calculateTimer(this._packetsTrackRetries[sequence], suggestedTimer);
               this._packetsTrackTime[sequence] = System.currentTimeMillis() + pTimer;
               this._packetsTrackRetries[sequence] = Math.min(this._packetsTrackRetries[sequence] + 1, this._config._maxBackoffPacketRetry);
               if (this._minTimeout > pTimer) {
                  this._packetsTrackTime.notifyAll();
               }
            } else {
               EventLogger.logEvent(MdpMFHUtil.GUID, 1414819192, 5);
            }
         }
      }
   }

   final void addDatagram(int reference, long suggestedTimer) {
      synchronized (this._packetsTrackTime) {
         if (suggestedTimer > 0) {
            if (this._packetsTrackRetries.length < 1 || this._packetsTrackTime.length < 1) {
               return;
            }

            this._packetsTrackRetries[this._packetsTrackRetries.length - 1] = 0;
            this._packetsTrackTime[this._packetsTrackTime.length - 1] = System.currentTimeMillis() + suggestedTimer;
            if (this._minTimeout > suggestedTimer) {
               this._packetsTrackTime.notifyAll();
            }
         }
      }
   }

   final void resetPacket(int reference, int sequence) {
      synchronized (this._packetsTrackTime) {
         if (sequence >= 0 && sequence < this._packetsTrackTime.length - 1) {
            this._packetsTrackTime[sequence] = Long.MAX_VALUE;
            this._packetsTrackRetries[sequence] = -(this._config._maxAggressivePacketRetry + this._config._maxImmediatePacketRetry);
            this._packetsTrackTime.notifyAll();
         }
      }
   }

   final void resetPacketsRetries(int reference) {
      synchronized (this._packetsTrackTime) {
         if (this._packetsTrackRetries.length > 1) {
            Arrays.fill(
               this._packetsTrackRetries,
               -(this._config._maxAggressivePacketRetry + this._config._maxImmediatePacketRetry),
               0,
               this._packetsTrackRetries.length - 1
            );
         }
      }
   }

   final void rollbackPacket(int reference, int sequence) {
      synchronized (this._packetsTrackTime) {
         if (sequence >= 0 && sequence < this._packetsTrackTime.length - 1) {
            this._packetsTrackTime[sequence] = Long.MAX_VALUE;
            this._packetsTrackRetries[sequence] = Math.max(
               this._packetsTrackRetries[sequence] - 1, -(this._config._maxAggressivePacketRetry + this._config._maxImmediatePacketRetry)
            );
            this._packetsTrackTime.notifyAll();
         }
      }
   }

   final void cancelPacket(int reference, int sequence) {
      synchronized (this._packetsTrackTime) {
         if (sequence >= 0 && sequence < this._packetsTrackTime.length - 1) {
            this._packetsTrackTime[sequence] = Long.MAX_VALUE;
            this._packetsTrackTime.notifyAll();
         }
      }
   }

   final void cancelDatagram(int reference) {
      synchronized (this._packetsTrackTime) {
         if (this._packetsTrackTime.length > 1) {
            this._packetsTrackTime[this._packetsTrackTime.length - 1] = Long.MAX_VALUE;
         }

         if (this._packetsTrackRetries.length > 1) {
            this._packetsTrackRetries[this._packetsTrackRetries.length - 1] = 0;
         }

         this.cancelPackets(reference);
      }
   }

   final void cancelPackets(int reference) {
      synchronized (this._packetsTrackTime) {
         if (this._packetsTrackTime.length > 1) {
            Arrays.fill(this._packetsTrackTime, Long.MAX_VALUE, 0, this._packetsTrackTime.length - 1);
         }

         if (this._packetsTrackRetries.length > 1) {
            Arrays.fill(
               this._packetsTrackRetries,
               -(this._config._maxAggressivePacketRetry + this._config._maxImmediatePacketRetry),
               0,
               this._packetsTrackRetries.length - 1
            );
         }

         this._minTimeout = this._timer = Long.MAX_VALUE;
         this._packetsTrackTime.notifyAll();
      }
   }

   private final void initPackets(int reference, int maxSequence) {
      synchronized (this._packetsTrackTime) {
         Array.resize(this._packetsTrackTime, maxSequence + 2);
         Array.resize(this._packetsTrackRetries, maxSequence + 2);
         Arrays.fill(this._packetsTrackTime, Long.MAX_VALUE, 0, this._packetsTrackTime.length - 1);
         Arrays.fill(
            this._packetsTrackRetries,
            -(this._config._maxAggressivePacketRetry + this._config._maxImmediatePacketRetry),
            0,
            this._packetsTrackRetries.length - 1
         );
         this._packetsTrackTime[this._packetsTrackTime.length - 1] = Long.MAX_VALUE;
         this._packetsTrackRetries[this._packetsTrackRetries.length - 1] = 0;
         this._minTimeout = this._timer = Long.MAX_VALUE;
      }
   }

   final boolean datagramTimerRunning(int reference) {
      synchronized (this._packetsTrackTime) {
         return this._packetsTrackTime.length > 1 ? this._packetsTrackTime[this._packetsTrackTime.length - 1] == Long.MAX_VALUE : false;
      }
   }

   final boolean packetTimerRunning(int reference, int sequence) {
      synchronized (this._packetsTrackTime) {
         if (sequence == -1) {
            for (int i = this._packetsTrackTime.length - 1; i >= 0; i--) {
               if (this._packetsTrackTime[i] <= this._config._maxPacketTimeout) {
                  return true;
               }
            }
         } else if (sequence >= 0 && sequence < this._packetsTrackTime.length - 1) {
            return this._packetsTrackTime[sequence] == Long.MAX_VALUE;
         }

         return false;
      }
   }
}
