package net.rim.tid.im.ui;

import net.rim.device.api.ui.container.VerticalFieldManager;

class TextFieldLookup$LookupVericalFieldManager extends VerticalFieldManager {
   public TextFieldLookup$LookupVericalFieldManager() {
   }

   public TextFieldLookup$LookupVericalFieldManager(long style) {
      super(style);
   }

   @Override
   protected void sublayout(int maxWidth, int maxHeight) {
      super.sublayout(maxWidth, maxHeight);
      this.setExtent(this.getContentWidth() + 5, this.getContentHeight());
   }
}
