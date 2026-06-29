package net.rim.device.cldc.io.btl2cap;

import com.sun.cldc.io.ConnectionBaseInterface;
import javax.microedition.io.Connection;
import net.rim.device.cldc.io.btspp.BluetoothURL;
import net.rim.device.cldc.io.utility.EventThreadCheck;
import net.rim.device.internal.bluetooth.BluetoothDeviceManager;

public class Protocol implements ConnectionBaseInterface {
   @Override
   public int getProperties(String name) {
      return 64;
   }

   @Override
   public Connection openPrim(String name, int mode, boolean timeouts) {
      EventThreadCheck.throwException();
      if (!BluetoothDeviceManager.getInstance().isRadioOnPromptIfOff(false)) {
         throw new Object("Radio is off");
      }

      BluetoothURL url = new BluetoothURL("btl2cap", name);
      byte[] address = url.getAddress();
      if (address == null) {
         return new L2CAPConnectionNotifierImpl(url);
      }

      L2CAPConnectionImpl conn = new L2CAPConnectionImpl(url);
      conn.connect();
      return conn;
   }
}
