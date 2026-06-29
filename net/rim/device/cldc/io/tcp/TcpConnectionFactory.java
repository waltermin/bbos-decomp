package net.rim.device.cldc.io.tcp;

import net.rim.device.api.lowmemory.LowMemoryListener;
import net.rim.device.api.lowmemory.LowMemoryManager;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.internal.io.tcp.TcpConstants;
import net.rim.device.internal.io.tcp.TcpTimerInterface;

final class TcpConnectionFactory implements LowMemoryListener, TcpConstants, TcpTimerInterface {
   private Protocol[] _cachedConnections = new Protocol[2];
   private int _cacheSize;
   private static TcpTimerThread _timers;
   private static long TCP_CONNECTION_FACTORY_ID = -1968152180982800519L;
   private static TcpConnectionFactory _instance;

   static final TcpConnectionFactory getInstance() {
      if (_instance == null) {
         ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
         _instance = (TcpConnectionFactory)ar.getOrWaitFor(TCP_CONNECTION_FACTORY_ID);
         if (_instance == null) {
            _instance = new TcpConnectionFactory();
            ar.put(TCP_CONNECTION_FACTORY_ID, _instance);
         }
      }

      return _instance;
   }

   private TcpConnectionFactory() {
      LowMemoryManager.addLowMemoryListener(this);
   }

   final Protocol getConnection(String name, int mode, boolean timeouts) {
      Protocol p;
      synchronized (this._cachedConnections) {
         if (this._cacheSize <= 0) {
            p = new Protocol();
         } else {
            this._cacheSize--;
            p = this._cachedConnections[this._cacheSize];
            this._cachedConnections[this._cacheSize] = null;
         }
      }

      p.init(name, mode, timeouts);
      return p;
   }

   final void addConnection(Protocol p) {
      if (_timers == null) {
         _timers = p._timers;
         _timers.addConnection(this);
      }

      synchronized (this._cachedConnections) {
         if (this._cacheSize < 2) {
            p.prepForCaching();
            this._cachedConnections[this._cacheSize++] = p;
            this.restartCacheTimer();
         }
      }
   }

   private final void restartCacheTimer() {
      if (_timers.isActive(this, 8)) {
         _timers.stopTimer(this, 8);
      }

      _timers.startTimer(this, 8);
   }

   private final void invoke() {
      synchronized (this._cachedConnections) {
         while (this._cacheSize > 1) {
            this._cacheSize--;
            this._cachedConnections[this._cacheSize] = null;
         }
      }
   }

   @Override
   public final void timerExpired(int timer) {
      switch (timer) {
         default:
            this.invoke();
      }
   }

   @Override
   public final boolean freeStaleObject(int priority) {
      synchronized (this._cachedConnections) {
         if (this._cacheSize > 0) {
            this._cacheSize--;
            LowMemoryManager.markAsRecoverable(this._cachedConnections[this._cacheSize]);
            this._cachedConnections[this._cacheSize] = null;
            return true;
         } else {
            return false;
         }
      }
   }
}
