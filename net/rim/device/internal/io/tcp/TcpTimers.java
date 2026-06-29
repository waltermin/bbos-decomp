package net.rim.device.internal.io.tcp;

import net.rim.vm.WeakReference;

public final class TcpTimers implements TcpConstants {
   public boolean[] _active;
   public boolean[] _running;
   public long[] _finish;
   public WeakReference _connection;

   public TcpTimers(TcpTimerInterface conn) {
      this._connection = (WeakReference)(new Object(conn));
      this._active = new boolean[10];
      this._running = new boolean[10];
      this._finish = new long[10];
   }

   public final TcpTimerInterface getConn() {
      return (TcpTimerInterface)this._connection.get();
   }
}
