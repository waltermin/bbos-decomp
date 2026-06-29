package net.rim.device.api.crypto.tls;

import net.rim.device.apps.api.options.OptionsItemVerb;

class DeviceTLSOptionsProvider$EditHostVerb extends OptionsItemVerb {
   int _item;
   private final DeviceTLSOptionsProvider this$0;

   DeviceTLSOptionsProvider$EditHostVerb(DeviceTLSOptionsProvider _1, int item) {
      super(DeviceTLSOptionsProvider._rb.getString(50), 1);
      this.this$0 = _1;
      this._item = item;
   }

   @Override
   public Object invoke(Object parameter) {
      DeviceTLSOptionsProvider$Host host = (DeviceTLSOptionsProvider$Host)this.this$0._currentHosts.elementAt(this._item);
      String[] possibleLabels = this.this$0.getLabelsFromDataArray();
      HostDialog dialog = new HostDialog(host._hostName, possibleLabels, host._hostCertificate);
      dialog.show();
      if (dialog.getCloseReason() == 1) {
         this.this$0.flagHostForRemoval(host._hostName);
         String hostName = dialog.getHostName();
         if (hostName.length() == 0) {
            return null;
         }

         int certificateIndex = dialog.getCertificateIndex();
         this.this$0.flagHostForAddition(hostName, certificateIndex);
         this.this$0._currentHosts.setElementAt(new DeviceTLSOptionsProvider$Host(hostName, possibleLabels[certificateIndex]), this._item);
         this.this$0._hostDefaultsListField.invalidate(this._item);
         this.this$0._vfm.setDirty(true);
      }

      return null;
   }
}
