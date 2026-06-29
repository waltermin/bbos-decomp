package net.rim.device.apps.internal.ribbon.launcher;

import java.util.Hashtable;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.FocusChangeListener;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.UiEngine;
import net.rim.device.api.ui.XYEdges;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.component.ListFieldCallback;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.ui.theme.Tag;
import net.rim.device.api.ui.theme.Theme;
import net.rim.device.api.ui.theme.ThemeAttributeSet;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.api.util.Arrays;
import net.rim.device.apps.api.ribbon.RibbonComponentInitializer;
import net.rim.device.internal.ui.Background;
import net.rim.device.internal.ui.Border;
import net.rim.vm.Array;

final class VerticalApplicationLauncherField extends VerticalFieldManager implements ApplicationLauncherField, ListFieldCallback, RibbonComponentInitializer {
   private boolean _isForApplicationMenu;
   private ApplicationLauncherFieldHelper _helper;
   private ListField _listField;
   private ApplicationEntry[] _visibleApplications;
   private ApplicationEntry[] _workingList;
   private ApplicationEntry[] _movingList;
   private ApplicationEntry _movingApplication;
   private ThemeAttributeSet _attrNormal;
   private ThemeAttributeSet _attrFocus;
   private final XYRect _borderRect = (XYRect)(new Object());
   private final XYRect _iconRect = (XYRect)(new Object());
   private Bitmap _backgroundBitmap;
   private int _rowHeight = -1;
   private boolean _hotKeysDisabled;
   private int _longestTextWidthFocus = -1;
   private int _longestTextWidthNormal = -1;
   private int _longestTextWidth;
   private Border _borderFocus;
   private XYEdges _paddingFocus;
   private XYEdges _marginFocus;
   private Border _borderNormal;
   private XYEdges _paddingNormal;
   private XYEdges _marginNormal;
   private int _horizontalOverheadFocus;
   private int _horizontalOverheadNormal;
   private int _textAlignDrawStyle;
   private int _borderOverheadFocus;
   private int _borderOverheadNormal;
   private int _marginStyleFocus;
   private int _marginStyleNormal;
   private int _marginAbsoluteXFocus;
   private int _marginAbsoluteXNormal;
   private boolean _bDirtyList;
   private static final int PADDING_STYLE_LEFT = 0;
   private static final int PADDING_STYLE_RIGHT = 1;
   private static final int PADDING_STYLE_CENTER = 2;
   private static final int PADDING_STYLE_TIGHT = 3;
   private static final int MARGIN_STYLE_LEFT = 0;
   private static final int MARGIN_STYLE_RIGHT = 1;
   private static final int MARGIN_STYLE_CENTER = 2;
   private static final int MARGIN_STYLE_FULL_SCREEN = 3;
   private static Tag TAG = Tag.create("homescreen");
   private static String TAG_ID = "chooser";
   private static String OPTIONS_ID = "net_rim_bb_options_app.Options";
   private static final String TEXT_DISPLACEMENT_FROM_ICON_ID = "ApplicationMenuTextDisplacementFromIcon";
   private static XYEdges _emptyEdges = (XYEdges)(new Object());
   private static Border _emptyBorder = (Border)(new Object(0, 0, 0, 0));

   VerticalApplicationLauncherField(HierarchyManager hierarchyManager, boolean isForApplicationMenu) {
      super(1152921504606846976L | (isForApplicationMenu ? 0 : 2305843009213693952L) | 281474976710656L | 17592186044416L);
      this._isForApplicationMenu = isForApplicationMenu;
      this._helper = new ApplicationLauncherFieldHelper(this, hierarchyManager);
      this.setId(TAG_ID);
      this._visibleApplications = new ApplicationEntry[0];
      this._workingList = new ApplicationEntry[0];
      this._listField = new ApplicationListField(0, 0);
      this._listField.setId(TAG_ID);
      this._listField.setThemeAttributeSet(ThemeManager.getActiveTheme().getAttributeSet(this._listField.getTag(), this._listField.getId(), 0));
      this._listField.setCallback(this);
      this.add(this._listField);
   }

   protected final boolean isForApplicationMenu() {
      return this._isForApplicationMenu;
   }

   @Override
   protected final void onDisplay() {
      if (this.isForApplicationMenu() && this._bDirtyList) {
         Application app = this.getUiApp();
         if (app != null) {
            synchronized (app.getAppEventLock()) {
               this.loadApplicationsWithLock();
               this._bDirtyList = false;
               return;
            }
         }
      }
   }

   @Override
   protected final void applyTheme() {
      super.applyTheme();
      Theme theme = ThemeManager.getActiveTheme();
      this._attrNormal = theme.getAttributeSet(TAG, this.getId(), 0);
      this._attrFocus = theme.getAttributeSet(TAG, this.getId(), 6);
      this.setThemeAttributeSet(this._attrFocus);
      this._borderFocus = this.getBorder(ThemeAttributeSet.getBorder(this));
      this._paddingFocus = this.getEdges(ThemeAttributeSet.getEdges(this, 0));
      this._marginFocus = this.getEdges(ThemeAttributeSet.getEdges(this, 2));
      this.setThemeAttributeSet(this._attrNormal);
      this._borderNormal = this.getBorder(ThemeAttributeSet.getBorder(this));
      this._paddingNormal = this.getEdges(ThemeAttributeSet.getEdges(this, 0));
      this._marginNormal = this.getEdges(ThemeAttributeSet.getEdges(this, 2));
      this._horizontalOverheadFocus = this._borderFocus.getLeft()
         + this._borderFocus.getRight()
         + this.getAbsoluteX(this._paddingFocus)
         + this.getAbsoluteX(this._marginFocus);
      this._horizontalOverheadNormal = this._borderNormal.getLeft()
         + this._borderNormal.getRight()
         + this.getAbsoluteX(this._paddingNormal)
         + this.getAbsoluteX(this._marginNormal);
      this._textAlignDrawStyle = ThemeAttributeSet.getTextAlignAsDrawStyle(this);
      this._borderOverheadFocus = this.getAbsoluteX(this._paddingFocus) + this._borderFocus.getLeft() + this._borderFocus.getRight();
      this._borderOverheadNormal = this.getAbsoluteX(this._paddingNormal) + this._borderNormal.getLeft() + this._borderNormal.getRight();
      this._marginStyleFocus = this.determineMarginStyle(this._marginFocus);
      this._marginStyleNormal = this.determineMarginStyle(this._marginNormal);
      this._marginAbsoluteXFocus = this.getAbsoluteX(this._marginFocus);
      this._marginAbsoluteXNormal = this.getAbsoluteX(this._marginNormal);
      this._longestTextWidthFocus = -1;
      this.setThemeAttributeSet(null);
   }

   @Override
   public final void initialize(Hashtable parms, Object context) {
      String value = (String)parms.get("rowHeight");
      if (value != null) {
         try {
            this._rowHeight = Integer.parseInt(value);
         } finally {
            return;
         }
      }
   }

   @Override
   public final void uninitialize() {
   }

   @Override
   protected final boolean isScrollCopyable() {
      return false;
   }

   private final int determineMarginStyle(XYEdges margin) {
      if (margin.right >= 0) {
         return margin.left >= 0 ? 3 : 1;
      } else {
         return margin.left >= 0 ? 0 : 2;
      }
   }

   private final Border getBorder(Border border) {
      return border == null ? _emptyBorder : border;
   }

   private final XYEdges getEdges(XYEdges edges) {
      return edges == null ? _emptyEdges : edges;
   }

   private final int getAbsoluteX(XYEdges edges) {
      int result = 0;
      result += edges.left > 0 ? edges.left : -edges.left;
      return result + (edges.right > 0 ? edges.right : -edges.right);
   }

   @Override
   public final void setFocusListener(FocusChangeListener listener) {
      this._listField.setFocusListener(listener);
   }

   @Override
   public final void setFocus() {
      super.setFocus();
      this.resetFocusToTop();
   }

   @Override
   public final void resetFocusToTop() {
      if (this._listField.getSize() > 0) {
         this.setListFieldSelectedIndex(0);
      }
   }

   @Override
   public final void clearFolderStack() {
      this._helper.clearFolderStack();
   }

   @Override
   public final boolean isRootFolder() {
      return this._helper.isRootFolder();
   }

   @Override
   public final boolean goToFolder(String folderName) {
      return this._helper.goToFolder(folderName);
   }

   @Override
   public final boolean popFolder() {
      return this._helper.popFolder();
   }

   @Override
   public final void setActiveFolder(InternalApplicationFolder folder) {
      this._helper.setActiveFolder(folder);
   }

   @Override
   public final ApplicationEntry getSelectedApplication() {
      synchronized (this._visibleApplications) {
         int index = this._listField.getSelectedIndex();
         return index >= 0 && index < this._visibleApplications.length ? this._visibleApplications[index] : null;
      }
   }

   @Override
   public final ApplicationEntry getMovingApplication() {
      return this.getSelectedApplication();
   }

   @Override
   public final void setFocus(ApplicationEntry application) {
      int count = this._visibleApplications.length;
      if (application != null) {
         synchronized (this._visibleApplications) {
            for (int i = 0; i < count; i++) {
               if (application == this._visibleApplications[i]) {
                  this.setListFieldSelectedIndex(i);
                  return;
               }
            }

            String uniqueName = application.getPropertiesName();
            if (uniqueName != null) {
               for (int i = 0; i < count; i++) {
                  if (uniqueName.equals(this._visibleApplications[i].getPropertiesName())) {
                     this.setListFieldSelectedIndex(i);
                     return;
                  }
               }
            }
         }
      }

      if (count > 0) {
         this.resetFocusToTop();
      }
   }

   @Override
   public final InternalApplicationFolder getActiveFolder() {
      return this._helper._activeFolder;
   }

   @Override
   public final boolean hidingIconsAllowed() {
      return this._helper.hidingIconsAllowed();
   }

   @Override
   public final boolean hasHiddenApplications() {
      return this._helper.hasHiddenApplications();
   }

   @Override
   public final void setShowHiddenApps(boolean showHiddenApps) {
      this._helper._showHiddenApps = showHiddenApps;
   }

   @Override
   public final ApplicationEntry getApplicationByHotKey(char key) {
      return this._helper._hierarchyManager.getApplicationByHotKey(key);
   }

   @Override
   public final void deleteAllApplications() {
      this.setListFieldSize(0, 0);
   }

   @Override
   public final void deleteAll() {
      throw new RuntimeException("cod2jar: tail call (jumpspecial)");
   }

   @Override
   public final void loadApplications() {
      Application app = this.getUiApp();
      if (app == null && this.isForApplicationMenu() && this._visibleApplications.length == this._helper.getVisibleApplicationCount()) {
         this._bDirtyList = true;
      } else {
         if (app != null) {
            synchronized (app.getAppEventLock()) {
               this.loadApplicationsWithLock();
            }
         } else {
            this.loadApplicationsWithLock();
         }

         this._bDirtyList = false;
      }
   }

   private final void loadApplicationsWithLock() {
      InternalApplicationFolder activeFolder = this.getActiveFolder();
      if (activeFolder != null) {
         int applicationIconHeight = this._rowHeight;
         if (this._rowHeight == -1) {
            applicationIconHeight = this._helper.getApplicationIconHeight();
         }

         int quantization = applicationIconHeight >> 1;
         this.setVerticalQuantization(quantization > 0 ? quantization : applicationIconHeight);
         this._listField.setRowHeight(applicationIconHeight);
         int focusIndex = 0;
         synchronized (this._workingList) {
            ApplicationEntry[] applications = activeFolder.getApplications();
            ApplicationEntry focusedApp = this.getSelectedApplication();
            boolean showHiddenApps = this._helper._showHiddenApps;
            boolean grabNext = false;
            if (focusedApp != null && this._visibleApplications.length > 0 && this._visibleApplications[0] == focusedApp) {
               focusedApp = null;
            }

            Theme theme = ThemeManager.getActiveTheme();
            int applicationIconWidth = theme.getRibbonIconWidth();
            applicationIconHeight = theme.getRibbonIconHeight();
            Array.resize(this._workingList, 0);

            for (int i = 0; i < applications.length; i++) {
               try {
                  ApplicationEntry appEntry = applications[i];
                  if (this._isForApplicationMenu && appEntry.getUniqueName().equals(OPTIONS_ID)) {
                     appEntry.setVisible(true);
                  }

                  if (!showHiddenApps && !appEntry.isVisible()) {
                     if (appEntry == focusedApp) {
                        grabNext = true;
                     }
                  } else {
                     if (grabNext || focusedApp != null && appEntry.getPropertiesName().equals(focusedApp.getPropertiesName())) {
                        focusIndex = grabNext ? i - 1 : i;
                        grabNext = false;
                     }

                     RibbonIconField icon = appEntry.getRibbonIcon();
                     icon.setSize(applicationIconWidth, applicationIconHeight);
                     icon.applyTheme();
                     Arrays.add(this._workingList, appEntry);
                  }
               } finally {
                  continue;
               }
            }

            synchronized (this._visibleApplications) {
               ApplicationEntry[] tmp = this._visibleApplications;
               this._visibleApplications = this._workingList;
               this._workingList = tmp;
            }

            Array.resize(this._workingList, 0);
         }

         this._listField.setSize(this._visibleApplications.length, focusIndex);
         this._longestTextWidthFocus = -1;
      }
   }

   private final Application getUiApp() {
      Screen screen = this.getScreen();
      UiEngine engine = screen != null ? screen.getUiEngine() : null;
      return engine != null ? screen.getApplication() : null;
   }

   private final void setListFieldSelectedIndex(int index) {
      Application app = this.getUiApp();
      if (app != null) {
         synchronized (app.getAppEventLock()) {
            this._listField.setSelectedIndex(index);
         }
      } else {
         this._listField.setSelectedIndex(index);
      }
   }

   private final void setListFieldSize(int size, int focusRow) {
      Application app = this.getUiApp();
      if (app != null) {
         synchronized (app.getAppEventLock()) {
            this._listField.setSize(size, focusRow);
         }
      } else {
         this._listField.setSize(size, focusRow);
      }
   }

   private final void computeTextLengths() {
      if (this._longestTextWidthFocus == -1) {
         Font focusFont = this._attrFocus.getFont();
         if (focusFont == null) {
            focusFont = Font.getDefault();
         }

         Font nonfocusFont = this._attrNormal.getFont();
         if (nonfocusFont == null) {
            nonfocusFont = Font.getDefault();
         }

         this._longestTextWidthNormal = -1;
         synchronized (this._visibleApplications) {
            for (int i = this._visibleApplications.length - 1; i >= 0; i--) {
               String text = this._visibleApplications[i].getDescription(this._hotKeysDisabled);
               int length = text.length();
               int width = focusFont.getBounds(text, 0, length);
               if (width > this._longestTextWidthFocus) {
                  this._longestTextWidthFocus = width;
               }

               width = nonfocusFont.getBounds(text, 0, length);
               if (width > this._longestTextWidthNormal) {
                  this._longestTextWidthNormal = width;
               }
            }
         }

         if (this._longestTextWidthFocus > this._longestTextWidthNormal) {
            this._longestTextWidth = this._longestTextWidthFocus;
            return;
         }

         this._longestTextWidth = this._longestTextWidthNormal;
      }
   }

   @Override
   public final void drawListRow(ListField listField, Graphics graphics, int index, int y, int width) {
      ApplicationEntry[] entries = this._movingList == null ? this._visibleApplications : this._movingList;
      synchronized (entries) {
         if (index < entries.length) {
            ApplicationEntry app = entries[index];
            String text = app.getDescription(this._hotKeysDisabled);
            boolean focusDraw = graphics.isDrawingStyleSet(8);
            Font oldFont = graphics.getFont();
            this.computeTextLengths();
            ThemeAttributeSet attributes;
            Border border;
            XYEdges padding;
            XYEdges margin;
            int horizontalOverhead;
            int borderOverhead;
            int marginStyle;
            int marginAbsoluteX;
            int hideOpacity;
            if (focusDraw) {
               attributes = this._attrFocus;
               border = this._borderFocus;
               padding = this._paddingFocus;
               margin = this._marginFocus;
               borderOverhead = this._borderOverheadFocus;
               marginStyle = this._marginStyleFocus;
               marginAbsoluteX = this._marginAbsoluteXFocus;
               horizontalOverhead = this._horizontalOverheadFocus;
               hideOpacity = 128;
            } else {
               attributes = this._attrNormal;
               border = this._borderNormal;
               padding = this._paddingNormal;
               margin = this._marginNormal;
               borderOverhead = this._borderOverheadNormal;
               marginStyle = this._marginStyleNormal;
               marginAbsoluteX = this._marginAbsoluteXNormal;
               horizontalOverhead = this._horizontalOverheadNormal;
               hideOpacity = 64;
            }

            int maximumTextWidth = width - horizontalOverhead;
            if (this._longestTextWidth > maximumTextWidth) {
               this._longestTextWidth = maximumTextWidth;
            }

            Font newFont = attributes.getFont();
            if (newFont == null) {
               newFont = Font.getDefault();
            }

            int textWidth = this._longestTextWidth;
            this._borderRect.width = textWidth + borderOverhead;
            switch (marginStyle) {
               case -1:
                  break;
               case 0:
               default:
                  this._borderRect.x = margin.left;
                  break;
               case 1:
                  this._borderRect.x = width - this._borderRect.width - marginAbsoluteX - margin.left;
                  break;
               case 2:
                  this._borderRect.x = (width - horizontalOverhead - textWidth >> 1) - margin.left;
                  break;
               case 3:
                  this._borderRect.width = width - marginAbsoluteX;
                  this._borderRect.x = margin.left;
                  textWidth = this._borderRect.width - borderOverhead;
            }

            if (this._backgroundBitmap != null) {
               graphics.drawBitmap(0, y, width, this._listField.getRowHeight(), this._backgroundBitmap, 0, y + this.getExtent().y - this.getVerticalScroll());
            }

            this._borderRect.y = y + margin.top;
            this._borderRect.height = this._listField.getRowHeight() - margin.top - margin.bottom;
            border.paint(graphics, this._borderRect);
            this._borderRect.subtract(border.getEdges());
            Background background = border.getBackground();
            if (background != null) {
               background.draw(graphics, this._borderRect);
            }

            Theme theme = ThemeManager.getActiveTheme();
            int textDisplacementFromIcon = 0;
            int opacity = graphics.getGlobalAlpha();
            if (this._isForApplicationMenu) {
               String value = theme.getOption("ApplicationMenuTextDisplacementFromIcon");
               if (value != null) {
                  label149:
                  try {
                     textDisplacementFromIcon = Integer.parseInt(value);
                  } finally {
                     break label149;
                  }
               }

               RibbonIconField icon = app.getRibbonIcon();
               this._iconRect.set(this._borderRect);
               this._iconRect.add(border.getEdges());
               graphics.pushRegion(this._iconRect);
               int widthOffset = 0;
               Bitmap up = Theme.getThemeBitmap(0);
               Bitmap down = Theme.getThemeBitmap(1);
               if (app == this._movingApplication && up != null && down != null) {
                  int movingIndicatorAdjust = up.getHeight() >> 2;
                  graphics.drawBitmap(movingIndicatorAdjust, movingIndicatorAdjust, up.getWidth(), up.getHeight(), up, 0, 0);
                  graphics.drawBitmap(
                     movingIndicatorAdjust, this._borderRect.height - down.getHeight() - movingIndicatorAdjust, down.getWidth(), down.getHeight(), down, 0, 0
                  );
                  widthOffset = up.getWidth() + movingIndicatorAdjust;
               }

               graphics.translate(widthOffset, 0);
               if (focusDraw && !app.isVisible()) {
                  graphics.setGlobalAlpha(128);
               }

               icon.paintIconOnly(graphics);
               graphics.popContext();
               textDisplacementFromIcon += theme.getRibbonIconWidth() + widthOffset;
            }

            int textRectY = this._borderRect.y;
            int fontHeight = newFont.getHeight();
            if (padding.top < 0 && padding.bottom >= 0) {
               textRectY += this._borderRect.height - fontHeight - padding.bottom;
            } else if (padding.top >= 0 && padding.bottom < 0) {
               textRectY += padding.top;
            } else {
               textRectY += (this._borderRect.height - fontHeight >> 1) + (padding.top > 0 ? padding.top : -padding.top);
            }

            attributes.applyToGraphics(graphics);
            if (!app.isVisible()) {
               graphics.setGlobalAlpha(hideOpacity);
            }

            graphics.drawText(
               text, textDisplacementFromIcon + this._borderRect.x, textRectY, this._textAlignDrawStyle | 64, textWidth - textDisplacementFromIcon
            );
            graphics.setGlobalAlpha(opacity);
            if (app == this._movingApplication) {
               graphics.setColor(0);
               graphics.drawRect(this._borderRect.x, this._borderRect.y, this._borderRect.width, this._borderRect.height);
            }

            graphics.setFont(oldFont);
         }
      }
   }

   @Override
   public final int getPreferredWidth(ListField listField) {
      return this.getPreferredWidth();
   }

   @Override
   public final Object get(ListField listField, int index) {
      String text = null;
      ApplicationEntry[] entries = this._movingList == null ? this._visibleApplications : this._movingList;
      synchronized (entries) {
         if (index < entries.length) {
            ApplicationEntry app = entries[index];
            text = app.getDescription(this._hotKeysDisabled);
         }

         return text;
      }
   }

   @Override
   public final int indexOfList(ListField listField, String prefix, int start) {
      return -1;
   }

   @Override
   public final void beginMoveApplication() {
      this._movingList = new ApplicationEntry[this._visibleApplications.length];
      System.arraycopy(this._visibleApplications, 0, this._movingList, 0, this._visibleApplications.length);
      this._movingApplication = this.getSelectedApplication();
   }

   @Override
   public final boolean moveApplicationInProgress() {
      return this._movingList != null;
   }

   @Override
   public final void completeMoveApplication(boolean doMove) {
      Application app = this.getUiApp();
      if (app != null) {
         synchronized (app.getAppEventLock()) {
            this.completeMoveApplicationWithLock(doMove);
         }
      } else {
         synchronized (this) {
            this.completeMoveApplicationWithLock(doMove);
         }
      }
   }

   private final void completeMoveApplicationWithLock(boolean doMove) {
      if (doMove) {
         int newIndex = Arrays.getIndex(this._movingList, this._movingApplication);
         this._helper._hierarchyManager.moveApplication(this._movingApplication, Arrays.getIndex(this._visibleApplications, this._movingApplication), newIndex);
         this._visibleApplications = this._movingList;
         this._listField.setSelectedIndex(newIndex);
      } else {
         int oldIndex = Arrays.getIndex(this._visibleApplications, this._movingApplication);
         this._listField.invalidate(oldIndex);
         this._listField.invalidate(this._listField.getSelectedIndex());
         this._listField.setSelectedIndex(oldIndex);
      }

      this._movingList = null;
      this._movingApplication = null;
   }

   @Override
   public final void updateMovingApplicationPosition() {
      Application app = this.getUiApp();
      if (app != null) {
         synchronized (app.getAppEventLock()) {
            this.updateMovingApplicationPositionWithLock();
         }
      } else {
         synchronized (this) {
            this.updateMovingApplicationPositionWithLock();
         }
      }
   }

   private final void updateMovingApplicationPositionWithLock() {
      int previousIndex = Arrays.getIndex(this._movingList, this._movingApplication);
      int currentIndex = this._listField.getSelectedIndex();
      if (previousIndex != currentIndex) {
         Arrays.removeAt(this._movingList, previousIndex);
         Arrays.insertAt(this._movingList, this._movingApplication, currentIndex);
         this._listField.invalidate(previousIndex);
         this._listField.invalidate(currentIndex);
         this._listField.setSelectedIndex(currentIndex);
      }
   }

   @Override
   public final void setBackgroundBitmap(Bitmap bitmap) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   @Override
   public final ApplicationEntry getApplicationAt(int index) {
      if (index < 0) {
         return null;
      }

      if (this._movingList != null) {
         if (index < this._movingList.length) {
            return this._movingList[index];
         }
      } else if (index < this._visibleApplications.length) {
         return this._visibleApplications[index];
      }

      return null;
   }

   @Override
   public final void setHotKeysDisabled(boolean disabled) {
      if (this._hotKeysDisabled != disabled) {
         this._hotKeysDisabled = disabled;
         this._longestTextWidthFocus = -1;
      }
   }

   @Override
   public final synchronized void refreshApplication(ApplicationEntry application) {
      int index = Arrays.getIndex(this._visibleApplications, application);
      if (index != -1) {
         this._listField.invalidate(index);
      }
   }
}
