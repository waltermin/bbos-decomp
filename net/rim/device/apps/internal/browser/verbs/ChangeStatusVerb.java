package net.rim.device.apps.internal.browser.verbs;

import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.apps.internal.browser.page.PageModel;

public final class ChangeStatusVerb extends BrowserVerb {
   private PageModel _pageModel;
   private int _newStatus;
   private int _labelResId;

   public ChangeStatusVerb(PageModel pageModel, int newStatus, int labelResId) {
      super(1184000);
      this._pageModel = pageModel;
      this._newStatus = newStatus;
      this._labelResId = labelResId;
   }

   @Override
   public final String toString() {
      return CommonResources.getString(this._labelResId);
   }

   @Override
   public final Object invoke(Object context) {
      this._pageModel.changeStatus(this._newStatus);
      return null;
   }
}
