package net.rim.device.apps.internal.ribbon.launcher;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.i18n.ResourceBundleFamily;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.apps.api.framework.verb.Verb;

final class OrganizeApplications$ResetThemeVerb extends Verb {
   private ResourceBundleFamily _resources;
   private final OrganizeApplications this$0;

   OrganizeApplications$ResetThemeVerb(OrganizeApplications _1, int order) {
      super(order);
      this.this$0 = _1;
      this._resources = ResourceBundle.getBundle(1137270090621229274L, "net.rim.device.apps.internal.resource.Ribbon");
   }

   @Override
   public final String toString() {
      return this._resources.getString(146);
   }

   @Override
   public final Object invoke(Object obj) {
      int result = Dialog.ask(3, this._resources.getString(146) + "?\n" + this._resources.getString(153));
      if (result == 4) {
         this.this$0._hierarchyManager.resetActiveHierarchy();
         OrganizeApplications.access$100(this.this$0).invalidate();
      }

      return null;
   }
}
