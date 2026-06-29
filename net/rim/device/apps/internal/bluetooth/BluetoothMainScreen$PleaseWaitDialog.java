package net.rim.device.apps.internal.bluetooth;

import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.container.DialogFieldManager;
import net.rim.device.api.ui.container.PopupScreen;
import net.rim.device.internal.ui.Image;
import net.rim.device.internal.ui.component.ImageField;

final class BluetoothMainScreen$PleaseWaitDialog extends PopupScreen {
   private final BluetoothMainScreen this$0;

   BluetoothMainScreen$PleaseWaitDialog(BluetoothMainScreen _1, int rc) {
      super((Manager)(new Object()), 0);
      this.this$0 = _1;
      DialogFieldManager dfm = (DialogFieldManager)this.getDelegate();
      RichTextField rtf = (RichTextField)(new Object(BluetoothMainScreen.getString(rc), 36028797018963968L));
      dfm.setMessage(rtf);
      Image image = _1._btManager.getDialogImage();
      ImageField ifield = (ImageField)(new Object());
      ifield.setImage(image);
      ifield.setPreferredSize(Display.getWidth() >> 2, Display.getHeight() >> 2);
      dfm.setIcon(ifield);
   }
}
