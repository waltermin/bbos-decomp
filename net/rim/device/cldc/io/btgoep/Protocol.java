package net.rim.device.cldc.io.btgoep;

import com.sun.cldc.io.ConnectionBaseInterface;
import javax.microedition.io.Connection;
import net.rim.device.cldc.io.btspp.BluetoothURL;
import net.rim.device.cldc.io.utility.EventThreadCheck;
import net.rim.device.internal.bluetooth.BluetoothDeviceManager;

public class Protocol implements ConnectionBaseInterface {
   static final int CONNECT;
   static final int DISCONNECT;
   static final int PUT;
   static final int PUT_FINAL;
   static final int GET;
   static final int GET_FINAL;
   static final int SET_PATH;
   static final int ABORT;
   static final int MAX_PACKET_SIZE;

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

      BluetoothURL url = new BluetoothURL("btgoep", name);
      byte[] address = url.getAddress();
      return address == null ? new BluetoothGOEPServerConnection(url) : new BluetoothGOEPClientConnection(url);
   }
}
