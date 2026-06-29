package net.rim.device.internal.system;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;
import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.api.system.ApplicationManager;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.system.Radio;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.system.SystemListener2;
import net.rim.device.api.system.USBPortListener2;
import net.rim.device.cldc.io.daemon.ProtocolDaemon;
import net.rim.device.internal.bluetooth.BluetoothME;
import net.rim.device.internal.ui.component.BackgroundDialog;

public class USBPasswordRedirectManager implements SystemListener2, USBPortListener2, GlobalEventListener {
   private Vector _channels;
   private Security _security;
   private boolean _allowRedirects;
   private Vector _disallowedChannels;
   private int _initialRadioState;
   private boolean _isRadioAllowedOn = true;
   private boolean _bluetoothWasOn;
   private static final long GUID = -6654808410320396441L;
   public static final long EVENT_LOGGER_GUID = -2691377221057526315L;
   public static final String EVENT_LOGGER_NAME = "net.rim.usb.pwd";
   public static final int EVENT_REGISTERED = 1382377321;
   public static final int EVENT_CONNECTION_AUTHENTICATION = 1131299189;
   public static final int EVENT_CHANNEL_ALLOWED = 1130905964;
   public static final int EVENT_CHANNEL_WAITING = 1130911604;
   public static final int EVENT_CHANNEL_PROMPT = 1130909812;
   public static final int EVENT_CHANNEL_DISCONNECTED = 1130906739;
   public static final int EVENT_CABLE_CONNECTED = 1130513262;
   public static final int EVENT_USB_ENUMERATED = 1164866925;
   public static final int EVENT_CABLE_DISCONNECTED = 1130513523;
   public static final int EVENT_GET_CHANNEL_NAME_ERROR = 1195593285;
   public static final int EVENT_CHANNEL_PROMPT_ERROR = 1130909765;
   public static final int EVENT_ALLOW_REDIRECTS = 1097618020;
   public static final int EVENT_DISALLOW_REDIRECTS = 1147753060;
   public static final int EVENT_CLEAR_CHANNELS = 1131168616;
   public static final int EVENT_NOCLEAR_CHANNELS = 1313031016;
   public static final int EVENT_ALLOW_CHANNEL_ERROR = 1094936645;
   public static final int EVENT_RADIO_POWER_ON = 1382305646;
   public static final int EVENT_RADIO_POWER_OFF = 1382305638;
   public static final int EVENT_ALLOW_CHANNEL = 1097625463;
   public static final int EVENT_DISALLOW_CHANNEL = 1147761523;
   public static final int EVENT_CANCEL = 1130458723;
   public static final int EVENT_DISMISS = 1147761517;
   public static final int EVENT_OLD_OS = 1331973971;
   public static final int EVENT_NEW_OS = 1316441939;
   private static USBPasswordRedirectManager _passwordManager;
   private static String _knownPassword = "blackberry";
   private static final boolean DEBUG = true;

   public Vector getAllChannels() {
      return this._channels;
   }

   public void addToDisallowedChannels(String channelName) {
      if (!this._disallowedChannels.contains(channelName)) {
         this._disallowedChannels.addElement(channelName);
      }
   }

   public boolean isRadioAllowedOn() {
      return this._isRadioAllowedOn;
   }

   public synchronized void allowRedirects(boolean allow) {
      if (allow) {
         logEvent(1097618020);
         System.out.println("USB ALLOW");
      } else {
         logEvent(1147753060);
         System.out.println("USB DISALLOW");
      }

      this._allowRedirects = allow;
      if (this._allowRedirects) {
         this.clearChannels(true);
      }
   }

   public synchronized void clearChannels(boolean allow) {
      if (allow) {
         logEvent(1131168616);
         System.out.println("USB CLEAR");
      } else {
         logEvent(1313031016);
         System.out.println("USB UNCLEAR");
      }

      Enumeration enumeration = this._channels.elements();

      while (enumeration.hasMoreElements()) {
         Integer channel = (Integer)enumeration.nextElement();
         this.allowChannel(channel, allow);
      }

      this._channels.removeAllElements();
   }

   public void allowChannel(int channel, boolean allow) {
      try {
         if (allow) {
            logEvent(1097625463);
         } else {
            logEvent(1147761523);
         }

         USBPortInternal$Internal.redirectedPasswordChallenge(channel, allow);
      } catch (Throwable t) {
         logEvent(1094936645);
         System.out.println("USB ALLOW ERROR");
      }
   }

   @Override
   public void connectionAuthenticationRequired(int channel) {
      logEvent(1131299189);
      System.out.println("USB PWRD");

      try {
         if (this._disallowedChannels.size() > 0) {
            for (int i = 0; i < this._disallowedChannels.size(); i++) {
               if (((String)this._disallowedChannels.elementAt(i)).equals(USBPortInternal$Internal.getChannelName(channel))) {
                  return;
               }
            }
         }
      } catch (IOException ioe) {
         logEvent(1195593285);
         System.out.println("USB PT GET CHAN ERROR");
      }

      ProtocolDaemon.getInstance().invokeLater(new USBPasswordRedirectManager$ConnectionAuthenticationRunnable(this, channel));
   }

   @Override
   public synchronized void disconnected(int channel) {
      logEvent(1130906739);
      System.out.println("USB DIS");
      this._channels.removeElement(new Integer(channel));
   }

   @Override
   public int getChannel() {
      return -1;
   }

   @Override
   public void disconnected() {
   }

   @Override
   public void receiveError(int error) {
   }

   @Override
   public void dataReceived(int length) {
   }

   @Override
   public void dataSent() {
   }

   @Override
   public void patternReceived(byte[] pattern) {
   }

   @Override
   public void dataNotSent() {
   }

   @Override
   public void connectionRequested() {
   }

   @Override
   public void connected() {
   }

   @Override
   public void usbConnectionStateChange(int state) {
      int usbConnectionState = USBPortInternal.getConnectionState();
      if (usbConnectionState != -1) {
         if ((state & 4) == 0) {
            logEvent(1130513262);
            System.out.println("USB CAB CON");
         } else {
            logEvent(1130513523);
            System.out.println("USB CAB DIS");
            this._disallowedChannels.removeAllElements();
         }

         this.checkState(usbConnectionState);
      }
   }

   @Override
   public void powerOff() {
   }

   @Override
   public void powerUp() {
   }

   @Override
   public void batteryLow() {
   }

   @Override
   public void batteryGood() {
   }

   @Override
   public void batteryStatusChange(int status) {
   }

   @Override
   public void powerOffRequested(int reason) {
   }

   @Override
   public void cradleMismatch(boolean mismatch) {
   }

   @Override
   public void fastReset() {
   }

   @Override
   public void backlightStateChange(boolean on) {
   }

   @Override
   public void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid == 8508406279413621091L || guid == -594020114676189989L) {
         int state = USBPortInternal.getConnectionState();
         if (state != -1) {
            this.checkState(state);
         }
      }
   }

   private USBPasswordRedirectManager() {
      EventLogger.register(-2691377221057526315L, "net.rim.usb.pwd", 2);
      this._channels = new Vector();
      this._disallowedChannels = new Vector();
      if (USBPortInternal.isSupported() && !InternalServices.isFermion()) {
         this._security = Security.getInstance();
         logEvent(1382377321);
         System.out.println("USB REGISTER");
         ProtocolDaemon pd = ProtocolDaemon.getInstance();
         pd.addSystemListener(this);
         pd.addIOPortListener(this);
         pd.addGlobalEventListener(this);
      }
   }

   private void enableRadio() {
      System.out.println("enableRadio Called");
      this._isRadioAllowedOn = true;
      System.out.println("Initial Radio State: " + this._initialRadioState);
      switch (this._initialRadioState) {
         case 1:
         case 2:
         case 5:
            int currentRadioState = RadioInfo.getState();
            if (currentRadioState != 1 && currentRadioState != 5 && currentRadioState != 2) {
               System.out.println("Turning ON Radio");
               Radio.requestPowerOn();
               logEvent(1382305646);
            }
         default:
            if (this._bluetoothWasOn) {
               System.out.println("Turning ON Bluetooth Radio");
               BluetoothME.requestPowerOn();
            }
      }
   }

   private void disableRadio() {
      System.out.println("DisableRadio Called");
      if (!this._isRadioAllowedOn) {
         System.out.println("Radio already Disabled, so return.");
      } else {
         this._initialRadioState = RadioInfo.getState();
         System.out.println("Initial Radio State: " + this._initialRadioState);
         this._isRadioAllowedOn = false;
         if (RadioInfo.getState() != 0) {
            System.out.println("Turning Off Radio");
            logEvent(1382305638);
            Radio.requestPowerOff();
         }

         if (BluetoothME.isSupported() && BluetoothME.isPowerOn()) {
            System.out.println("Turning Off Bluetooth Radio");
            BluetoothME.requestPowerOff();
            this._bluetoothWasOn = true;
         } else {
            this._bluetoothWasOn = false;
         }
      }
   }

   private void connectionAuthenticationRequiredPrivate(int channel) {
      boolean promptNecessary = false;
      synchronized (this) {
         if (this._allowRedirects) {
            logEvent(1130905964);
            System.out.println("USB CH ALLOWED");
            this.allowChannel(channel, true);
         } else if (ApplicationManager.getApplicationManager().isSystemLocked()) {
            logEvent(1130911604);
            System.out.println("USB CH WAITING");
            this._channels.addElement(new Integer(channel));
         } else {
            logEvent(1130909812);
            System.out.println("USB CH PT");
            this._channels.addElement(new Integer(channel));
            promptNecessary = true;
         }
      }

      if (promptNecessary) {
         try {
            String usbPeripheralName;
            try {
               usbPeripheralName = USBPortInternal$Internal.getChannelName(channel);
            } catch (Throwable t) {
               usbPeripheralName = "USB";
               logEvent(1195593285);
               System.out.println("USB PT GET CHAN ERROR");
            }

            USBPasswordRedirectManager$USBPasswordRedirectDialog dialog = new USBPasswordRedirectManager$USBPasswordRedirectDialog(this, usbPeripheralName);
            BackgroundDialog.show(dialog);
            return;
         } catch (Throwable t) {
            logEvent(1130909765);
            System.out.println("USB PT ERROR");
            this.allowChannel(channel, false);
         }
      }
   }

   private void checkState(int state) {
      int connectionType = ITPolicy.getInteger(24, 37, 0);
      System.out.println("CheckState called. state: " + state + " connectionType: " + connectionType);
      boolean cableConnected = (state & 4) == 0;
      boolean deviceEnumerated = (state & 2) != 0 || (state & 16) != 0;
      if ((!cableConnected || connectionType != 1) && (!deviceEnumerated || connectionType != 1 && connectionType != 2)) {
         if (!this.isRadioAllowedOn()) {
            this.enableRadio();
         }
      } else {
         this.disableRadio();
      }
   }

   public static USBPasswordRedirectManager getInstance() {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      synchronized (ar) {
         _passwordManager = (USBPasswordRedirectManager)ar.get(-6654808410320396441L);
         if (_passwordManager == null) {
            _passwordManager = new USBPasswordRedirectManager();
            ar.put(-6654808410320396441L, _passwordManager);
         }
      }

      return _passwordManager;
   }

   public static void initialize() {
      getInstance();
   }

   public static void logEvent(int event) {
      EventLogger.logEvent(-2691377221057526315L, event, 0);
   }
}
