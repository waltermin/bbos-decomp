package net.rim.device.apps.internal.explorer.Media.dialogs;

import net.rim.device.api.collection.util.KeywordFilterList;
import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.CollectionListField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.KeywordFilterCollectionListField;
import net.rim.device.api.ui.component.KeywordFilteredListFinder;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.component.ListFieldCallback;
import net.rim.device.api.ui.container.PopupScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.apps.api.framework.model.PaintProvider;
import net.rim.device.apps.api.ui.PopupStatus;
import net.rim.device.apps.internal.explorer.Media.ListScrollbarManager;
import net.rim.device.apps.internal.explorer.MediaLibrary.FilterConstants;
import net.rim.device.apps.internal.explorer.MediaLibrary.MediaInfo;
import net.rim.device.apps.internal.explorer.MediaLibrary.MediaInfoCollection;
import net.rim.device.apps.internal.explorer.MediaLibrary.MediaLibrary;
import net.rim.device.apps.internal.explorer.MediaLibrary.PlaylistItem;
import net.rim.device.apps.internal.explorer.file.resource.ExplorerResources;

public final class PlaylistSelectionPopup extends PopupScreen implements ListFieldCallback {
   private MediaInfoCollection _collection;
   private KeywordFilteredListFinder _finder;
   private MediaInfo[] _media;
   private Object _contextInfo;

   public final boolean isGenre() {
      return this._contextInfo instanceof Object;
   }

   public final boolean isAlbum() {
      return this._contextInfo instanceof Object;
   }

   public final boolean isArtist() {
      return this._contextInfo instanceof Object;
   }

   @Override
   public final int indexOfList(ListField listField, String prefix, int start) {
      return -1;
   }

   @Override
   public final int getPreferredWidth(ListField listField) {
      return Display.getWidth();
   }

   @Override
   public final Object get(ListField listField, int index) {
      Object item = null;
      if (!(listField instanceof Object)) {
         return this._collection.getAt(index);
      }

      CollectionListField clf = (CollectionListField)listField;
      return clf.getElementAt(index);
   }

   @Override
   public final void drawListRow(ListField listField, Graphics graphics, int index, int y, int width) {
      if (index == 0) {
         graphics.drawText(ExplorerResources.getString(182), 0, y);
      } else {
         CollectionListField clf = null;
         Object item = null;
         if (!(listField instanceof Object)) {
            item = this._collection.getAt(index);
         } else {
            clf = (CollectionListField)listField;
            item = clf.getElementAt(index);
         }

         if (!(item instanceof Object)) {
            if (clf != null && index == clf.getExtraRowCount()) {
               graphics.drawText(ExplorerResources.getString(161), 0, y, 4, width);
            }
         } else {
            PaintProvider painter = (PaintProvider)item;
            painter.paint(graphics, 0, y, width, listField.getRowHeight(), null);
         }
      }
   }

   private final PlaylistItem getSelectedPlaylist() {
      Field field = this.getLeafFieldWithFocus();
      if (!(field instanceof Object)) {
         return null;
      }

      KeywordFilterCollectionListField kList = (KeywordFilterCollectionListField)field;
      Object element = kList.getSelectedElement();
      return (PlaylistItem)element;
   }

   @Override
   protected final boolean keyChar(char c, int status, int time) {
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
   protected final boolean navigationClick(int status, int time) {
      if (super.navigationClick(status, time)) {
         return true;
      }

      this.invoke();
      return true;
   }

   private final int getSelectedIndex() {
      Field field = this.getLeafFieldWithFocus();
      int index = -1;
      if (field instanceof Object) {
         KeywordFilterCollectionListField kList = (KeywordFilterCollectionListField)field;
         index = kList.getSelectedIndex();
      }

      return index;
   }

   private final void initialize() {
      String title = ExplorerResources.getString(183);
      LabelField label = (LabelField)(new Object(title));
      VerticalFieldManager listManager = (VerticalFieldManager)(new Object(281474976710656L));
      KeywordFilterList filter = this._collection.getKeywordFilterListInstance(null);
      String[] keywords = new Object[]{FilterConstants.PLAYLIST_PREFIX};
      this._collection.addCriteria(filter, keywords);
      KeywordFilterCollectionListField list = (KeywordFilterCollectionListField)(new Object(filter, this));
      list.setExtraRowCount(1);
      listManager.add(list);
      ListScrollbarManager scrollManager = new ListScrollbarManager(listManager);
      String search = ExplorerResources.getString(145);
      this._finder = (KeywordFilteredListFinder)(new Object(search, search, false));
      this._finder.linkToField(list);
      this.add(label);
      this.add((Field)(new Object()));
      this.add(this._finder);
      this.add((Field)(new Object()));
      this.add(scrollManager);
   }

   public PlaylistSelectionPopup(MediaInfo[] media, Object context) {
      super((Manager)(new Object(562949953421312L)));
      this._media = media;
      this._collection = MediaLibrary.getInstance().getPlaylistCollection();
      this._contextInfo = context;
      this.initialize();
   }

   private final String getType() {
      if (this.isArtist()) {
         return ExplorerResources.getString(195);
      } else {
         return this.isAlbum() ? ExplorerResources.getString(196) : ExplorerResources.getString(197);
      }
   }

   private final void invoke() {
      Field field = this.getLeafFieldWithFocus();
      boolean createPlaylist = true;
      if (field instanceof Object) {
         PlaylistItem playlist = this.getSelectedPlaylist();
         if (this.isArtist() || this.isAlbum() || this.isGenre()) {
            String formattedText = MessageFormat.format(ExplorerResources.getString(201), new Object[]{this.getType()});
            Dialog dialog = (Dialog)(new Object(4, formattedText, -1, null, 0));
            createPlaylist = dialog.doModal() == 0;
         }

         if (createPlaylist) {
            if (playlist == null && this.getSelectedIndex() == 0) {
               NewPlaylistPopup newList = new NewPlaylistPopup(0);
               Object list = newList.doModal();
               if (!(list instanceof Object)) {
                  return;
               }

               playlist = (PlaylistItem)list;
            }

            if (playlist != null) {
               for (int i = 0; i < this._media.length; i++) {
                  playlist.addMedia(this._media[i], false);
               }

               label72:
               try {
                  playlist.save();
               } finally {
                  break label72;
               }

               this.close();
               PopupStatus.show(ExplorerResources.getString(184), 700);
            }
         }
      }
   }
}
