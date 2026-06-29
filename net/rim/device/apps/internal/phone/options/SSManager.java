package net.rim.device.apps.internal.phone.options;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.Phone;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.system.SIMCard;
import net.rim.device.apps.api.phone.VoiceServices;
import net.rim.device.apps.internal.phone.api.PhoneUtilities;
import net.rim.device.internal.ui.component.SimpleInputDialog;

public final class SSManager {
   private boolean _gotFeatureReadyMessage;
   private boolean _simValid;
   private SSMessageHandler _messageHandler;
   public static final int SS_OPTION_NOT_PROVISIONED = 0;
   public static final int SIM_NOT_READY = 100;
   private static final long GUID = -6825634774143615650L;
   private static SSManager _instance;
   private static boolean _isSimulator = DeviceInfo.isSimulator();
   private static Phone _phone = Phone.getInstance();

   private SSManager() {
      if (hasCentralizedMessageHandler()) {
         this._messageHandler = new SSMessageHandler();
      }
   }

   public static final void initialize() {
      getInstance();
      if (_instance._messageHandler != null) {
         VoiceServices.addPhoneEventListener(_instance._messageHandler);
      }
   }

   public static final synchronized SSManager getInstance() {
      if (_instance == null) {
         ApplicationRegistry reg = ApplicationRegistry.getApplicationRegistry();
         _instance = (SSManager)reg.get(-6825634774143615650L);
         if (_instance == null) {
            _instance = new SSManager();
            synchronized (reg) {
               reg.put(-6825634774143615650L, _instance);
            }
         }
      }

      return _instance;
   }

   public final boolean simReady() {
      return RadioInfo.getNetworkType() != 3 ? true : this._gotFeatureReadyMessage && this._simValid || _isSimulator;
   }

   static final boolean hasCentralizedMessageHandler() {
      return true;
   }

   public static final void suppressMessageDialogs(boolean suppress) {
      SSMessageHandler messageHandler = getInstance()._messageHandler;
      if (messageHandler != null) {
         messageHandler.suppressMessageDialogs(suppress);
      }
   }

   public static final void resetSuppressMessageDialogs() {
      SSMessageHandler messageHandler = getInstance()._messageHandler;
      if (messageHandler != null) {
         messageHandler.resetSuppressMessageDialogs();
      }
   }

   public static final String getCallBarringPasswordFromUser(String prompt) {
      SimpleInputDialog dlg = (SimpleInputDialog)(new Object(6, prompt, 4, 4, 0));
      dlg.setModal(true);
      dlg.show();
      return dlg.getText().trim();
   }

   public static final boolean isSSOptionProvisioned(int ssOptionFlags) {
      return (ssOptionFlags & 1) != 0 || (ssOptionFlags & 2) != 0;
   }

   public static final boolean isFDNAvailable() {
      try {
         return _phone.isFDNAvailable();
      } finally {
         ;
      }
   }

   public static final boolean isCallForwardUnconditionalActive() {
      int[] lineIds = PhoneUtilities.getAllLineIds();

      for (int i = lineIds.length - 1; i >= 0; i--) {
         if (isCallForwardUnconditionalActive(lineIds[i])) {
            return true;
         }
      }

      return false;
   }

   public static final boolean isCallForwardUnconditionalActive(int line) {
      boolean isActive = getInstance()._messageHandler.isCallForwardUnconditionalActive(line);
      System.out.println(((StringBuffer)(new Object("SSMgr CFU = "))).append(isActive).toString());
      return isActive;
   }

   public static final void updateCallForwardingUnconditionalActive(boolean active) {
      CFU.update(active);
   }

   public static final boolean callBarringSupported() {
      if (!PhoneUtilities.platformSupportsCallBarring()) {
         return false;
      }

      try {
         int cspFlags = SIMCard.getCSPFlags(1);
         int[] types = CallBarringOption.CSP_TYPES_MAP;

         for (int i = 0; i < types.length; i++) {
            if ((cspFlags & types[i]) != 0) {
               return true;
            }
         }

         return false;
      } finally {
         ;
      }
   }

   public static final boolean callForwardingSupported() {
      if (_isSimulator) {
         return true;
      }

      int supportedFeatures = VoiceServices.getVoiceNetworkCapabilities();
      return (supportedFeatures & 512) != 0 || (supportedFeatures & 1024) != 0 || (supportedFeatures & 2048) != 0 || (supportedFeatures & 4096) != 0;
   }

   public static final boolean callWaitingSupported() {
      switch (RadioInfo.getNetworkType()) {
         case 3:
            return true;
         case 4:
         case 5:
         default:
            if ((VoiceServices.getVoiceNetworkCapabilities() & 1048576) != 0) {
               return true;
            }

            return false;
         case 6:
            return false;
      }
   }

   public final void onFeatureReady() {
      getInstance()._gotFeatureReadyMessage = true;
   }

   public final void onSIMValid() {
      getInstance()._simValid = true;
   }

   public final void onSIMInvalid() {
      getInstance()._simValid = false;
      getInstance()._gotFeatureReadyMessage = false;
   }
}
