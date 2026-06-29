package net.rim.wica.runtime.persistence;

import net.rim.device.api.util.Persistable;
import net.rim.wica.runtime.provisioning.DeploymentDescriptor;

public final class ApplicationSyncModel implements Persistable {
   private long _id;
   private DeploymentDescriptor _descriptor;
   private String _packageLocation;
   private CollectionSyncModel[] _collections;

   public final CollectionSyncModel[] getCollections() {
      return this._collections;
   }

   public final void setCollections(CollectionSyncModel[] collections) {
      this._collections = collections;
   }

   public final DeploymentDescriptor getDescriptor() {
      return this._descriptor;
   }

   public final void setDescriptor(DeploymentDescriptor descriptor) {
      this._descriptor = descriptor;
   }

   public final long getId() {
      return this._id;
   }

   public final void setId(long id) {
      this._id = id;
   }

   public final String getPackageLocation() {
      return this._packageLocation;
   }

   public final void setPackageLocation(String packageLocation) {
      this._packageLocation = packageLocation;
   }
}
