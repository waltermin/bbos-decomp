package net.rim.device.apps.api.messaging.messagelist;

import net.rim.device.api.i18n.DateFormat;
import net.rim.device.api.i18n.Locale;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.VariableHeightListField;
import net.rim.device.api.ui.theme.Tag;
import net.rim.device.api.ui.theme.ThemeAttributeSet;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.api.util.WeakReferenceUtilities;
import net.rim.device.apps.api.framework.model.ColumnPaintProvider;
import net.rim.device.apps.api.framework.model.ColumnPainter;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.PaintProvider;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.ui.PriorityIcons;
import net.rim.device.apps.api.ui.VariableRowHeightProxy;
import net.rim.device.apps.api.utility.columninfo.ColumnInformation;
import net.rim.device.internal.i18n.DateTimeFormatOptions;
import net.rim.device.internal.ui.IconCollection;
import net.rim.device.internal.ui.RichText;
import net.rim.vm.WeakReference;

public class MessageListColumnPainter extends ColumnPainter {
   private ColumnInformation _columnInformation;
   private int _linesPerEntry;
   private int _fontHeight;
   private Font _originalFont;
   private Font _unemphasisFont;
   private boolean _levelOne;
   private int _currentLineAttributes = 0;
   private Graphics _graphics;
   private VariableHeightListField _listField;
   private int _y;
   private int _height;
   private ContextObject _cachedContext = (ContextObject)(new Object());
   private int _themeGeneration;
   private ThemeAttributeSet _themeAttributesHeader;
   private ThemeAttributeSet _themeAttributesLine1;
   private ThemeAttributeSet _themeAttributesLine2;
   private ThemeAttributeSet _themeAttributesLine1Level1;
   private ThemeAttributeSet _themeAttributesLine2Level1;
   private boolean _disableEllipses = false;
   private short _listSeparatorAppearance;
   private static WeakReference _bufferWR = (WeakReference)(new Object(null));
   private static DateFormat _timeColumnFormat = DateFormat.getInstance(7);
   private static int _currentTimeFormat = DateTimeFormatOptions.getTimeFormat();
   private static int _cachedLocaleCode;
   private static Tag TAG_HEADER = Tag.create("header");
   private static Tag TAG_LINE1 = Tag.create("messagelist-line1");
   private static Tag TAG_LINE2 = Tag.create("messagelist-line2");
   private static Tag TAG_LINE1_LEVEL1 = Tag.create("messagelist-line1-level1");
   private static Tag TAG_LINE2_LEVEL1 = Tag.create("messagelist-line2-level1");
   private static final int LINE_SEPARATOR_SPACE = 1;

   public static int getIconWidth(Font font) {
      return PriorityIcons.ICONS.getWidth(font);
   }

   public MessageListColumnPainter() {
      this._columnInformation = new MessageListColumnPainter$MessageListColumnInformation(this);
      this.updateColumnInformation();
   }

   public void updateColumnInformation() {
      this.updateColumnInformation(Font.getDefault());
   }

   public void updateColumnInformation(Font font) {
      if (this._graphics == null) {
         UiApplication uiApp = UiApplication.getUiApplication();
         if (uiApp != null) {
            Screen activeScreen = uiApp.getActiveScreen();
            if (activeScreen != null) {
               this._graphics = activeScreen.getGraphics();
            }
         }
      }

      MessageListOptions options = MessageListOptions.getOptions();
      options.updateColumnInfo(this._columnInformation, font);
      this._disableEllipses = options.getDisableMessageListEllipses();
      this._listSeparatorAppearance = options.getListSeparatorAppearance();
      int bottomLineMajorColumnIndex = 3;
      this._linesPerEntry = this._columnInformation.getColumnOffset(bottomLineMajorColumnIndex) >= Display.getWidth() ? 2 : 1;
      this._fontHeight = font.getHeight();
   }

   public ColumnInformation getColumnInformation() {
      return this._columnInformation;
   }

   public int getLinesPerEntry() {
      return this._linesPerEntry;
   }

   public int getRowHeight(Object model) {
      int height = this._fontHeight + (this.isLineSeparatorsMode() ? 1 : 0);
      if (model instanceof Object) {
         height *= this._linesPerEntry;
      }

      return height;
   }

   public int getVerticalQuantization() {
      return !this.isLineSeparatorsMode() ? -1 : this._fontHeight + 1;
   }

   private void updateThemeAttributes() {
      int themeGeneration = ThemeManager.getGeneration();
      if (themeGeneration != this._themeGeneration) {
         this._themeGeneration = themeGeneration;
         this._themeAttributesHeader = ThemeManager.getActiveTheme().getAttributeSet(TAG_HEADER);
         this._themeAttributesLine1 = ThemeManager.getActiveTheme().getAttributeSet(TAG_LINE1);
         this._themeAttributesLine2 = ThemeManager.getActiveTheme().getAttributeSet(TAG_LINE2);
         this._themeAttributesLine1Level1 = ThemeManager.getActiveTheme().getAttributeSet(TAG_LINE1_LEVEL1);
         this._themeAttributesLine2Level1 = ThemeManager.getActiveTheme().getAttributeSet(TAG_LINE2_LEVEL1);
      }
   }

   private void setThemeAttributesForLine(VariableHeightListField listField, Graphics graphics, int line) {
      int newLine = 1 + line * 2 + (this._levelOne ? 1 : 0);
      if (newLine != this._currentLineAttributes) {
         ThemeAttributeSet newAttributes;
         switch (newLine) {
            case 1:
            default:
               newAttributes = this._themeAttributesLine1;
               break;
            case 2:
               newAttributes = this._themeAttributesLine1Level1;
               break;
            case 3:
               newAttributes = this._themeAttributesLine2;
               break;
            case 4:
               newAttributes = this._themeAttributesLine2Level1;
         }

         listField.setThemeAttributesSpecial(newAttributes, graphics);
         this._currentLineAttributes = newLine;
         if (this._unemphasisFont != null && line == 0) {
            this.setEmphasis(true);
         }
      }
   }

   public void drawRow(VariableHeightListField listField, Graphics graphics, int y, int width, Object model) {
      this._graphics = graphics;
      this._listField = listField;
      this._y = y;
      this._height = graphics.getFont().getHeight();
      this._columnInformation.clearColumnFilled();
      this._originalFont = graphics.getFont();
      this.updateThemeAttributes();
      this._currentLineAttributes = 0;
      this._levelOne = false;
      listField.setThemeAttributesSpecialClear(true);
      int linePadding = this.isLineSeparatorsMode() ? 1 : 0;
      int lineHeight = this._height + linePadding;
      if (!(model instanceof Object)) {
         if (model instanceof DateSeparator) {
            graphics.pushRegion(0, y, width, lineHeight, 0, 0);
            listField.setThemeAttributesSpecial(this._themeAttributesHeader, graphics);
            PaintProvider paintProvider = (PaintProvider)model;
            paintProvider.paint(graphics, 0, 0, width, lineHeight, this._cachedContext);
         } else if (model instanceof Object) {
            graphics.pushRegion(0, y, width, lineHeight, 0, 0);
            this.setThemeAttributesForLine(listField, graphics, 0);
            listField.setThemeAttributesSpecialClear(false);
            PaintProvider paintProvider = (PaintProvider)model;
            paintProvider.paint(graphics, 0, y, width, lineHeight, this._cachedContext);
         }
      } else {
         ColumnPaintProvider paintProvider = (ColumnPaintProvider)model;
         lineHeight = (this._height + linePadding) * this._linesPerEntry;
         graphics.pushRegion(0, y, width, lineHeight, 0, 0);
         this.setThemeAttributesForLine(listField, graphics, 0);
         listField.setThemeAttributesSpecialClear(false);
         paintProvider.paint(this, this._cachedContext);
      }

      if (this.isLineSeparatorsMode()) {
         graphics.setColor(0);
         graphics.setGlobalAlpha(32);
         graphics.drawLine(0, lineHeight - 1, width, lineHeight - 1);
      }

      this._unemphasisFont = null;
      listField.setThemeAttributesSpecial(null, null);
      graphics.popContext();
      graphics.setFont(this._originalFont);
      this._originalFont = null;
      this._listField = null;
   }

   @Override
   public void setPriority(int priority) {
      if (!this._columnInformation.getColumnFilled(0)) {
         int index;
         switch (priority) {
            case 1:
               index = 1;
               break;
            case 2:
            default:
               index = 2;
               break;
            case 3:
               index = 0;
         }

         this.drawIcon(0, PriorityIcons.ICONS, index);
      }
   }

   @Override
   public void setEmphasis(boolean emphasis) {
      if (emphasis) {
         this._unemphasisFont = this._graphics.getFont();
         int style = this._unemphasisFont.getStyle();
         if ((style & 1) != 0) {
            style = style & -2 | 64;
         } else {
            style |= 1;
         }

         Font newFont = this._unemphasisFont.derive(style);
         this._graphics.setFont(newFont);
      } else {
         if (this._unemphasisFont != null) {
            this._graphics.setFont(this._unemphasisFont);
            this._unemphasisFont = null;
         }
      }
   }

   @Override
   public void setLevelOne(boolean on) {
      if (this._levelOne != on) {
         this._levelOne = on;
         this.setThemeAttributesForLine(this._listField, this._graphics, (this._currentLineAttributes - 1) / 2);
      }
   }

   @Override
   public void drawIcon(int columnId, IconCollection icons, int index) {
      if (!this._columnInformation.getColumnFilled(columnId)) {
         this._columnInformation.setColumnFilled(columnId);
         int columnWidth = this._columnInformation.getColumnWidth(columnId);
         if (columnWidth != 0) {
            int y = VariableRowHeightProxy.getAdjustedY(this._cachedContext, this._y) - this._y;
            icons.paint(this._graphics, this._columnInformation.getColumnOffset(columnId), y, columnWidth, this._graphics.getFont().getHeight(), index);
         }
      }
   }

   @Override
   public void drawText(int columnId, String text, boolean annex) {
      if (!this._columnInformation.getColumnFilled(columnId)) {
         this._columnInformation.setColumnFilled(columnId);
         int columnWidth = this._columnInformation.getColumnWidth(columnId);
         if (annex && columnId == 3 && this.isColumnEmpty(4)) {
            columnWidth += this._columnInformation.getColumnWidth(4);
            this._columnInformation.setColumnFilled(4);
         }

         if (columnWidth != 0) {
            if (text != null && text.length() > 100) {
               text = text.substring(0, 100);
            }

            int offset = this._columnInformation.getColumnOffset(columnId);
            int screenWidth = Display.getWidth();
            int y = this._y;
            if (offset > screenWidth) {
               y += this._graphics.getFont().getHeight();
               offset -= screenWidth;
               this.setThemeAttributesForLine(this._listField, this._graphics, 1);
            } else {
               this.setThemeAttributesForLine(this._listField, this._graphics, 0);
            }

            Font originalFont = this._graphics.getFont();
            Font fontToUse = originalFont;
            y = VariableRowHeightProxy.getAdjustedY(this._cachedContext, fontToUse, text, y) - this._y;
            int paragraphOrdering = RichText.getDefaultParagDirection();
            int drawFlags = 6;
            if (text != null && text.length() > 0) {
               if (RichText.getLineDirection(text) == 2) {
                  paragraphOrdering = 3;
                  drawFlags = 5;
               } else {
                  paragraphOrdering = 2;
               }
            }

            int rightmostExtent = offset + columnWidth;
            if (rightmostExtent > screenWidth) {
               columnWidth = screenWidth - offset;
               rightmostExtent = screenWidth;
            }

            if (!Graphics.SCREEN_HAS_BORDER && rightmostExtent == Display.getWidth()) {
               columnWidth--;
            }

            this._graphics.setFont(fontToUse);
            if (this._disableEllipses) {
               this._graphics.drawText(text, offset, y, drawFlags, columnWidth);
            } else {
               RichText.drawTextWithEllipses(this._graphics, text, offset, y, columnWidth, paragraphOrdering, drawFlags);
            }

            this._graphics.setFont(originalFont);
         }
      }
   }

   public void updateTimeFormat() {
      int timeFormat = DateTimeFormatOptions.getTimeFormat();
      int currentCode = Locale.getDefault().getCode();
      if (_cachedLocaleCode != currentCode || _currentTimeFormat != timeFormat) {
         _cachedLocaleCode = currentCode;
         _currentTimeFormat = timeFormat;
         _timeColumnFormat = DateFormat.getInstance(7);
      }
   }

   @Override
   public void drawTime(int columnId, long time) {
      if (!this._columnInformation.getColumnFilled(columnId)) {
         this._columnInformation.setColumnFilled(columnId);
         int columnWidth = this._columnInformation.getColumnWidth(columnId);
         if (columnWidth != 0) {
            this.setThemeAttributesForLine(this._listField, this._graphics, 0);
            StringBuffer _buffer = WeakReferenceUtilities.getStringBuffer(_bufferWR);
            synchronized (_buffer) {
               _buffer.setLength(0);
               _timeColumnFormat.formatLocal(_buffer, time);
               int y = VariableRowHeightProxy.getAdjustedY(this._cachedContext, this._y) - this._y;
               this._graphics.drawText(_buffer, 0, _buffer.length(), this._columnInformation.getColumnOffset(columnId), y, 69, columnWidth);
            }
         }
      }
   }

   @Override
   public void drawModel(int columnId, RIMModel model, Object context) {
      this.drawModel(columnId, model, context, false);
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public void drawModel(int columnId, RIMModel model, Object context, boolean annex) {
      if (!this._columnInformation.getColumnFilled(columnId)) {
         this._columnInformation.setColumnFilled(columnId);
         int columnWidth = this._columnInformation.getColumnWidth(columnId);
         if (annex && columnId == 3 && this.isColumnEmpty(4)) {
            columnWidth += this._columnInformation.getColumnWidth(4);
            this._columnInformation.setColumnFilled(4);
         }

         if (columnWidth != 0) {
            int offset = this._columnInformation.getColumnOffset(columnId);
            int screenWidth = Display.getWidth();
            int y = 0;
            if (offset > screenWidth) {
               y += this._graphics.getFont().getHeight();
               offset -= screenWidth;
               this.setThemeAttributesForLine(this._listField, this._graphics, 1);
            } else {
               this.setThemeAttributesForLine(this._listField, this._graphics, 0);
            }

            if (model instanceof Object) {
               PaintProvider paintProvider = (PaintProvider)model;
               ContextObject contextObject = ContextObject.castOrCreate(context);
               boolean originalEllipsis = ContextObject.getFlag(contextObject, 17);
               boolean var14 = false /* VF: Semaphore variable */;

               try {
                  var14 = true;
                  if (!this._disableEllipses) {
                     ContextObject.setFlag(contextObject, 17);
                  } else {
                     ContextObject.clearFlag(contextObject, 17);
                  }

                  paintProvider.paint(this._graphics, offset, y, columnWidth, this._height, contextObject);
                  var14 = false;
               } finally {
                  if (var14) {
                     if (!originalEllipsis) {
                        ContextObject.clearFlag(contextObject, 17);
                     } else {
                        ContextObject.setFlag(contextObject, 17);
                     }
                  }
               }

               if (!originalEllipsis) {
                  ContextObject.clearFlag(contextObject, 17);
               } else {
                  ContextObject.setFlag(contextObject, 17);
               }
            }
         }
      }
   }

   @Override
   public boolean isColumnEmpty(int columnId) {
      return !this._columnInformation.getColumnFilled(columnId) && this._columnInformation.getColumnWidth(columnId) != 0;
   }

   @Override
   public void clear(int columnId) {
      if (this._columnInformation.getColumnFilled(columnId)) {
         this._graphics.clear(this._columnInformation.getColumnOffset(columnId), 0, this._columnInformation.getColumnWidth(columnId), this._height);
         this._columnInformation.clearColumnFilled(columnId);
      }
   }

   @Override
   public void setColumnFilled(int columnId) {
      this._columnInformation.setColumnFilled(columnId);
   }

   protected boolean isLineSeparatorsMode() {
      return this._listSeparatorAppearance == 2 && this._linesPerEntry > 1;
   }
}
