package net.rim.device.cldc.io.datarecovery;

public interface DataRecoveryListener {
   int EVENT_CONDITION_NOMINAL = 1;
   int EVENT_RECOVERY_ACTION = 2;
   int EVENT_RELAY_UNREACHABLE = 3;

   void dataRecoveryEventOccurred(int var1, int var2);
}
