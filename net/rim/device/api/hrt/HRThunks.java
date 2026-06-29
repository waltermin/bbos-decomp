package net.rim.device.api.hrt;

public interface HRThunks {
   long HRT_THUNKS_GUID;

   void displayEditor(HostRoutingTable var1, int var2);

   void sendRegistrationRequest();

   void sendRegistrationInfoRequest();

   void enableRequestThread(boolean var1);

   void setRegistrationServerPresent(boolean var1);

   void requestThreadAbort();

   boolean isRequestThreadIdle();

   void useRegistrationVersion(int var1);

   boolean toggleSendEFSPN();
}
