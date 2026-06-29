package net.rim.device.apps.internal.qm.peer;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.theme.Tag;
import net.rim.device.apps.internal.qm.peer.common.QmUtil;
import net.rim.device.apps.internal.qm.resource.QmResources;

final class EmptyBranchPlaceholder extends Field implements BranchLeaf {
   private static Tag TAG = Tag.create("bbmessenger-emptyleaf");

   public EmptyBranchPlaceholder() {
      this.setTag(TAG);
   }

   @Override
   public final int getPreferredHeight() {
      return QmUtil.calculateTrueFontHeight(QmResources.getString(27));
   }

   @Override
   protected final void layout(int width, int height) {
      height = this.getPreferredHeight();
      this.setExtent(width, height);
   }

   @Override
   protected final void paint(Graphics graphics) {
      int indent = 15;
      int y = QmUtil.calculateCharacterDecoratorVerticalOffset(QmResources.getString(27));
      graphics.drawText(QmResources.getString(27), indent, y, 6, this.getWidth());
   }

   @Override
   public final boolean isFocusable() {
      return true;
   }
}
