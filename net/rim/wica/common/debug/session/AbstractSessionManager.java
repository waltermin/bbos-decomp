package net.rim.wica.common.debug.session;

import net.rim.wica.common.debug.io.IInputByteStreamAdapter;
import net.rim.wica.common.debug.io.IOutputByteStreamAdapter;

public class AbstractSessionManager {
   protected boolean _initHasBeenCalled;
   protected IInputByteStreamAdapter _inStream;
   protected IOutputByteStreamAdapter _outStream;
   protected ProtocolStateMachine _fsm;

   protected final void checkInitHasBeenCalled() {
      if (!this._initHasBeenCalled) {
         throw new IllegalStateException("Can't start debugging before calling init");
      }
   }
}
