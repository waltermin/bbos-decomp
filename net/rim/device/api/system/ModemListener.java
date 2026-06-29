package net.rim.device.api.system;

public interface ModemListener extends RadioListener {
   int NETWORK_SELECTION_MODE_CHANGED;
   int NETWORK_DISPLAY_NAME_QUERY;
   int RADIO_NETWORK_CHANGE_SUCCESS;
   int RADIO_NETWORK_CHANGE_PHONE_IN_USE;
   int RADIO_NETWORK_CHANGE_INVALID_OPERATING_MODE;
   int RADIO_NETWORK_CHANGE_GENERAL_ERROR;

   void networkSelectionModeChanged(int var1);

   void queryNetworkDisplayName(int var1);

   void networkChangeResult(int var1, int var2);
}
