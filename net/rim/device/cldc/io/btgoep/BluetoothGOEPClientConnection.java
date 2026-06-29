package net.rim.device.cldc.io.btgoep;

import net.rim.device.cldc.io.btspp.BluetoothConnection;
import net.rim.device.cldc.io.btspp.BluetoothSerialConnection;
import net.rim.device.cldc.io.btspp.BluetoothURL;

public final class BluetoothGOEPClientConnection extends OBEXClientSession implements BluetoothConnection {
   private BluetoothSerialConnection _clientConnection;

   public BluetoothGOEPClientConnection(BluetoothURL url) {
      this._clientConnection = new BluetoothSerialConnection(url.getAddress(), url.getChannel(), 8192, true);
      this.init(this._clientConnection.openInputStream(), this._clientConnection.openOutputStream());
   }

   @Override
   public final byte[] getRemoteAddress() {
      return this._clientConnection.getRemoteAddress();
   }

   @Override
   public final boolean isConnected() {
      return this._clientConnection.isConnected();
   }

   @Override
   public final void close() {
      try {
         this._clientConnection.close();
      } finally {
         return;
      }
   }
}
