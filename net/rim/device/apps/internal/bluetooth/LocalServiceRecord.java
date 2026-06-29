package net.rim.device.apps.internal.bluetooth;

import java.util.Enumeration;
import javax.bluetooth.DataElement;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRegistrationException;
import javax.bluetooth.UUID;
import net.rim.device.api.util.Arrays;
import net.rim.device.cldc.io.btspp.BluetoothServerConnection;
import net.rim.device.internal.bluetooth.BluetoothME;
import net.rim.device.internal.bluetooth.BluetoothSDP;

public final class LocalServiceRecord extends ServiceRecordImpl {
   private int _type;
   private BluetoothServerConnection _conn;
   private int _classes;
   public static final int TYPE_L2CAP = 0;
   public static final int TYPE_RFCOMM = 1;
   public static final int TYPE_GOEP = 2;

   public LocalServiceRecord() {
      super._address = BluetoothME.getLocalDeviceAddress();
   }

   public LocalServiceRecord(BluetoothServerConnection conn, UUID uuid, String serviceName, int type) {
      this();
      this._type = type;
      this._conn = conn;
      DataElement element = new DataElement(48);
      element.addElement(new DataElement(24, uuid));
      if (type == 1) {
         element.addElement(new DataElement(24, ServiceRecordImpl.SERIAL_PORT_UUID));
      }

      this.setAttributeValue(1, element);
      element = new DataElement(48);
      DataElement element1 = new DataElement(48);
      element1.addElement(new DataElement(24, ServiceRecordImpl.L2CAP_UUID));
      if (type == 0) {
         element1.addElement(new DataElement(9, this._conn.getPSM()));
      }

      element.addElement(element1);
      if (type == 1 || type == 2) {
         element1 = new DataElement(48);
         element1.addElement(new DataElement(24, ServiceRecordImpl.RFCOMM_UUID));
         element1.addElement(new DataElement(8, this._conn.getRFCOMMChannel()));
         element.addElement(element1);
      }

      if (type == 2) {
         element1 = new DataElement(48);
         element1.addElement(new DataElement(24, ServiceRecordImpl.OBEX_UUID));
         element.addElement(element1);
      }

      this.setAttributeValue(4, element);
      element = new DataElement(48);
      element.addElement(new DataElement(24, new UUID(4098)));
      this.setAttributeValue(5, element);
      element = new DataElement(48);
      element.addElement(new DataElement(9, 25966));
      element.addElement(new DataElement(9, 106));
      element.addElement(new DataElement(9, 256));
      this.setAttributeValue(6, element);
      this.setAttributeValue(256, new DataElement(32, serviceName));
   }

   public final void validate() {
      if (this.getAttributeValue(1) == null) {
         throw new IllegalArgumentException();
      }

      DataElement pdl = this.getAttributeValue(4);
      if (pdl != null && pdl.getDataType() == 48) {
         boolean gotL2CAP = false;
         boolean gotRFCOMM = false;
         boolean gotOBEX = false;
         int psm = -1;
         int channel = -1;
         Enumeration e = (Enumeration)pdl.getValue();

         while (e.hasMoreElements()) {
            DataElement de = (DataElement)e.nextElement();
            if (de.getDataType() != 48) {
               throw new IllegalArgumentException();
            }

            Enumeration e1 = (Enumeration)de.getValue();
            if (e1.hasMoreElements()) {
               DataElement de1 = (DataElement)e1.nextElement();
               if (de1.getDataType() == 24) {
                  UUID uuid = (UUID)de1.getValue();
                  if (uuid.equals(ServiceRecordImpl.L2CAP_UUID)) {
                     gotL2CAP = true;
                     if (e1.hasMoreElements()) {
                        de1 = (DataElement)e1.nextElement();
                        if (de1.getDataType() == 9) {
                           psm = (int)de1.getLong();
                        }
                     }
                  } else if (uuid.equals(ServiceRecordImpl.RFCOMM_UUID)) {
                     gotRFCOMM = true;
                     if (e1.hasMoreElements()) {
                        de1 = (DataElement)e1.nextElement();
                        if (de1.getDataType() == 8) {
                           channel = (int)de1.getLong();
                        }
                     }
                  } else if (uuid.equals(ServiceRecordImpl.OBEX_UUID)) {
                     gotOBEX = true;
                  }
               }
            }
         }

         switch (this._type) {
            case -1:
               return;
            case 0:
            default:
               if (gotL2CAP && psm == this._conn.getPSM()) {
                  return;
               }

               throw new IllegalArgumentException();
            case 1:
               if (gotL2CAP && gotRFCOMM && channel == this._conn.getRFCOMMChannel()) {
                  return;
               }

               throw new IllegalArgumentException();
            case 2:
               if (!gotL2CAP || !gotRFCOMM || !gotOBEX || channel != this._conn.getRFCOMMChannel()) {
                  throw new IllegalArgumentException();
               }
         }
      } else {
         throw new IllegalArgumentException();
      }
   }

   public final void updateRecord() throws ServiceRegistrationException {
      this.validate();
      int recordHandle = this._conn.getServiceRecordHandle();
      if (recordHandle != -1) {
         int[] attributes = this.getAttributeIDs();
         byte[][] values = this.getAttributeValues();
         if (BluetoothSDP.updateRecord(recordHandle, attributes, values, this._classes) != 0) {
            throw new ServiceRegistrationException();
         }
      }
   }

   @Override
   public final boolean populateRecord(int[] attrIDs) {
      throw new RuntimeException("Local ServiceRecord");
   }

   @Override
   public final void setDeviceServiceClasses(int classes) {
      if ((classes & 0xFF0000) != classes) {
         throw new IllegalArgumentException();
      }

      this._classes = classes;
   }

   public final int getDeviceServiceClasses() {
      return this._classes;
   }

   @Override
   public final synchronized boolean setAttributeValue(int attrID, DataElement attrValue) {
      if (attrID > 0 && attrID <= 65535) {
         int i;
         for (i = 0; i < super._attributeIDs.length; i++) {
            if (super._attributeIDs[i] == attrID) {
               if (attrValue == null) {
                  Arrays.removeAt(super._attributeIDs, i);
                  Arrays.removeAt(super._attributeValues, i);
                  return true;
               }

               super._attributeValues[i] = attrValue;
               return true;
            }

            if (super._attributeIDs[i] > attrID) {
               break;
            }
         }

         if (attrValue == null) {
            return false;
         }

         Arrays.insertAt(super._attributeIDs, attrID, i);
         Arrays.insertAt(super._attributeValues, attrValue, i);
         return true;
      } else {
         throw new IllegalArgumentException();
      }
   }

   @Override
   public final RemoteDevice getHostDevice() {
      return null;
   }
}
