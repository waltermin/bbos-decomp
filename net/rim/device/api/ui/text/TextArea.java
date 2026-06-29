package net.rim.device.api.ui.text;

import net.rim.device.api.i18n.Locale;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.i18n.ResourceBundleFamily;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.DrawTextParam;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.TextMetrics;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.theme.Tag;
import net.rim.device.api.ui.theme.ThemeAttributeSet;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.internal.ui.ArticInterface;
import net.rim.device.internal.ui.ArticInterface$LayoutRun;
import net.rim.device.internal.ui.ArticInterface$Line;
import net.rim.device.internal.ui.ArticInterface$LineInfo;
import net.rim.device.internal.ui.FormatParams;
import net.rim.device.internal.ui.Formatter;
import net.rim.device.internal.ui.Formatter$TextRenderer;
import net.rim.tid.text.AttributedString;
import net.rim.tid.text.AttributedString$Iterator;

public class TextArea implements Formatter$TextRenderer {
   private Field _field;
   private Tag _tag;
   private String _id;
   private ThemeAttributeSet _tas;
   private int _style;
   private int _rbKey;
   private int _cachedLocaleCode;
   private long _rbId;
   private String _rbName;
   private Object _textObject;
   private XYRect _extent = new XYRect();
   boolean _layoutValid = false;
   private int _layoutWidth;
   private int _layoutOffsetX = 0;
   private int _layoutOffsetY = 0;
   protected int _width;
   private int _lineCount;
   private AttributedString _text;
   private int _anchor;
   private int _cursor;
   private ArticInterface$Line _lineList;
   private ArticInterface$LineInfo _tempLineInfo = new ArticInterface$LineInfo();
   private FormatParams _formatParams = new FormatParams();
   private int _lastFormatLength;
   private boolean _removeLastLine;
   protected int _widthForPaintWithEllipsis;

   public void applyTheme() {
      this._tas = ThemeManager.getActiveTheme().getAttributeSet(this._tag, this._id, 0);
   }

   public XYRect getExtent() {
      return this._extent;
   }

   public int getHeight() {
      return this._extent.height;
   }

   public int getLineCount() {
      return this._lineCount;
   }

   public int getLineHeight(int lineId) {
      int id = 0;
      ArticInterface$Line line = null;

      for (line = this._lineList; line != null && id < lineId; line = line._next) {
         id++;
      }

      return line == null ? 0 : line._boundsBottom - line._boundsTop;
   }

   public Object getText() {
      this.checkLocale();
      return this._textObject;
   }

   public String getTextString() {
      this.checkLocale();
      return this._text.toString();
   }

   public int getWidth() {
      return this._layoutWidth - this._layoutOffsetX;
   }

   public boolean isLayoutValid() {
      return this._layoutValid;
   }

   public void invalidateLayout() {
      this._layoutValid = false;
   }

   public synchronized void layout(int width, int height) {
      this.checkLocale();
      this.setAttributesFromFont();
      int adjustedWidth;
      if ((this._style & 64) != 0) {
         adjustedWidth = Integer.MAX_VALUE;
         this._widthForPaintWithEllipsis = width == Display.getWidth() ? width - 1 : width;
      } else {
         adjustedWidth = width == Display.getWidth() ? width - 1 : width;
      }

      this.update(adjustedWidth);
      if (this._lineCount > 1 && (this._style & 64) != 0) {
         this._lineCount = 1;
      }

      int sumOfHeights = this.getLineTop(this._lineCount);
      this.setExtent(width, sumOfHeights);
   }

   public void setSelection(int selStart, int selEnd) {
      this._lineList = Formatter.setSelection(this._lineList, this._anchor, this._cursor, selStart, selEnd, true, this._width, this._text, this._formatParams);
      this._anchor = selStart;
      this._cursor = selEnd;
      if (this._removeLastLine) {
         this.removeLastLine();
      }
   }

   protected void paint(Graphics _1) {
      throw null;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public void paintSelf(Graphics graphics) {
      boolean notEmpty = graphics.pushRegion(this.getExtent());
      boolean var5 = false /* VF: Semaphore variable */;

      try {
         var5 = true;
         if (notEmpty) {
            if (this._tas != null) {
               this._field.setThemeAttributesSpecial(this._tas, graphics);
            }

            graphics.setStipple(-1);
            this.paint(graphics);
            if (this._tas != null) {
               this._field.setThemeAttributesSpecial(null, graphics);
               var5 = false;
            } else {
               var5 = false;
            }
         } else {
            var5 = false;
         }
      } finally {
         if (var5) {
            graphics.popContext();
         }
      }

      graphics.popContext();
   }

   protected final void setExtent(int width, int height) {
      this._extent.width = width;
      this._extent.height = height;
      this._layoutValid = true;
   }

   public boolean reduceWidthToFit(int width) {
      if (this._layoutValid && width >= this.getWidth() && width <= this._extent.width && this._width != Integer.MAX_VALUE) {
         this._extent.width = width;
         if (this._layoutOffsetX != 0) {
            int halign = this._style & 7;
            int delta = 0;
            if (halign == 5) {
               delta = this._width - width;
            } else if (halign == 4) {
               delta = (this._width - width) / 2;
            }

            this._layoutOffsetX -= delta;
            this._width = width;

            for (ArticInterface$Line line = this._lineList; line != null; line = line._next) {
               line._originX -= delta;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   public void setId(String id) {
      this._id = id;
   }

   public final void setPosition(int x, int y) {
      this._extent.x = x;
      this._extent.y = y;
   }

   public void setTag(Tag tag) {
      this._tag = tag;
   }

   public void setText(Object text) {
      this._rbId = 0;
      this._rbName = null;
      this.setTextInternal(text);
   }

   public void setText(ResourceBundleFamily family, int id) {
      this._rbId = family.getId();
      this._rbName = family.getName();
      this._rbKey = id;
      this._cachedLocaleCode = 0;
      this.checkLocale();
   }

   public int getStyle() {
      return this._style;
   }

   public void setStyle(int style) {
      this._style = style;
   }

   public XYRect[] getTextBounds(int start, int end) {
      if ((this._style & 128) != 0) {
         return new XYRect[]{this.getTruncatedTextBounds(start, end)};
      }

      end--;
      XYRect rect1 = Ui.getTmpXYRect();
      ArticInterface.DocPosToCaret(rect1, this._text, this._lineList, 0, 0, start, true);
      this.invertForR2L(rect1);
      XYRect rect2 = Ui.getTmpXYRect();
      ArticInterface.DocPosToCaret(rect2, this._text, this._lineList, 0, 0, end, true);
      this.invertForR2L(rect2);
      XYRect[] bounds = null;
      if (rect2.y <= rect1.y + rect1.height) {
         bounds = new XYRect[]{new XYRect(rect1)};
         bounds[0].union(rect2);
      } else {
         ArticInterface$LineInfo info = this.getLineInfoForDocPos(start, true);
         ArticInterface$Line firstLine = info._line;
         int offset = info._start;
         XYRect rect = Ui.getTmpXYRect();
         ArticInterface.DocPosToCaret(rect, this._text, this._lineList, 0, 0, offset + firstLine._textLength, false);
         this.invertForR2L(rect);
         XYRect firstRect = new XYRect(rect1);
         firstRect.union(rect);
         info = this.getLineInfoForDocPos(end, false);
         XYRect lastRect = new XYRect(rect2);
         ArticInterface.DocPosToCaret(rect, this._text, this._lineList, 0, 0, info._start, false);
         this.invertForR2L(rect);
         lastRect.union(rect);
         Ui.returnTmpXYRect(rect);
         bounds = new XYRect[]{firstRect, lastRect};
      }

      for (int i = 0; i < bounds.length; i++) {
         bounds[i].x = bounds[i].x + this._extent.x;
         bounds[i].y = bounds[i].y + this._extent.y;
      }

      Ui.returnTmpXYRect(rect1);
      Ui.returnTmpXYRect(rect2);
      return bounds;
   }

   public ArticInterface$LineInfo getLineInfoForYPos(int aY) {
      this._tempLineInfo._start = 0;
      this._tempLineInfo._top = 0;
      this._tempLineInfo._line = this._lineList;
      Formatter.getLineInfoForYPos(aY, this._tempLineInfo);
      return this._tempLineInfo;
   }

   public ArticInterface$LineInfo getLineInfoForDocPos(int aDocPos, boolean aLeadingEdge) {
      this._tempLineInfo._start = 0;
      this._tempLineInfo._top = 0;
      this._tempLineInfo._line = this._lineList;
      Formatter.getLineInfoForDocPos(aDocPos, aLeadingEdge, this._lineList, this._tempLineInfo, true);
      return this._tempLineInfo;
   }

   public synchronized void getSelectionRect(XYRect rect) {
      rect.set(0, 0, 0, 0);
      if (this._cursor != this._anchor) {
         ArticInterface$LineInfo info = this.getLineInfoForDocPos(this._anchor, true);
         ArticInterface$Line firstLine = info._line;
         int offset = info._start;
         int y = info._top;
         info = this.getLineInfoForDocPos(this._cursor, true);
         ArticInterface$Line lastLine = info._line;
         boolean atTheEnd = false;

         for (ArticInterface$Line currentLine = firstLine; currentLine != lastLine._next; currentLine = currentLine._next) {
            int left = currentLine._originX;
            int width = 0;
            int runs = currentLine._layoutRun == null ? 0 : currentLine._layoutRun.length;

            for (int j = 0; j < runs; j++) {
               ArticInterface$LayoutRun run = currentLine._layoutRun[j];
               if (offset + run._textStart == this._anchor) {
                  left += width;
                  width = 0;
               }

               width += run._advance;
               if (offset + run._textStart + run._textLength == this._cursor) {
                  atTheEnd = true;
                  break;
               }
            }

            int lineHeight = currentLine._boundsBottom - currentLine._boundsTop;
            rect.union(left, y, width, lineHeight);
            if (atTheEnd) {
               break;
            }

            int lineLength = currentLine._textLength + currentLine._skippedCharacters;
            offset += lineLength;
            y += lineHeight;
         }

         rect.x = rect.x + this._extent.x;
         rect.y = rect.y + this._extent.y;
      }
   }

   public void getTextBounds(XYRect rect) {
      rect.set(this._layoutOffsetX, this._layoutOffsetY, this.getWidth(), this.getHeight());
      rect.x = rect.x + this._extent.x;
      rect.y = rect.y + this._extent.y;
   }

   Field getField() {
      return this._field;
   }

   protected void setDrawTextParamFromStyle(DrawTextParam drawTextParam) {
      int drawStyle = this.getStyle();
      drawTextParam.iAlignment = 8;
      if (this._width > 0) {
         drawTextParam.iMaxAdvance = this._width;
      }

      if ((drawStyle & 64) != 0) {
         if ((drawStyle & 128) != 0) {
            drawTextParam.iTruncateWithEllipsis = 1;
         } else {
            drawTextParam.iTruncateWithEllipsis = 2;
         }

         drawTextParam.iMaxAdvance = this._widthForPaintWithEllipsis;
      } else {
         if ((drawStyle & 128) != 0) {
            drawTextParam.iTruncateWithEllipsis = 3;
         }
      }
   }

   public AttributedString$Iterator getTextIterator() {
      return this._text.getIterator();
   }

   @Override
   public int drawText(Graphics aGraphics, int aOffset, int aLength, int aX, int aY, DrawTextParam aDrawTextParam) {
      return aGraphics.drawText(this._text.getText(), aOffset, aLength, aX, aY, aDrawTextParam, null);
   }

   @Override
   public Font getFont() {
      return this._field.getFont();
   }

   private XYRect getTruncatedTextBounds(int start, int end) {
      XYRect rect = new XYRect(
         this._lineList._originX, this._lineList._originY + this._lineList._boundsTop, 0, this._lineList._boundsBottom - this._lineList._boundsTop
      );
      Font font = this.getFont();
      DrawTextParam drawTextParam = Ui.getTmpDrawTextParam();
      TextMetrics metrics = Ui.getTmpTextMetrics();
      this.setDrawTextParamFromStyle(drawTextParam);
      font.measureText(this._text.getText(), start, this._text.length() - start, drawTextParam, metrics);
      int subtractedLength = metrics.iAdvanceX;
      drawTextParam.iMaxAdvance -= subtractedLength;
      font.measureText(this._text.getText(), 0, start, drawTextParam, metrics);
      int left = metrics.iAdvanceX;
      rect.x += left;
      drawTextParam.iMaxAdvance += subtractedLength;
      font.measureText(this._text.getText(), start, end - start, drawTextParam, metrics);
      rect.width = metrics.iAdvanceX;
      Ui.returnTmpDrawTextParam(drawTextParam);
      Ui.returnTmpTextMetrics(metrics);
      return rect;
   }

   protected TextArea(Field field) {
      this(field, 0);
   }

   private synchronized void setTextInternal(Object text) {
      this._textObject = text;
      if (text == null) {
         text = "";
      }

      if (text instanceof StringBuffer) {
         this._text.replace(0, this._text.length(), (StringBuffer)text);
      } else {
         this._text.replace(0, this._text.length(), text.toString());
      }

      this._layoutValid = false;
   }

   private void setAttributesFromFont() {
      Font font = null;
      if (this._tas != null) {
         font = this._tas.getFont();
      }

      if (font == null) {
         font = this._field.getFont();
      }

      if (font != null) {
         long attrib = Ui.getAttributesFromFont(font) | Ui.DEFAULT_COLOR_ATTRIBS;
         int halign = this._style & 7;
         if (halign == 5) {
            attrib |= 4194304;
         } else if (halign == 4) {
            attrib |= 12582912;
         }

         this._text.setAttrib(0, this._text.length(), attrib, -1);
         long hanStyle = 0;
         switch (font.getStyle() & 7168) {
            case 1024:
               hanStyle = 1;
               break;
            case 2048:
               hanStyle = 2;
               break;
            case 3072:
               hanStyle = 3;
               break;
            case 4096:
               hanStyle = 4;
         }

         this._text.setGlobalAttrib(hanStyle, 7);
      }
   }

   private void update(int width) {
      this._formatParams.init(0, this._lastFormatLength, this._text.length(), 0, false, this._lineList);
      this._formatParams.computeParamsAfterTextChanged(false, Integer.MAX_VALUE);
      this._formatParams.initCursorLine(this._lineList, 0, this._lineList._boundsTop);
      this._lastFormatLength = this._text.length();
      this._lineList = Formatter.incrementalFormat(this._formatParams, this._field, width, this._text, 0, true, 0, false);
      this._lineCount = this._formatParams._lineCount;
      this._layoutWidth = this._formatParams._layoutWidth;
      this._width = width;
      this._layoutOffsetX = Integer.MAX_VALUE;
      this._removeLastLine = false;

      for (ArticInterface$Line line = this._lineList; line != null; line = line._next) {
         int offsetX = line._boundsLeft < 0 ? line._originX + line._boundsLeft : line._originX;
         this._layoutOffsetX = Math.min(this._layoutOffsetX, offsetX);
         if (line._next == null && this._lineCount > 1 && line._textLength + line._skippedCharacters == 0) {
            this._removeLastLine = true;
         }
      }

      if (this._removeLastLine) {
         this.removeLastLine();
      }
   }

   private void checkLocale() {
      if (this._rbId != 0) {
         int currentCode = Locale.getDefault().getCode();
         if (this._cachedLocaleCode != currentCode) {
            this._cachedLocaleCode = currentCode;
            ResourceBundleFamily family = ResourceBundle.getBundle(this._rbId, this._rbName);
            String translated = family.getString(this._rbKey);
            this.setTextInternal(translated);
         }
      }
   }

   private void invertForR2L(XYRect rect) {
      if (rect.width < 0) {
         rect.x = rect.x + rect.width;
         rect.width = -rect.width;
      }
   }

   private int getLineTop(int aIndex) {
      int y = 0;
      if (aIndex >= 0 && aIndex <= this._lineCount) {
         int i = 0;

         for (ArticInterface$Line currentLine = this._lineList; currentLine != null && i < aIndex; i++) {
            y += currentLine._boundsBottom - currentLine._boundsTop;
            currentLine = currentLine._next;
         }

         return y;
      } else {
         throw new IllegalArgumentException();
      }
   }

   protected TextArea(Field field, int style) {
      this._field = field;
      this._style = style;
      this._text = new AttributedString();
      this._lineList = new ArticInterface$Line();
      this._lineList._flags = 3;
      this._lineCount = 1;
   }

   private void removeLastLine() {
      ArticInterface$Line prev = null;

      for (ArticInterface$Line line = this._lineList; line != null; line = line._next) {
         int offsetX = line._boundsLeft < 0 ? line._originX + line._boundsLeft : line._originX;
         this._layoutOffsetX = Math.min(this._layoutOffsetX, offsetX);
         if (line._next == null && line._textLength + line._skippedCharacters == 0 && prev != null) {
            this._removeLastLine = true;
            prev._next = null;
            this._lineCount--;
         }

         prev = line;
      }
   }
}
