package net.rim.device.api.crypto.tls;

import net.rim.device.apps.api.options.OptionsItemVerb;

class DeviceTLSOptionsProvider$AddHostVerb extends OptionsItemVerb {
   private final DeviceTLSOptionsProvider this$0;

   DeviceTLSOptionsProvider$AddHostVerb(DeviceTLSOptionsProvider _1) {
      super(DeviceTLSOptionsProvider._rb.getString(35), 1);
      this.this$0 = _1;
   }

   @Override
   public Object invoke(Object parameter) {
      String[] possibleLabels = this.this$0.getLabelsFromDataArray();
      HostDialog dialog = new HostDialog(possibleLabels);
      dialog.show();
      if (dialog.getCloseReason() == 1) {
         String hostName = dialog.getHostName();
         if (hostName.length() == 0) {
            return null;
         }

         int certificateIndex = dialog.getCertificateIndex();
         this.this$0.flagHostForAddition(hostName, certificateIndex);
         this.this$0._currentHosts.addElement(new DeviceTLSOptionsProvider$Host(hostName, possibleLabels[certificateIndex]));
         this.this$0._hostDefaultsListField.setSize(this.this$0._currentHosts.size());
         this.this$0._vfm.setDirty(true);
      }

      return null;
   }
}
