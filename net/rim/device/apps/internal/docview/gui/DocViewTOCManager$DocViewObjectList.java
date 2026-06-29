package net.rim.device.apps.internal.docview.gui;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.ui.ContextMenu;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.component.ObjectListField;
import net.rim.device.internal.ui.RichText;

final class DocViewTOCManager$DocViewObjectList extends ObjectListField {
   private MenuItem _viewItem;
   private MenuItem _retrieveItem;
   private final DocViewTOCManager this$0;

   DocViewTOCManager$DocViewObjectList(DocViewTOCManager _1) {
      super(70);
      this.this$0 = _1;
      this._viewItem = new DocViewTOCManager$DocViewObjectList$1(
         this, ResourceBundle.getBundle(-4603212010799374808L, "net.rim.device.apps.internal.resource.DocView"), 9, 131088, 0
      );
      this._retrieveItem = new DocViewTOCManager$DocViewObjectList$2(
         this, ResourceBundle.getBundle(-4603212010799374808L, "net.rim.device.apps.internal.resource.DocView"), 8, 131088, 0
      );
      this.setSearchable(false);
   }

   private final void defaultMenu() {
      throw new RuntimeException("cod2jar: invokevirtual: slot out of range");
   }

   @Override
   public final void drawListRow(ListField listField, Graphics g, int index, int y, int width) {
      int color = g.getColor();
      Font f = g.getFont();
      DocViewDisplayField$ItemInfo obj = (DocViewDisplayField$ItemInfo)this.get(listField, index);
      g.setColor(255);
      Font newFont = f.derive(f.getStyle() | (obj._available == 2 ? 12 : 4));
      g.setFont(newFont);
      String text = obj.toString();
      RichText.drawTextWithEllipses(g, text, 0, y, width, RichText.getLineDirection(text), (int)(this.getStyle() & 71));
      g.setFont(f);
      g.setColor(color);
   }

   @Override
   public final void getFocusRect(XYRect rect) {
      super.getFocusRect(rect);
      DocViewDisplayField$ItemInfo obj = (DocViewDisplayField$ItemInfo)this.get(this, this.getSelectedIndex());
      int width = this.getFont().getBounds(obj.toString());
      if (width < DocViewGUIInternalConstants.SCREEN_WIDTH) {
         rect.width = width;
      }
   }

   @Override
   protected final void drawFocus(Graphics graphics, boolean on) {
      XYRect focusRect = new XYRect();
      this.getFocusRect(focusRect);
      focusRect.height--;
      graphics.invert(focusRect);
      XYRect var4 = null;
   }

   @Override
   protected final boolean invokeAction(int action) {
      switch (action) {
         case 1:
            this.defaultMenu();
            return true;
         default:
            return false;
      }
   }

   @Override
   protected final void makeContextMenu(ContextMenu contextMenu) {
      super.makeContextMenu(contextMenu);
      boolean available = ((DocViewDisplayField$ItemInfo)this.get(this, this.getSelectedIndex()))._available != 2;
      if (available) {
         contextMenu.addItem(this._viewItem);
         contextMenu.setDefaultItem(this._viewItem);
      } else {
         if (this.this$0._delegateMgr.isMoreSupported()) {
            contextMenu.addItem(this._retrieveItem);
            contextMenu.setDefaultItem(this._retrieveItem);
         }
      }
   }
}
