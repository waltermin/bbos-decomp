package net.rim.device.apps.internal.explorer.Media.Fields;

import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.apps.internal.explorer.Media.ThemeUtilities;

public final class AlbumTitleManager extends VerticalFieldManager {
   private int _managerWidth;
   private int _managerHeight;
   private RichTextField _titleField;

   public AlbumTitleManager(String albumTitle, int height) {
      super(281526516318208L);
      this._managerHeight = height;
      VerticalFieldManager vfm = (VerticalFieldManager)(new Object());
      this._titleField = new AlbumTitleField(albumTitle, 36028797018963968L, height);
      this._titleField.setTag(ThemeUtilities.TITLE_TAG);
      vfm.add(this._titleField);
      this.add(vfm);
   }

   @Override
   public final void sublayout(int width, int height) {
      if (this._managerWidth == 0) {
         this._managerWidth = width;
      }

      if (this._managerHeight == 0) {
         this._managerHeight = height;
      }

      super.sublayout(this._managerWidth, this._managerHeight);
   }
}
