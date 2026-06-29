package net.rim.device.apps.internal.qm.peer;

import net.rim.device.apps.internal.qm.peer.common.QmVerb;

class PeerVerb extends QmVerb {
   PeerVerb(int ordering, int id) {
      super(ordering, id);
   }

   @Override
   public String toString() {
      return PeerResources.getString(super._id);
   }

   @Override
   protected void invoke() {
      QmVerb._screen.invokeVerbSpecial(super._id);
   }
}
