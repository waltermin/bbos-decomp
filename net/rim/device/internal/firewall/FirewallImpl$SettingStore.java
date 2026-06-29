package net.rim.device.internal.firewall;

import java.util.Vector;
import net.rim.device.api.util.IntHashtable;
import net.rim.device.api.util.Persistable;
import net.rim.device.api.util.ToIntHashtable;
import net.rim.device.internal.system.ITPolicyInternal;

final class FirewallImpl$SettingStore implements Persistable {
   public int _override = 3;
   public Vector _settings = (Vector)(new Object());
   public ToIntHashtable _pipeControl = (ToIntHashtable)(new Object());
   public boolean _enabled = ITPolicyInternal.isITPolicyEnabled();
   public IntHashtable _blockings = (IntHashtable)(new Object());
   public IntHashtable _droppings = (IntHashtable)(new Object());

   public FirewallImpl$SettingStore() {
   }
}
