package net.rim.device.apps.internal.activation;

import net.rim.vm.Persistable;
import net.rim.vm.Persistence;

final class OTASyncServiceProgress$OTASyncServiceState implements Persistable {
   private int _syncState;
   private boolean _pimConfigRequestRecieved;

   private OTASyncServiceProgress$OTASyncServiceState() {
   }

   private final void commitState() {
      Persistence.commit(this, true);
   }

   OTASyncServiceProgress$OTASyncServiceState(OTASyncServiceProgress$1 x0) {
      this();
   }
}
