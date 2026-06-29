package net.rim.device.api.system;

public interface ExtendedRadioStatusListener extends RadioStatusListener {
   int NET_SELECT_FAIL;
   int NET_SELECT_BUSY;
   int NET_SELECT_REFUSED;
   int NET_SELECT_NO_CONTROL;
   int NET_SELECT_INVALID;
   int NET_SCAN_STALL;
   int NET_SCAN_RESTART;

   void networkSelectionFailed(int var1, int var2);

   void flowControlStatusChange(int var1);

   void networkScanStatus(int var1);

   void networkNameChangeViaNITZ(int var1, int var2);
}
