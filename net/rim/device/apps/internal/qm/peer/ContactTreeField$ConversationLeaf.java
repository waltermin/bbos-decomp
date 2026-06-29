package net.rim.device.apps.internal.qm.peer;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.theme.Tag;
import net.rim.device.apps.internal.qm.peer.common.QmUtil;

final class ContactTreeField$ConversationLeaf extends Field implements TreeItem, BranchLeaf {
   private Tag TAG;
   private boolean _unread;
   private final ContactTreeField this$0;

   ContactTreeField$ConversationLeaf(ContactTreeField _1, PeerConversation conversation) {
      this.this$0 = _1;
      this.TAG = Tag.create("bbmessenger-conversationleaf");
      this.setTag(this.TAG);
      this._unread = conversation.isUnread();
      this.setCookie(conversation);
   }

   @Override
   public final void doInvalidate(boolean andRelated) {
      PeerConversation conversation = (PeerConversation)this.getCookie();
      if (this._unread != conversation.isUnread()) {
         this._unread = !this._unread;
         ContactTreeField.access$1800(this.this$0).incrementUnreadCount(this._unread);
         if (this._unread && !ContactTreeField.access$1800(this.this$0).isExpanded()) {
            ContactTreeField.access$1800(this.this$0).toggleExpansion();
         }
      }

      this.invalidate();
      if (andRelated && conversation.getParticipants().size() == 1) {
         PeerContact contact = conversation.getFirstParticipant();
         TreeItem field = ContactTreeField.access$1900(this.this$0, contact);
         if (field != null) {
            field.doInvalidate(false);
         }
      }
   }

   @Override
   public final int getPreferredHeight() {
      int height = QmUtil.calculateTrueFontHeight(((PeerConversation)this.getCookie()).getTitle());
      return Math.max(
         Math.max(height, ContactTreeField.access$1600(this.this$0)),
         ContactTreeField._smileyFacility != null ? ContactTreeField._smileyFacility.emoticonSize() : 0
      );
   }

   @Override
   protected final void layout(int width, int height) {
      height = this.getPreferredHeight();
      this.setExtent(width, height);
   }

   @Override
   public final boolean isFocusable() {
      return true;
   }

   @Override
   protected final void paint(Graphics graphics) {
      this.this$0.paintConversation(graphics, (PeerConversation)this.getCookie());
   }

   static final boolean access$700(ContactTreeField$ConversationLeaf x0) {
      return x0._unread;
   }
}
