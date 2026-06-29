package net.rim.device.internal.io.file;

import net.rim.vm.WeakReference;

class FilteredListenerRec {
   private WeakReference _listener;
   private int _mediaTypeMask;

   FilteredListenerRec(Object listener) {
      this._listener = new WeakReference(listener);
   }

   public Object getListener() {
      return this._listener.get();
   }

   public boolean isListeningFor(int mediaType) {
      return (1 << mediaType & this._mediaTypeMask) != 0;
   }

   public void setMediaTypeMask(int mediaTypeMask) {
      this._mediaTypeMask = mediaTypeMask;
   }

   public int getMediaTypeMask() {
      return this._mediaTypeMask;
   }
}
