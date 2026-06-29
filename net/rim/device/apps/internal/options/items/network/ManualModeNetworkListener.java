package net.rim.device.apps.internal.options.items.network;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.system.RadioStatusListener;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.DialogClosedListener;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.internal.options.resources.OptionsResources;
import net.rim.device.internal.proxy.Proxy;
import net.rim.device.internal.system.RadioInternal;
import net.rim.vm.PersistentInteger;

public final class ManualModeNetworkListener implements RadioStatusListener, DialogClosedListener {
   private boolean _active;
   public static final int SELECT_AUTOMATIC_MODE_WHEN_MANUAL_SELECTED_NETWORK_UNVAILABLE_FLAG = 1;
   public static final int DONT_PROMPT_USER_FLAG = 2;
   private static final long MANUAL_MODE_LISTENER_STORE_ID = 5218202731973538809L;
   private static int _persistentStoreId = PersistentInteger.getId(5218202731973538809L, 0);
   public static final long GUID = 6080671377848507991L;
   private static final int SWITCH_TO_AUTOMATIC_BY_PROMPT_LOG = 1396790608;
   private static final int SWITCH_TO_AUTOMATIC_AUTOMATICALLY_LOG = 1396790593;
   private static ManualModeNetworkListener _instance;

   public ManualModeNetworkListener() {
      Proxy proxy = Proxy.getInstance();
      proxy.addRadioListener(1, this);
      this.setActive(true);
   }

   public static final ManualModeNetworkListener getInstance() {
      if (_instance == null) {
         ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
         _instance = (ManualModeNetworkListener)ar.getOrWaitFor(6080671377848507991L);
         if (_instance == null) {
            _instance = new ManualModeNetworkListener();
            ar.put(6080671377848507991L, _instance);
         }
      }

      return _instance;
   }

   public final void setActive(boolean turnOn) {
      this._active = turnOn;
   }

   public final void setFlags(int flags) {
      PersistentInteger.set(_persistentStoreId, flags);
   }

   public final int getFlags() {
      return PersistentInteger.get(_persistentStoreId);
   }

   @Override
   public final void networkStarted(int networkId, int service) {
      this.checkManuallySelectedNetwork(networkId, service);
   }

   @Override
   public final void radioTurnedOff() {
   }

   @Override
   public final void networkServiceChange(int networkId, int service) {
      this.checkManuallySelectedNetwork(networkId, service);
   }

   @Override
   public final void signalLevel(int level) {
   }

   @Override
   public final void baseStationChange() {
   }

   @Override
   public final void pdpStateChange(int apn, int state, int cause) {
   }

   @Override
   public final void networkStateChange(int state) {
   }

   @Override
   public final void networkScanComplete(boolean success) {
   }

   @Override
   public final void dialogClosed(Dialog dialog, int choice) {
      int flags = 0;
      if (dialog.isDontAskAgainChecked()) {
         flags |= 2;
      }

      int manuallySelNetId = RadioInternal.getManuallySelectedNetworkID();
      if (dialog.getSelectedValue() == 4) {
         flags |= 1;

         label27:
         try {
            RadioInternal.setNetworkSelectionMode(0);
            this.logEvent(1396790608, manuallySelNetId);
         } finally {
            break label27;
         }
      }

      this.setFlags(flags);
   }

   private final void checkManuallySelectedNetwork(int networkId, int service) {
      if ((RadioInternal.getActiveRadios() & 1) != 0
         && this._active
         && RadioInternal.getNetworkSelectionMode() == 3
         && (RadioInternal.getAvailableNetworkSelectionModes() & 1) != 0) {
         int manuallySelNetId = RadioInternal.getManuallySelectedNetworkID();
         if (manuallySelNetId != -1 && RadioInfo.getNumberOfNetworks() > 0) {
            int manuallySelMCC = manuallySelNetId & 65535;
            if (manuallySelMCC == (networkId & 65535)) {
               return;
            }

            label109:
            try {
               for (int i = RadioInfo.getNumberOfNetworks() - 1; i >= 0; i--) {
                  if (RadioInfo.getMCC(i) == manuallySelMCC) {
                     return;
                  }
               }
            } finally {
               break label109;
            }

            int flags = this.getFlags();
            if ((flags & 2) == 0) {
               Dialog dialog = (Dialog)(new Object(3, OptionsResources.getString(2065), 0, Bitmap.getPredefinedBitmap(1), 33554432));
               dialog.setDontAskAgainPrompt(true);
               dialog.setDialogClosedListener(this);
               dialog.show(10);
               return;
            }

            if ((flags & 1) != 0) {
               try {
                  RadioInternal.setNetworkSelectionMode(0);
                  this.logEvent(1396790593, manuallySelNetId);
                  return;
               } finally {
                  return;
               }
            }
         }
      }
   }

   private final void logEvent(int code, int networkId) {
      StringBuffer tempBuffer = (StringBuffer)(new Object(StringUtilities.intToString(code)));
      tempBuffer.append(":");
      tempBuffer.append(Integer.toHexString(networkId));
      EventLogger.logEvent(-4272982832973947638L, tempBuffer.toString().getBytes(), 0);
   }
}
