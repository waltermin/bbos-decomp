package net.rim.device.apps.internal.qm.peer;

import net.rim.device.api.ui.Graphics;

final class ConversationTitle extends TitleField {
   private PeerConversation _conversation;

   ConversationTitle(PeerConversation conversation) {
      super(false);
      this._conversation = conversation;
      this.refresh();
   }

   @Override
   final int getIconHeight() {
      return PeerResources.getIconHeight(this.getFont());
   }

   @Override
   final int drawIcon(Graphics graphics, int x, int y) {
      return this._conversation.getParticipants().size() == 1
         ? this._conversation.getFirstParticipant().drawIcon(graphics, x, y, this._conversation.isUnread())
         : PeerResources.drawIcon(graphics, x, y, this._conversation.isUnread() ? 5 : 4);
   }

   public final void refresh() {
      this.setTitle(this._conversation.getTitle());
      this.invalidate();
   }
}
