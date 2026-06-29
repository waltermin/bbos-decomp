package net.rim.device.apps.internal.browser.plugin.media.field;

import net.rim.device.api.system.Application;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.component.ListFieldCallback;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.ui.theme.Tag;
import net.rim.device.api.ui.theme.ThemeAttributeSet;
import net.rim.device.cldc.io.utility.URIDecoder;
import net.rim.device.internal.io.file.FileUtilities;
import net.rim.device.internal.media.Playlist;

public final class PlaylistField extends VerticalFieldManager implements ListFieldCallback {
   private Playlist _playlist;
   private ListField _list;
   private int _currentIndex;
   private static Tag TAG = Tag.create("media-playlist");

   public PlaylistField(Playlist playlist) {
      super(281474976710656L);
      this._playlist = playlist;
      this._list = (ListField)(new Object(this._playlist.getNumberOfItems(), 3458764513820540928L));
      this._list.setCallback(this);
      this._list.setSearchable(false);
      this._list.setTag(TAG);
      this.add(this._list);
   }

   @Override
   public final void setChangeListener(FieldChangeListener listener) {
      super.setChangeListener(listener);
      this._list.setChangeListener(listener);
   }

   public final int getSelectedIndex() {
      return this._list.getSelectedIndex();
   }

   public final void setCurrentItem(int item) {
      this._currentIndex = item;
      Application.getApplication().invokeAndWait(new PlaylistField$1(this, item));
   }

   @Override
   public final void drawListRow(ListField listField, Graphics graphics, int index, int y, int width) {
      if (index == this._currentIndex && !graphics.isDrawingStyleSet(16)) {
         graphics.setColor(ThemeAttributeSet.getColor(this._list, 4));
         int rowHeight = this._list.getRowHeight(index);
         graphics.drawRect(0, y, width, rowHeight);
         graphics.setColor(ThemeAttributeSet.getColor(this._list, 1));
      }

      String title = this._playlist.getTitle(index);
      if (title == null || title.length() == 0) {
         title = this._playlist.getUrl(index);
         if (title == null) {
            return;
         }

         title = FileUtilities.getName(title);
      }

      int length = this._playlist.getLength(index);
      if (length != -1) {
         ;
      }

      title = URIDecoder.decode(title, "UTF-8", false);
      title = ((StringBuffer)(new Object())).append(Integer.toString(index + 1)).append(". ").append(title).toString();
      graphics.drawText(title, 0, y, 64, width);
   }

   @Override
   public final int getPreferredWidth(ListField listField) {
      return Display.getWidth();
   }

   @Override
   public final int indexOfList(ListField listField, String prefix, int start) {
      return -1;
   }

   @Override
   public final Object get(ListField listField, int index) {
      return this._playlist.getUrl(index);
   }

   @Override
   protected final boolean trackwheelClick(int status, int time) {
      return super.trackwheelClick(status, time) ? true : this.onClick();
   }

   @Override
   protected final boolean navigationClick(int status, int time) {
      return super.navigationClick(status, time) ? true : this.onClick();
   }

   @Override
   protected final boolean navigationUnclick(int status, int time) {
      return super.navigationUnclick(status, time) ? true : true;
   }

   private final boolean onClick() {
      this.fieldChangeNotify(0);
      return true;
   }

   @Override
   protected final boolean keyChar(char character, int status, int time) {
      if (super.keyChar(character, status, time)) {
         return true;
      } else {
         return character == '\n' ? this.onClick() : false;
      }
   }

   @Override
   protected final void onVisibilityChange(boolean visible) {
      super.onVisibilityChange(visible);
      if (visible) {
         int item = this._currentIndex;
         this._list.setSelectedIndex(item);
         XYRect rect = Ui.getTmpXYRect();
         rect.x = 0;
         rect.y = this._list.getYForRow(item);
         rect.height = this._list.getRowHeight(item);
         rect.width = this.getWidth();
         this.makeFocusVisible(false, rect, true, false);
         Ui.returnTmpXYRect(rect);
      }
   }

   static final void access$100(PlaylistField x0, boolean x1, XYRect x2, boolean x3, boolean x4) {
      x0.makeFocusVisible(x1, x2, x3, x4);
   }
}
