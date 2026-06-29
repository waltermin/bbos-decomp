package net.rim.device.apps.internal.qm.peer;

import net.rim.device.api.ui.ContextMenu;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.theme.Tag;
import net.rim.device.apps.internal.qm.peer.common.QmUtil;

final class BranchTitleField extends Field {
   private BranchTitleField$BranchMenuItem _expandMenuItem = new BranchTitleField$BranchMenuItem(this, 11);
   private BranchTitleField$BranchMenuItem _collapseMenuItem = new BranchTitleField$BranchMenuItem(this, 12);
   private static Tag TAG = Tag.create("bbmessenger-branchtitle");

   public BranchTitleField() {
      this.setTag(TAG);
   }

   @Override
   public final int getPreferredHeight() {
      return Math.max(QmUtil.calculateTrueFontHeight(((Branch)this.getManager()).name()), PeerResources.getIconHeight(Font.getDefault()));
   }

   @Override
   protected final void layout(int width, int height) {
      height = this.getPreferredHeight();
      this.setExtent(width, height);
   }

   @Override
   protected final void paint(Graphics graphics) {
      int x = 0;
      int fontHeight = QmUtil.calculateTrueFontHeight(((Branch)this.getManager()).name());
      int iconHeight = PeerResources.getIconHeight(this.getFont());
      int y = fontHeight > iconHeight ? (fontHeight - iconHeight) / 2 : 0;
      x += PeerResources.drawIcon(graphics, x, y, ((Branch)this.getManager()).isExpanded() ? 15 : 14);
      x += 2;
      y = fontHeight < iconHeight ? (iconHeight - fontHeight) / 2 : 0;
      y += QmUtil.calculateCharacterDecoratorVerticalOffset(((Branch)this.getManager()).name());
      ((Branch)this.getManager()).drawText(graphics, x, y, this.getWidth() - x);
   }

   @Override
   public final boolean isFocusable() {
      return true;
   }

   @Override
   protected final boolean keyChar(char character, int status, int time) {
      if (character == ' ') {
         this.toggleExpansion();
         return true;
      } else {
         return false;
      }
   }

   final boolean isExpanded() {
      return ((Branch)this.getManager()).isExpanded();
   }

   final void toggleExpansion() {
      ((Branch)this.getManager()).toggleExpansion();
   }

   @Override
   protected final void makeContextMenu(ContextMenu contextMenu, int instance) {
      super.makeContextMenu(contextMenu, instance);
      MenuItem item = this.isExpanded() ? this._collapseMenuItem : this._expandMenuItem;
      contextMenu.addItem(item);
      contextMenu.setDefaultItem(item);
   }
}
