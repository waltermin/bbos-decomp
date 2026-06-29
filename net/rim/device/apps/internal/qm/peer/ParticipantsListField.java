package net.rim.device.apps.internal.qm.peer;

import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.component.ListField;

final class ParticipantsListField extends ListField {
   int _fontHeight = this.getFont().getHeight();
   int _iconHeight = PeerResources.getIconHeight(this.getFont());

   ParticipantsListField(int size) {
      super(size);
   }

   @Override
   protected final void paint(Graphics g) {
      this.setRowHeight(Math.max(this._fontHeight, this._iconHeight));
      super.paint(g);
   }
}
