package javax.microedition.midlet;

import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;

class MIDletMain$StreamConnectionNotifier_PushRegistryConnectionWrapper implements StreamConnectionNotifier {
   protected StreamConnectionNotifier _scn;
   protected StreamConnection _streamConnection;

   @Override
   public void close() {
      this._scn.close();
   }

   @Override
   public StreamConnection acceptAndOpen() {
      synchronized (this) {
         if (this._streamConnection != null) {
            StreamConnection sc = this._streamConnection;
            this._streamConnection = null;
            return sc;
         }
      }

      return this._scn.acceptAndOpen();
   }

   void pushBack(StreamConnection sc) {
      this._streamConnection = sc;
   }

   public MIDletMain$StreamConnectionNotifier_PushRegistryConnectionWrapper(StreamConnectionNotifier impl) {
      this._scn = impl;
   }
}
