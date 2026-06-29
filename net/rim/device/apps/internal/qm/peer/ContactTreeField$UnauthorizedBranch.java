package net.rim.device.apps.internal.qm.peer;

import net.rim.device.apps.internal.qm.resource.QmResources;

final class ContactTreeField$UnauthorizedBranch extends ContactTreeField$SortedBranch {
   ContactTreeField$UnauthorizedBranch() {
      super._expanded = PeerData.isExpanded(ContactTreeField.access$800());
   }

   @Override
   final void toggleExpansion() {
      super.toggleExpansion();
      PeerData.setExpanded(ContactTreeField.access$800(), super._expanded);
   }

   @Override
   final boolean hideWhenEmpty() {
      return true;
   }

   @Override
   final String name() {
      return QmResources.getString(70);
   }
}
