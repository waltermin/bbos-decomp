package net.rim.tid.im.ui;

import java.util.Vector;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.i18n.ResourceBundleFamily;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Trackball;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.ui.theme.Theme;
import net.rim.device.internal.ui.Border;
import net.rim.tid.im.spellcheck.SpellCheckInputMethodVariant;

class ChooseVariantManager extends VerticalFieldManager implements FieldChangeListener {
   ChooseVariantManager$LookupListField _list;
   ButtonField _menuButton;
   private ChooseVariantManager$RigidManager _listManager;
   private Vector _listData;
   int _preferredWidth;
   private boolean _itemsTruncated;
   SpellCheckInputMethodVariant _spellChecker;
   XYRect _anchorRect;
   private int _scrollArrowWidth;
   int _lineCount;
   private ResourceBundleFamily _bundleFamily = ResourceBundle.getBundle(-7934727403592703506L, "net.rim.tid.im.options.SpellCheck.SpellCheck");

   public ChooseVariantManager(SpellCheckInputMethodVariant spellChecker) {
      this.setScrollArrowWidth();
      this._spellChecker = spellChecker;
      if (this.includeMenuButton()) {
         this._menuButton = (ButtonField)(new Object(this._bundleFamily.getString(22)));
         this.add(this._menuButton);
      }

      this._list = new ChooseVariantManager$LookupListField(this);
      this._listManager = new ChooseVariantManager$RigidManager(0, 0, 299067162755072L, this._list.getFont());
      this._listManager.add(this._list);
      this.add(this._listManager);
   }

   public void setItems(Vector listData) {
      this._listData = listData;
      if (this._menuButton != null) {
         this._menuButton.setLabel(this._bundleFamily.getString(22));
      }

      this.setScrollArrowWidth();
      this._preferredWidth = this.getLongestWidth(this._list.getFont());
      this._list.setFocus();
   }

   public int getSelectedIndex() {
      return this._list.getSelectedIndex();
   }

   private boolean includeMenuButton() {
      return !Trackball.isSupported();
   }

   private void setScrollArrowWidth() {
      Bitmap bm = Theme.getThemeBitmap(0);
      if (bm != null) {
         this._scrollArrowWidth = bm.getWidth();
      }

      bm = Theme.getThemeBitmap(1);
      if (bm != null) {
         this._scrollArrowWidth = Math.max(this._scrollArrowWidth, bm.getWidth());
      }
   }

   int getLongestWidth(Font font) {
      if (this._listData == null) {
         return this.getMenuButtonWidth();
      }

      int longestWidth = 0;
      int size = this._listData.size();

      for (int i = 0; i < size; i++) {
         String item = this._listData.elementAt(i).toString();
         if (item != null) {
            int len = font.getBounds(item);
            if (len > longestWidth) {
               longestWidth = len;
            }
         }
      }

      if (size > 4) {
         longestWidth += this._scrollArrowWidth;
      }

      if (longestWidth > Display.getWidth()) {
         longestWidth = Display.getWidth();
         this._itemsTruncated = true;
      } else {
         this._itemsTruncated = false;
      }

      int menuWidth = this.getMenuButtonWidth();
      if (menuWidth > Display.getWidth()) {
         menuWidth = Display.getWidth();
      }

      return longestWidth > menuWidth ? longestWidth : menuWidth;
   }

   int getMenuButtonWidth() {
      if (this._menuButton == null) {
         return 0;
      }

      Border border = this._menuButton.getBorder();
      return border != null ? this._menuButton.getPreferredWidth() + border.getLeft() + border.getRight() : this._menuButton.getPreferredWidth();
   }

   int getMenuButtonHeight() {
      return this._menuButton == null ? 0 : this._menuButton.getPreferredHeight();
   }

   int calcLineCount(int height) {
      int rowHeight = this._list.getRowHeight();
      int buttonHeight = this.getMenuButtonHeight();
      this._lineCount = Math.min((height - buttonHeight) / rowHeight, 4);
      if (this._lineCount > 0) {
         this._lineCount = Math.min(this._lineCount, this._listData.size());
      }

      return this._lineCount;
   }

   @Override
   protected void sublayout(int width, int height) {
      width = this.getLongestWidth(this._list.getFont());
      int rowHeight = this._list.getRowHeight();
      int buttonHeight = this.getMenuButtonHeight();
      this._lineCount = Math.min((height - buttonHeight) / rowHeight, 4);
      if (this._lineCount > 0) {
         this._lineCount = Math.min(this._lineCount, this._listData.size());
         int x = width;
         int y = rowHeight * this._lineCount;
         this._listManager.setSize(x, y);
      }

      super.sublayout(width, height);
   }

   @Override
   protected int moveFocus(int amount, int status, int time) {
      throw new RuntimeException("cod2jar: tail call (jumpspecial)");
   }

   @Override
   public void fieldChanged(Field field, int context) {
   }
}
