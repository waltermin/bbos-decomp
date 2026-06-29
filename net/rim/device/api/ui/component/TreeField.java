package net.rim.device.api.ui.component;

import net.rim.device.api.ui.ContextMenu;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.accessibility.AccessibleContext;
import net.rim.device.api.ui.accessibility.AccessibleContextFactory;
import net.rim.device.api.ui.accessibility.AccessibleContextProxy;
import net.rim.device.api.ui.theme.Tag;
import net.rim.device.internal.i18n.CommonResource;
import net.rim.device.internal.ui.SystemIcon;
import net.rim.device.internal.ui.Tree;

public class TreeField extends Field implements VariableRowHeightProvider {
   private Tree _data = new Tree();
   private String _emptyString = CommonResource.getString(1012);
   private int _emptyStyle = 4;
   private int _rowHeightSet = -1;
   private int _rowHeight;
   private int _focusRectX;
   private int _focusRectY;
   private int _focusRectWidth;
   private int _focusRectHeight;
   private TreeFieldCallback _callback;
   private int _indent = 5;
   private int _focusNode = -1;
   private boolean _haveFocus;
   private int _lastStartNode = -1;
   private int _lastStartNodeIndex;
   private int _iconWidth;
   private int _iconGap;
   private RowHeightAdjuster _rowHeightAdjuster = new RowHeightAdjuster();
   private boolean _drawFocus;
   private boolean _iconFocusChanged;
   private static Tag TAG = Tag.create("tree");
   private static final int ROW_HEIGHT_FONT;
   private static MenuItem _expandItem = new TreeField$TreeFieldMenuItem(11, true);
   private static MenuItem _collapseItem = new TreeField$TreeFieldMenuItem(12, false);

   public Object getCookie(int node) {
      return this._data.getCookie(node);
   }

   public int getCurrentNode() {
      return this._focusNode;
   }

   public boolean getExpanded(int node) {
      return this._data.getNodeExpansion(node);
   }

   public int getFirstChild(int node) {
      return this._data.getFirstChild(node);
   }

   public int getFirstRoot() {
      return this._data.getFirstRoot();
   }

   public int getNextSibling(int node) {
      return this._data.getNextSibling(node);
   }

   public int getNodeCount() {
      return this._data.getNodeCount();
   }

   public int getParent(int node) {
      return this._data.getParent(node);
   }

   public int getPreviousSibling(int node) {
      return this._data.getPreviousSibling(node);
   }

   public int getRowHeight() {
      return this._rowHeight;
   }

   public boolean getVisible(int node) {
      return this._data.getVisible(node);
   }

   public void invalidateNode(int node) {
      Manager mgr = this.getManager();
      if (this._lastStartNode != -1 && mgr != null) {
         if (this._data.getVisible(node)) {
            int maxVisibleRows = (mgr.getVisibleHeight() + this._rowHeight - 1) / this._rowHeight;
            int index = -1;
            int stepNode = this._lastStartNode;

            for (int i = 0; i < maxVisibleRows; i++) {
               if (stepNode == node) {
                  index = this._lastStartNodeIndex + i;
                  break;
               }

               stepNode = this._data.nextNode(stepNode, false);
               if (stepNode == -1) {
                  break;
               }
            }

            if (index == -1) {
               stepNode = this._lastStartNode;

               for (int i = 0; i < maxVisibleRows; i++) {
                  stepNode = this._data.previousNode(stepNode, false);
                  if (stepNode == -1) {
                     return;
                  }

                  if (stepNode == node) {
                     index = this._lastStartNodeIndex - i - 1;
                     break;
                  }
               }
            }

            this.invalidate(0, this.getYForRow(index), this.getWidth(), this.getRowHeight(index));
         }
      }
   }

   public int getVisibleNodeCount() {
      return this._data.getVisibleCount();
   }

   public void setEmptyString(String emptyString, int style) {
      emptyString.length();
      this._emptyString = emptyString;
      this._emptyStyle = style;
   }

   public String getEmptyString() {
      return this._emptyString;
   }

   public int getEmptyStringStyle() {
      return this._emptyStyle;
   }

   public void setCurrentNode(int node) {
      if (!this._data.getVisible(node)) {
         throw new IllegalArgumentException("invisible node cannot be made current");
      }

      if (this._haveFocus) {
         this.focusRemove();
      }

      this._focusNode = node;
      Manager mgr = this.getManager();
      if (mgr != null && mgr.isValidLayout()) {
         this.calcFocusRect();
      }

      if (this._haveFocus) {
         this.focusAdd(true);
      }
   }

   public void deleteAll() {
      int node = this.getFirstRoot();

      while (node != -1) {
         int next = this.getNextSibling(node);
         this.deleteSubtree(node);
         node = next;
      }
   }

   public void deleteSubtree(int node) {
      boolean update = this._data.getVisible(node);

      for (int step = this._focusNode; step > 0; step = this._data.getParent(step)) {
         if (step == node) {
            int last = this._data.getLastNode(node, false);
            this._focusNode = this._data.nextNode(last, false);
            if (this._focusNode == -1) {
               this._focusNode = this._data.previousNode(node, false);
            }
            break;
         }
      }

      this._data.deleteSubtree(node);
      if (update) {
         this.updateLayout();
      }
   }

   public int addChildNode(int parent, Object cookie) {
      int id = this._data.addChildNode(parent, cookie);
      if (this._haveFocus && this._focusNode == -1) {
         this._focusNode = id;
      }

      if (this._data.getVisible(id)) {
         this.updateLayout();
      }

      return id;
   }

   public int addSiblingNode(int previousSibling, Object cookie) {
      int id = this._data.addSiblingNode(previousSibling, cookie);
      if (this._data.getVisible(id)) {
         this.updateLayout();
      }

      return id;
   }

   public void setDefaultExpanded(boolean expanded) {
      this._data.setDefaultExpansion(expanded);
   }

   public void setExpanded(int node, boolean expanded) {
      boolean expansion = this._data.getNodeExpansion(node);
      if (expansion != expanded) {
         this._data.setNodeExpansion(node, expanded);
         if (this._data.getVisible(node)) {
            this.updateLayout();
         }
      }
   }

   public int nextNode(int node, int root, boolean followCollapsed) {
      return this._data.nextSubtreeNode(node, root, followCollapsed);
   }

   public int previousNode(int node, boolean followCollapsed) {
      return this._data.previousNode(node, followCollapsed);
   }

   public int getLastNode(int node, boolean followCollapsed) {
      return this._data.getLastNode(node, followCollapsed);
   }

   public void setCookie(int node, Object cookie) {
      this._data.setCookie(node, cookie);
   }

   public void setIndentWidth(int indent) {
      if (indent < 0) {
         throw new IllegalArgumentException();
      }

      this._indent = indent;
   }

   public void setRowHeight(int rowHeight) {
      if (rowHeight <= 0) {
         throw new IllegalArgumentException("Invalid rowHeight");
      }

      this._rowHeightSet = rowHeight;
      this._rowHeight = this._rowHeightSet;
      this._rowHeightAdjuster.setRowHeight(this._rowHeight);
      this.updateLayout();
   }

   public void setRowHeightInLines(int rowHeight) {
      if (rowHeight <= 0) {
         throw new IllegalArgumentException("Invalid rowHeight");
      }

      this._rowHeightSet = rowHeight * -1;
      this._rowHeight = this.getFont().getHeight() * -this._rowHeightSet;
      this._rowHeightAdjuster.setRowHeight(this._rowHeight);
      this.updateLayout();
   }

   public int getIndentForNode(int node) {
      return this._data.getNodeDepth(node) * this._indent;
   }

   public int getRowForY(int y) {
      return this._rowHeightAdjuster.getRowForY(y);
   }

   public int getRowHeight(int row) {
      return this._rowHeightAdjuster.getRowHeight(row);
   }

   int getYForRow(int row) {
      return this._rowHeightAdjuster.getYForRow(row);
   }

   public int getLineNumberForNode(int node) {
      return this._data.getIndexOf(node, false);
   }

   @Override
   public int getAdjustedY(Font font, StringBuffer text, int offset, int len, int y) {
      return this._rowHeightAdjuster.getAdjustedY(font, text, offset, len, y);
   }

   @Override
   public int getAdjustedY(int currentY) {
      return this._rowHeightAdjuster.getAdjustedY(currentY);
   }

   @Override
   public int getAdjustedY(Font font, String text, int y) {
      return this._rowHeightAdjuster.getAdjustedY(font, text, y);
   }

   private void calcFocusRect() {
      int index = 0;
      int x = 0;
      int width = this.getWidth();
      if (this._focusNode != -1) {
         index = this._data.getIndexOf(this._focusNode, false);
         if (index == -1) {
            throw new RuntimeException("unable to find node with focus");
         }

         x = this.getIndentForNode(this._focusNode) + this._iconWidth + this._iconGap - 1;
         if (this._callback instanceof TreeFieldCallback2) {
            TreeFieldCallback2 callback2 = (TreeFieldCallback2)this._callback;
            width = Math.min(width, callback2.getWidth(this, this._focusNode) + 1);
         }
      }

      this._focusRectX = x;
      this._focusRectY = this.getYForRow(index);
      this._focusRectWidth = width;
      this._focusRectHeight = this.getRowHeight(index);
   }

   @Override
   protected void drawFocus(Graphics graphics, boolean on) {
      this._drawFocus = on;
      super.drawFocus(graphics, on);
   }

   @Override
   public int getAccessibleRole() {
      return 27;
   }

   @Override
   public int getAccessibleChildCount() {
      return this.getNodeCount();
   }

   @Override
   public void getFocusRect(XYRect rect) {
      this.calcFocusRect();
      rect.set(this._focusRectX, this._focusRectY, this._focusRectWidth, this._focusRectHeight);
   }

   @Override
   public boolean isAccessibleStateSet(int state) {
      return (super.getAccessibleStateSet() & state) != 0;
   }

   @Override
   public boolean isAccessibleChildSelected(int index) {
      return this._focusNode == index + 1;
   }

   @Override
   protected boolean keyChar(char character, int status, int time) {
      if (character != ' ') {
         return false;
      }

      if (this._focusNode == -1) {
         return false;
      }

      if (this._data.nextSubtreeNode(this._focusNode, this._focusNode, true) == -1) {
         return true;
      }

      boolean expanding = !this._data.getNodeExpansion(this._focusNode);
      this._data.setNodeExpansion(this._focusNode, expanding);
      this.updateLayout();
      if (expanding) {
         this.showDescendants(this._focusNode);
         if (Ui.isTTSEnabled()) {
            super.accessibleEventOccurred(1, new Integer(1024), new Integer(512), this);
            return true;
         }
      } else if (Ui.isTTSEnabled()) {
         super.accessibleEventOccurred(1, new Integer(512), new Integer(1024), this);
      }

      return true;
   }

   private void showDescendants(int node) {
      if (this._data.getNodeExpansion(node)) {
         int maxVisibleRows = (this.getManager().getVisibleHeight() + this._rowHeight - 1) / this._rowHeight;
         int lastNode = -1;
         int childNode = this._data.nextSubtreeNode(node, node, false);

         for (int i = 1; i < maxVisibleRows && childNode != -1; i++) {
            lastNode = childNode;
            childNode = this._data.nextSubtreeNode(childNode, node, false);
         }

         if (lastNode != -1) {
            this.setCurrentNode(lastNode);
            this.setCurrentNode(node);
         }
      }
   }

   @Override
   protected void layout(int width, int height) {
      if (this._rowHeightSet < 0) {
         this._rowHeight = this.getFont().getHeight() * -this._rowHeightSet;
      }

      this._iconWidth = SystemIcon.COLLECTION.getWidth(this._rowHeight, this._rowHeight);
      this._iconGap = Math.max(1, this._rowHeight >> 3);
      if (this._focusNode == -1) {
         this._lastStartNode = -1;
      } else {
         int index = this._data.getIndexOf(this._focusNode, false);
         this._lastStartNode = this._focusNode;
         this._lastStartNodeIndex = index;
      }

      int numRows = Math.max(1, this._data.getVisibleCount());
      this._rowHeightAdjuster.setSize(numRows);
      this._rowHeightAdjuster.setRowHeight(this._rowHeight);
      this.setExtent(width, this._rowHeightAdjuster.getHeight());
   }

   @Override
   protected void makeMenu(Menu menu, int instance) {
      super.makeMenu(menu, instance);
      if (this._focusNode != -1 && this._data.getFirstChild(this._focusNode) != -1) {
         menu.add(this.getExpanded(this._focusNode) ? _collapseItem : _expandItem);
      }
   }

   @Override
   protected void makeContextMenu(ContextMenu contextMenu) {
      super.makeContextMenu(contextMenu);
   }

   @Override
   protected void moveFocus(int x, int y, int status, int time) {
      int row = this.getRowForY(y);
      int node = this.getFirstRoot();

      while (--row >= 0) {
         node = this._data.nextNode(node, false);
         if (node == -1) {
            break;
         }
      }

      if (node != this._focusNode) {
         this.invalidateNode(this._focusNode);
         if (this._data.getFirstChild(node) != -1) {
            this._iconFocusChanged = true;
         }
      }

      this._focusNode = node;
      if (Ui.isTTSEnabled()) {
         super.accessibleEventOccurred(6, new Integer(1), new Integer(2), this);
      }
   }

   @Override
   protected int moveFocus(int amount, int status, int time) {
      if (this._data.getVisibleCount() != 0 && (status & 65536) == 0) {
         while (amount > 0) {
            int focusNode = this._data.nextNode(this._focusNode, false);
            if (focusNode == -1) {
               return amount;
            }

            if (focusNode != this._focusNode) {
               this.invalidateNode(this._focusNode);
               if (this._data.getFirstChild(focusNode) != -1) {
                  this._iconFocusChanged = true;
               }
            }

            this._focusNode = focusNode;
            amount--;
         }

         while (amount < 0) {
            int focusNode = this._data.previousNode(this._focusNode, false);
            if (focusNode == -1) {
               return amount;
            }

            if (focusNode != this._focusNode) {
               this.invalidateNode(this._focusNode);
               if (this._data.getFirstChild(focusNode) != -1) {
                  this._iconFocusChanged = true;
               }
            }

            this._focusNode = focusNode;
            amount++;
         }

         if (Ui.isTTSEnabled()) {
            super.accessibleEventOccurred(6, new Integer(1), new Integer(2), this);
         }

         return 0;
      } else {
         return amount;
      }
   }

   @Override
   protected void onFocus(int direction) {
      this._haveFocus = true;
      if (this._data.getVisibleCount() != 0) {
         switch (direction) {
            case -2:
               break;
            case -1:
               if (this._focusNode == -1) {
                  this._focusNode = this._data.getLastNode(0, false);
               }
               break;
            case 0:
            case 1:
            default:
               if (this._focusNode == -1) {
                  this._focusNode = this._data.nextNode(0, false);
               }
         }
      }

      if (Ui.isTTSEnabled()) {
         super.accessibleEventOccurred(6, new Integer(1), new Integer(2), this);
      }

      super.onFocus(direction);
   }

   @Override
   protected void onUnfocus() {
      this._haveFocus = false;
      this._focusNode = -1;
      super.onUnfocus();
   }

   @Override
   protected void paint(Graphics graphics) {
      if (this._data.getVisibleCount() == 0) {
         int availableWidth = Math.min(this.getContentWidth(), this.getManager().getVisibleWidth());
         graphics.drawText(this._emptyString, 0, 0, this._emptyStyle, availableWidth);
      } else {
         XYRect invalidRect = graphics.getClippingRect();
         int startLine = this.getRowForY(invalidRect.y);
         int endLine = Math.min(this._data.getVisibleCount() - 1, this.getRowForY(invalidRect.y + invalidRect.height - 1));
         if (this._lastStartNode == -1) {
            this._lastStartNode = this._data.nextNode(0, false);
            this._lastStartNodeIndex = 0;
         }

         while (this._lastStartNodeIndex > startLine) {
            this._lastStartNode = this._data.previousNode(this._lastStartNode, false);
            this._lastStartNodeIndex--;
         }

         while (this._lastStartNodeIndex < startLine) {
            this._lastStartNode = this._data.nextNode(this._lastStartNode, false);
            this._lastStartNodeIndex++;
         }

         int node = this._lastStartNode;
         int y = this.getYForRow(this._lastStartNodeIndex);
         int width = this.getContentWidth();
         int size = this._rowHeight;
         boolean updateLayoutRequired = false;

         for (int line = startLine; line <= endLine; line++) {
            int indent = this.getIndentForNode(node);
            boolean hasChildren = this._data.getFirstChild(node) != -1;
            int icon = 4;
            if (hasChildren) {
               if (this.getExpanded(node)) {
                  icon = 5;
                  if (this._drawFocus && node == this._focusNode && SystemIcon.COLLECTION.containsIcon(size, 17)) {
                     icon = 17;
                  }
               } else {
                  icon = 6;
                  if (this._drawFocus && node == this._focusNode && SystemIcon.COLLECTION.containsIcon(size, 18)) {
                     icon = 18;
                  }
               }
            }

            SystemIcon.COLLECTION.paint(graphics, indent, y, this._iconWidth, size, icon);
            indent += this._iconWidth + this._iconGap;
            this._rowHeightAdjuster.start(line, y);
            this._callback.drawTreeItem(this, graphics, node, y, width - indent, indent);
            updateLayoutRequired |= this._rowHeightAdjuster.finish(line);
            node = this._data.nextNode(node, false);
            y += this.getRowHeight(line);
         }

         if (updateLayoutRequired) {
            this.updateLayout();
         }

         if (this._iconFocusChanged) {
            int startY = this.getYForRow(this._lastStartNodeIndex);
            this.invalidate(0, startY, this.getContentWidth(), y - startY);
            this._iconFocusChanged = false;
         }
      }
   }

   public TreeField(TreeFieldCallback callback, long style) {
      super((style & 36028797018963968L) > 0 ? 0 : 18014398509481984L);
      this.setTag(TAG);
      if (callback == null) {
         if (!(this instanceof TreeFieldCallback)) {
            throw new NullPointerException();
         }

         callback = (TreeFieldCallback)this;
      }

      if ((style & -54043195528445953L) != 0) {
         throw new IllegalArgumentException();
      }

      this._callback = callback;
   }

   @Override
   public AccessibleContext getAccessibleChildAt(int index) {
      if (this._data != null) {
         Object temp = this._data.getCookie(index);
         if (temp != null) {
            if (!(temp instanceof AccessibleContext)) {
               return new AccessibleContextFactory(temp.toString());
            }

            return (AccessibleContext)temp;
         }
      }

      return null;
   }

   @Override
   public AccessibleContext getAccessibleSelectionAt(int index) {
      if (this._data != null) {
         Object temp = null;
         int tempIndex;
         if (this._haveFocus) {
            tempIndex = this._focusNode + index;
         } else {
            tempIndex = this._data.getFirstRoot() + index;
         }

         temp = this._data.getCookie(tempIndex);
         if (temp != null) {
            if (!(temp instanceof AccessibleContext)) {
               if (!(temp instanceof AccessibleContextProxy)) {
                  return !this.getExpanded(tempIndex)
                     ? new AccessibleContextFactory(temp.toString(), 27, 256)
                     : new AccessibleContextFactory(temp.toString(), 27, 512);
               } else {
                  return ((AccessibleContextProxy)temp).getAccessibleContext();
               }
            } else {
               return (AccessibleContext)temp;
            }
         } else {
            return new AccessibleContextFactory(this.getEmptyString(), 27, 4);
         }
      } else {
         return null;
      }
   }

   @Override
   public void getFocusRectPhantom(XYRect rect) {
      this.calcFocusRect();
      rect.set(this._focusRectX, this._focusRectY, this._focusRectWidth, this._focusRectHeight);
      int left = this._iconWidth + this._iconGap;
      if (rect.x >= left) {
         rect.x -= left;
         rect.width += left;
      } else {
         rect.width = rect.width + rect.x;
         rect.x = 0;
      }
   }
}
