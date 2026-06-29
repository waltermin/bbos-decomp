package net.rim.wica.runtime.persistence.internal.backup;

import net.rim.device.api.synchronization.ConverterUtilities;
import net.rim.device.api.util.DataBuffer;
import net.rim.wica.runtime.lifecycle.UpgradeTaskInfo;
import net.rim.wica.runtime.provisioning.DeploymentDescriptor;
import net.rim.wica.runtime.util.SerializerUtil;

final class UpgradeTaskInfoSerializer extends AbstractSerializer {
   private static UpgradeTaskInfoSerializer _instance;
   private static final byte DEPLOYMENT_DESCRIPTOR;
   private static final byte ACTION;
   private static final byte EXPIRY_DATE;
   private static final byte SHOWN_DIALOG;

   static final UpgradeTaskInfoSerializer getInstance() {
      if (_instance == null) {
         _instance = new UpgradeTaskInfoSerializer();
      }

      return _instance;
   }

   static final void nullInstance() {
      _instance = null;
   }

   @Override
   protected final void deserializeObjectField(DataBuffer buffer, Object obj, int type) {
      UpgradeTaskInfo upgradeTask = (UpgradeTaskInfo)obj;
      switch (type) {
         case -1:
            ConverterUtilities.skipField(buffer);
            return;
         case 0:
         default:
            upgradeTask.setDeploymentDescriptor((DeploymentDescriptor)DeploymentDescriptorSerializer.getInstance().deserialize(buffer));
            return;
         case 1:
            upgradeTask.setAction(ConverterUtilities.readInt(buffer));
            return;
         case 2:
            upgradeTask.setExpiryDate(ConverterUtilities.readLong(buffer));
            return;
         case 3:
            upgradeTask.setShownDialog(SerializerUtil.readBoolean(buffer));
      }
   }

   @Override
   protected final Object createObject() {
      return new UpgradeTaskInfo();
   }

   private UpgradeTaskInfoSerializer() {
   }
}
