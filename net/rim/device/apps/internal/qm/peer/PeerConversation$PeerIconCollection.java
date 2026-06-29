package net.rim.device.apps.internal.qm.peer;

import net.rim.device.api.ui.Graphics;
import net.rim.device.apps.api.utility.columninfo.ColumnInformation;
import net.rim.device.internal.ui.IconCollection;

final class PeerConversation$PeerIconCollection extends IconCollection {
   private final PeerConversation this$0;

   private PeerConversation$PeerIconCollection(PeerConversation _1) {
      super(1, 1);
      this.this$0 = _1;
   }

   @Override
   public final int paint(Graphics graphics, int x, int y, int width, int height, int index) {
      int res = 0;
      if (index == -1) {
         PeerContact pc = this.this$0.getFirstParticipant();
         int icon = this.getIcon(pc);
         int overlay = this.getOverlay(pc);
         int typingUnread = pc != null && pc.isTyping() ? 12 : -1;
         res = PeerResources.getIconHeight(graphics.getFont());
         int widthOffset = width - res >> 1;
         int heightOffset = height - res >> 1;
         PeerResources.drawIcon(graphics, x + widthOffset, y + heightOffset, icon);
         if (overlay != -1) {
            PeerResources.drawIcon(graphics, x + widthOffset, y + heightOffset, overlay);
         }

         if (typingUnread != -1 && PeerConversation.access$100(this.this$0) != null) {
            ColumnInformation ci = PeerConversation.access$100(this.this$0).getColumnInformation();
            x = ci.getColumnOffset(0);
            PeerResources.drawIcon(graphics, x, y, typingUnread);
         }
      }

      return res;
   }

   private final int getIcon(PeerContact pc) {
      int icon = this.this$0.getParticipants().size() != 1 ? 4 : 1;
      if (pc != null) {
         if (!pc.isUnreachable() && pc.isAvailable() && !pc.isBusy()) {
            icon = pc.getPresenceIconId();
         } else {
            icon = 0;
         }
      }

      return this.this$0.isUnread() ? this.getAdjustedIcon(icon) : icon;
   }

   private final int getAdjustedIcon(int icon) {
      switch (icon) {
         case 0:
            return 2;
         case 4:
            return 5;
         default:
            return 3;
      }
   }

   private final int getOverlay(PeerContact pc) {
      int overlay = -1;
      if (this.this$0.isUnread()) {
         return 11;
      }

      if (pc != null) {
         if (pc.isUnreachable()) {
            return 8;
         }

         if (!pc.isAvailable()) {
            return 7;
         }

         if (pc.isBusy()) {
            overlay = 6;
         }
      }

      return overlay;
   }

   PeerConversation$PeerIconCollection(PeerConversation x0, PeerConversation$1 x1) {
      this(x0);
   }
}
