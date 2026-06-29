package net.rim.device.apps.internal.ribbon.launcher;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.i18n.ResourceBundleFamily;
import net.rim.device.apps.api.framework.verb.Verb;

final class OrganizeApplications$OrganizeApplicationsVerb extends Verb {
   private ResourceBundleFamily _resources;
   private int _type;
   private final OrganizeApplications this$0;

   OrganizeApplications$OrganizeApplicationsVerb(OrganizeApplications _1, int order, int type) {
      super(order);
      this.this$0 = _1;
      this._resources = ResourceBundle.getBundle(1137270090621229274L, "net.rim.device.apps.internal.resource.Ribbon");
      this._type = type;
   }

   @Override
   public final String toString() {
      String string = this._resources.getString(this._type);
      if (this._type == 31 && !this.this$0._selectedApplication.isVisible()) {
         string = this._resources.getString(152);
      }

      return string;
   }

   @Override
   public final Object invoke(Object obj) {
      switch (this._type) {
         case 21:
            this.this$0.invokeMoveAction();
            return null;
         case 22:
            if (this.this$0._iconArea.moveApplicationInProgress()) {
               this.this$0._iconArea.completeMoveApplication(false);
               return null;
            }
            break;
         case 23:
            if (this.this$0._iconArea.moveApplicationInProgress()) {
               this.this$0._iconArea.completeMoveApplication(true);
               this.this$0._ribbonOptions.commit();
            }

            OrganizeApplications.access$600(this.this$0).invalidate();
            return null;
         case 31:
            boolean newState = !this.this$0._selectedApplication.isVisible();
            this.this$0._hierarchyManager.showApplication(this.this$0._selectedApplication, newState);
            this.this$0._iconArea.loadApplications();
            return null;
         case 151:
            this.this$0.discard();
            OrganizeApplications.access$700(this.this$0).close();
      }

      return null;
   }
}
