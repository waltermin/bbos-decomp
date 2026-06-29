package net.rim.device.apps.internal.browser.core;

import net.rim.device.api.system.Application;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.apps.internal.browser.common.AbortListener;
import net.rim.device.apps.internal.browser.resources.BrowserResources;

public final class BrowserError extends Dialog implements Runnable, FieldChangeListener, AbortListener {
   private boolean _showDetails;
   private boolean _saveRequest;
   private ButtonField _details;
   private ButtonField _save;
   private ButtonField _ok;

   public final boolean saveRequest() {
      return this._saveRequest;
   }

   public final boolean showDetails() {
      return this._showDetails;
   }

   @Override
   public final void run() {
      this.doModal();
      synchronized (this) {
         this.notify();
      }
   }

   @Override
   public final void abort(Object abortContext) {
      synchronized (Application.getEventLock()) {
         this.close();
      }
   }

   @Override
   public final void fieldChanged(Field field, int context) {
      if (field != null) {
         if (field == this._details) {
            this._showDetails = true;
            this.select();
            return;
         }

         if (field == this._save) {
            this._saveRequest = true;
            this.select();
            return;
         }

         if (field == this._ok) {
            this.select();
         }
      }
   }

   public BrowserError(String message, boolean detailsAvailable, boolean saveAvailable) {
      super(message, null, null, 0, null);
      HorizontalFieldManager mgr = (HorizontalFieldManager)(new Object(12884901888L));
      this.add(mgr);
      this._ok = (ButtonField)(new Object(CommonResources.getString(117), 12884901888L));
      this._ok.setChangeListener(this);
      mgr.add(this._ok);
      if (detailsAvailable) {
         this._details = (ButtonField)(new Object(CommonResources.getString(9046), 12884901888L));
         this._details.setChangeListener(this);
         mgr.add(this._details);
      }

      if (saveAvailable) {
         this._save = (ButtonField)(new Object(BrowserResources.getString(112), 12884901888L));
         this._save.setChangeListener(this);
         mgr.add(this._save);
      }

      this.setEscapeEnabled(true);
   }
}
