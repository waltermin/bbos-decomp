package net.rim.device.api.system;

public interface ExtendedRadioStatusListener extends RadioStatusListener {
   int NET_SELECT_FAIL = 1;
   int NET_SELECT_BUSY = 2;
   int NET_SELECT_REFUSED = 3;
   int NET_SELECT_NO_CONTROL = 4;
   int NET_SELECT_INVALID = 5;
   int NET_SCAN_STALL = 0;
   int NET_SCAN_RESTART = 1;

   void networkSelectionFailed(int var1, int var2);

   void flowControlStatusChange(int var1);

   void networkScanStatus(int var1);

   void networkNameChangeViaNITZ(int var1, int var2);
}
