package net.rim.device.internal.firewall;

import java.util.Vector;
import net.rim.device.api.util.IntHashtable;
import net.rim.device.api.util.Persistable;
import net.rim.device.api.util.ToIntHashtable;
import net.rim.device.internal.system.ITPolicyInternal;

final class FirewallImpl$SettingStore implements Persistable {
   public int _override = 3;
   public Vector _settings = new Vector();
   public ToIntHashtable _pipeControl = new ToIntHashtable();
   public boolean _enabled = ITPolicyInternal.isITPolicyEnabled();
   public IntHashtable _blockings = new IntHashtable();
   public IntHashtable _droppings = new IntHashtable();

   public FirewallImpl$SettingStore() {
   }
}
