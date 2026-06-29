package net.rim.device.api.system;

import net.rim.device.internal.system.AudioInternalListener;
import net.rim.device.internal.system.AudioTTYListener;
import net.rim.device.internal.system.EventDispatcher;
import net.rim.device.internal.system.InternalServices;
import net.rim.vm.Message;

final class AudioEventDispatcher extends EventDispatcher {
   private boolean _hasMultibuttonHeadset = InternalServices.isSoftwareCapable(14);

   @Override
   public final void dispatch(Message message, Object listener) {
      int event = message.getEvent();
      int subMessage = message.getSubMessage();
      int data0 = message.getData0();
      int data1 = message.getData1();
      if (listener instanceof AudioInternalListener) {
         AudioInternalListener aiListener = (AudioInternalListener)listener;
         switch (event) {
            case 5378:
               aiListener.responseAVCModeChange(subMessage != 0, data0);
               break;
            case 5386:
               aiListener.dtmfDataAvailable();
               break;
            case 5388:
               aiListener.micStatusChange(true);
               break;
            case 5389:
               aiListener.micStatusChange(false);
               break;
            case 5391:
               aiListener.dtmfDataBufferFull();
               break;
            case 6148:
               aiListener.recordStreamDone(subMessage, data0);
               break;
            case 6150:
               aiListener.recordStreamFail(subMessage);
         }
      }

      if (listener instanceof AudioHeadsetListener) {
         AudioHeadsetListener headsetListener = (AudioHeadsetListener)listener;
         switch (event) {
            case 2176:
               headsetListener.headsetButtonClick(this._hasMultibuttonHeadset ? data0 : 0, data1);
               break;
            case 2184:
               headsetListener.headsetButtonUnclick(this._hasMultibuttonHeadset ? data0 : 0, data1);
               break;
            case 2192:
               headsetListener.headsetInserted(subMessage);
               break;
            case 2193:
               headsetListener.headsetRemoved();
         }
      }

      if (listener instanceof AudioTTYListener) {
         AudioTTYListener ttyListener = (AudioTTYListener)listener;
         switch (event) {
            case 5377:
               ttyListener.responseTTYModeChange(subMessage != 0, data0);
               return;
            case 5383:
               ttyListener.responseHACModeChange(true, subMessage != 0);
               return;
            case 5384:
               ttyListener.ttyDataAvailable();
               return;
            case 5385:
               ttyListener.ttyDataReady();
               return;
            case 5387:
               ttyListener.ttyStatusUpdate(data0 != 0);
               return;
            case 5390:
               ttyListener.ttyReadBufferFull();
         }
      }
   }
}
