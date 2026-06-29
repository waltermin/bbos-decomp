package net.rim.device.apps.internal.qm.peer;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.apps.internal.qm.peer.common.EllipsesText;

final class ContactTreeField$ContactListBranch extends ContactTreeField$SortedBranch {
   private StringBuffer _label = (StringBuffer)(new Object(16));
   private int _availableCount = 0;

   ContactTreeField$ContactListBranch(PeerContactList contactList) {
      this.setCookie(contactList);
      super._expanded = PeerData.isExpanded(contactList);
   }

   @Override
   final void toggleExpansion() {
      super.toggleExpansion();
      PeerData.setExpanded((PeerContactList)this.getCookie(), super._expanded);
   }

   final void lock() {
      this._label.setLength(0);
      this._label.setLength(this._label.capacity());
      this._label.setLength(0);
   }

   @Override
   final void clear() {
      this._availableCount = 0;
      this._label.setLength(0);
   }

   @Override
   public final void doInvalidate(boolean andRelated) {
      this._label.setLength(0);
      this.invalidate();
   }

   final void format() {
      Object cookie = this.getCookie();
      if (cookie != null) {
         this._label.append(cookie.toString());
      }

      this._label.append(" (");
      this._label.append(this._availableCount);
      this._label.append("/");
      this._label.append(((PeerContactList)cookie).getContactsCount());
      this._label.append(")");
      this.updateLayout();
   }

   @Override
   final void drawText(Graphics graphics, int x, int y, int width) {
      if (this._label.length() == 0) {
         this.format();
      }

      EllipsesText.draw(graphics, this._label, x, y, width);
   }

   @Override
   public final String name() {
      return this._label.toString();
   }

   @Override
   public final boolean isFocusable() {
      return this.getHeight() > 0;
   }

   @Override
   public final void add(Field contactLeaf) {
      Object cookie = contactLeaf.getCookie();
      if (cookie instanceof PeerContact) {
         this._availableCount++;
         this._label.setLength(0);
      }

      super.add(contactLeaf);
   }

   @Override
   public final void delete(Field contactLeaf) {
      Object cookie = contactLeaf.getCookie();
      if (cookie instanceof PeerContact) {
         this._availableCount--;
         this._label.setLength(0);
      }

      super.delete(contactLeaf);
   }
}
