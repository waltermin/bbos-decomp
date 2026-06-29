package net.rim.device.api.ui.container;

class DialogFieldManager$ButtonManager extends VerticalFieldManager {
   DialogFieldManager$ButtonManager() {
      super(12884901888L);
   }

   int getPreferredButtonHeight() {
      return this.getPreferredHeightOfChild(this.getField(0));
   }
}
