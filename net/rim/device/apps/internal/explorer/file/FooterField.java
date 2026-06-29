package net.rim.device.apps.internal.explorer.file;

import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.theme.Tag;
import net.rim.device.apps.internal.explorer.file.resource.ExplorerIcons;
import net.rim.device.internal.ui.Image;

public final class FooterField extends Field {
   private Image _glyph;
   private String _filename;
   private String _size;
   private static final Tag STATUS_TAG = Tag.create("explorer-status");

   public FooterField() {
      super(45035996273704960L);
      this.setTag(STATUS_TAG);
   }

   @Override
   protected final void layout(int width, int height) {
      this.setExtent(Math.min(width, this.getPreferredWidth()), Math.min(height, this.getPreferredHeight()));
   }

   @Override
   protected final void paint(Graphics graphics) {
      int width = this.getContentWidth();
      int sizeStartX = width * 7 / 10;
      int textStartX = 0;
      if (this._glyph != null) {
         int glyphStartX = 2;
         int glyphStartY = this.getContentHeight() / 2 - 6;
         int glyphWidth = this._glyph.getWidth(16, 16);
         int glyphHeight = this._glyph.getHeight(16, 16);
         this._glyph.paint(graphics, glyphStartX, glyphStartY, glyphWidth, glyphHeight);
         textStartX = 16;
      }

      graphics.drawText(this._filename, textStartX, 0, 118, sizeStartX);
      graphics.drawText(this._size, sizeStartX, 0, 53, width - sizeStartX);
   }

   public final void setItem(FileItemField item) {
      this._glyph = null;
      if (item != null && (item == null || !item.isAlias() && !item.isDirectory())) {
         this._filename = item.getDisplayName();
         this._size = item.getSizeString();
         if (item.isRemovable()) {
            this._glyph = ExplorerIcons.getGlyphs().getImage(1);
         }
      } else {
         this._filename = null;
         this._size = null;
      }

      this.invalidate();
   }

   @Override
   public final int getPreferredWidth() {
      return Display.getWidth();
   }

   @Override
   public final int getPreferredHeight() {
      return this.getFont().getHeight();
   }
}
