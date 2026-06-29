package net.rim.wica.runtime.provisioning;

import net.rim.wica.runtime.persistence.CollectionSyncModel;

public interface ProvisioningService {
   int NOTIFY_NOONE;
   int NOTIFY_AG;
   int NOTIFY_PROV_WICLET;

   void installApplication(
      DeploymentDescriptor var1, byte[] var2, long var3, int var5, boolean var6, boolean var7, int var8, boolean var9, CollectionSyncModel[] var10
   );

   void installApplication(DeploymentDescriptor var1, String var2, long var3, int var5, boolean var6, boolean var7, boolean var8, CollectionSyncModel[] var9);

   void installApplication(
      DeploymentDescriptor var1, String var2, long var3, int var5, boolean var6, boolean var7, int var8, boolean var9, CollectionSyncModel[] var10
   );
}
