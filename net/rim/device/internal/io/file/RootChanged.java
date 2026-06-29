package net.rim.device.internal.io.file;

import javax.microedition.io.file.FileSystemListener;

final class RootChanged implements Runnable {
   private FileSystemListener _listener;
   private String _rootName;
   private int _state;

   public RootChanged(FileSystemListener listener, int state, String rootName) {
      this._listener = listener;
      this._state = state;
      this._rootName = rootName;
   }

   @Override
   public final void run() {
      this._listener.rootChanged(this._state, this._rootName);
   }
}
