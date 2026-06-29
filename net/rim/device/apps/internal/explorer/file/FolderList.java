package net.rim.device.apps.internal.explorer.file;

import net.rim.device.api.collection.Collection;
import net.rim.device.api.collection.CollectionListener;
import net.rim.device.api.collection.ReadableList;
import net.rim.device.api.collection.util.UnsortedReadableList;
import net.rim.device.api.math.Fixed32;
import net.rim.device.api.system.Display;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.theme.Tag;
import net.rim.device.api.ui.theme.Theme;
import net.rim.device.api.ui.theme.ThemeAttributeSet;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.api.util.MathUtilities;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.api.ui.HintPollingThread;
import net.rim.device.apps.api.ui.HintPollingThread$HintProvider;
import net.rim.device.apps.api.ui.HintPopup;
import net.rim.device.apps.internal.explorer.file.options.ExplorerOptions;
import net.rim.device.apps.internal.explorer.file.resource.ExplorerIcons;
import net.rim.device.apps.internal.explorer.file.util.BitmapCache;
import net.rim.device.internal.io.file.MetaDataFile;
import net.rim.device.internal.ui.Border3d;
import net.rim.device.internal.ui.Image;

public final class FolderList extends Field implements CollectionListener, HintPollingThread$HintProvider {
   private ExploreCallback _callback;
   private MetaDataFile _metadataDb;
   private ThumbnailFetcher _thumbnailFetcher;
   private BitmapCache _bitmapCache;
   private ReadableList _items;
   private int _currentIndex;
   private int _columns;
   private int _verticalSize;
   private int _horizontalSize;
   private int _thumbnailHeight;
   private int _thumbnailWidth;
   private int _mode;
   private int _positionAdjustment;
   private boolean _showToolTip;
   private String _selectedFilename;
   private boolean _userSelected;
   private boolean _bulkAddMode;
   private static final Tag TOOLTIP_TAG = Tag.create("fileviewtooltip");
   public static final int VIEW_MODE_THUMBNAIL;
   public static final int VIEW_MODE_DETAILS;
   public static final int VIEW_MODE_TITLE;
   public static final int VIEW_MODE_MAX_VALUE;
   public static final int VIEW_MODE_ICON;
   private static final int THUMBNAIL_PADDING;
   private static final int FOCUS_BORDER_WIDTH;
   private static Border3d _focusBorder;
   private static ReadableList _emptyList = new FolderList$EmptyList();

   public final void setNumberOfColumns(int columns) {
      this._columns = columns;
      if (this._mode == 0) {
         this.updateLayout();
         if (this._thumbnailFetcher != null) {
            this.adjustThumbnailCacheSize();
         }
      }
   }

   public final void setViewMode(int mode) {
      if (this._mode == 1 || this._mode == 2) {
         this._columns = ExplorerOptions.getOptions().getNumberOfColumns();
      }

      this._mode = mode;
      if (this._callback != null) {
         switch (mode) {
            case -1:
               break;
            case 0:
            default:
               this._callback.statusOn();
               break;
            case 1:
            case 2:
               this._callback.statusOff();
         }
      }

      if (this._thumbnailFetcher != null) {
         this._thumbnailFetcher.setMode(mode);
         this.adjustThumbnailCacheSize();
      }

      this.updateLayout();
      this.invalidate();
   }

   final void setThumbnailFetcher(ThumbnailFetcher thumbnailFetcher) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   public final int getViewMode() {
      return this._mode;
   }

   public final void setCurrentView(String directory, ReadableList items) {
      if (this._items instanceof Object) {
         ((UnsortedReadableList)this._items).removeCollectionListener(this);
      }

      if (items != null) {
         this._items = items;
      } else {
         this._items = _emptyList;
      }

      this._bitmapCache.clear();
      this.invalidateThumbsToLoad();
      if (directory != null) {
         this._metadataDb = MetaDataFile.getOrCreate(directory);
      }

      this._userSelected = false;
      this._currentIndex = this.determineInitialIndex();
      this.updateLayout();
      if (items instanceof Object) {
         ((UnsortedReadableList)items).addCollectionListener(this);
      }
   }

   public final int getFirstNonExecutableAliasIndex() {
      int size = this._items.size();

      for (int i = 0; i < size; i++) {
         if (!((FileItemField)this._items.getAt(i)).isExecutableAlias()) {
            return i;
         }
      }

      return -1;
   }

   public final int determineInitialIndex() {
      int index = this.getFirstNonExecutableAliasIndex();
      if (index > 0) {
         return index;
      } else {
         return this._items.size() > 1 && this._items.getAt(0) instanceof UpAliasFileItemField ? 1 : 0;
      }
   }

   final void completedFetch(FileItemField fileItem) {
      int index = this._items.getIndex(fileItem);
      if (index >= 0) {
         this.invalidateItem(index);
      }
   }

   protected final void invalidateThumbsToLoad() {
      if (this._thumbnailFetcher != null) {
         this._thumbnailFetcher.invalidateThumbsToLoad();
      }
   }

   final void setBulkAddMode(boolean on) {
      this._bulkAddMode = on;
      if (!on) {
         this.setSelectedIndex(this._currentIndex);
      }
   }

   public final ReadableList getList() {
      return this._items;
   }

   public final int getFileCount() {
      return this._items.size();
   }

   public final FileItemField getSelectedItem() {
      FileItemField fileItem = null;
      if (this.getFileCount() > 0) {
         fileItem = (FileItemField)this._items.getAt(this._currentIndex);
      }

      return fileItem;
   }

   public final int getSelectedIndex() {
      return this._currentIndex;
   }

   public final void setSelectedIndex(int index) {
      index = MathUtilities.clamp(0, index, Math.max(0, this.getFileCount() - 1));
      this.setCurrentItem(index);
      this.focusAdd(false);
   }

   public final int setSelectedItem(String filename) {
      int i = this.getFileCount();

      while (--i >= 0) {
         FileItemField fileItem = (FileItemField)this._items.getAt(i);
         if (StringUtilities.strEqualIgnoreCase(filename, fileItem.getName())) {
            this.setCurrentItem(i);
            this._userSelected = true;
            return i;
         }
      }

      this._selectedFilename = filename;
      return -1;
   }

   final boolean isUserSelected() {
      return this._userSelected;
   }

   public final FileItemField getFile(int index) {
      return (FileItemField)this._items.getAt(index);
   }

   public final FileItemField getPreviousViewableFile(FileItemField startItem, boolean select) {
      for (int i = this._items.getIndex(startItem) - 1; i >= 0; i--) {
         FileItemField item = (FileItemField)this._items.getAt(i);
         if (item.canView()) {
            if (select) {
               this.setSelectedIndex(i);
            }

            return item;
         }
      }

      return null;
   }

   public final FileItemField getNextViewableFile(FileItemField startItem, boolean wrap, boolean select) {
      int startItemIndex = this._items.getIndex(startItem);
      int size = this.getFileCount();

      for (int i = startItemIndex + 1; wrap ? (i %= size) != startItemIndex : i < size; i++) {
         FileItemField item = (FileItemField)this._items.getAt(i);
         if (item.canView()) {
            if (select) {
               this.setSelectedIndex(i);
            }

            return item;
         }
      }

      return null;
   }

   @Override
   public final void elementRemoved(Collection collection, Object element) {
      if (collection == this._items) {
         this.setSelectedIndex(this._currentIndex);
         if (this._metadataDb != null) {
            this._metadataDb.purgeMetadataFromCache(((FileItemField)element).getName());
         }

         this.updateLayout();
         if (this.getFileCount() == 0 && this.getManager().getLeafFieldWithFocus() == this) {
            Screen screen = this.getScreen();
            if (screen != null) {
               screen.setFocus();
            }
         }
      }
   }

   @Override
   public final void elementUpdated(Collection collection, Object oldElement, Object newElement) {
      if (collection == this._items) {
         int index = this._items.getIndex(newElement);
         this.invalidateRange(this._currentIndex, index);
         this.setCurrentItem(this._currentIndex);
         if (this._metadataDb != null) {
            this._metadataDb.purgeMetadataFromCache(((FileItemField)newElement).getName());
         }
      }
   }

   @Override
   public final void elementAdded(Collection collection, Object element) {
      if (collection == this._items) {
         ReadableList items = this._items;
         if (items.getIndex(element) <= this._currentIndex) {
            if (this._userSelected) {
               this._currentIndex++;
            }

            if (!this._bulkAddMode) {
               this.setSelectedIndex(this._currentIndex);
            }
         }

         if (this._selectedFilename != null && element instanceof FileItemField) {
            FileItemField fileSpec = (FileItemField)element;
            if (fileSpec.getName().equals(this._selectedFilename)) {
               this.setSelectedIndex(items.getIndex(element));
               this._selectedFilename = null;
               this._userSelected = true;
            }
         }

         if (!this._bulkAddMode) {
            this.updateLayout();
         }
      }
   }

   @Override
   public final void reset(Collection collection) {
      if (collection == this._items) {
         this._bitmapCache.clear();
         this.invalidateThumbsToLoad();
         this.setSelectedIndex(this.determineInitialIndex());
         this._userSelected = false;
         this.updateLayout();
      }
   }

   @Override
   public final Object getHint() {
      LabelField hint = null;
      FileItemField field;
      if (this._showToolTip && (field = this.getSelectedItem()) != null) {
         hint = (LabelField)(new Object(field.getDisplayName(), 36028848558571520L));
         hint.setTag(TOOLTIP_TAG);
      }

      return hint;
   }

   @Override
   public final void getHintPosition(XYRect rect) {
      int yPos = rect.y + this._currentIndex / this._columns * this._verticalSize;
      int xPos = rect.x + this._currentIndex % this._columns * this._horizontalSize;
      int width = this.getManager().getExtent().width;
      int leftMargin = Display.getWidth() - width >> 1;
      xPos = MathUtilities.clamp(-leftMargin, xPos, Math.max(-leftMargin, width - rect.width));
      rect.x = xPos;
      rect.y = yPos;
      HintPopup.transformToScreen(this, rect);
      rect.translate(10, 10 - rect.height);
   }

   @Override
   public final void getFocusRect(XYRect rect) {
      if (this.getFileCount() > 0) {
         rect.set(
            this._currentIndex % this._columns * this._horizontalSize + this._positionAdjustment,
            this._currentIndex / this._columns * this._verticalSize,
            this._horizontalSize,
            this._verticalSize
         );
      }
   }

   @Override
   public final void layout(int width, int height) {
      int fontHeight = this.getFont().getHeight();
      switch (this._mode) {
         case -1:
            break;
         case 0:
         default:
            if (this._columns < 1) {
               this._columns = 1;
            }

            this._horizontalSize = width / this._columns;
            this._thumbnailWidth = this._horizontalSize - 4;
            this._positionAdjustment = width - this._horizontalSize * this._columns >> 1;
            this._thumbnailHeight = 3 * this._thumbnailWidth / 4;
            this._verticalSize = this._thumbnailHeight + 4;
            this._bitmapCache.setCacheParameters(this._thumbnailWidth, this._thumbnailHeight);
            break;
         case 1:
         case 2:
            this._columns = 1;
            this._thumbnailHeight = fontHeight + 2;
            this._thumbnailWidth = 20;
            this._horizontalSize = width;
            this._verticalSize = this._thumbnailHeight;
      }

      this.setExtent(width, this._verticalSize * ((this.getFileCount() + this._columns - 1) / this._columns));
   }

   @Override
   protected final void onUndisplay() {
      super.onUndisplay();
      if (this._thumbnailFetcher != null) {
         this._thumbnailFetcher.shutdown();
      }

      if (this._showToolTip) {
         HintPollingThread.reset();
      }
   }

   @Override
   protected final void paint(Graphics graphics) {
      XYRect clip = graphics.getClippingRect();
      int endY = clip.Y2();
      int y = clip.y;
      y = y / this._verticalSize * this._verticalSize;
      int skipXItems = clip.x / this._horizontalSize;
      int startX = skipXItems * this._horizontalSize + this._positionAdjustment;
      int rightColWidth = 0;
      int mode = this._mode;
      if (mode == 2) {
         rightColWidth = graphics.getFont().getAdvance("xx:xx");
      } else if (mode == 1) {
         rightColWidth = graphics.getFont().getAdvance("xxxmm");
      }

      int itemCount = this.getFileCount();

      while (y < endY) {
         int x = startX;
         int index = y / this._verticalSize * this._columns + skipXItems;

         for (int columnCount = 0; columnCount < this._columns && index < itemCount; x += this._horizontalSize) {
            if (graphics.pushRegion(x, y, this._horizontalSize, this._verticalSize, 0, 0)) {
               EncodedImage thumb = null;
               String title = null;
               Long duration = null;
               boolean thumbDrawn = false;
               FileItemField item = (FileItemField)this._items.getAt(index);
               if (mode != 0 && mode != 3) {
                  if (this._mode == 2 && this._metadataDb != null && item.hasThumbnailAvailabe()) {
                     String name = item.getName();
                     Object obj1 = this._metadataDb.getMetadataFromCache(name, 1);
                     if (obj1 instanceof Object) {
                        Object obj2 = this._metadataDb.getMetadataFromCache(name, 3);
                        if (obj2 instanceof Object) {
                           StringBuffer sb = (StringBuffer)(new Object((String)obj1));
                           sb.append(' ').append('-').append(' ').append((String)obj2);
                           title = sb.toString();
                        } else {
                           title = (String)obj1;
                        }
                     }

                     Object obj2 = this._metadataDb.getMetadataFromCache(name, 2);
                     if (obj2 instanceof Object) {
                        duration = (Long)obj2;
                     }

                     if ((obj1 == null || obj2 == null) && this._thumbnailFetcher != null) {
                        this._thumbnailFetcher.addElement(item);
                     }
                  }

                  item.paint(
                     graphics,
                     thumb,
                     this._horizontalSize,
                     this._verticalSize,
                     this._thumbnailWidth,
                     this._thumbnailHeight,
                     title,
                     duration,
                     mode,
                     this._bitmapCache,
                     rightColWidth
                  );
               } else {
                  if (this._metadataDb != null && item.hasThumbnailAvailabe()) {
                     Object obj = this._metadataDb.getMetadataFromCache(item.getName(), 0);
                     if (obj instanceof Object) {
                        thumb = (EncodedImage)obj;
                     }

                     if (obj == null && this._thumbnailFetcher != null) {
                        this._thumbnailFetcher.addElement(item);
                     }
                  }

                  label181:
                  try {
                     if (thumb != null) {
                        int reductionFactorY = Fixed32.div(Fixed32.toFP(thumb.getHeight()), Fixed32.toFP(this._thumbnailHeight));
                        int reductionFactorX = Fixed32.div(Fixed32.toFP(thumb.getWidth()), Fixed32.toFP(this._thumbnailWidth));
                        if (reductionFactorX > reductionFactorY) {
                           thumb.setScaleX32(reductionFactorX);
                           thumb.setScaleY32(reductionFactorX);
                        } else {
                           thumb.setScaleX32(reductionFactorY);
                           thumb.setScaleY32(reductionFactorY);
                        }

                        int borderWidth = this._horizontalSize - thumb.getScaledWidth() >> 1;
                        int borderHeight = this._verticalSize - thumb.getScaledHeight() >> 1;
                        int thumbWidth = thumb.getScaledWidth();
                        int thumbHeight = thumb.getScaledHeight();
                        this._bitmapCache.paint(graphics, thumb, borderWidth, borderHeight, thumbWidth, thumbHeight);
                        thumbDrawn = true;
                        if (graphics.isDrawingStyleSet(8)) {
                           XYRect rect = Ui.getTmpXYRect();
                           rect.set(borderWidth - 2, borderHeight - 2, thumbWidth + 4, thumbHeight + 4);
                           this.getFocusBorder().paint(graphics, rect);
                           Ui.returnTmpXYRect(rect);
                        }

                        if (item.isDRMForwardLocked()) {
                           Image glyph = ExplorerIcons.getGlyphs().getImage(0);
                           int glyphWidth = glyph.getWidth(16, 16);
                           int glyphHeight = glyph.getHeight(16, 16);
                           int xPos = this._horizontalSize - borderWidth + Math.min(1, borderWidth - glyphWidth - this._positionAdjustment);
                           glyph.paint(graphics, xPos, Math.max(0, borderHeight - glyphHeight), glyphWidth, glyphHeight);
                        }
                     }
                  } finally {
                     break label181;
                  }

                  if (!thumbDrawn) {
                     if (graphics.isDrawingStyleSet(8)) {
                        graphics.setColor(ThemeAttributeSet.getColor(this, 3));
                        graphics.setBackgroundColor(ThemeAttributeSet.getColor(this, 2));
                        graphics.setBackgroundImage(null, 0, 0);
                        graphics.clear();
                     }

                     Image icon = item.getImage(this._thumbnailHeight, graphics);
                     int iconWidth = icon.getWidth(this._horizontalSize, this._thumbnailWidth);
                     int iconHeight = icon.getHeight(this._verticalSize, this._thumbnailHeight);
                     icon.paint(graphics, this._horizontalSize - iconWidth >> 1, this._verticalSize - iconHeight >> 1, iconWidth, iconHeight);
                  }
               }
            }

            graphics.popContext();
            index++;
            columnCount++;
         }

         y += this._verticalSize;
      }

      if (this._thumbnailFetcher != null) {
         this._thumbnailFetcher.fetchNow();
      }
   }

   public FolderList(ExploreCallback screen, int mode, int columns, boolean showTooltips) {
      super(18014398509481984L);
      this._callback = screen;
      this._items = _emptyList;
      this._columns = columns;
      this._showToolTip = showTooltips;
      this.setViewMode(mode);
      this._bitmapCache = new BitmapCache(15);
   }

   @Override
   public final int getPreferredHeight() {
      return Display.getHeight();
   }

   @Override
   protected final void onFocus(int direction) {
      if (direction > 0) {
         this.setCurrentItem(this._currentIndex);
      } else {
         this.setCurrentItem(Math.max(0, this.getFileCount() - 1));
      }
   }

   @Override
   protected final int moveFocus(int amount, int status, int time) {
      int itemCount = this.getFileCount();
      if (itemCount == 0) {
         return amount;
      }

      int increment;
      if ((status & 536870912) == 0) {
         if ((status & 1) != 0) {
            increment = this._columns;
         } else {
            increment = 1;
         }
      } else if ((status & 65536) != 0) {
         if ((status & 1) != 0) {
            int newItem;
            if (amount > 0) {
               newItem = (this._currentIndex / this._columns + 1) * this._columns - 1;
               if (newItem >= itemCount) {
                  newItem = itemCount - 1;
               }

               increment = newItem - this._currentIndex;
            } else {
               newItem = this._currentIndex / this._columns * this._columns;
            }

            increment = Math.abs(newItem - this._currentIndex);
         } else {
            increment = 1;
            if (amount > 0) {
               if (this._currentIndex % this._columns == this._columns - 1) {
                  return 0;
               }
            } else if (this._currentIndex % this._columns == 0) {
               return 0;
            }
         }
      } else if ((status & 1) != 0) {
         int itemsInScreen = this.getManager().getVisibleHeight() / this._verticalSize * this._columns;
         int newItem;
         if (amount > 0) {
            newItem = this._currentIndex + itemsInScreen;
            if (newItem >= itemCount) {
               newItem = itemCount - this._columns + this._currentIndex % this._columns + 1;
            }
         } else {
            newItem = this._currentIndex - itemsInScreen;
            if (newItem < 0) {
               newItem = this._currentIndex % this._columns;
            }
         }

         increment = Math.abs(newItem - this._currentIndex);
      } else {
         increment = this._columns;
      }

      int newValue = this._currentIndex;
      if (amount > 0) {
         newValue += increment;
         if (newValue >= itemCount) {
            return amount;
         }
      } else {
         newValue -= increment;
         if (newValue < 0) {
            return amount;
         }
      }

      this.setCurrentItem(newValue);
      this._userSelected = true;
      return 0;
   }

   private final Border3d getFocusBorder() {
      if (_focusBorder == null) {
         int color_blue = ThemeAttributeSet.getColor(this, 2);
         int color_darkBlue = 139;
         _focusBorder = (Border3d)(new Object(
            4, 4, 4, 4, color_blue, color_darkBlue, color_blue, color_darkBlue, color_blue, color_darkBlue, color_blue, color_darkBlue
         ));
      }

      return _focusBorder;
   }

   @Override
   public final boolean isFocusable() {
      return this.getFileCount() == 0 ? false : super.isFocusable();
   }

   @Override
   protected final void moveFocus(int x, int y, int status, int time) {
      int index = (y + this._verticalSize - 1) / this._verticalSize * this._columns + (x + this._horizontalSize - 1) / this._horizontalSize;
      if (index >= 0 || index < this.getFileCount()) {
         this.setCurrentItem(index);
         this._userSelected = true;
      }
   }

   private final void setCurrentItem(int newValue) {
      int itemCount = this._items.size();
      if (itemCount == 0) {
         this._currentIndex = 0;
         if (this._callback != null) {
            this._callback.currentItemChanged(this, (FileItemField)null);
         }
      } else {
         this._currentIndex = MathUtilities.clamp(0, newValue, itemCount - 1);
         if (this._callback != null) {
            this._callback.currentItemChanged(this, this.getSelectedItem());
         }

         if (this._showToolTip) {
            HintPollingThread.reset();
         }
      }
   }

   @Override
   protected final void onObscured() {
      super.onObscured();
      if (this._showToolTip) {
         HintPollingThread.reset();
      }
   }

   @Override
   public final int getPreferredWidth() {
      return Display.getWidth();
   }

   private final void invalidateRange(int fromIndex, int toIndex) {
      int top = Math.min(fromIndex, toIndex) / this._columns * this._verticalSize;
      int height = (Math.abs(toIndex - fromIndex) + 1) * this._verticalSize;
      this.invalidate(0, top, this._columns * this._horizontalSize, height);
   }

   private final void invalidateItem(int index) {
      this.invalidate(index % this._columns * this._horizontalSize, index / this._columns * this._verticalSize, this._horizontalSize, this._verticalSize);
   }

   @Override
   protected final void drawFocus(Graphics graphics, boolean on) {
      if (this._mode != 0) {
         super.drawFocus(graphics, on);
      } else {
         XYRect rect = Ui.getTmpXYRect();
         this.getFocusRect(rect);
         if (graphics.pushContext(rect.x, rect.y, rect.width, rect.height, 0, 0)) {
            graphics.setDrawingStyle(8, on);
            this.paint(graphics);
         }

         graphics.popContext();
         Ui.returnTmpXYRect(rect);
      }
   }

   private final void adjustThumbnailCacheSize() {
      XYRect rect = this.getManager().getExtent();
      int maxRows = (rect.height + this._verticalSize) / this._verticalSize;
      this._thumbnailFetcher.setSize(++maxRows * this._columns);
   }

   @Override
   protected final void applyTheme() {
      super.applyTheme();
      Theme theme = ThemeManager.getActiveTheme();
      _focusBorder = (Border3d)theme.getBorder("explorer-thumbnail");
   }
}
