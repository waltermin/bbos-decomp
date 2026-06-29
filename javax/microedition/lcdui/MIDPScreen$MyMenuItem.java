package javax.microedition.lcdui;

import net.rim.device.api.ui.MenuItem;
import net.rim.device.internal.lcdui.Lcdui;

class MIDPScreen$MyMenuItem extends MenuItem {
   Object _cookie;
   private final MIDPScreen this$0;

   MIDPScreen$MyMenuItem(MIDPScreen _1, String label, Object cookie) {
      super(label, 0, 0);
      this.this$0 = _1;
      this._cookie = cookie;
   }

   @Override
   public void run() {
      if (this._cookie == null) {
         this.this$0.destroyMIDlet();
      } else {
         if (this._cookie instanceof MIDPScreen$CommandCookie) {
            Command command = ((MIDPScreen$CommandCookie)this._cookie)._cmd;
            Item commandOwner = ((MIDPScreen$CommandCookie)this._cookie)._owner;
            if (commandOwner != null) {
               commandOwner.getItemCommandListener().commandAction(command, commandOwner);
               return;
            }

            Lcdui.setCommandActionCallback(this.this$0._commandListener, command, this.this$0.getDisplayable());
         }
      }
   }
}
