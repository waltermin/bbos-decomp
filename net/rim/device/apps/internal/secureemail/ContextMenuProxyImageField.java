package net.rim.device.apps.internal.secureemail;

import net.rim.device.api.ui.ContextMenu;
import net.rim.device.internal.ui.Image;
import net.rim.device.internal.ui.component.ImageField;

public class ContextMenuProxyImageField extends ImageField {
   private ContextMenuDelegate _delegate;

   public ContextMenuProxyImageField(ContextMenuDelegate delegate, Image image, long style) {
      super(style);
      this.setImage(image);
      this._delegate = delegate;
   }

   @Override
   protected void makeContextMenu(ContextMenu contextMenu) {
      throw new RuntimeException("cod2jar: tail call (jumpspecial)");
   }

   @Override
   protected void makeContextMenu(ContextMenu contextMenu, int instance) {
      super.makeContextMenu(contextMenu, instance);
      this._delegate.makeDelegateContextMenu(contextMenu);
   }
}
