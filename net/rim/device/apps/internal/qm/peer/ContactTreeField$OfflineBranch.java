package net.rim.device.apps.internal.qm.peer;

import net.rim.device.apps.internal.qm.resource.QmResources;

final class ContactTreeField$OfflineBranch extends ContactTreeField$SortedBranch {
   ContactTreeField$OfflineBranch() {
      super._expanded = PeerData.isExpanded(ContactTreeField.access$900());
   }

   @Override
   final void toggleExpansion() {
      super.toggleExpansion();
      PeerData.setExpanded(ContactTreeField.access$900(), super._expanded);
   }

   @Override
   final boolean hideWhenEmpty() {
      return true;
   }

   @Override
   final String name() {
      return QmResources.getString(50);
   }
}
