package net.rim.device.apps.internal.qm.peer;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.apps.internal.qm.peer.common.EllipsesText;
import net.rim.device.apps.internal.qm.resource.QmResources;

final class ContactTreeField$ConversationBranch extends Branch {
   int _unreadCount;
   private StringBuffer _label = (StringBuffer)(new Object(16));

   private ContactTreeField$ConversationBranch() {
      super._expanded = PeerData.isExpanded(ContactTreeField.access$600());
   }

   @Override
   final void toggleExpansion() {
      super.toggleExpansion();
      PeerData.setExpanded(ContactTreeField.access$600(), super._expanded);
   }

   private final void lock() {
      this._label.setLength(0);
      this._label.setLength(this._label.capacity());
      this._label.setLength(0);
   }

   @Override
   final String name() {
      return QmResources.getString(1);
   }

   @Override
   final void clear() {
      this._unreadCount = 0;
      this._label.setLength(0);
      super.clear();
   }

   final void incrementUnreadCount(boolean increment) {
      this._unreadCount += increment ? 1 : -1;
      this._label.setLength(0);
      this.invalidate();
   }

   @Override
   public final void add(Field field) {
      if (field instanceof ContactTreeField$ConversationLeaf) {
         ContactTreeField$ConversationLeaf conversationLeaf = (ContactTreeField$ConversationLeaf)field;
         if (ContactTreeField$ConversationLeaf.access$700(conversationLeaf)) {
            this._unreadCount++;
         }
      }

      this._label.setLength(0);
      this.invalidate();
      super.add(field);
   }

   @Override
   public final void delete(Field field) {
      if (field instanceof ContactTreeField$ConversationLeaf) {
         ContactTreeField$ConversationLeaf conversationLeaf = (ContactTreeField$ConversationLeaf)field;
         if (ContactTreeField$ConversationLeaf.access$700(conversationLeaf)) {
            this._unreadCount--;
         }
      }

      this._label.setLength(0);
      this.invalidate();
      super.delete(field);
   }

   @Override
   public final void doInvalidate(boolean andRelated) {
      this._label.setLength(0);
      this.invalidate();
   }

   final void format() {
      if (!super._empty && this.getFieldCount() == 2) {
         this._label.append(QmResources.getString(114));
      } else {
         if (!super._empty) {
            this._label.append(this.getFieldCount() - 1);
            this._label.append(" ");
         }

         this._label.append(this.name());
         if (!super._empty && this._unreadCount > 0) {
            this._label.append(QmResources.format(71, String.valueOf(this._unreadCount)));
         }
      }
   }

   @Override
   final void drawText(Graphics graphics, int x, int y, int width) {
      if (this._label.length() == 0) {
         this.format();
      }

      EllipsesText.draw(graphics, this._label, x, y, width);
   }

   ContactTreeField$ConversationBranch(ContactTreeField$1 x0) {
      this();
   }

   static final void access$100(ContactTreeField$ConversationBranch x0) {
      x0.lock();
   }
}
