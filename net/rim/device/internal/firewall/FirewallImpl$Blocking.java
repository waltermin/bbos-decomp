package net.rim.device.internal.firewall;

import net.rim.device.api.util.Persistable;

final class FirewallImpl$Blocking implements Persistable {
   byte _type;
   boolean _enabled;
   int _count;

   public FirewallImpl$Blocking(byte type, boolean enabled, int count) {
      this._type = type;
      this._enabled = enabled;
      this._count = count;
   }
}
