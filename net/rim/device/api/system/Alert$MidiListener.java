package net.rim.device.api.system;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import net.rim.device.api.util.Arrays;

class Alert$MidiListener implements AlertListener2 {
   private Reference[] _midiListeners = new Reference[0];
   public static final long GUID = 2808165152854904955L;

   private Alert$MidiListener() {
   }

   private synchronized int midiStart(AlertListener2 listener, int ret) {
      Arrays.add(this._midiListeners, listener == null ? null : new WeakReference(listener));
      return ret;
   }

   private synchronized boolean isStopped() {
      return this._midiListeners.length == 0;
   }

   @Override
   public void midiDone(int reason) {
      Reference _midiListener;
      synchronized (this) {
         boolean isEmpty = this.isStopped();
         _midiListener = isEmpty ? null : this._midiListeners[0];
         if (!isEmpty) {
            Arrays.removeAt(this._midiListeners, 0);
         }
      }

      AlertListener2 listener = _midiListener == null ? null : (AlertListener2)_midiListener.get();
      if (listener != null) {
         listener.midiDone(reason);
      }
   }

   @Override
   public void audioDone(int reason) {
   }

   @Override
   public void buzzerDone(int reason) {
   }

   @Override
   public void vibrateDone(int reason) {
   }

   Alert$MidiListener(Alert$1 x0) {
      this();
   }
}
