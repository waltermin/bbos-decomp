package net.rim.device.apps.internal.bluetooth;

import java.io.OutputStream;
import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.DeviceClass;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.DiscoveryListener;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;
import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.obex.ClientSession;
import javax.obex.HeaderSet;
import javax.obex.Operation;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.i18n.ResourceBundleFamily;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.util.Factory;
import net.rim.device.apps.api.addressbook.AddressCardModel;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.internal.bluetooth.BluetoothDeviceManager;

final class PushAddressBookVerb extends Verb implements DiscoveryListener, Runnable {
   private ServiceRecord _serviceRecord;
   private int _transactionID;
   private String _url;
   private BluetoothDevice _device;
   private static ResourceBundleFamily _rb = ResourceBundle.getBundle(2718987090951705092L, "net.rim.device.apps.internal.resource.Bluetooth");

   PushAddressBookVerb(BluetoothDevice device) {
      super(1572868, _rb, 65);
      this._device = device;
   }

   @Override
   public final Object invoke(Object pararameter) {
      try {
         this._device.notifyOperationStart();
         DiscoveryAgent discoveryAgent = LocalDevice.getLocalDevice().getDiscoveryAgent();
         UUID[] uuids = new UUID[]{new UUID(4357)};
         this._serviceRecord = null;
         this._transactionID = discoveryAgent.searchServices(null, uuids, this._device.getRemoteDevice(), this);
         return null;
      } catch (BluetoothStateException var4) {
         return null;
      }
   }

   @Override
   public final void deviceDiscovered(RemoteDevice btDevice, DeviceClass cod) {
   }

   @Override
   public final void servicesDiscovered(int transID, ServiceRecord[] servRecord) {
      if (transID == this._transactionID) {
         System.out.println(((StringBuffer)(new Object("servicesDiscovered: "))).append(transID).append(',').append(servRecord).toString());
         this._serviceRecord = servRecord[0];
      }
   }

   @Override
   public final void serviceSearchCompleted(int transID, int respCode) {
      if (transID == this._transactionID) {
         System.out.println(((StringBuffer)(new Object("serviceSearchCompleted: "))).append(transID).append(',').append(respCode).toString());
         if (this._serviceRecord == null) {
            this._device.notifyOperationComplete(BluetoothMainScreen.getString(70));
            return;
         }

         this._url = this._serviceRecord.getConnectionURL(0, false);
         ((Thread)(new Object(this))).start();
      }
   }

   @Override
   public final void inquiryCompleted(int discType) {
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void run() {
      boolean failed = true;
      System.out.println(this._url);
      ClientSession cs = null;
      boolean var105 = false /* VF: Semaphore variable */;

      label902: {
         label903: {
            try {
               label875:
               try {
                  var105 = true;
                  ApplicationRegistry ex = ApplicationRegistry.getApplicationRegistry();
                  Factory converter = ex.get(-5888220356524146836L);
                  BluetoothDeviceManagerImpl btManager = (BluetoothDeviceManagerImpl)BluetoothDeviceManager.getInstance();
                  AddressCardModel[] cards = btManager.getAddressCards(false);
                  int count = 0;
                  if (cards != null) {
                     count = cards.length;
                  }

                  if (count == 0) {
                     this._device.notifyOperationComplete(BluetoothMainScreen.getString(72));
                     var105 = false;
                     break label902;
                  }

                  if (count > 200) {
                     this._device.notifyOperationStart(BluetoothMainScreen.getString(71));
                  }

                  ContextObject context = (ContextObject)(new Object());
                  StupidCLDCByteArrayOutputStream vCardStream = new StupidCLDCByteArrayOutputStream();
                  context.put(-980891548873596767L, vCardStream);
                  context.setFlag(127);

                  for (int i = 0; i < count; i++) {
                     context.put(254, cards[i]);
                     ((Factory)converter).createInstance(context);
                  }

                  this._device.notifyOperationStart(BluetoothMainScreen.getString(66));
                  cs = (ClientSession)Connector.open(this._url);
                  cs.connect(null);
                  HeaderSet hs = cs.createHeaderSet();
                  hs.setHeader(1, "Phonebook.vcf");
                  hs.setHeader(195, new Object(vCardStream.size()));
                  Operation op = cs.put(hs);
                  OutputStream out = op.openOutputStream();
                  vCardStream.writeTo(out);
                  out.close();
                  op.close();
                  this._device.notifyOperationComplete(BluetoothMainScreen.getString(67));
                  failed = false;
                  var105 = false;
                  break label903;
               } catch (Throwable var124) {
                  System.out.println(ex);
                  var105 = false;
                  break label875;
               }
            } finally {
               if (var105) {
                  if (cs != null) {
                     label831:
                     try {
                        cs.disconnect(null);
                     } finally {
                        break label831;
                     }

                     label829:
                     try {
                        cs.close();
                     } finally {
                        break label829;
                     }
                  }

                  if (failed) {
                     this._device.notifyOperationComplete(BluetoothMainScreen.getString(68));
                  }
               }
            }

            if (cs != null) {
               label856:
               try {
                  cs.disconnect(null);
               } finally {
                  break label856;
               }

               label853:
               try {
                  cs.close();
               } finally {
                  break label853;
               }
            }

            if (failed) {
               this._device.notifyOperationComplete(BluetoothMainScreen.getString(68));
               return;
            }

            return;
         }

         if (cs != null) {
            label848:
            try {
               cs.disconnect(null);
            } finally {
               break label848;
            }

            label845:
            try {
               cs.close();
            } finally {
               break label845;
            }
         }

         if (failed) {
            this._device.notifyOperationComplete(BluetoothMainScreen.getString(68));
            return;
         }

         return;
      }

      if (cs != null) {
         label840:
         try {
            cs.disconnect(null);
         } finally {
            break label840;
         }

         label838:
         try {
            cs.close();
         } finally {
            break label838;
         }
      }

      if (failed) {
         this._device.notifyOperationComplete(BluetoothMainScreen.getString(68));
      }
   }
}
