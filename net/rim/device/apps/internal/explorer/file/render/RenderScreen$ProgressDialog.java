package net.rim.device.apps.internal.explorer.file.render;

import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.container.DialogFieldManager;
import net.rim.device.api.ui.container.PopupScreen;
import net.rim.device.apps.internal.explorer.file.resource.ExplorerResources;

final class RenderScreen$ProgressDialog extends PopupScreen {
   private DialogFieldManager _dfm = (DialogFieldManager)this.getDelegate();

   public RenderScreen$ProgressDialog() {
      super((Manager)(new Object()), 0);
      RichTextField label = (RichTextField)(new Object(ExplorerResources.getString(97), 36028797018963968L));
      this._dfm.setMessage(label);
   }

   public final void show() {
      UiApplication.getUiApplication().pushScreen(this);
   }
}
