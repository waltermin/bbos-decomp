package net.rim.device.internal.firewall;

import net.rim.device.api.util.Persistable;

final class FirewallImpl$Setting implements Persistable {
   String _moduleName;
   int _appIndex;
   String _protocol;
   String _target;
   int _permission;
   byte[] _moduleHash;

   public FirewallImpl$Setting(String moduleName, int appIndex, String protocol, String target, int permission, byte[] moduleHash) {
      this._moduleName = moduleName;
      this._appIndex = appIndex;
      this._moduleHash = moduleHash;
      this._protocol = protocol;
      this._target = target;
      this._permission = permission;
   }
}
