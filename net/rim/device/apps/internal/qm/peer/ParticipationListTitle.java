package net.rim.device.apps.internal.qm.peer;

import net.rim.device.api.ui.Graphics;

final class ParticipationListTitle extends TitleField {
   String _title;

   ParticipationListTitle(String title) {
      super(false);
      this._title = title;
      this.refresh();
   }

   @Override
   final int getIconHeight() {
      return PeerResources.getIconHeight(this.getFont());
   }

   @Override
   final int drawIcon(Graphics graphics, int x, int y) {
      PeerResources.drawIcon(graphics, x, y, 4);
      return PeerResources.getIconHeight(this.getFont());
   }

   public final void refresh() {
      this.setTitle(this._title);
      this.invalidate();
   }
}
