package net.rim.wica.runtime.provisioning;

import net.rim.device.api.util.Persistable;

public final class DeploymentDescriptor$Dependency implements Persistable {
   public int _type;
   public String _name;
   public String _version;
   public static final int RE_DEPENDENCY_TYPE = 1;

   public DeploymentDescriptor$Dependency() {
   }

   public DeploymentDescriptor$Dependency(int type, String name, String version) {
      this._type = type;
      this._name = name;
      this._version = version;
   }

   public final int getType() {
      return this._type;
   }

   public final String getName() {
      return this._name;
   }

   public final String getVersion() {
      return this._version;
   }
}
