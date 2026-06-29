package net.rim.device.apps.internal.bluetooth;

import java.io.IOException;
import javax.bluetooth.DataElement;
import javax.bluetooth.RemoteDevice;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.internal.bluetooth.BluetoothDeviceManager;
import net.rim.vm.Persistable;

public final class RemoteServiceRecord extends ServiceRecordImpl implements Persistable {
   private boolean _attributesUpdated;
   private int _handle;
   private static final boolean DEBUG = false;

   public RemoteServiceRecord(byte[] address, byte[] attributeListData) {
      super._address = address;
      this.updateAttributes(attributeListData);
   }

   public final int getHandle() {
      return this._handle;
   }

   public final synchronized void updateAttributes(byte[] attributeListData) throws IOException {
      if (attributeListData != null) {
         DataBuffer buf = new DataBuffer(attributeListData, 0, attributeListData.length, true);

         while (!buf.eof()) {
            if (buf.readUnsignedByte() != 9) {
               throw new IOException();
            }

            int id = buf.readUnsignedShort();
            DataElement value = DataElementUtilities.read(buf);
            int index = Arrays.getIndex(super._attributeIDs, id);
            if (index != -1) {
               super._attributeValues[index] = value;
            } else {
               Arrays.add(super._attributeIDs, id);
               Arrays.add(super._attributeValues, value);
            }

            if (id == 0) {
               this._handle = (int)value.getLong();
            }

            this._attributesUpdated = true;
         }
      }

      this.notify();
   }

   public final synchronized void updateAttributesFailed() {
      this.notify();
   }

   @Override
   public final synchronized boolean populateRecord(int[] attrIDs) throws IOException {
      if (attrIDs.length != 0 && attrIDs.length <= 10) {
         int[] attributes = new int[0];

         for (int i = 0; i < attrIDs.length; i++) {
            if (attrIDs[i] < 0 || attrIDs[i] > 65535) {
               throw new IllegalArgumentException();
            }

            if (Arrays.getIndex(attributes, attrIDs[i]) != -1) {
               throw new IllegalArgumentException();
            }

            Arrays.add(attributes, attrIDs[i]);
         }

         Arrays.sort(attributes, 0, attributes.length);
         this._attributesUpdated = false;
         BluetoothDeviceManagerImpl btManager = (BluetoothDeviceManagerImpl)BluetoothDeviceManager.getInstance();
         BluetoothDevice device = btManager.getDevice(super._address, 0);
         if (device.doServiceAttributeRequest(this, attributes)) {
            label54:
            try {
               this.wait(60000);
            } finally {
               break label54;
            }

            if (this._attributesUpdated) {
               return true;
            }
         }

         throw new IOException("Could not contact remote device");
      } else {
         throw new IllegalArgumentException();
      }
   }

   @Override
   public final void setDeviceServiceClasses(int classes) {
      throw new RuntimeException("Remote ServiceRecord");
   }

   @Override
   public final boolean setAttributeValue(int attrID, DataElement attrValue) {
      throw new RuntimeException("Remote ServiceRecord");
   }

   @Override
   public final RemoteDevice getHostDevice() {
      BluetoothDeviceManagerImpl btManager = (BluetoothDeviceManagerImpl)BluetoothDeviceManager.getInstance();
      return btManager.getDevice(super._address, 0).getRemoteDevice();
   }
}
