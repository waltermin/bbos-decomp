package javax.microedition.location;

import net.rim.device.api.bluetooth.BluetoothSerialPort;
import net.rim.device.api.bluetooth.BluetoothSerialPortInfo;

final class BluetoothGPSRegistry$BluetoothConnectThread extends Thread {
   private final BluetoothGPSRegistry this$0;

   BluetoothGPSRegistry$BluetoothConnectThread(BluetoothGPSRegistry _1) {
      this.this$0 = _1;
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void run() {
      try {
         BluetoothSerialPortInfo[] portInfo = BluetoothSerialPort.getSerialPortInfo();
         int index = 0;
         if (portInfo.length == 0) {
            this.this$0._state = 3;
         } else {
            String dataSource = LocationProvider.getDataSource();

            for (int port = portInfo.length - 1; port >= 0; port--) {
               if (portInfo[port].getDeviceName().equals(dataSource)) {
                  index = port;
                  break;
               }
            }

            this.this$0._port = (BluetoothSerialPort)(new Object(portInfo[index], 1, 3, 0, 2048, 256, this.this$0));
            this.this$0._state = 2;
         }
      } catch (Throwable var9) {
         this.this$0._state = 2;
         this.this$0.notifyLocationListeners(2);
         synchronized (this.this$0._BTLocationLock) {
            this.this$0._BTLocationLock.notifyAll();
         }

         System.out.println(e);
         return;
      }
   }
}
