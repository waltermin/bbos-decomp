package net.rim.device.apps.internal.explorer.Media.Fields;

import net.rim.device.api.ui.component.RichTextField;

public final class AlbumTitleField extends RichTextField {
   private int _managerHeight;

   public AlbumTitleField(String albumTitle, long style, int managerHeight) {
      super(albumTitle, style);
      this._managerHeight = managerHeight;
   }

   @Override
   public final boolean isFocusable() {
      return this.getContentHeight() > this._managerHeight;
   }
}
