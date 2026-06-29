package net.rim.wica.runtime.persistence.internal.backup;

import net.rim.device.api.synchronization.ConverterUtilities;
import net.rim.device.api.util.DataBuffer;
import net.rim.wica.runtime.provisioning.DeploymentDescriptor$Dependency;
import net.rim.wica.runtime.util.SerializerUtil;

final class DependencySerializer extends AbstractArraySerializer {
   private static DependencySerializer _instance;
   private static final byte TYPE = 0;
   private static final byte NAME = 1;
   private static final byte VERSION = 2;

   static final DependencySerializer getInstance() {
      if (_instance == null) {
         _instance = new DependencySerializer();
      }

      return _instance;
   }

   static final void nullInstance() {
      _instance = null;
   }

   @Override
   protected final void serializeObject(DataBuffer buffer, Object obj) {
      DeploymentDescriptor$Dependency dependency = (DeploymentDescriptor$Dependency)obj;
      ConverterUtilities.writeInt(buffer, 0, dependency.getType());
      SerializerUtil.writeString(buffer, (byte)1, dependency.getName());
      SerializerUtil.writeString(buffer, (byte)2, dependency.getVersion());
   }

   @Override
   protected final void deserializeObjectField(DataBuffer buffer, Object obj, int type) {
      DeploymentDescriptor$Dependency dependency = (DeploymentDescriptor$Dependency)obj;
      switch (type) {
         case -1:
            ConverterUtilities.skipField(buffer);
            return;
         case 0:
         default:
            dependency._type = ConverterUtilities.readInt(buffer);
            return;
         case 1:
            dependency._name = ConverterUtilities.readString(buffer);
            return;
         case 2:
            dependency._version = ConverterUtilities.readString(buffer);
      }
   }

   @Override
   protected final Object createObject() {
      return new DeploymentDescriptor$Dependency();
   }

   @Override
   protected final Object[] createArray(int size) {
      return new DeploymentDescriptor$Dependency[size];
   }

   private DependencySerializer() {
   }
}
