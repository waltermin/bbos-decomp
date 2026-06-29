package net.rim.wica.runtime.management;

public interface ManagementService {
   int REGISTERED = 1;
   int UNREGISTERED = 2;
   int UPGRADE_AVAILABLE = 3;
   int UPGRADE_REQUIRED = 4;
   int UPGRADE_SUCCESS = 5;
   int UPGRADE_DOWNLOAD_SUCCESS = 6;
   int UPGRADE_DOWNLOAD_FAILED = 7;
   int UPGRADE_INSTALLATION_SUCCESS = 8;
   int UPGRADE_INSTALLATION_FAILED_NO_SPACE = 9;
   int UPGRADE_INSTALLATION_FAILED_INVALID_COD = 10;
   int REGISTRATION_FAILED = 11;
   int ACTIVATING = 12;
   int ACTIVATION_PROGRESS_START_HANDSHAKE = 0;
   int ACTIVATION_PROGRESS_HANDSHAKE_COMPLETE = 1;
   int ACTIVATION_PROGRESS_RESTATUS_COMPLETE = 2;
   int ACTIVATION_PROGRESS_POLICIES_RECEIVED = 3;
   String MDS_SB_CID = "MDS";
   String PUBLIC_MDS_SB_CID = "PMDS";

   RuntimeInfo getRuntimeInfo();

   void sendREStatusMessage(long var1);

   String getIPPPUid();

   void register(AGInfo var1);

   void unregister();

   void cancelRegistration();

   void defaultWicletsInstalled();

   AGInfo getAGInfo(long var1);
}
