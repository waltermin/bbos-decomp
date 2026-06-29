package javax.bluetooth;

import net.rim.device.internal.system.SystemPropertyManager;
import net.rim.device.internal.system.SystemPropertyProvider;

class LocalDevice$BluetoothProperties implements SystemPropertyProvider {
   LocalDevice$BluetoothProperties() {
      SystemPropertyManager.getInstance().addProvider(this);
   }

   @Override
   public String getProperty(String property) {
      if (property.equals("bluetooth.api.version")) {
         return "1.1";
      } else if (property.equals("obex.api.version")) {
         return "1.1";
      } else if (property.equals("bluetooth.l2cap.receiveMTU.max")) {
         return "1024";
      } else if (property.equals("bluetooth.connected.devices.max")) {
         return "5";
      } else if (property.equals("bluetooth.connected.inquiry")) {
         return "true";
      } else if (property.equals("bluetooth.connected.page")) {
         return "true";
      } else if (property.equals("bluetooth.connected.inquiry.scan")) {
         return "true";
      } else if (property.equals("bluetooth.connected.page.scan")) {
         return "true";
      } else if (property.equals("bluetooth.master.switch")) {
         return "false";
      } else if (property.equals("bluetooth.sd.trans.max")) {
         return "1";
      } else {
         return property.equals("bluetooth.sd.attr.retrievable.max") ? "10" : null;
      }
   }
}
