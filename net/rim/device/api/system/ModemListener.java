package net.rim.device.api.system;

public interface ModemListener extends RadioListener {
   int NETWORK_SELECTION_MODE_CHANGED = 1680;
   int NETWORK_DISPLAY_NAME_QUERY = 1681;
   int RADIO_NETWORK_CHANGE_SUCCESS = 1;
   int RADIO_NETWORK_CHANGE_PHONE_IN_USE = 2;
   int RADIO_NETWORK_CHANGE_INVALID_OPERATING_MODE = 3;
   int RADIO_NETWORK_CHANGE_GENERAL_ERROR = 4;

   void networkSelectionModeChanged(int var1);

   void queryNetworkDisplayName(int var1);

   void networkChangeResult(int var1, int var2);
}
