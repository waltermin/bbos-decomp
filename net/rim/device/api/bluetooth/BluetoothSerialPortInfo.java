package net.rim.device.api.bluetooth;

import net.rim.device.internal.bluetooth.BluetoothME;

public class BluetoothSerialPortInfo {
   private byte[] _address;
   private int _pageScanInfo;
   private String _name;
   private int _serverID;
   private String _serviceName;

   public BluetoothSerialPortInfo(byte[] address, int serverID, String serviceName) {
      this(address, 0, null, serverID, serviceName);
   }

   public BluetoothSerialPortInfo(byte[] address, String name, int serverID, String serviceName) {
      this(address, 0, name, serverID, serviceName);
   }

   public BluetoothSerialPortInfo(byte[] address, int pageScanInfo, String name, int serverID, String serviceName) {
      this._address = address;
      this._pageScanInfo = pageScanInfo;
      this._name = name;
      this._serverID = serverID;
      this._serviceName = serviceName;
   }

   @Override
   public String toString() {
      StringBuffer sb = new StringBuffer("btspp://");
      if (this._address != null) {
         sb.append(BluetoothME.deviceAddressToString(this._address, false));
      }

      sb.append(':');
      sb.append(this._serverID);
      return sb.toString();
   }

   public byte[] getDeviceAddress() {
      return this._address;
   }

   public int getDevicePageScanInfo() {
      return this._pageScanInfo;
   }

   public String getDeviceName() {
      return this._name;
   }

   public int getServerID() {
      return this._serverID;
   }

   public String getServiceName() {
      return this._serviceName;
   }
}
