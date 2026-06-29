package net.rim.device.apps.internal.bluetooth;

import java.util.Enumeration;
import java.util.Vector;
import javax.bluetooth.DeviceClass;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;
import net.rim.device.api.bluetooth.BluetoothSerialPortInfo;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.ObjectChoiceField;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.component.Status;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.options.SaveableMainScreenOptionsListItem;
import net.rim.device.apps.api.ui.BooleanChoiceField;
import net.rim.device.apps.api.utility.framework.VerbToMenu;
import net.rim.device.internal.bluetooth.BluetoothDeviceManager;
import net.rim.device.internal.bluetooth.BluetoothME;
import net.rim.device.internal.bluetooth.HandsfreeGateway;
import net.rim.device.internal.i18n.CommonResource;
import net.rim.device.internal.ui.component.PropertyField;

final class BluetoothDevicePropertiesScreen extends SaveableMainScreenOptionsListItem implements BluetoothDeviceManagerListener {
   private EditField _nameField;
   private ObjectChoiceField _authorizedField;
   private BooleanChoiceField _encryptField;
   private ObjectChoiceField _nrecField;
   private BluetoothDevice _device;
   private boolean _showAllOptions;
   private MainScreen _mainScreen;
   private VerticalFieldManager _servicesVFM;

   private BluetoothDevicePropertiesScreen(BluetoothDevice device) {
      super(BluetoothMainScreen.getString(20));
      this._device = device;
   }

   @Override
   protected final void populateMainScreen(MainScreen mainScreen) {
      this._mainScreen = mainScreen;
      this._nameField = new EditField(BluetoothMainScreen.getString(1), this._device.getFriendlyName(), 64, 0);
      mainScreen.add(this._nameField);
      this._authorizedField = new ObjectChoiceField(BluetoothMainScreen.getString(21), CommonResource.getStringArray(10179), this._device.getAuthorized());
      mainScreen.add(this._authorizedField);
      BluetoothDeviceManagerImpl btManager = (BluetoothDeviceManagerImpl)BluetoothDeviceManager.getInstance();
      boolean encryptionEnforced = btManager.getSecurityMode() == 1;
      boolean encryptionEnabled;
      if (encryptionEnforced) {
         encryptionEnabled = true;
      } else {
         encryptionEnabled = this._device.isEncryptionEnabled();
      }

      this._encryptField = new BooleanChoiceField(BluetoothMainScreen.getString(42) + ':', 2, encryptionEnabled, 268435456);
      this._encryptField.setEditable(!encryptionEnforced);
      mainScreen.add(this._encryptField);
      if (this._device.hasHandsfree() && HandsfreeGateway.isNRECSupported()) {
         this._nrecField = new ObjectChoiceField(BluetoothMainScreen.getString(61), BluetoothMainScreen.getStringArray(62), this._device.getNRECMode());
         mainScreen.add(this._nrecField);
      }

      mainScreen.add(new SeparatorField());
      mainScreen.add(
         new PropertyField(BluetoothMainScreen.getString(77), BluetoothME.deviceAddressToString(this._device.getAddress(), true), 36028797018963968L)
      );
      mainScreen.add(new SeparatorField());
      mainScreen.add(new LabelField(BluetoothMainScreen.getString(34)));
      this._servicesVFM = new VerticalFieldManager();
      this.refreshServices();
      mainScreen.add(this._servicesVFM);
      btManager.addListener(this);
   }

   @Override
   public final boolean confirm(Verb verb, Object context) {
      if (!super.confirm(verb, context)) {
         return false;
      }

      ((BluetoothDeviceManagerImpl)BluetoothDeviceManager.getInstance()).removeListener(this);
      return true;
   }

   @Override
   protected final boolean invokeOptionsAction(int action) {
      switch (action) {
         case 1:
            return true;
         default:
            return false;
      }
   }

   private final void refreshServices() {
      this._servicesVFM.deleteAll();
      Enumeration e = this._device.getServiceNames();

      while (e.hasMoreElements()) {
         this._servicesVFM.add(new LabelField("  " + e.nextElement()));
      }
   }

   @Override
   protected final void addScreenVerbs(VerbToMenu verbToMenu, int instance) {
      super.addScreenVerbs(verbToMenu, instance);
      verbToMenu.addVerb(new BluetoothDevicePropertiesScreen$RefreshServicesVerb(this));
   }

   @Override
   protected final boolean save() {
      String friendlyName = this._nameField.getText().trim();
      if (friendlyName.length() == 0) {
         Status.show(BluetoothMainScreen.getString(46));
         return false;
      }

      this._device.setFriendlyName(friendlyName);
      this._device.setAuthorized(this._authorizedField.getSelectedIndex());
      this._device.setEncryptionEnabled(this._encryptField.isAffirmative());
      if (this._nrecField != null) {
         this._device.setNRECMode(this._nrecField.getSelectedIndex());
      }

      ((BluetoothDeviceManagerImpl)BluetoothDeviceManager.getInstance()).devicePropertiesUpdated(this._device);
      return super.save();
   }

   @Override
   public final boolean openDevelopmentBackdoor(int backdoorCode) {
      switch (backdoorCode) {
         case 1112298821:
            if (!this._showAllOptions) {
               this._showAllOptions = true;
               this._mainScreen.add(new SeparatorField());
               Vector v = new Vector();
               this._device.getSerialPortInfo(v);

               for (int i = v.size() - 1; i >= 0; i--) {
                  this._mainScreen.add(new LabelField(((BluetoothSerialPortInfo)v.elementAt(i)).toString()));
               }
            }
            break;
         case 1279872587:
            byte[] linkKey = this._device.getLinkKey();
            StringBuffer sb = new StringBuffer();

            for (int i = linkKey.length - 1; i >= 0; i--) {
               String s = Integer.toHexString(linkKey[i] & 255);
               if (s.length() == 1) {
                  sb.append('0');
               }

               sb.append(s);
               if (i % 2 == 0) {
                  sb.append(' ');
               }
            }

            Dialog.inform("Link key:\n" + sb.toString().toUpperCase());
            return true;
         case 1380928581:
            String role;
            switch (BluetoothME.getCurrentRole(this._device.getAddress())) {
               case -1:
                  role = "Unknown";
                  break;
               case 0:
               default:
                  role = "Master";
                  break;
               case 1:
                  role = "Slave";
            }

            if (Dialog.ask(3, "Current role: " + role + "\nSwitch roles?") != -1) {
               System.out.println("switchRole: " + BluetoothME.switchRole(this._device.getAddress()));
               return true;
            }
            break;
         case 1397639494:
            Status.show("Sniff mode enabled: " + this._device.toggleSniffModeEnabled());
            return true;
         default:
            return super.openDevelopmentBackdoor(backdoorCode);
      }

      return true;
   }

   @Override
   public final void inquiryComplete() {
   }

   @Override
   public final void inquiryCancelled() {
   }

   @Override
   public final void deviceAdded() {
   }

   @Override
   public final void deviceRemoved() {
   }

   @Override
   public final void deviceListUpdated() {
   }

   @Override
   public final void deviceInfoUpdated() {
      this.refreshServices();
   }

   @Override
   public final void stateChanged() {
   }

   @Override
   public final void inquiryResult(RemoteDevice remoteDevice, DeviceClass deviceClass) {
   }

   @Override
   public final void servicesDiscovered(int transactionID, ServiceRecord[] services) {
   }

   @Override
   public final void serviceSearchCompleted(int transactionID, int result) {
   }

   @Override
   public final void pairingInProgress() {
   }

   public static final void show(BluetoothDevice device) {
      BluetoothDevicePropertiesScreen screen = new BluetoothDevicePropertiesScreen(device);
      screen.perform(6099736323056465049L, null);
   }
}
