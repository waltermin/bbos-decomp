package net.rim.wica.runtime.persistence.internal.backup;

import net.rim.device.api.synchronization.ConverterUtilities;
import net.rim.device.api.util.DataBuffer;
import net.rim.wica.runtime.provisioning.DeploymentDescriptor;
import net.rim.wica.runtime.provisioning.DeploymentDescriptor$Dependency;
import net.rim.wica.runtime.util.SerializerUtil;

final class DeploymentDescriptorSerializer extends AbstractSerializer {
   private static DeploymentDescriptorSerializer _instance;
   private static final byte URI;
   private static final byte VERSION;
   private static final byte NAME;
   private static final byte VENDOR;
   private static final byte DESCRIPTION;
   private static final byte SIZE;
   private static final byte TYPE;
   private static final byte INSTALL_NOTIFY_URL;
   private static final byte BUNDLE_URL;
   private static final byte DEPENDENCIES;
   private static final byte LANGUAGES;
   private static final byte TARGET_FOLDER;
   private static final byte DEDICATED_AG_URL;
   private static final byte ENDORSED;
   private static final byte MULTI_DOMAIN;

   static final DeploymentDescriptorSerializer getInstance() {
      if (_instance == null) {
         _instance = new DeploymentDescriptorSerializer();
      }

      return _instance;
   }

   static final void nullInstance() {
      _instance = null;
   }

   @Override
   protected final void serializeObject(DataBuffer buffer, Object obj) {
      DeploymentDescriptor deployment = (DeploymentDescriptor)obj;
      SerializerUtil.writeString(buffer, (byte)0, deployment.getUri());
      SerializerUtil.writeString(buffer, (byte)1, deployment.getVersion());
      SerializerUtil.writeString(buffer, (byte)2, deployment.getName());
      SerializerUtil.writeString(buffer, (byte)3, deployment.getVendor());
      SerializerUtil.writeString(buffer, (byte)4, deployment.getDescription());
      SerializerUtil.writeString(buffer, (byte)5, deployment.getSize());
      SerializerUtil.writeString(buffer, (byte)6, deployment.getType());
      SerializerUtil.writeString(buffer, (byte)7, deployment.getInstallNotifyUrl());
      SerializerUtil.writeString(buffer, (byte)8, deployment.getBundleUrl());
      DependencySerializer.getInstance().serializeArray(buffer, (byte)9, deployment.getDependencies());
      StringSerializer.getInstance().serializeArray(buffer, (byte)10, deployment.getLanguages());
      SerializerUtil.writeString(buffer, (byte)11, deployment.getTargetFolder());
      SerializerUtil.writeString(buffer, (byte)12, deployment.getDedicatedAgUrl());
      SerializerUtil.writeBoolean(buffer, (byte)13, deployment.isEndorsed());
      SerializerUtil.writeBoolean(buffer, (byte)14, deployment.isMultiDomain());
   }

   @Override
   protected final void deserializeObjectField(DataBuffer buffer, Object obj, int type) {
      DeploymentDescriptor deployment = (DeploymentDescriptor)obj;
      switch (type) {
         case -1:
            ConverterUtilities.skipField(buffer);
            return;
         case 0:
         default:
            deployment.setUri(ConverterUtilities.readString(buffer));
            return;
         case 1:
            deployment.setVersion(ConverterUtilities.readString(buffer));
            return;
         case 2:
            deployment.setName(ConverterUtilities.readString(buffer));
            return;
         case 3:
            deployment.setVendor(ConverterUtilities.readString(buffer));
            return;
         case 4:
            deployment.setDescription(ConverterUtilities.readString(buffer));
            return;
         case 5:
            deployment.setSize(ConverterUtilities.readString(buffer));
            return;
         case 6:
            deployment.setType(ConverterUtilities.readString(buffer));
            return;
         case 7:
            deployment.setInstallNotifyUrl(ConverterUtilities.readString(buffer));
            return;
         case 8:
            deployment.setBundleUrl(ConverterUtilities.readString(buffer));
            return;
         case 9:
            deployment.setDependencies((DeploymentDescriptor$Dependency[])DependencySerializer.getInstance().deserializeArray(buffer));
            return;
         case 10:
            deployment.setLanguages(StringSerializer.getInstance().deserializeArray(buffer));
            return;
         case 11:
            deployment.setTargetFolder(ConverterUtilities.readString(buffer));
            return;
         case 12:
            deployment.setDedicatedAgUrl(ConverterUtilities.readString(buffer));
            return;
         case 13:
            deployment.setEndorsed(SerializerUtil.readBoolean(buffer));
            return;
         case 14:
            deployment.setMultiDomain(SerializerUtil.readBoolean(buffer));
      }
   }

   @Override
   protected final Object createObject() {
      return new DeploymentDescriptor();
   }

   private DeploymentDescriptorSerializer() {
   }
}
