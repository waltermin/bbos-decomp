package net.rim.wica.runtime.activation.internal;

import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.component.Dialog;
import net.rim.wica.runtime.management.AGInfo;
import net.rim.wica.runtime.resources.RuntimeResources;

class AGMainScreen$ActivateMenuItem extends MenuItem {
   private final AGMainScreen this$0;

   public AGMainScreen$ActivateMenuItem(AGMainScreen this$0) {
      super(RuntimeResources.getString(48), 1, 100);
      this.this$0 = this$0;
   }

   @Override
   public void run() {
      AGInfo serverInfo = this.this$0.getActiveServerInfo();
      if (!((ActivationServiceImpl)this.this$0._activationService).disallowUserInitiatedActivation()) {
         String url = this.this$0.getActivationUrl();
         if (((ActivationServiceImpl)this.this$0._activationService).getState() == 1
            && !url.equals(serverInfo.getAgRegURL())
            && Dialog.ask(3, RuntimeResources.getString(83)) == -1) {
            return;
         }

         ServiceRecord transportRecord = this.this$0._records[this.this$0._transportChoiceField.getSelectedIndex()];
         serverInfo = new AGInfo();
         serverInfo.setAGCompactMsgURL(url == null ? "" : AGInfo.createAGCompactMsgURL(url));
         serverInfo.setAGRegURL(url == null ? "" : url);
         serverInfo.setIPPP_UID(transportRecord.getUid());
      }

      ((ActivationServiceImpl)this.this$0._activationService).activate(serverInfo);
   }
}
