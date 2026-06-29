package net.rim.device.apps.internal.qm.peer;

import net.rim.device.apps.internal.qm.resource.QmResources;

final class ContactTreeField$PendingBranch extends ContactTreeField$SortedBranch {
   ContactTreeField$PendingBranch() {
      super._expanded = PeerData.isExpanded(ContactTreeField.access$1000());
   }

   @Override
   final void toggleExpansion() {
      super.toggleExpansion();
      PeerData.setExpanded(ContactTreeField.access$1000(), super._expanded);
   }

   @Override
   final boolean hideWhenEmpty() {
      return true;
   }

   @Override
   final String name() {
      return QmResources.getString(56);
   }
}
