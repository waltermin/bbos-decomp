package net.rim.device.cldc.io.btspp;

import com.sun.cldc.io.ConnectionBaseInterface;
import java.io.IOException;
import javax.microedition.io.Connection;
import net.rim.device.cldc.io.utility.EventThreadCheck;
import net.rim.device.internal.bluetooth.BluetoothDeviceManager;

public class Protocol implements ConnectionBaseInterface {
   static final int MAX_PACKET_SIZE = 2048;

   @Override
   public int getProperties(String name) {
      return 64;
   }

   @Override
   public Connection openPrim(String name, int mode, boolean timeouts) throws IOException {
      EventThreadCheck.throwException();
      if (!BluetoothDeviceManager.getInstance().isRadioOnPromptIfOff(false)) {
         throw new IOException("Radio is off");
      }

      BluetoothURL url = new BluetoothURL("btspp", name);
      byte[] address = url.getAddress();
      return address == null ? new BluetoothServerSocketConnection(url) : new BluetoothSerialConnection(address, url.getChannel(), 2048, false);
   }
}
