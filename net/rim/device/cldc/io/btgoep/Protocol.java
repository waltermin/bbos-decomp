package net.rim.device.cldc.io.btgoep;

import com.sun.cldc.io.ConnectionBaseInterface;
import java.io.IOException;
import javax.microedition.io.Connection;
import net.rim.device.cldc.io.btspp.BluetoothURL;
import net.rim.device.cldc.io.utility.EventThreadCheck;
import net.rim.device.internal.bluetooth.BluetoothDeviceManager;

public class Protocol implements ConnectionBaseInterface {
   static final int CONNECT = 128;
   static final int DISCONNECT = 129;
   static final int PUT = 2;
   static final int PUT_FINAL = 130;
   static final int GET = 3;
   static final int GET_FINAL = 131;
   static final int SET_PATH = 133;
   static final int ABORT = 255;
   static final int MAX_PACKET_SIZE = 8192;

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

      BluetoothURL url = new BluetoothURL("btgoep", name);
      byte[] address = url.getAddress();
      return address == null ? new BluetoothGOEPServerConnection(url) : new BluetoothGOEPClientConnection(url);
   }
}
