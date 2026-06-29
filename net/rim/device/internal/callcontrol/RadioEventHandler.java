package net.rim.device.internal.callcontrol;

import net.rim.device.api.system.Application;
import net.rim.device.api.system.Audio;
import net.rim.device.internal.system.AudioInternal;
import net.rim.device.internal.system.AudioInternalListener;
import net.rim.device.internal.system.EventDispatchManager;

final class RadioEventHandler extends CallEventHandler implements AudioInternalListener {
   private byte[] _dtmfBuffer = new byte[16];

   public RadioEventHandler() {
      super(10);
      AbstractCallEventHandler.internalRegister(this);
      EventDispatchManager.getInstance().setDispatcher(35, new RadioEventDispatcher());
   }

   public final void startListening(Application app) {
      app.addListener(35, this);
      Audio.addListener(app, this);
   }

   @Override
   public final void recordStreamDone(int handle, int headerLength) {
   }

   @Override
   public final void recordStreamFail(int handle) {
   }

   @Override
   public final void responseAVCModeChange(boolean success, int mode) {
   }

   @Override
   public final void micStatusChange(boolean micEnabled) {
   }

   @Override
   public final void dtmfDataAvailable() {
      int count = AudioInternal.dtmfRead(this._dtmfBuffer);

      for (int idx = 0; idx < count; idx++) {
         this.dtmfData(this._dtmfBuffer[idx]);
      }
   }

   @Override
   public final void dtmfDataBufferFull() {
      this.dtmfDataAvailable();
   }
}
