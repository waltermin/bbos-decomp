package net.rim.device.apps.api.setupwizard;

import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.XYRect;
import net.rim.device.internal.ui.component.Scrollbar;

public class WizardLayoutManager extends Manager {
   private Manager _header;
   private Field _content;
   private Field _footer;
   private Field _sideBar;
   private int _sideBarWidth;
   private Scrollbar _scrollbar;
   private XYRect _paintingRect = (XYRect)(new Object());
   private boolean _isRightToLeft;

   public WizardLayoutManager() {
      super(3459327463773962240L);
   }

   public void setScrollbarEnabled(boolean val) {
      if (val) {
         if (this._scrollbar == null) {
            this._scrollbar = (Scrollbar)(new Object(true));
            this.add(this._scrollbar);
            if (this._content instanceof Object) {
               this._scrollbar.setClient((Manager)this._content);
               return;
            }
         }
      } else if (this._scrollbar != null) {
         this.delete(this._scrollbar);
         this._scrollbar.setClient(null);
         this._scrollbar = null;
      }
   }

   public boolean getScrollbarEnabled() {
      return this._scrollbar != null;
   }

   public void setRightToLeftScrollbar(boolean isRightToLeft) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   public void setHeader(Field header) {
      if (this._header != null) {
         Field tmp = this._header;
         this._header = null;
         this.delete(tmp);
      }

      if (header != null) {
         this._header = (Manager)(new Object());
         this._header.add(header);
         this._header.add((Field)(new Object()));
         this.add(this._header);
      }
   }

   public Field getHeader() {
      return this._header;
   }

   public void setContent(Field content) {
      if (this._content != null) {
         Field tmp = this._content;
         this._content = null;
         this.delete(tmp);
      }

      this._content = content;
      if (this._content != null) {
         this.add(this._content);
         if (this._scrollbar != null && this._content instanceof Object) {
            this._scrollbar.setClient((Manager)this._content);
         }
      }
   }

   public Field getContent() {
      return this._content;
   }

   public void setSideBar(Field sideBar, int width) {
      if (this._sideBar != null) {
         Field tmp = this._sideBar;
         this._sideBar = null;
         this.delete(tmp);
      }

      this._sideBar = sideBar;
      this._sideBarWidth = width;
      if (this._sideBar != null) {
         this.add(this._sideBar);
      }
   }

   public Field getSideBar() {
      return this._sideBar;
   }

   public void setFooter(Field footer) {
      if (this._footer != null) {
         Field tmp = this._footer;
         this._footer = null;
         this.delete(tmp);
         this._footer = null;
      }

      if (footer != null) {
         this._footer = footer;
         this.add(this._footer);
      }
   }

   public Field getFooter() {
      return this._footer;
   }

   @Override
   protected void sublayout(int width, int height) {
      int headerHeight = 0;
      int footerHeight = 0;
      if (this._header != null) {
         this.layoutChild(this._header, Display.getWidth(), Display.getHeight());
         headerHeight = this._header.getExtent().height;
         this.setPositionChild(this._header, 0, 0);
         this.layoutChild(this._header, width, headerHeight);
      }

      if (this._footer != null) {
         this.layoutChild(this._footer, Display.getWidth(), Display.getHeight());
         footerHeight = this._footer.getExtent().height;
         int left = 0;
         if ((this._footer.getStyle() & 12884901888L) != 0) {
            int footerWidth = this._footer.getExtent().width;
            left = (width >> 1) - (footerWidth >> 1);
         }

         this.setPositionChild(this._footer, left, height - footerHeight);
         this.layoutChild(this._footer, width, footerHeight);
      }

      int contentHeight = height - headerHeight - footerHeight;
      int contentWidth = width;
      int contentLeft = 0;
      if (this._sideBar != null) {
         contentWidth -= this._sideBarWidth;
      }

      if (this._scrollbar != null) {
         contentWidth -= this._scrollbar.getPreferredWidth();
         if (this._isRightToLeft) {
            contentLeft = this._scrollbar.getPreferredWidth();
         }
      }

      if (this._content != null) {
         this.layoutChild(this._content, contentWidth, contentHeight);
         this.setPositionChild(this._content, contentLeft, headerHeight);
      }

      if (this._sideBar != null) {
         this.layoutChild(this._sideBar, this._sideBarWidth, contentHeight);
         this.setPositionChild(this._sideBar, contentWidth + contentLeft, headerHeight);
      }

      if (this._scrollbar != null) {
         this.layoutChild(this._scrollbar, this._scrollbar.getPreferredWidth(), contentHeight);
         int scrollbarLeft = 0;
         if (!this._isRightToLeft) {
            scrollbarLeft = contentWidth + this._sideBarWidth;
         }

         this.setPositionChild(this._scrollbar, scrollbarLeft, headerHeight);
      }

      this.setExtent(width, height);
      this.setVirtualExtent(width, height);
   }

   @Override
   protected void subpaint(Graphics g) {
      this._paintingRect.set(g.getClippingRect());
      if (this._header != null && this._paintingRect.y < this._header.getExtent().Y2()) {
         this.paintChild(g, this._header);
      }

      if (this._footer != null && this._paintingRect.Y2() > this._footer.getExtent().y) {
         this.paintChild(g, this._footer);
      }

      if (this._content != null && this._content.getExtent().intersects(this._paintingRect)) {
         this.paintChild(g, this._content);
      }

      if (this._sideBar != null && this._sideBar.getExtent().intersects(this._paintingRect)) {
         this.paintChild(g, this._sideBar);
      }

      if (this._scrollbar != null
         && (
            !this._isRightToLeft && this._paintingRect.X2() > this._scrollbar.getExtent().x
               || this._isRightToLeft && this._paintingRect.x < this._scrollbar.getExtent().width
         )) {
         this.paintChild(g, this._scrollbar);
      }
   }
}
