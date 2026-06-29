package net.rim.wica.common.debug.session;

public final class ProtocolStateMachine {
   private int _curState = 0;
   public static final int STATE_ERROR = -1;
   public static final int STATE_INITIALIZED = 0;
   public static final int STATE_CONNECTED = 1;
   public static final int STATE_HANDSHAKE_SUCCESSFUL = 2;
   public static final int STATE_SESSION_INITIALIZED = 3;
   public static final int STATE_DETACHED = 4;

   public final void init() {
      this._curState = 0;
   }

   public final boolean waitForState(int state, int timeout) {
      long curTime = System.currentTimeMillis();

      do {
         synchronized (this) {
            if (this._curState > state) {
               this.setState(-1);
            }

            if (this._curState == state || this._curState == -1) {
               return this._curState == state;
            }

            if (this.waitForStateChange(timeout) == state) {
               return true;
            }
         }
      } while (timeout <= 0 || curTime + timeout > System.currentTimeMillis());

      return false;
   }

   public final synchronized int waitForStateChange(int timeout) {
      try {
         this.wait(timeout);
         return this._curState;
      } finally {
         ;
      }
   }

   public final synchronized void eventConnected() throws ProtocolStateMachine$InvalidStateException {
      if (this._curState == 0) {
         this.setState(1);
      } else {
         throw new ProtocolStateMachine$InvalidStateException(
            ((StringBuffer)(new Object("Connection event not valid from state: "))).append(this._curState).toString()
         );
      }
   }

   public final synchronized void eventHandshakeSucceeded() throws ProtocolStateMachine$InvalidStateException {
      if (this._curState == 1) {
         this.setState(2);
      } else {
         throw new ProtocolStateMachine$InvalidStateException(
            ((StringBuffer)(new Object("Handshake event not valid from state: "))).append(this._curState).toString()
         );
      }
   }

   public final synchronized void eventSessionReset() throws ProtocolStateMachine$InvalidStateException {
      switch (this._curState) {
         case 2:
            this.setState(3);
            return;
         default:
            throw new ProtocolStateMachine$InvalidStateException(
               ((StringBuffer)(new Object("Session initializatin not valid from state: "))).append(this._curState).toString()
            );
      }
   }

   public final synchronized void eventDetached() throws ProtocolStateMachine$InvalidStateException {
      switch (this._curState) {
         case 0:
            throw new ProtocolStateMachine$InvalidStateException(
               ((StringBuffer)(new Object("Session initializatin not valid from state: "))).append(this._curState).toString()
            );
         default:
            this.setState(4);
      }
   }

   public final synchronized void eventError() {
      this.setState(-1);
   }

   private final synchronized void setState(int newState) {
      this._curState = newState;
      this.notifyAll();
   }
}
