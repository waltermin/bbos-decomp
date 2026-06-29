package javax.bluetooth;

import java.io.IOException;
import javax.microedition.io.Connection;
import net.rim.device.api.util.Arrays;
import net.rim.device.apps.internal.bluetooth.BluetoothDevice;
import net.rim.device.apps.internal.bluetooth.BluetoothDeviceManagerImpl;
import net.rim.device.cldc.io.btspp.BluetoothConnection;
import net.rim.device.cldc.io.btspp.BluetoothServerConnection;
import net.rim.device.internal.applicationcontrol.ApplicationControl;
import net.rim.device.internal.bluetooth.BluetoothDeviceManager;
import net.rim.device.internal.bluetooth.BluetoothME;

public class RemoteDevice {
   private String _address;
   private byte[] _addressAsBytes;
   private static final int WAIT = 0;
   private static final int SUCCEEDED = 1;

   protected RemoteDevice(String address) {
      this._address = address;
      this._addressAsBytes = BluetoothME.stringToDeviceAddress(address);
      if (Arrays.equals(this._addressAsBytes, BluetoothME.getLocalDeviceAddress())) {
         throw new IllegalArgumentException();
      }
   }

   private BluetoothDevice getDevice() {
      BluetoothDeviceManagerImpl btManager = (BluetoothDeviceManagerImpl)BluetoothDeviceManager.getInstance();
      return btManager.getDevice(this._addressAsBytes, 0);
   }

   public boolean isTrustedDevice() {
      return this.getDevice().getAuthorized() == 1;
   }

   public String getFriendlyName(boolean alwaysAsk) throws IOException {
      BluetoothDevice device = this.getDevice();
      if (alwaysAsk && !device.fetchName()) {
         throw new IOException("Could not contact device");
      } else {
         return device.getName();
      }
   }

   public final String getBluetoothAddress() {
      return this._address;
   }

   @Override
   public boolean equals(Object obj) {
      if (!(obj instanceof RemoteDevice)) {
         return false;
      }

      RemoteDevice other = (RemoteDevice)obj;
      return this._address.equals(other._address);
   }

   @Override
   public int hashCode() {
      return this._address.hashCode();
   }

   public static RemoteDevice getRemoteDevice(Connection conn) throws IOException {
      assertPermission();
      if (conn == null) {
         throw new NullPointerException();
      }

      if (!(conn instanceof BluetoothConnection)) {
         throw new IllegalArgumentException();
      }

      BluetoothConnection btconn = (BluetoothConnection)conn;
      byte[] address = btconn.getRemoteAddress();
      if (address == null) {
         throw new IOException("Connection is closed");
      }

      BluetoothDeviceManagerImpl btManager = (BluetoothDeviceManagerImpl)BluetoothDeviceManager.getInstance();
      return btManager.getDevice(address, 0).getRemoteDevice();
   }

   public boolean authenticate() throws IOException {
      if (this.getDevice().isConnected()) {
         return true;
      } else {
         throw new IOException();
      }
   }

   public boolean authorize(Connection conn) throws IOException {
      if (getRemoteDevice(conn) != this) {
         throw new IllegalArgumentException();
      } else if (!(conn instanceof BluetoothServerConnection)) {
         throw new IllegalArgumentException();
      } else {
         BluetoothServerConnection serverConn = (BluetoothServerConnection)conn;
         if (serverConn.isConnected()) {
            return true;
         } else {
            throw new IOException();
         }
      }
   }

   public boolean encrypt(Connection conn, boolean on) {
      if (getRemoteDevice(conn) != this) {
         throw new IllegalArgumentException();
      }

      if (!on) {
         return !this.isEncrypted();
      }

      this.getDevice().encryptConnection();
      return true;
   }

   public boolean isAuthenticated() {
      return this.getDevice().isConnected();
   }

   public boolean isAuthorized(Connection conn) throws IOException {
      if (!(conn instanceof BluetoothServerConnection)) {
         if (!(conn instanceof BluetoothConnection)) {
            throw new IllegalArgumentException();
         } else {
            BluetoothConnection clientConn = (BluetoothConnection)conn;
            if (clientConn.isConnected()) {
               return false;
            } else {
               throw new IOException();
            }
         }
      } else {
         BluetoothServerConnection serverConn = (BluetoothServerConnection)conn;
         if (serverConn.isConnected()) {
            return true;
         } else {
            throw new IOException();
         }
      }
   }

   public boolean isEncrypted() {
      return this.getDevice().isConnectionEncrypted();
   }

   @Override
   public String toString() {
      String name = this.getDevice().getFriendlyName();
      return name != null ? name : this._address;
   }

   private static void assertPermission() {
      ApplicationControl.assertBluetoothSerialProfileAllowed(true);
   }
}
