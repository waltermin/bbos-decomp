package net.rim.device.apps.internal.ribbon.skin.svg.manager;

import java.util.Hashtable;
import net.rim.device.apps.api.ribbon.RibbonComponent;
import net.rim.device.apps.internal.ribbon.indicators.UnreadCountComponent;
import net.rim.plazmic.internal.mediaengine.model.intarray.v1_2.ModelInteractorImpl;
import net.rim.plazmic.internal.mediaengine.service.node.TextNode;

class SMSMMSUnreadCountComponentInteractor extends UnreadCountComponentInteractor {
   private boolean _displayNewSMSMMSIndicator;
   private UnreadCountComponent _ucComponent;
   protected ModelInteractorImpl _modelInteractor;
   private static final String NEW_SMSMMS = "newSMSMMS";
   private static final String NO_NEW_SMSMMS = "noNewSMSMMS";

   SMSMMSUnreadCountComponentInteractor(TextNode node, Hashtable params, ModelInteractorImpl mi) {
      super(node, params);
      this._modelInteractor = mi;
      this._ucComponent = (UnreadCountComponent)super._component;
      this.displayNewSMSMMSIndicatorChanged(this._ucComponent.hasNewStatus());
   }

   @Override
   public void ribbonComponentChanged(RibbonComponent component) {
      boolean newStatus;
      if (this._ucComponent != null) {
         newStatus = this._ucComponent.hasNewStatus();
      } else {
         newStatus = ((UnreadCountComponent)super._component).hasNewStatus();
      }

      if (this._displayNewSMSMMSIndicator != newStatus) {
         this.displayNewSMSMMSIndicatorChanged(newStatus);
      }

      super.ribbonComponentChanged(component);
   }

   private void displayNewSMSMMSIndicatorChanged(boolean newValue) {
      this._displayNewSMSMMSIndicator = newValue;
      if (this._modelInteractor != null) {
         if (newValue) {
            this._modelInteractor.trigger(107, this._modelInteractor.getHandle("newSMSMMS"), null);
         } else {
            this._modelInteractor.trigger(107, this._modelInteractor.getHandle("noNewSMSMMS"), null);
         }
      }
   }
}
