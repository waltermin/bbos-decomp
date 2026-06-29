package net.rim.device.apps.internal.elt;

import net.rim.device.api.system.Application;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.apps.api.ui.BooleanChoiceField;

final class LocationServicesOptionsProviderImpl$1 implements Runnable {
   private final ETManager val$manager;
   private final BooleanChoiceField val$locationTrackingState;
   private final LocationServicesOptionsProviderImpl this$0;

   LocationServicesOptionsProviderImpl$1(LocationServicesOptionsProviderImpl _1, ETManager _2, BooleanChoiceField _3) {
      this.this$0 = _1;
      this.val$manager = _2;
      this.val$locationTrackingState = _3;
   }

   @Override
   public final void run() {
      synchronized (Application.getEventLock()) {
         boolean enabled = this.val$manager.isEnabledByITPolicy();
         boolean userEnabled = false;
         if (enabled) {
            LocationServicesOptionsProviderImpl$DefaultNoDialog ask = new LocationServicesOptionsProviderImpl$DefaultNoDialog();
            if (ask.doModal() == 4) {
               userEnabled = true;
            }
         } else {
            Dialog.inform(LocationServicesOptionsProviderImpl._lbsBundle.getString(2));
         }

         if (this.val$locationTrackingState != null && this.val$manager.isEnabledByITPolicy()) {
            this.val$locationTrackingState.setAffirmative(userEnabled);
         }

         if (userEnabled) {
            this.val$manager.enableTracking();
         } else {
            this.val$manager.disableTracking();
         }

         ITPolicyListener.getInstance().setITPolicyChanged(false);
         if (!enabled) {
            System.exit(0);
         }
      }
   }
}
