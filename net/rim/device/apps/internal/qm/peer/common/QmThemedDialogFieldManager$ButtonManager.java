package net.rim.device.apps.internal.qm.peer.common;

import net.rim.device.api.ui.container.VerticalFieldManager;

final class QmThemedDialogFieldManager$ButtonManager extends VerticalFieldManager {
   QmThemedDialogFieldManager$ButtonManager() {
      super(12884901888L);
   }

   final int getPreferredButtonHeight() {
      return this.getPreferredHeightOfChild(this.getField(0));
   }
}
