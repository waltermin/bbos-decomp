package net.rim.device.apps.internal.bluetooth;

import java.util.Vector;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.component.ListFieldCallback;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.container.DialogFieldManager;
import net.rim.device.api.ui.container.PopupScreen;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.internal.ui.component.ImageField;

final class BluetoothMainScreen$DeviceSelectionDialog extends PopupScreen implements ListFieldCallback {
   private Vector _devices;
   private ListField _listField;
   private int _selectedIndex;
   private final BluetoothMainScreen this$0;

   final void redraw() {
      this._listField.invalidate();
   }

   @Override
   public final int getPreferredWidth(ListField listField) {
      return listField.getPreferredWidth();
   }

   @Override
   public final Object get(ListField listField, int index) {
      return (BluetoothDevice)this._devices.elementAt(index);
   }

   @Override
   public final int indexOfList(ListField listField, String prefix, int start) {
      return -1;
   }

   @Override
   public final void drawListRow(ListField listField, Graphics graphics, int index, int y, int width) {
      ((BluetoothDevice)this._devices.elementAt(index)).paint(graphics, y, width);
   }

   private final void selectDeviceAndClose() {
      if (this.getLeafFieldWithFocus() == this._listField) {
         this._selectedIndex = this._listField.getSelectedIndex();
      }

      this.close();
   }

   @Override
   protected final boolean keyChar(char key, int status, int time) {
      switch (key) {
         case '\n':
         case ' ':
            this.selectDeviceAndClose();
            return true;
         case '\u001b':
            this.close();
            return true;
         default:
            return super.keyChar(key, status, time);
      }
   }

   @Override
   protected final boolean trackwheelClick(int status, int time) {
      super.trackwheelClick(status, time);
      this.selectDeviceAndClose();
      return true;
   }

   @Override
   protected final boolean invokeAction(int action) {
      boolean handled = super.invokeAction(action);
      if (!handled) {
         switch (action) {
            case 1:
               this.selectDeviceAndClose();
               return true;
         }
      }

      return handled;
   }

   BluetoothMainScreen$DeviceSelectionDialog(BluetoothMainScreen _1, Vector devices) {
      super((Manager)(new Object()));
      this.this$0 = _1;
      this._selectedIndex = -1;
      DialogFieldManager dfm = (DialogFieldManager)this.getDelegate();
      dfm.setMessage((RichTextField)(new Object(BluetoothMainScreen.getString(6), 36028797018963968L)));
      ImageField imageField = (ImageField)(new Object());
      imageField.setImage(_1._btManager.getDialogImage());
      dfm.setIcon(imageField);
      this._listField = (ListField)(new Object());
      this._listField.setCallback(this);
      dfm.addCustomField(this._listField);
      dfm.addCustomField((Field)(new Object(5)));
      this._devices = devices;
      this._listField.setSize(devices.size());
      dfm.addCustomField((Field)(new Object(CommonResources.getString(9042), 12884901888L)));
   }

   @Override
   public final void close() {
      if (this._selectedIndex != -1) {
         this.this$0._btManager.pairDevice((BluetoothDevice)this._devices.elementAt(this._selectedIndex));
      }

      this.this$0._btManager.cancelNameLookups();
      super.close();
   }
}
