package net.rim.device.apps.internal.bluetooth;

import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.media.control.AudioPathControl;
import net.rim.device.apps.api.framework.verb.Verb;

final class BluetoothA2DPManager$ConnectVerb extends Verb {
   private BluetoothDevice _deviceToConnect;
   private String _prompt;
   private AudioPathControl _control;
   private final BluetoothA2DPManager this$0;

   public BluetoothA2DPManager$ConnectVerb(BluetoothA2DPManager _1, BluetoothDevice device, AudioPathControl control) {
      super(1266752);
      this.this$0 = _1;
      this._deviceToConnect = device;
      this._control = control;
      Object[] args = new Object[]{device};
      this._prompt = MessageFormat.format(BluetoothMainScreen.getString(43), args);
   }

   @Override
   public final synchronized Object invoke(Object context) {
      if (this.this$0._state == 0) {
         this._deviceToConnect.connect(true);
         return null;
      }

      if (this.this$0._device == this._deviceToConnect) {
         try {
            this._control.setAudioPath(5);
            return null;
         } finally {
            return null;
         }
      } else {
         return null;
      }
   }

   @Override
   public final String toString() {
      return this._prompt;
   }
}
