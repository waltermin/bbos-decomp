package net.rim.device.apps.internal.qm.peer;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;

public final class PeerIconField extends Field {
   private int _id;

   public PeerIconField(int id) {
      this._id = id;
   }

   final void setId(int id) {
      this._id = id;
      this.invalidate();
   }

   @Override
   protected final void drawFocus(Graphics graphics, boolean on) {
      this.drawHighlightRegion(graphics, 1, on, 0, 0, this.getWidth(), this.getHeight());
   }

   @Override
   public final int getPreferredHeight() {
      return Font.getDefault().getHeight();
   }

   @Override
   public final int getPreferredWidth() {
      return this.getPreferredHeight();
   }

   @Override
   protected final void layout(int width, int height) {
      this.setExtent(this.getPreferredWidth(), this.getPreferredHeight());
   }

   @Override
   protected final void paint(Graphics g) {
      PeerResources.drawMessageIcon(g, 0, 0, this._id);
   }
}
