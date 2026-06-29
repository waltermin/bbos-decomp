package net.rim.device.apps.internal.passwordkeeper;

import java.util.Vector;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.Persistable;

public final class PasswordKeeperVector extends Vector implements Persistable {
   public final void sort() {
      Arrays.sort(super.elementData, new PasswordKeeperComparator());
   }
}
