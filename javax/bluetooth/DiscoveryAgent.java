package javax.bluetooth;

import java.util.Vector;
import net.rim.device.api.util.Arrays;
import net.rim.device.apps.internal.bluetooth.BluetoothDevice;
import net.rim.device.apps.internal.bluetooth.BluetoothDeviceManagerImpl;
import net.rim.device.apps.internal.bluetooth.RemoteServiceRecord;
import net.rim.device.internal.bluetooth.BluetoothME;

public class DiscoveryAgent {
   private BluetoothDeviceManagerImpl _btManager;
   private int _sdpTransactionID;
   public static final int NOT_DISCOVERABLE = 0;
   public static final int GIAC = 10390323;
   public static final int LIAC = 10390272;
   public static final int CACHED = 0;
   public static final int PREKNOWN = 1;
   private static final int[] DEFAULT_ATTRIBUTE_IDS = new int[]{
      0, 1, 2, 3, 4, -804651002, 0, 1, 2, 3, 4, 256, -805044213, 775162112, 774909491, 3420721, -805044199, 1699878656, 1918985587, 1226860643
   };

   DiscoveryAgent(BluetoothDeviceManagerImpl btManager) {
      this._btManager = btManager;
   }

   public RemoteDevice[] retrieveDevices(int option) {
      Vector devices;
      if (option == 1) {
         devices = this._btManager.getPairedDevices();
      } else {
         if (option != 0) {
            throw new Object();
         }

         devices = this._btManager.getInRangeDevices(false);
      }

      int length = devices.size();
      RemoteDevice[] remoteDevices = new RemoteDevice[length];

      for (int i = 0; i < length; i++) {
         BluetoothDevice device = (BluetoothDevice)devices.elementAt(i);
         remoteDevices[i] = device.getRemoteDevice();
      }

      return remoteDevices;
   }

   public synchronized boolean startInquiry(int accessCode, DiscoveryListener listener) {
      if (listener == null) {
         throw new Object();
      }

      switch (accessCode) {
         case 10390272:
         case 10390323:
            DiscoveryAgent$EventHandler handler = new DiscoveryAgent$DeviceEventHandler(this, listener);
            if (this._btManager.startInquiry(accessCode)) {
               return true;
            }

            handler.destroy();
            throw new BluetoothStateException();
         default:
            if (accessCode >= 10390272 && accessCode <= 953151) {
               return false;
            } else {
               throw new Object();
            }
      }
   }

   public synchronized boolean cancelInquiry(DiscoveryListener listener) {
      if (listener == null) {
         throw new Object();
      }

      Object[] listeners = this._btManager.getListeners();
      boolean haveListener = false;
      if (listeners != null) {
         for (int i = listeners.length - 1; i >= 0; i--) {
            Object var10000 = listeners[i];
            if (listeners[i] instanceof DiscoveryAgent$DeviceEventHandler) {
               DiscoveryAgent$DeviceEventHandler h = (DiscoveryAgent$DeviceEventHandler)var10000;
               if (h._listener == listener) {
                  haveListener = true;
                  break;
               }
            }
         }
      }

      if (!haveListener) {
         return false;
      }

      this._btManager.cancelInquiry();
      return true;
   }

   public int searchServices(int[] attrSet, UUID[] uuidSet, RemoteDevice btDev, DiscoveryListener listener) {
      if (uuidSet != null && btDev != null && listener != null) {
         int[] attributes = null;
         if (attrSet == null) {
            attributes = DEFAULT_ATTRIBUTE_IDS;
         } else {
            if (attrSet.length == 0) {
               throw new Object();
            }

            attributes = new int[0];

            for (int i = 0; i < attrSet.length; i++) {
               if (attrSet[i] < 0 || attrSet[i] > 65535) {
                  throw new Object();
               }

               if (Arrays.getIndex(attributes, attrSet[i]) != -1) {
                  throw new Object();
               }

               if (Arrays.getIndex(DEFAULT_ATTRIBUTE_IDS, attrSet[i]) == -1) {
                  Arrays.add(attributes, attrSet[i]);
               }
            }

            Arrays.append(attributes, DEFAULT_ATTRIBUTE_IDS);
            Arrays.sort(attributes, 0, attributes.length);
         }

         int numUUIDs = uuidSet.length;
         if (numUUIDs != 0 && numUUIDs <= 12) {
            for (int i = 0; i < numUUIDs; i++) {
               if (uuidSet[i] == null) {
                  throw new Object();
               }

               for (int j = 0; j < numUUIDs; j++) {
                  if (i != j && uuidSet[i].equals(uuidSet[j])) {
                     throw new Object();
                  }
               }
            }

            byte[] b = BluetoothME.stringToDeviceAddress(btDev.getBluetoothAddress());
            BluetoothDevice device = this._btManager.getDevice(b, 0);
            this._sdpTransactionID++;
            DiscoveryAgent$EventHandler handler = new DiscoveryAgent$ServiceEventHandler(this, listener);
            if (device.doServiceSearchAttributeRequest(attributes, uuidSet, this._sdpTransactionID)) {
               return this._sdpTransactionID;
            }

            handler.destroy();
            throw new BluetoothStateException();
         } else {
            throw new Object();
         }
      } else {
         throw new Object();
      }
   }

   public boolean cancelServiceSearch(int transID) {
      return false;
   }

   public String selectService(UUID uuid, int security, boolean master) {
      if (uuid == null) {
         throw new Object();
      }

      switch (security) {
         case -1:
         default:
            throw new Object();
         case 0:
         case 1:
         case 2:
            Vector v = this._btManager.getInRangeDevices(false);

            for (int i = v.size() - 1; i >= 0; i--) {
               BluetoothDevice device = (BluetoothDevice)v.elementAt(i);
               RemoteServiceRecord record = device.getServiceRecord(uuid);
               if (record != null) {
                  return record.getConnectionURL(security, master);
               }
            }

            return null;
      }
   }
}
