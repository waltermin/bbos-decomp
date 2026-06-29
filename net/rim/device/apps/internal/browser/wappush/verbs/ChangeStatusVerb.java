package net.rim.device.apps.internal.browser.wappush.verbs;

import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.browser.push.BrowserPushResources;
import net.rim.device.apps.internal.browser.wappush.WAPPushModel;

public final class ChangeStatusVerb extends Verb {
   private WAPPushModel _model;
   private int _newStatus;
   private int _labelResId;

   public ChangeStatusVerb(WAPPushModel pushModel, int newStatus, int labelResId) {
      super(602448);
      this._model = pushModel;
      this._newStatus = newStatus;
      this._labelResId = labelResId;
   }

   @Override
   public final String toString() {
      return BrowserPushResources.getString(this._labelResId);
   }

   @Override
   public final Object invoke(Object context) {
      this._model.changeStatus(this._newStatus);
      return null;
   }
}
