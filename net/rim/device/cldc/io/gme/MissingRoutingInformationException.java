package net.rim.device.cldc.io.gme;

import java.io.IOException;
import javax.microedition.io.Datagram;

final class MissingRoutingInformationException extends IOException {
   private Thread _thread = null;
   private boolean _cancelled;
   private Datagram _dg;

   public MissingRoutingInformationException() {
      this._cancelled = false;
   }

   public MissingRoutingInformationException(String s) {
      super(s);
   }

   public final void setThreadAsCurrent() {
      this._thread = Thread.currentThread();
   }

   public final boolean isThreadAlive() {
      return this._thread != null ? this._thread.isAlive() : false;
   }

   public final void setCancelled(boolean f) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   public final boolean isCancelled() {
      return this._cancelled;
   }

   public final void setDatagram(Datagram dg) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   public final Datagram getDatagram() {
      return this._dg;
   }
}
