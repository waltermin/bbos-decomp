package net.rim.device.internal.EScreens;

import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.component.ListFieldCallback;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.internal.ui.UiInternal;

public final class EScreenGraphicsInfo extends MainScreen implements ListFieldCallback {
   private int _width;
   private int _height;
   private int _width_mm;
   private int _height_mm;
   private int _num_colors;
   private int _horz_ppm;
   private int _vert_ppm;
   private int[] _cacheStats;
   private ListField _list;
   private static final int MENU_CLEAR_CACHE_STATS = 1;
   private static final int MENU_REFRESH = 2;
   private static final int DIMENSIONS = 0;
   private static final int DIMENSIONS_MM = 1;
   private static final int COLORS = 2;
   private static final int RESOLUTION = 3;
   private static final int CACHE = 4;
   private static final int NUM_COMMON_IMMUTABLES = 5;
   private static final String[] NAMES = new String[]{
      "Screen dimensions: ",
      "Screen dimensions: ",
      "Colors: ",
      "Resolution: ",
      "Cache",
      "    Hits: ",
      "    Misses: ",
      "    Num cached: ",
      "    Flush count: ",
      "    Avg bm size: ",
      "    Avg flush count: ",
      "    Cache bms added: ",
      "    Size: ",
      "    Free: "
   };

   public EScreenGraphicsInfo(Font font) {
      this.setFont(font);
      this._cacheStats = new int[9];
      this._list = new ListField();
      this._list.setSize(14);
      this._list.setCallback(this);
      this.add(this._list);
      this.setTitle("Graphics Information");
   }

   public final void refreshData() {
      this._width = Display.getWidth();
      this._height = Display.getHeight();
      this._horz_ppm = Display.getHorizontalResolution();
      this._vert_ppm = Display.getVerticalResolution();
      this._width_mm = (1000 * Display.getWidth() + (this._horz_ppm >> 1)) / this._horz_ppm;
      this._height_mm = (1000 * Display.getHeight() + (this._vert_ppm >> 1)) / this._vert_ppm;
      this._num_colors = Graphics.getNumColors();
      UiInternal.getCacheStatistics(this._cacheStats);
   }

   @Override
   public final void makeMenu(Menu menu, int instance) {
      menu.add(new EScreenGraphicsInfo$MyMenu(this, "Refresh", 2));
      menu.add(new EScreenGraphicsInfo$MyMenu(this, "Clear cache stats", 1));
      super.makeMenu(menu, instance);
   }

   private static final int log2(int number) {
      number >>= 1;

      int log;
      for (log = 0; number != 0; log++) {
         number >>= 1;
      }

      return log;
   }

   @Override
   protected final void onUiEngineAttached(boolean attached) {
      super.onUiEngineAttached(attached);
      if (attached) {
         this.refreshData();
      }
   }

   @Override
   protected final boolean keyChar(char c, int status, int time) {
      boolean result = super.keyChar(c, status, time);
      if (!result && c == 'r') {
         this.refreshData();
         this._list.invalidate();
         result = true;
      }

      return result;
   }

   @Override
   public final void drawListRow(ListField listField, Graphics graphics, int index, int y, int width) {
      StringBuffer buffer = new StringBuffer(32);
      buffer.append(NAMES[index]);
      switch (index) {
         case -1:
            int value = this._cacheStats[index - 5];
            buffer.append(value);
            break;
         case 0:
         default:
            buffer.append(this._width);
            buffer.append('x');
            buffer.append(this._height);
            buffer.append(" px");
            break;
         case 1:
            buffer.append(this._width_mm);
            buffer.append('x');
            buffer.append(this._height_mm);
            buffer.append(" mm");
            break;
         case 2:
            buffer.append(this._num_colors);
            buffer.append('(');
            buffer.append(log2(this._num_colors));
            buffer.append("bit)");
            break;
         case 3:
            buffer.append(this._horz_ppm);
            buffer.append('x');
            buffer.append(this._vert_ppm);
            buffer.append(" ppm(px/m)");
         case 4:
      }

      graphics.drawText(buffer, 0, buffer.length(), 0, y, 0, width);
   }

   @Override
   public final int getPreferredWidth(ListField listField) {
      return this.getWidth();
   }

   @Override
   public final Object get(ListField listField, int index) {
      return "";
   }

   @Override
   public final int indexOfList(ListField listField, String prefix, int start) {
      return listField.getSelectedIndex();
   }
}
