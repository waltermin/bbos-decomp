package net.rim.device.apps.internal.bis.api.ui;

import net.rim.device.api.ui.Screen;

public final class ApplicationController$Forward {
   private int _viewID;
   private Screen _view;

   public ApplicationController$Forward(int viewID) {
      this._viewID = viewID;
   }

   public final Screen getView() {
      if (this._view == null) {
         try {
            this._view = ApplicationController.getInstance().getLinkView(this._viewID);
         } finally {
            return this._view;
         }
      }

      return this._view;
   }
}
