package net.rim.device.cldc.io.tcp;

import net.rim.device.internal.io.tcp.TcpTimerInterface;

final class TcpTimerThread$ExpiredInfo {
   TcpTimerInterface _connection;
   int _timer;

   TcpTimerThread$ExpiredInfo(TcpTimerInterface connection, int timer) {
      this._connection = connection;
      this._timer = timer;
   }
}
