package net.rim.device.apps.internal.qm.peer;

import net.rim.device.api.ui.Field;

final class ContactTreeField$NewRequestBranch extends ContactTreeField$SortedBranch {
   private int _requestsCount;

   ContactTreeField$NewRequestBranch() {
      super._expanded = PeerData.isExpanded(ContactTreeField.access$1100());
   }

   @Override
   final void toggleExpansion() {
      super.toggleExpansion();
      PeerData.setExpanded(ContactTreeField.access$1100(), super._expanded);
   }

   @Override
   final boolean hideWhenEmpty() {
      return true;
   }

   @Override
   final String name() {
      Branch._sBuffer.setLength(0);
      Branch._sBuffer.append(String.valueOf(this._requestsCount));
      Branch._sBuffer.append(' ');
      Branch._sBuffer.append(PeerResources.getString(2043));
      return Branch._sBuffer.toString();
   }

   @Override
   public final void add(Field contactLeaf) {
      Object cookie = contactLeaf.getCookie();
      if (cookie instanceof PeerRequest) {
         this._requestsCount++;
      }

      super.add(contactLeaf);
   }

   @Override
   public final void delete(Field contactLeaf) {
      Object cookie = contactLeaf.getCookie();
      if (cookie instanceof PeerRequest) {
         this._requestsCount--;
      }

      super.delete(contactLeaf);
   }
}
