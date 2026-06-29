package javax.microedition.midlet;

import javax.microedition.io.ServerSocketConnection;

final class MIDletMain$ServerSocketConnection_PushRegistryConnectionWrapper
   extends MIDletMain$StreamConnectionNotifier_PushRegistryConnectionWrapper
   implements ServerSocketConnection {
   public MIDletMain$ServerSocketConnection_PushRegistryConnectionWrapper(ServerSocketConnection impl) {
      super(impl);
   }

   @Override
   public final String getLocalAddress() {
      return ((ServerSocketConnection)super._scn).getLocalAddress();
   }

   @Override
   public final int getLocalPort() {
      return ((ServerSocketConnection)super._scn).getLocalPort();
   }
}
