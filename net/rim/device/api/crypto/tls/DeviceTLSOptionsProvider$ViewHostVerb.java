package net.rim.device.api.crypto.tls;

import net.rim.device.apps.api.options.OptionsItemVerb;

class DeviceTLSOptionsProvider$ViewHostVerb extends OptionsItemVerb {
   int _item;
   private final DeviceTLSOptionsProvider this$0;

   DeviceTLSOptionsProvider$ViewHostVerb(DeviceTLSOptionsProvider _1, int item) {
      super(DeviceTLSOptionsProvider._rb.getString(49), 1);
      this.this$0 = _1;
      this._item = item;
   }

   @Override
   public Object invoke(Object parameter) {
      DeviceTLSOptionsProvider$Host host = (DeviceTLSOptionsProvider$Host)this.this$0._currentHosts.elementAt(this._item);
      HostDialog dialog = new HostDialog(host._hostName, host._hostCertificate);
      dialog.show();
      return null;
   }
}
