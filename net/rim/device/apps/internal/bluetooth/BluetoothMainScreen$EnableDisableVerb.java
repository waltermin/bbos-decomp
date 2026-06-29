package net.rim.device.apps.internal.bluetooth;

import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.ui.SecurityDialog;

final class BluetoothMainScreen$EnableDisableVerb extends Verb {
   private boolean _enable;
   private final BluetoothMainScreen this$0;

   BluetoothMainScreen$EnableDisableVerb(BluetoothMainScreen _1, boolean enable) {
      super(1441792, BluetoothMainScreen._rb, enable ? 17 : 18);
      this.this$0 = _1;
      this._enable = enable;
   }

   @Override
   public final Object invoke(Object context) {
      if (this._enable) {
         this.this$0._turnPowerOffOnExit = false;
         if (ITPolicy.getBoolean(34, 11, false) && !SecurityDialog.challengeUser(BluetoothMainScreen.getString(59), false, true, '\u0000', true)) {
            this._enable = false;
            return null;
         }
      }

      this.this$0._userRequestedPowerChange = true;
      this.this$0._btManager.setPowerOn(this._enable);
      return null;
   }
}
