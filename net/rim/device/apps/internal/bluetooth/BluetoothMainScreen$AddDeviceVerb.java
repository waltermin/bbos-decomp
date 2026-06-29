package net.rim.device.apps.internal.bluetooth;

import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.RadioButtonField;
import net.rim.device.api.ui.component.RadioButtonGroup;
import net.rim.device.api.ui.component.Status;
import net.rim.device.api.ui.container.DialogFieldManager;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.internal.ui.component.ImageField;

final class BluetoothMainScreen$AddDeviceVerb extends Verb {
   private final BluetoothMainScreen this$0;

   BluetoothMainScreen$AddDeviceVerb(BluetoothMainScreen _1) {
      super(1572865, BluetoothMainScreen._rb, 4);
      this.this$0 = _1;
   }

   @Override
   public final Object invoke(Object context) {
      if (ITPolicy.getBoolean(34, 2, false)) {
         String msg = BluetoothMainScreen.getString(45);
         Object[] args = new Object[]{msg};
         String prompt = MessageFormat.format(BluetoothMainScreen.getString(44), args);
         Status.show(prompt, this.this$0._btManager.getDialogImage(), 3000, 0, true, false, 0);
         return null;
      } else {
         Dialog dialog = (Dialog)(new Object(4, BluetoothMainScreen.getString(4), 0, null, 0));
         DialogFieldManager dfm = (DialogFieldManager)dialog.getDelegate();
         ImageField imageField = (ImageField)(new Object());
         imageField.setImage(this.this$0._btManager.getDialogImage());
         dfm.setIcon(imageField);
         Manager mm = dfm.getMiddleManager();
         RadioButtonGroup rbg = (RadioButtonGroup)(new Object());
         RadioButtonField rbf1 = (RadioButtonField)(new Object(BluetoothMainScreen.getString(98)));
         RadioButtonField rbf2 = (RadioButtonField)(new Object(BluetoothMainScreen.getString(99)));
         rbg.add(rbf1);
         rbg.add(rbf2);
         rbg.setSelectedIndex(0);
         mm.add(rbf1);
         mm.add(rbf2);
         dfm.getButtonField(0).setFocus();
         if (dialog.doModal() != 0) {
            return null;
         } else {
            boolean inquiry = rbg.getSelectedIndex() == 0;
            if (!inquiry && ITPolicy.getBoolean(34, 6, false)) {
               String msg = BluetoothMainScreen.getString(100);
               Object[] args = new Object[]{msg};
               String prompt = MessageFormat.format(BluetoothMainScreen.getString(44), args);
               Status.show(prompt, this.this$0._btManager.getDialogImage(), 3000, 0, true, false, 0);
               return null;
            } else {
               this.this$0._addDeviceDialog = new BluetoothMainScreen$AddDeviceDialog(this.this$0, inquiry);
               this.this$0._addDeviceDialog.start();
               return null;
            }
         }
      }
   }
}
