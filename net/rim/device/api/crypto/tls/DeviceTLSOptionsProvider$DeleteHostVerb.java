package net.rim.device.api.crypto.tls;

import net.rim.device.apps.api.options.OptionsItemVerb;
import net.rim.device.internal.ui.component.SimpleChoiceDialog;

class DeviceTLSOptionsProvider$DeleteHostVerb extends OptionsItemVerb {
   int _item;
   private final DeviceTLSOptionsProvider this$0;

   DeviceTLSOptionsProvider$DeleteHostVerb(DeviceTLSOptionsProvider _1, int item) {
      super(DeviceTLSOptionsProvider._rb.getString(34), 1);
      this.this$0 = _1;
      this._item = item;
   }

   @Override
   public Object invoke(Object parameter) {
      DeviceTLSOptionsProvider$Host host = (DeviceTLSOptionsProvider$Host)this.this$0._currentHosts.elementAt(this._item);
      String hostName = host._hostName;
      if (!SimpleChoiceDialog.askYesNoQuestion(DeviceTLSOptionsProvider._rb.getString(41), hostName)) {
         return null;
      }

      this.this$0.flagHostForRemoval(hostName);
      this.this$0._currentHosts.removeElementAt(this._item);
      this.this$0._hostDefaultsListField.delete(this._item);
      this.this$0._vfm.setDirty(true);
      return null;
   }
}
