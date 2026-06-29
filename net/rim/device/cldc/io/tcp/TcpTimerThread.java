package net.rim.device.cldc.io.tcp;

import java.util.Vector;
import net.rim.device.internal.io.tcp.TcpConstants;
import net.rim.device.internal.io.tcp.TcpTimerInterface;
import net.rim.device.internal.io.tcp.TcpTimers;

final class TcpTimerThread extends Thread implements TcpConstants {
   private Vector _timers = (Vector)(new Object());
   private Vector _expired = (Vector)(new Object());
   private boolean _dieNow;
   private Object _timerSync = new Object();
   private long _currTime;
   private int _currIterate;
   private int _returnTimer;

   final void addConnection(TcpTimerInterface conn) {
      synchronized (this._timerSync) {
         if (this.getTimer(conn) != null) {
            System.out.println("Connection already exists");
         } else {
            TcpTimers newTimers = new TcpTimers(conn);
            this._timers.addElement(newTimers);
         }
      }
   }

   final void removeConnection(TcpTimerInterface conn) {
      synchronized (this._timerSync) {
         int index = this.locateTimer(conn);
         if (index >= 0) {
            this._timers.removeElementAt(index);
         }
      }
   }

   final boolean isActive(TcpTimerInterface conn, int timer) {
      synchronized (this._timerSync) {
         TcpTimers thisTimer = this.getTimer(conn);
         return thisTimer == null ? false : thisTimer._active[timer];
      }
   }

   final boolean isActiveOrRunning(TcpTimerInterface conn, int timer) {
      synchronized (this._timerSync) {
         TcpTimers thisTimer = this.getTimer(conn);
         return thisTimer == null ? false : thisTimer._active[timer] || thisTimer._running[timer];
      }
   }

   final void startTimer(TcpTimerInterface conn, int timer) {
      this.startTimer(conn, timer, TcpConstants.TIMER_DEFAULT[timer]);
   }

   final void startTimer(TcpTimerInterface conn, int timer, int timeout) {
      synchronized (this._timerSync) {
         TcpTimers thisTimer = this.getTimer(conn);
         if (thisTimer == null) {
            throw new Object();
         }

         thisTimer._active[timer] = true;
         thisTimer._finish[timer] = System.currentTimeMillis() + timeout;
         this._timerSync.notifyAll();
      }
   }

   final void stopTimer(TcpTimerInterface conn, int timer) {
      synchronized (this._timerSync) {
         TcpTimers thisTimer = this.getTimer(conn);
         if (thisTimer == null) {
            throw new Object();
         }

         thisTimer._active[timer] = false;
      }
   }

   final void stopAll(TcpTimerInterface conn) {
      synchronized (this._timerSync) {
         TcpTimers thisTimer = this.getTimer(conn);
         if (thisTimer != null) {
            for (int ii = 0; ii < 10; ii++) {
               thisTimer._active[ii] = false;
            }
         }
      }
   }

   final TcpTimers getTimers(TcpTimerInterface conn) {
      return this.getTimer(conn);
   }

   private final TcpTimers getTimer(TcpTimerInterface conn) {
      int ii = 0;
      if (conn != null) {
         while (ii < this._timers.size()) {
            TcpTimers thisTimer = (TcpTimers)this._timers.elementAt(ii);
            TcpTimerInterface curr = thisTimer.getConn();
            if (curr == null) {
               this._timers.removeElementAt(ii);
            } else {
               if (curr == conn) {
                  this._returnTimer = ii;
                  return thisTimer;
               }

               ii++;
            }
         }
      }

      return null;
   }

   private final int locateTimer(TcpTimerInterface conn) {
      return this.getTimer(conn) == null ? -1 : this._returnTimer;
   }

   private final TcpTimers startIterate() {
      this._currIterate = 0;
      return this.nextIterate();
   }

   private final TcpTimers nextIterate() {
      while (this._currIterate < this._timers.size()) {
         TcpTimers thisTimer = (TcpTimers)this._timers.elementAt(this._currIterate);
         if (thisTimer.getConn() != null) {
            this._currIterate++;
            return thisTimer;
         }

         this._timers.removeElementAt(this._currIterate);
      }

      return null;
   }

   private final boolean findExpired() {
      int numExpired = 0;

      for (TcpTimers thisTimer = this.startIterate(); thisTimer != null; thisTimer = this.nextIterate()) {
         for (int ii = 0; ii < 10; ii++) {
            if (thisTimer._active[ii] && thisTimer._finish[ii] - this._currTime <= 5) {
               thisTimer._active[ii] = false;
               thisTimer._running[ii] = true;
               this._expired.addElement(new TcpTimerThread$ExpiredInfo(thisTimer.getConn(), ii));
               numExpired++;
            }
         }
      }

      return numExpired > 0;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final void runExpired() {
      try {
         while (this._expired.size() > 0) {
            TcpTimerThread$ExpiredInfo info = (TcpTimerThread$ExpiredInfo)this._expired.elementAt(0);
            TcpTimers thisTimer = this.getTimer(info._connection);
            if (thisTimer != null) {
               boolean var8 = false /* VF: Semaphore variable */;

               try {
                  var8 = true;
                  info._connection.timerExpired(info._timer);
                  var8 = false;
               } finally {
                  if (var8) {
                     thisTimer._running[info._timer] = false;
                  }
               }

               thisTimer._running[info._timer] = false;
            }

            this._expired.removeElementAt(0);
         }
      } finally {
         return;
      }
   }

   private final long findNextExpiry(long initial) {
      for (TcpTimers thisTimer = this.startIterate(); thisTimer != null; thisTimer = this.nextIterate()) {
         for (int ii = 0; ii < 10; ii++) {
            if (thisTimer._active[ii] && thisTimer._finish[ii] < initial) {
               initial = thisTimer._finish[ii];
            }
         }
      }

      return initial;
   }

   final void stopThread() {
      this._dieNow = true;
   }

   @Override
   public final void run() {
      while (!this._dieNow) {
         boolean anyExpired = false;
         synchronized (this._timerSync) {
            long minTime = this.findNextExpiry(Long.MAX_VALUE);
            long minWait;
            if (minTime < Long.MAX_VALUE) {
               minWait = minTime - System.currentTimeMillis();
               if (minWait <= 0) {
                  minWait = -1;
               }
            } else {
               minWait = 0;
            }

            if (minWait >= 0) {
               label57:
               try {
                  this._timerSync.wait(minWait);
               } finally {
                  break label57;
               }
            }

            this._currTime = System.currentTimeMillis();
            anyExpired = this.findExpired();
         }

         if (this._dieNow) {
            return;
         }

         if (anyExpired) {
            this.runExpired();
         }
      }
   }
}
