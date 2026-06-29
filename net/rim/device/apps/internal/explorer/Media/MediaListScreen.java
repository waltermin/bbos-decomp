package net.rim.device.apps.internal.explorer.Media;

import net.rim.device.api.collection.util.KeywordFilterList;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.CollectionListField;
import net.rim.device.api.ui.component.KeywordFilterCollectionListField;
import net.rim.device.api.ui.component.KeywordFilteredListFinder;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.component.ListFieldCallback;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.apps.api.framework.model.ActionProvider;
import net.rim.device.apps.api.framework.model.PaintProvider;
import net.rim.device.apps.api.ui.AppsMainScreen;
import net.rim.device.apps.api.ui.SystemEnabledMenu;
import net.rim.device.apps.internal.explorer.Media.verbs.MediaVerb;
import net.rim.device.apps.internal.explorer.MediaLibrary.ContextInfo;
import net.rim.device.apps.internal.explorer.MediaLibrary.MediaInfo;
import net.rim.device.apps.internal.explorer.MediaLibrary.MediaInfoCollection;
import net.rim.device.apps.internal.explorer.file.SelectionListener;
import net.rim.device.apps.internal.explorer.file.menu.OptionsMenuItem;
import net.rim.device.apps.internal.explorer.file.menu.ReceiveBluetooth;
import net.rim.device.apps.internal.explorer.file.resource.ExplorerResources;

class MediaListScreen extends AppsMainScreen implements ListFieldCallback, ActionProvider {
   protected MediaInfoCollection _collection;
   protected KeywordFilterList _keywordList;
   protected KeywordFilteredListFinder _finder;
   protected ContextInfo _contextInfo;
   private KeywordFilterCollectionListField _list;
   private int _padding;
   protected boolean _allAdded;
   private String _allString = ExplorerResources.getString(147);

   protected MediaInfoCollection getCollection() {
      throw null;
   }

   protected void buildSubset() {
      throw null;
   }

   protected String getTitle() {
      throw null;
   }

   protected void checkToAddAll(KeywordFilterCollectionListField _1) {
      throw null;
   }

   protected void checkToAddNew(KeywordFilterCollectionListField _1) {
      throw null;
   }

   protected boolean isTwoLine() {
      return false;
   }

   protected SelectionListener getSelectionListener() {
      Object item = this._contextInfo.getItem();
      return !(item instanceof SelectionListener) ? null : (SelectionListener)item;
   }

   protected String getEmptyString() {
      throw null;
   }

   protected final boolean isExternal() {
      return this._contextInfo.isExternal();
   }

   protected void selectAll() {
      int size = this._keywordList.size();
      Object[] items = new Object[size];

      for (int i = 0; i < size; i++) {
         items[i] = this._keywordList.getAt(i);
      }

      SelectionListener listener = this.getSelectionListener();
      if (listener != null) {
         listener.selected(items);
      }

      this.close();
   }

   protected void invoke() {
      throw null;
   }

   protected int getSelectedIndex() {
      Field field = this.getLeafFieldWithFocus();
      int index = -1;
      if (field instanceof KeywordFilterCollectionListField) {
         KeywordFilterCollectionListField kList = (KeywordFilterCollectionListField)field;
         index = kList.getSelectedIndex();
      }

      return index;
   }

   protected Object getSelectedObject() {
      Field field = this.getLeafFieldWithFocus();
      if (!(field instanceof KeywordFilterCollectionListField)) {
         return null;
      }

      KeywordFilterCollectionListField kList = (KeywordFilterCollectionListField)field;
      return kList.getSelectedElement();
   }

   protected boolean canAddToPlaylist() {
      return true;
   }

   @Override
   public boolean perform(long actionId, Object context) {
      if (actionId == 6099736323056465049L || actionId == 8725737741808251806L) {
         this.invoke();
         return true;
      } else if (actionId == 1518675114010876469L) {
         this.selectAll();
         return true;
      } else {
         return false;
      }
   }

   @Override
   public int indexOfList(ListField listField, String prefix, int start) {
      return -1;
   }

   @Override
   public int getPreferredWidth(ListField listField) {
      return Display.getWidth();
   }

   @Override
   public Object get(ListField listField, int index) {
      Object item = null;
      if (!(listField instanceof CollectionListField)) {
         return this._collection.getAt(index);
      }

      CollectionListField clf = (CollectionListField)listField;
      return clf.getElementAt(index);
   }

   @Override
   public void drawListRow(ListField listField, Graphics graphics, int index, int y, int width) {
      if (this.isTwoLine()) {
         int graphicsColor = graphics.getColor();
         int graphicsAlpha = graphics.getGlobalAlpha();
         int drawColor = 10066329;
         graphics.setColor(drawColor);
         graphics.setGlobalAlpha(65);
         graphics.drawLine(5, y + listField.getRowHeight() - 1, width - 5, y + listField.getRowHeight() - 1);
         graphics.setColor(graphicsColor);
         graphics.setGlobalAlpha(graphicsAlpha);
      }

      if (index == 0 && this._allAdded) {
         int yLoc = listField.getRowHeight() - graphics.getFont().getHeight() >> 1;
         graphics.drawText(this._allString, 5, y + yLoc);
      } else {
         CollectionListField clf = null;
         Object item = null;
         if (!(listField instanceof CollectionListField)) {
            item = this._collection.getAt(index);
         } else {
            clf = (CollectionListField)listField;
            item = clf.getElementAt(index);
         }

         if (!(item instanceof PaintProvider)) {
            if (clf != null && index == clf.getExtraRowCount()) {
               graphics.drawText(this.getEmptyString(), 0, y + (this._padding >> 1), 4, width);
            }
         } else {
            PaintProvider painter = (PaintProvider)item;
            painter.paint(graphics, 5, y + (this._padding >> 1), width, listField.getRowHeight() - this._padding, this._contextInfo);
         }
      }
   }

   @Override
   protected boolean keyChar(char c, int status, int time) {
      if (c == 27) {
         String text = this._finder.getText();
         if (text != null && text.length() > 0) {
            this._finder.resetSearch();
            this._finder.setText(null);
            return true;
         } else {
            UiApplication.getUiApplication().popScreen(this);
            return true;
         }
      } else if (c == '\n') {
         this.invoke();
         return true;
      } else {
         return super.keyChar(c, status, time);
      }
   }

   @Override
   protected boolean navigationClick(int status, int time) {
      if (super.navigationClick(status, time)) {
         return true;
      }

      if ((status & 1073741824) != 0) {
         return false;
      }

      this.invoke();
      return true;
   }

   @Override
   protected void makeMenu(SystemEnabledMenu menu, int instance) {
      MediaVerb openSelect = null;
      MediaInfo selection = (MediaInfo)this.getSelectedMedia();
      if (selection != null) {
         if (this.getSelectionListener() == null) {
            openSelect = new MediaVerb(6099736323056465049L, this, 569345, null, 157);
            menu.add(new MediaVerb(3712387911241450002L, this, 569346, ExplorerResources.getResourceBundleFamily(), 92));
            if (this.canAddToPlaylist()) {
               menu.add(new MediaVerb(7051267296949023454L, this, 569347, null, 156));
            }
         } else {
            openSelect = new MediaVerb(8725737741808251806L, this, 569345, null, 40);
            String text = this._finder.getText();
            if (text != null && text.length() > 0 && this._contextInfo.isSelectAll() && this._keywordList.size() > 1) {
               menu.add(new MediaVerb(1518675114010876469L, this, 569346, null, 181));
            }
         }

         menu.add(openSelect);
         menu.setDefault(openSelect);
      }

      if (MediaUtilities.checkBluetoothPolicy()) {
         menu.add(new ReceiveBluetooth());
      }

      menu.add(new OptionsMenuItem());
      MediaUtilities.addDownloadTunesMenuItems(menu);
   }

   @Override
   public void doLayout() {
      super.doLayout();
      Font font = this._list.getFont();
      int fontHeightInPxs = font.getHeight();
      if (this.isTwoLine()) {
         int fontHeightInPts = font.getHeight(2);
         Font newFont = font.derive(0, fontHeightInPts - 1, 2);
         this._padding = 4;
         fontHeightInPxs += newFont.getHeight() + 1;
      } else {
         this._padding = 6;
      }

      this._list.setRowHeight(fontHeightInPxs + this._padding);
   }

   @Override
   protected void onUiEngineAttached(boolean attached) {
      super.onUiEngineAttached(attached);
      if (!attached) {
         this._collection.addCriteria(this._keywordList, null);
      }
   }

   MediaListScreen(Object context) {
      super(562949953421312L);
      this.setTag(ThemeUtilities.SCREEN_TAG);
      Manager manager = this.getMainManager();
      if (manager != null) {
         manager.setTag(ThemeUtilities.SCREEN_TAG);
      }

      this._collection = this.getCollection();
      this._contextInfo = (ContextInfo)context;
      this.initialize();
      this.setHelp(32247);
   }

   private void initialize() {
      this.buildSubset();
      Field banner = ThemeUtilities.getTitleField(this.getTitle());
      VerticalFieldManager listManager = new VerticalFieldManager(281474976710656L);
      if (this._keywordList == null) {
         if (this.isExternal()) {
            this._keywordList = this._collection.getKeywordFilterListInstance(null);
         } else {
            this._keywordList = this._collection.getKeywordFilterList();
         }
      }

      this._list = new KeywordFilterCollectionListField(this._keywordList, this);
      this._list.setTag(ThemeUtilities.LIST_TAG);
      this.checkToAddAll(this._list);
      this.checkToAddNew(this._list);
      if (this._contextInfo.getType() == 16) {
         this.checkToAddNew(this._list);
      }

      listManager.add(this._list);
      ListScrollbarManager scrollManager = new ListScrollbarManager(listManager);
      String search = ExplorerResources.getString(145);
      this._finder = new KeywordFilteredListFinder(search, search, true);
      this._finder.setTag(ThemeUtilities.FIND_TAG);
      this._finder.linkToField(this._list);
      this.add(banner);
      this.add(this._finder);
      this.add(scrollManager);
   }

   private Object getSelectedMedia() {
      Field field = this.getLeafFieldWithFocus();
      if (!(field instanceof KeywordFilterCollectionListField)) {
         return null;
      }

      KeywordFilterCollectionListField kList = (KeywordFilterCollectionListField)field;
      return kList.getSelectedElement();
   }
}
