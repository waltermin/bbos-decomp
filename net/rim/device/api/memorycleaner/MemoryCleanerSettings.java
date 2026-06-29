package net.rim.device.api.memorycleaner;

import net.rim.device.api.util.Persistable;

final class MemoryCleanerSettings implements Persistable {
   public boolean _userCleanEnabled;
   public boolean _cleanWhenHolstered;
   public boolean _cleanWhenIdle;
   public boolean _showAppOnRibbon;
   public long _idleTimeoutSeconds;

   public MemoryCleanerSettings() {
   }
}
