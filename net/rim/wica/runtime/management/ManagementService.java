package net.rim.wica.runtime.management;

public interface ManagementService {
   int REGISTERED;
   int UNREGISTERED;
   int UPGRADE_AVAILABLE;
   int UPGRADE_REQUIRED;
   int UPGRADE_SUCCESS;
   int UPGRADE_DOWNLOAD_SUCCESS;
   int UPGRADE_DOWNLOAD_FAILED;
   int UPGRADE_INSTALLATION_SUCCESS;
   int UPGRADE_INSTALLATION_FAILED_NO_SPACE;
   int UPGRADE_INSTALLATION_FAILED_INVALID_COD;
   int REGISTRATION_FAILED;
   int ACTIVATING;
   int ACTIVATION_PROGRESS_START_HANDSHAKE;
   int ACTIVATION_PROGRESS_HANDSHAKE_COMPLETE;
   int ACTIVATION_PROGRESS_RESTATUS_COMPLETE;
   int ACTIVATION_PROGRESS_POLICIES_RECEIVED;
   String MDS_SB_CID;
   String PUBLIC_MDS_SB_CID;

   RuntimeInfo getRuntimeInfo();

   void sendREStatusMessage(long var1);

   String getIPPPUid();

   void register(AGInfo var1);

   void unregister();

   void cancelRegistration();

   void defaultWicletsInstalled();

   AGInfo getAGInfo(long var1);
}
