package net.rim.device.apps.internal.bis.api.ui;

import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.container.DialogFieldManager;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.PopupScreen;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.apps.internal.bis.ApplicationResources;
import net.rim.device.internal.ui.component.AnimatedBitmapField;

public final class BusyDialog extends PopupScreen {
   RichTextField _messageField;

   public BusyDialog() {
      super((Manager)(new Object()));
      String busyMessage = ApplicationResources.getString(152);
      this._messageField = (RichTextField)(new Object(busyMessage, 36028848558571520L));
      DialogFieldManager dfm = (DialogFieldManager)this.getDelegate();
      EncodedImage hourglassImage = ThemeManager.getActiveTheme().getImage("osicon_hourglass");
      AnimatedBitmapField hourGlassIcon = (AnimatedBitmapField)(new Object(hourglassImage, 1000, 0));
      HorizontalFieldManager hfm = (HorizontalFieldManager)(new Object());
      hfm.add(hourGlassIcon);
      hfm.add(this._messageField);
      dfm.addCustomField(hfm);
   }

   public final void reloadMessage() {
      String busyMessage = ApplicationResources.getString(152);
      this._messageField.setText(busyMessage);
   }
}
