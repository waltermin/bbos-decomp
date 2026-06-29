package net.rim.device.api.ui.component;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Manager;
import net.rim.tid.text.AttributedString;
import net.rim.tid.text.AttributedString$Iterator;

class SymbolScreen$Header extends Manager {
   SymbolScreen$Encoding _encoding;
   SymbolScreen$CharacterCodeField _code;
   int _pageWidth;
   TextField _page;
   AttributedString _pageAttributedString;
   AttributedString$Iterator _pageAttributedStringIterator;
   StringBuffer _pageBuffer;
   private final SymbolScreen this$0;

   public SymbolScreen$Header(SymbolScreen _1) {
      super(1152921504606846976L);
      this.this$0 = _1;
      this._encoding = new SymbolScreen$Encoding(this.this$0, 18014398509481984L, 0, this);
      this._page = new TextField(36028797018963968L);
      this._pageAttributedString = new AttributedString();
      this._pageAttributedStringIterator = this._pageAttributedString.getIterator();
      Font f = this.getFont();
      f = f.derive((Graphics.isColor() ? 1 : 0) | f.getStyle() & 7168, 10, 2);
      this._encoding.setFont(f);
      this._code = new SymbolScreen$CharacterCodeField(_1, 0, f);
      String pageString = "55/55";
      this._pageBuffer = new StringBuffer(pageString);
      this._pageWidth = f.getBounds(pageString);
      this._page.setFont(f);
      this.add(this._encoding);
      this.add(this._code);
      this.add(this._page);
   }

   public void setHeader(int encoding, int code, boolean showAnyway) {
      this._encoding.setEncoding(encoding);
      if (!this._code.setCode(code) && !showAnyway) {
         this._code.setType(-1);
      }

      this.update();
   }

   public int getEncoding() {
      return this._encoding.getEncoding();
   }

   public int getCode() {
      return this._code.getCode();
   }

   @Override
   protected void sublayout(int maxWidth, int maxHeight) {
      this.layoutChild(this._encoding, maxWidth, maxHeight);
      this._code.setHeight(this._encoding.getHeight());
      this.layoutChild(this._code, this._code.getWidth(), maxHeight);
      this.layoutChild(this._page, this._pageWidth, maxHeight);
      int height = this._encoding.getHeight();
      this.setVirtualExtent(maxWidth, height);
      this.setPositionChild(this._encoding, 0, 0);
      this.setPositionChild(this._code, this._encoding.getWidth() + 2, 0);
      this.setPositionChild(this._page, maxWidth - this._page.getWidth(), 0);
      this.setExtent(maxWidth, height);
   }

   public void update() {
      this.sublayout(this.getWidth(), this.getHeight());
      this.invalidate();
   }

   public void setFocusOnCode() {
      this.setFieldWithFocus(this._code);
   }

   @Override
   public int moveFocus(int amount, int status, int time) {
      Field field = this.getFieldWithFocus();
      if (field == this._encoding && amount < 0) {
         this.this$0._symbols.nextPage(false, true);
         return 0;
      } else if (field == this._code && amount > 0) {
         this.this$0._symbols._focusRow = 0;
         this.this$0._symbols._focusColumn = 0;
         return amount;
      } else {
         return super.moveFocus(amount, status, time);
      }
   }

   public void setPageNumber(int page) {
      if (page < 10) {
         this._pageBuffer.setCharAt(0, ' ');
         this._pageBuffer.setCharAt(1, (char)(page + 48));
      } else if (page < 20) {
         this._pageBuffer.setCharAt(0, (char)(page / 10 + 48));
         this._pageBuffer.setCharAt(1, (char)(page % 10 + 48));
      } else {
         this._pageBuffer.setCharAt(0, '?');
         this._pageBuffer.setCharAt(1, '?');
      }

      page = this.this$0._symbols._pages.length;
      if (page < 10) {
         this._pageBuffer.setCharAt(3, (char)(page + 48));
         this._pageBuffer.setCharAt(4, ' ');
      } else if (page < 20) {
         this._pageBuffer.setCharAt(3, (char)(page / 10 + 48));
         this._pageBuffer.setCharAt(4, (char)(page % 10 + 48));
      } else {
         this._pageBuffer.setCharAt(3, '?');
         this._pageBuffer.setCharAt(4, '?');
      }

      this._pageAttributedString.replace(0, this._pageAttributedString.length(), this._pageBuffer);
      this._pageAttributedStringIterator.set(0, this._pageAttributedString.length());
      this._page.replace(0, this._page.getTextLength(), this._pageAttributedStringIterator, 0, 0);
   }
}
