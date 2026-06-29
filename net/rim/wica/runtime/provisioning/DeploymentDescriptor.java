package net.rim.wica.runtime.provisioning;

import net.rim.device.api.util.Persistable;
import net.rim.wica.runtime.messaging.MessageException;
import net.rim.wica.runtime.messaging.ReadableDataStream;
import net.rim.wica.runtime.messaging.WritableDataStream;

public final class DeploymentDescriptor implements Persistable {
   private String _uri;
   private String _version;
   private String _name;
   private String _vendor;
   private String _description;
   private String _size;
   private String _type;
   private String _installNotifyUrl;
   private String _bundleUrl;
   private DeploymentDescriptor$Dependency[] _dependencies;
   private String[] _languages;
   private String _targetFolder;
   private String _dedicatedServerUrl;
   private boolean _endorsed;
   private boolean _multiDomain;

   public final String getUri() {
      return this._uri;
   }

   public final void setUri(String uri) {
      this._uri = uri;
   }

   public final String getVersion() {
      return this._version;
   }

   public final void setVersion(String version) {
      this._version = version;
   }

   public final String getName() {
      return this._name;
   }

   public final void setName(String name) {
      this._name = name;
   }

   public final String getVendor() {
      return this._vendor;
   }

   public final void setVendor(String vendor) {
      this._vendor = vendor;
   }

   public final String getDescription() {
      return this._description;
   }

   public final void setDescription(String description) {
      this._description = description;
   }

   public final String getSize() {
      return this._size;
   }

   public final void setSize(String size) {
      this._size = size;
   }

   public final String getType() {
      return this._type;
   }

   public final void setType(String type) {
      this._type = type;
   }

   public final String getInstallNotifyUrl() {
      return this._installNotifyUrl;
   }

   public final void setInstallNotifyUrl(String installNotifyUrl) {
      this._installNotifyUrl = installNotifyUrl;
   }

   public final String getBundleUrl() {
      return this._bundleUrl;
   }

   public final void setBundleUrl(String bundleUrl) {
      this._bundleUrl = bundleUrl;
   }

   public final DeploymentDescriptor$Dependency[] getDependencies() {
      return this._dependencies;
   }

   public final void setDependencies(DeploymentDescriptor$Dependency[] dependencies) {
      this._dependencies = dependencies;
   }

   public final String[] getLanguages() {
      return this._languages;
   }

   public final void setLanguages(String[] languages) {
      this._languages = languages;
   }

   public final String getTargetFolder() {
      return this._targetFolder;
   }

   public final void setTargetFolder(String targetFolder) {
      this._targetFolder = targetFolder;
   }

   public final String getDedicatedAgUrl() {
      return this._dedicatedServerUrl;
   }

   public final void setDedicatedAgUrl(String dedicatedServerUrl) {
      this._dedicatedServerUrl = dedicatedServerUrl;
   }

   public final boolean isEndorsed() {
      return this._endorsed;
   }

   public final void setEndorsed(boolean endorsed) {
      this._endorsed = endorsed;
   }

   public final boolean isMultiDomain() {
      return this._multiDomain;
   }

   public final void setMultiDomain(boolean multiDomain) {
      this._multiDomain = multiDomain;
   }

   public static final DeploymentDescriptor readFromStream(ReadableDataStream readStream) {
      if (!readStream.startComponentRead()) {
         throw new MessageException("Format Error");
      }

      DeploymentDescriptor descriptor = new DeploymentDescriptor();
      int numDependencies = readStream.startComponentArrayRead();
      descriptor._dependencies = new DeploymentDescriptor$Dependency[numDependencies];

      for (int i = 0; i < numDependencies; i++) {
         if (readStream.startComponentRead()) {
            String name = readStream.readString();
            int type = readStream.readInt();
            String version = readStream.readString();
            descriptor._dependencies[i] = new DeploymentDescriptor$Dependency(type, name, version);
         }
      }

      descriptor._vendor = readStream.readString();
      descriptor._type = readStream.readString();
      descriptor._dedicatedServerUrl = readStream.readString();
      descriptor._targetFolder = readStream.readString();
      descriptor._languages = readStream.readStringArray();
      descriptor._version = readStream.readString();
      descriptor._bundleUrl = readStream.readString();
      descriptor._uri = readStream.readString();
      descriptor._name = readStream.readString();
      descriptor._description = readStream.readString();
      descriptor._installNotifyUrl = readStream.readString();
      descriptor._multiDomain = readStream.readBoolean();
      descriptor._size = readStream.readString();
      descriptor._endorsed = readStream.readBoolean();
      return descriptor;
   }

   public static final void writeToStream(DeploymentDescriptor descriptor, WritableDataStream writeStream) {
      writeStream.startComponentWrite(false);
      int numDependencies = descriptor._dependencies.length;
      writeStream.startComponentArrayWrite(numDependencies);

      for (int i = 0; i < numDependencies; i++) {
         writeStream.startComponentWrite(false);
         writeStream.writeString(descriptor._dependencies[i].getName());
         writeStream.writeInt(descriptor._dependencies[i].getType());
         writeStream.writeString(descriptor._dependencies[i].getVersion());
      }

      writeStream.writeString(descriptor._vendor);
      writeStream.writeString(descriptor._type);
      writeStream.writeString(descriptor._dedicatedServerUrl);
      writeStream.writeString(descriptor._targetFolder);
      writeStream.writeStringArray(descriptor._languages);
      writeStream.writeString(descriptor._version);
      writeStream.writeString(descriptor._bundleUrl);
      writeStream.writeString(descriptor._uri);
      writeStream.writeString(descriptor._name);
      writeStream.writeString(descriptor._description);
      writeStream.writeString(descriptor._installNotifyUrl);
      writeStream.writeBoolean(descriptor._multiDomain);
      writeStream.writeString(descriptor._size);
      writeStream.writeBoolean(descriptor._endorsed);
   }
}
