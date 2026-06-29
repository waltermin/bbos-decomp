package net.rim.device.apps.internal.browser.ui;

import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.theme.Tag;

final class EditScreen extends Screen {
   private Field _source;
   private Field _editor;
   private XYRect _rect;
   public static final long ALT_DISMISS;
   private static final int MAX_WIDTH = Display.getWidth();
   private static final int MAX_HEIGHT = Display.getHeight();
   private static final Tag TAG = Tag.create("inplace");

   public EditScreen(Field source, Field editor) {
      this(source, editor, 299067162755073L);
   }

   public EditScreen(Field source, Field editor, long style) {
      super((Manager)(new Object(style)), style);
      this.setTag(TAG);
      this._source = source;
      this._editor = editor;
      this._rect = (XYRect)(new Object());
      this.getDelegate().add(this._editor);
   }

   public final void doModal() {
      Ui.getUiEngine().pushModalScreen(this);
   }

   @Override
   protected final void sublayout(int width, int height) {
      int over = 0;
      XYRect extent = this._rect;
      extent.set(this._source.getExtent());
      this._source.getManager().transformToScreen(extent);
      extent.height = Math.min(this._editor.getPreferredHeight(), height);
      extent.width = Math.min(this._editor.getPreferredWidth(), width);
      if (extent.x < 0) {
         extent.x = 0;
      }

      if (extent.y < 0) {
         extent.y = 0;
      }

      if (extent.width >= MAX_WIDTH) {
         extent.width = MAX_WIDTH;
      }

      int tempHeight = this.getBorderTop() + this.getPaddingTop() + this.getBorderBottom() + this.getPaddingBottom();
      if (extent.height + tempHeight >= MAX_HEIGHT) {
         extent.height = MAX_HEIGHT - tempHeight;
      }

      over = extent.X2() + this.getBorderRight() + this.getPaddingRight() + this.getBorderLeft() + this.getPaddingLeft() - MAX_WIDTH;
      if (over > 0) {
         extent.x -= over;
      }

      over = extent.Y2() + this.getBorderBottom() + this.getPaddingBottom() + this.getBorderTop() + this.getPaddingTop() - MAX_HEIGHT;
      if (over > 0) {
         extent.y -= over;
      }

      this.setPosition(extent.x, extent.y);
      this.setExtent(extent.width, extent.height);
      this.setPositionDelegate(0, 0);
      this.layoutDelegate(this.getContentWidth(), this.getContentHeight());
   }
}
