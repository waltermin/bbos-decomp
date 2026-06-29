package net.rim.device.apps.internal.bis.launch.ui;

import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.container.DialogFieldManager;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.PopupScreen;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.internal.ui.component.AnimatedBitmapField;

public final class BusyDialog extends PopupScreen {
   RichTextField _messageField = new RichTextField(36028848558571520L);

   public BusyDialog() {
      super(new DialogFieldManager());
      DialogFieldManager dfm = (DialogFieldManager)this.getDelegate();
      EncodedImage hourglassImage = ThemeManager.getActiveTheme().getImage("osicon_hourglass");
      AnimatedBitmapField hourGlassIcon = new AnimatedBitmapField(hourglassImage, 1000, 0);
      HorizontalFieldManager hfm = new HorizontalFieldManager();
      hfm.add(hourGlassIcon);
      hfm.add(this._messageField);
      dfm.addCustomField(hfm);
   }

   public final void setMessage(String message) {
      this._messageField.setText(message);
   }
}
