package net.rim.device.cldc.io.srp;

import java.util.Vector;

final class SrpConnectionManager$SrpConnectionMapper$ServiceConnectionPair {
   String _service;
   Vector _connections;
   private final SrpConnectionManager$SrpConnectionMapper this$0;

   private SrpConnectionManager$SrpConnectionMapper$ServiceConnectionPair(SrpConnectionManager$SrpConnectionMapper _1) {
      this.this$0 = _1;
      this._connections = new Vector(3);
   }

   SrpConnectionManager$SrpConnectionMapper$ServiceConnectionPair(SrpConnectionManager$SrpConnectionMapper x0, SrpConnectionManager$1 x1) {
      this(x0);
   }
}
