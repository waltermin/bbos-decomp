package net.rim.device.cldc.io.datarecovery;

public interface DataRecoveryListener {
   int EVENT_CONDITION_NOMINAL;
   int EVENT_RECOVERY_ACTION;
   int EVENT_RELAY_UNREACHABLE;

   void dataRecoveryEventOccurred(int var1, int var2);
}
