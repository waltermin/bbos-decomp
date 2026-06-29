package net.rim.device.apps.internal.explorer.Media;

import net.rim.device.api.ui.Manager;
import net.rim.device.internal.ui.component.Scrollbar;

public final class ListScrollbarManager extends Manager {
   private Scrollbar _scrollbar = new Scrollbar();
   private Manager _list;

   public ListScrollbarManager(Manager list) {
      super(0);
      this._list = list;
      this._scrollbar.setTag(ThemeUtilities.SCROLL_BAR_TAG);
      this.add(list);
      this.add(this._scrollbar);
      this._scrollbar.setClient(this._list);
   }

   @Override
   protected final void sublayout(int width, int height) {
      this.setExtent(width, height);
      this.setVirtualExtent(width, height);
      int remainingWidth = width - this._scrollbar.getPreferredWidth();
      if (this._list != null) {
         this.layoutChild(this._list, remainingWidth, height);
         this.setPositionChild(this._list, 0, 0);
      }

      this.layoutChild(this._scrollbar, width, height);
      this.setPositionChild(this._scrollbar, remainingWidth, 0);
   }
}
