package net.rim.wica.runtime.persistence.internal.backup;

import net.rim.device.api.synchronization.ConverterUtilities;
import net.rim.device.api.util.DataBuffer;
import net.rim.wica.runtime.persistence.ApplicationSyncModel;
import net.rim.wica.runtime.provisioning.DeploymentDescriptor;
import net.rim.wica.runtime.util.SerializerUtil;

public class ApplicationSyncModelSerializer extends AbstractSerializer {
   private static ApplicationSyncModelSerializer _instance;
   private static final byte ID;
   private static final byte DESCRIPTOR;
   private static final byte PACKAGE_LOCATION;
   private static final byte COLLECTIONS;

   static ApplicationSyncModelSerializer getInstance() {
      if (_instance == null) {
         _instance = new ApplicationSyncModelSerializer();
      }

      return _instance;
   }

   static void nullInstance() {
      _instance = null;
   }

   @Override
   protected void serializeObject(DataBuffer buffer, Object obj) {
      ApplicationSyncModel model = (ApplicationSyncModel)obj;
      ConverterUtilities.writeLong(buffer, 0, model.getId());
      DeploymentDescriptorSerializer.getInstance().serialize(buffer, (byte)1, model.getDescriptor());
      SerializerUtil.writeString(buffer, (byte)2, model.getPackageLocation());
      IntPersistablePairSerializer.getInstance().serializeArray(buffer, (byte)3, model.getCollections());
   }

   @Override
   protected void deserializeObjectField(DataBuffer buffer, Object obj, int type) {
      ApplicationSyncModel model = (ApplicationSyncModel)obj;
      switch (type) {
         case -1:
            ConverterUtilities.skipField(buffer);
            return;
         case 0:
         default:
            model.setId(ConverterUtilities.readLong(buffer));
            return;
         case 1:
            model.setDescriptor((DeploymentDescriptor)DeploymentDescriptorSerializer.getInstance().deserialize(buffer));
            return;
         case 2:
            model.setPackageLocation(ConverterUtilities.readString(buffer));
            return;
         case 3:
            model.setCollections((IntPersistablePair[])IntPersistablePairSerializer.getInstance().deserializeArray(buffer));
      }
   }

   @Override
   protected Object createObject() {
      return new ApplicationSyncModel();
   }
}
