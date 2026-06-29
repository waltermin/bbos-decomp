package net.rim.device.apps.internal.explorer.Media;

import javax.microedition.io.InputConnection;
import net.rim.device.api.collection.util.KeywordFilterList;
import net.rim.device.api.collection.util.SortedReadableList;
import net.rim.device.api.io.MIMETypeAssociations;
import net.rim.device.api.io.file.FileIOException;
import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.api.system.Display;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.BitmapField;
import net.rim.device.api.ui.component.CollectionListField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.KeywordFilterCollectionListField;
import net.rim.device.api.ui.component.KeywordFilteredListFinder;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.component.ListFieldCallback;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.Comparator;
import net.rim.device.apps.api.framework.file.AliasFileEntry;
import net.rim.device.apps.api.framework.file.ExplorerRegistry;
import net.rim.device.apps.api.framework.file.FileSelectionFilter;
import net.rim.device.apps.api.framework.model.ActionProvider;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.PaintProvider;
import net.rim.device.apps.api.framework.model.VerbProvider;
import net.rim.device.apps.api.framework.registration.MIMEContentVerbRepository;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.ui.AppsMainScreen;
import net.rim.device.apps.api.ui.PopupStatus;
import net.rim.device.apps.api.ui.SystemEnabledMenu;
import net.rim.device.apps.internal.browser.util.ImageConverter;
import net.rim.device.apps.internal.explorer.Media.Fields.AlbumTitleManager;
import net.rim.device.apps.internal.explorer.Media.dialogs.PlaylistSelectionPopup;
import net.rim.device.apps.internal.explorer.Media.verbs.MediaVerb;
import net.rim.device.apps.internal.explorer.Media.verbs.SelectMediaVerb;
import net.rim.device.apps.internal.explorer.MediaLibrary.Album;
import net.rim.device.apps.internal.explorer.MediaLibrary.Artist;
import net.rim.device.apps.internal.explorer.MediaLibrary.ContextInfo;
import net.rim.device.apps.internal.explorer.MediaLibrary.FilterConstants;
import net.rim.device.apps.internal.explorer.MediaLibrary.Genre;
import net.rim.device.apps.internal.explorer.MediaLibrary.MediaInfo;
import net.rim.device.apps.internal.explorer.MediaLibrary.MediaInfoCollection;
import net.rim.device.apps.internal.explorer.MediaLibrary.MediaLibrary;
import net.rim.device.apps.internal.explorer.MediaLibrary.PlaylistItem;
import net.rim.device.apps.internal.explorer.MediaLibrary.Playlistable;
import net.rim.device.apps.internal.explorer.MediaLibrary.Track;
import net.rim.device.apps.internal.explorer.MediaLibrary.Video;
import net.rim.device.apps.internal.explorer.MediaLibrary.util.TrackNumberComparator;
import net.rim.device.apps.internal.explorer.file.AliasFileItemField;
import net.rim.device.apps.internal.explorer.file.ExploreCallback;
import net.rim.device.apps.internal.explorer.file.FileExplorerApp;
import net.rim.device.apps.internal.explorer.file.FileItemField;
import net.rim.device.apps.internal.explorer.file.FolderList;
import net.rim.device.apps.internal.explorer.file.SelectionListener;
import net.rim.device.apps.internal.explorer.file.menu.OptionsMenuItem;
import net.rim.device.apps.internal.explorer.file.menu.PropertiesMenuItem;
import net.rim.device.apps.internal.explorer.file.menu.ReceiveBluetooth;
import net.rim.device.apps.internal.explorer.file.menu.SendBluetooth;
import net.rim.device.apps.internal.explorer.file.resource.ExplorerResources;
import net.rim.device.apps.internal.explorer.file.util.FileItemComparator;
import net.rim.device.apps.internal.explorer.file.verbs.RenameFileVerb;
import net.rim.device.internal.i18n.CommonResource;
import net.rim.device.internal.io.file.FileUtilities;
import net.rim.device.internal.media.M3UPlaylist;
import net.rim.device.internal.media.MediaPlayerState;

public final class TrackListScreen extends AppsMainScreen implements ListFieldCallback, ActionProvider, ExploreCallback {
   private ContextInfo _contextInfo;
   private MediaInfoCollection _collection;
   private KeywordFilterList _keywordList;
   private MediaInfo[] _tracks;
   private Album _album;
   private KeywordFilteredListFinder _finder;
   private int _height = -1;
   private PlaylistItem _playlist = null;
   private boolean _dirty;
   private int _padding;
   private TrackListScreen$MyListField _list;
   private TrackListScreen$ExtractImageRunnable _imageExtractor;
   private ListScrollbarManager _scrollManager;
   private boolean _haveRecordButton = false;
   private AliasFileEntry _videoCameraAlias = null;
   private int _movingCurIndex;
   private int _movedItemIndex;
   private static final String UTF8 = "UTF-8";
   private static final int DESIRED_ALBUM_WIDTH = 90;
   private static final int DESIRED_ALBUM_HEIGHT = 90;
   private static final int ALBUM_CHAR_LIMIT = 50;
   private static EncodedImage NO_ART = null;

   protected final void setText(String text) {
      if (this._finder != null && text != null) {
         this._finder.setSearchPattern(text);
      }
   }

   final void setScreenDirty() {
      this._dirty = true;
   }

   public final boolean isPlaylist() {
      return (this._contextInfo.getType() & 16) != 0;
   }

   public final boolean isVoicenote() {
      return (this._contextInfo.getType() & 256) != 0;
   }

   public final boolean isVideo() {
      return (this._contextInfo.getType() & 32) != 0;
   }

   public final boolean isRingtone() {
      return (this._contextInfo.getType() & 64) != 0;
   }

   final void cleanUp() {
      this._collection.addCriteria(this._keywordList, null);
      this.cancelPendingPlaylistMove();
      if (this._dirty && this._playlist != null) {
         try {
            this._playlist.save();
         } finally {
            return;
         }
      }
   }

   @Override
   public final boolean perform(long actionId, Object context) {
      if (actionId == 3712387911241450002L || actionId == 8725737741808251806L) {
         this.invoke(false);
         return true;
      }

      if (actionId == 1518675114010876469L) {
         this.invoke(true);
         return true;
      }

      if (actionId == 6068253483645874830L) {
         this.delete();
         return true;
      }

      if (actionId == 6021295727530465908L) {
         SelectMediaVerb selectMedia = new SelectMediaVerb(5, 1);
         selectMedia.setExternal(false);
         Object[] selections = selectMedia.invokeSelect(null);
         if (selections != null) {
            int selectionsSize = selections.length;

            for (int i = 0; i < selectionsSize; i++) {
               this._playlist.addMedia((MediaInfo)selections[i], false);
            }

            this._dirty = true;
            PopupStatus.show(ExplorerResources.getString(184), 700);
            return false;
         }
      } else {
         if (actionId == 5933455911148432190L) {
            MediaInfo[] media = new Object[]{this.getSelectedMedia()};
            this.addToPlaylist(media, context);
            return true;
         }

         if (actionId == 6640987057326686423L || actionId == 7051267296949023454L) {
            int size = this._keywordList.size();
            if (size > 0) {
               MediaInfo[] media = new Object[size];

               for (int i = 0; i < size; i++) {
                  media[i] = (MediaInfo)this._keywordList.getAt(i);
               }

               this.addToPlaylist(media, context);
            }

            return true;
         }

         if (actionId == 5803508244060051872L) {
            synchronized (this) {
               this.notifyAll();
               return true;
            }
         }

         if (actionId == -6042251527357905929L) {
            this.invoke(true);
            return true;
         }

         if (actionId == 5204271450404318686L) {
            this._movingCurIndex = this._movedItemIndex = this._list.getSelectedIndex();
            this._list.setMoveMode(true);
            return true;
         }

         if (actionId == 3121618378580614779L) {
            this.record();
            return true;
         }
      }

      return false;
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
      CollectionListField clf = null;
      Object item = null;
      if (this._list.getMoveMode()) {
         index = this.translateIndex(index);
      }

      if (!(listField instanceof Object)) {
         item = this._collection.getAt(index);
      } else {
         clf = (CollectionListField)listField;
         item = clf.getElementAt(index);
      }

      if (this._height > 0) {
         int graphicsColor = graphics.getColor();
         int graphicsAlpha = graphics.getGlobalAlpha();
         int drawColor = 10066329;
         graphics.setColor(drawColor);
         graphics.setGlobalAlpha(65);
         graphics.drawLine(17, y + listField.getRowHeight() - 1, width - 17, y + listField.getRowHeight() - 1);
         graphics.setColor(graphicsColor);
         graphics.setGlobalAlpha(graphicsAlpha);
      }

      if (!(item instanceof Object)) {
         if (clf != null && index == clf.getExtraRowCount()) {
            graphics.drawText(this.getEmptyString(), 0, y + (this._padding >> 1), 4, width);
         }
      } else {
         PaintProvider painter = (PaintProvider)item;
         painter.paint(graphics, 0, y + (this._padding >> 1), width, listField.getRowHeight() - this._padding, this._contextInfo);
      }
   }

   @Override
   public final void pathSet(Object path) {
   }

   @Override
   public final void statusOn() {
   }

   @Override
   public final void statusOff() {
   }

   @Override
   public final void currentItemChanged(Field field, FileItemField item) {
   }

   @Override
   protected final void onVisibilityChange(boolean visible) {
      super.onVisibilityChange(visible);
      if (!visible) {
         this.cancelPendingPlaylistMove();
      }
   }

   @Override
   protected final void onObscured() {
      super.onObscured();
      this.cancelPendingPlaylistMove();
   }

   private final void initialize() {
      this.buildSubset();
      Field banner = null;
      banner = ThemeUtilities.getTitleField(this.getTitle());
      VerticalFieldManager listManager = (VerticalFieldManager)(new Object(281474976710656L));
      if (this._keywordList == null) {
         if (this.isExternal()) {
            this._keywordList = this._collection.getKeywordFilterListInstance(null);
         } else {
            this._keywordList = this._collection.getKeywordFilterList();
         }
      }

      this._list = new TrackListScreen$MyListField(this._keywordList, this);
      this._list.setTag(ThemeUtilities.LIST_TAG);
      listManager.add(this._list);
      this._scrollManager = new ListScrollbarManager(listManager);
      String search = ExplorerResources.getString(145);
      this._finder = (KeywordFilteredListFinder)(new Object(search, search, true));
      this._finder.setTag(ThemeUtilities.FIND_TAG);
      this._finder.linkToField(this._list);
      Manager titleManager = this.drawAlbumArt();
      this.add(banner);
      if (titleManager != null) {
         this.add(titleManager);
         this.add((Field)(new Object()));
      }

      if (this.isVoicenote() || this.isVideo()) {
         FolderList shortcuts = new FolderList(this, 2, 1, false);
         int count = this.setFolderListView(shortcuts);
         if (count > 0) {
            shortcuts.setTag(ThemeUtilities.LIST_TAG);
            this.add(shortcuts);
            this.add((Field)(new Object()));
            this._haveRecordButton = true;
         }
      }

      this.add(this._finder);
      this.add(this._scrollManager);
      this._scrollManager.setFocus();
   }

   private final void cancelPendingPlaylistMove() {
      if (this._list.getMoveMode()) {
         this._list.setMoveMode(false);
         this._list.setSelectedIndex(this._movedItemIndex);
      }
   }

   private final boolean shouldAddRecordMenuItem() {
      if (!this.isVoicenote() && !this.isVideo()) {
         return false;
      }

      if (this.isVoicenote() && ITPolicy.getBoolean(21, 10, false)) {
         return false;
      }

      if (!this.isVideo()
         || this._videoCameraAlias != null && this._videoCameraAlias.isActiveInDirectory(this.getDefaultPath(), (FileSelectionFilter)(new Object()))) {
         if (!this._haveRecordButton) {
            return false;
         }

         try {
            if (this.isVoicenote()) {
               Class.forName("net.rim.device.apps.internal.voicenotesrecorder.VoiceNotesRecorderMain");
            }

            if (this.isVideo()) {
               Class.forName("net.rim.device.apps.internal.videorecorder.VideoRecorderMain");
               return true;
            } else {
               return true;
            }
         } finally {
            ;
         }
      } else {
         return false;
      }
   }

   @Override
   protected final void makeMenu(SystemEnabledMenu menu, int instance) {
      Verb[] verbs = new Object[0];
      MediaInfo selection = (MediaInfo)this.getSelectedMedia();
      ContextObject context = (ContextObject)(new Object());
      MediaVerb play = null;
      boolean isSelecting = this.getSelectionListener() != null;
      String text = this._finder.getText();
      boolean searchFull = text != null && text.length() > 0;
      if (selection != null) {
         String location = selection.getLocation();
         context.put(2765042845091913199L, location);
         ContextObject.put(menu.getContext(), 2765042845091913199L, location);
         if (MediaUtilities.canSend(location) && !isSelecting) {
            if (MediaUtilities.checkBluetoothPolicy()) {
               menu.add(new SendBluetooth(location));
            }

            String mimeType = MIMETypeAssociations.getMIMEType(location);
            MenuItem[] menuItems = MIMEContentVerbRepository.getMenuItems(mimeType, location, null);
            if (menuItems != null) {
               for (int i = 0; i < menuItems.length; i++) {
                  menu.add(menuItems[i]);
               }
            }
         }

         menu.add(new PropertiesMenuItem(selection));
         if (isSelecting) {
            play = new MediaVerb(8725737741808251806L, this, 569345, null, 40);
            if (text != null && text.length() > 0 && this._keywordList.size() > 1) {
               menu.add(new MediaVerb(1518675114010876469L, this, 569346, null, 181));
            }
         } else {
            play = new MediaVerb(3712387911241450002L, this, 569345, null, 92);
         }

         menu.add(play);
         menu.setDefault(play);
         if (this.canDelete(selection) && !isSelecting) {
            menu.add(new MediaVerb(6068253483645874830L, this, 569349, CommonResource.getBundle(), 17));
         }

         if (!this.isVideo() && !this.isRingtone() && !this.isPlaylist() && !this.isVoicenote() && !isSelecting) {
            menu.add(new MediaVerb(5933455911148432190L, this, 569347, null, 156));
            if (searchFull) {
               menu.add(new MediaVerb(6640987057326686423L, this, 569348, null, 189));
            }
         }

         if ((this.isVoicenote() || this.isVideo()) && !isSelecting) {
            menu.add(new RenameFileVerb(null, null));
         }

         if (searchFull && this._contextInfo.getType() == 1 && !this.isRingtone() && !this.isVideo() && !this.isVoicenote() && !isSelecting) {
            menu.add(new MediaVerb(-6042251527357905929L, this, 569346, null, 179));
         }

         if (!searchFull && this.isPlaylist() && this._collection.size() > 1 && !isSelecting) {
            menu.add(new MediaVerb(5204271450404318686L, this, 569346, null, 37));
         }
      }

      if (this.shouldAddRecordMenuItem() && !isSelecting) {
         menu.add(new MediaVerb(3121618378580614779L, this, 569353, null, 178));
      }

      if (selection instanceof Object && !isSelecting) {
         VerbProvider verbProvider = (VerbProvider)selection;
         verbProvider.getVerbs(context, verbs);

         for (int i = verbs.length - 1; i >= 0; i--) {
            menu.add(verbs[i]);
         }
      }

      if (MediaUtilities.checkBluetoothPolicy() && !isSelecting) {
         menu.add(new ReceiveBluetooth());
      }

      if (!isSelecting) {
         menu.add(new OptionsMenuItem());
      }

      if (this.isRingtone()) {
         MediaUtilities.addDownloadTunesMenuItems(menu);
      }

      if (this.isPlaylist()) {
         MediaVerb addSongs = new MediaVerb(6021295727530465908L, this, 484864, null, 200);
         menu.add(addSongs);
         if (this._keywordList.size() <= 0) {
            menu.setDefault(addSongs);
         }
      }
   }

   @Override
   protected final boolean keyChar(char c, int status, int time) {
      if (c == 27) {
         String text = this._finder.getText();
         if (text != null && text.length() > 0) {
            this._finder.resetSearch();
            this._finder.setText(null);
            return true;
         } else if (this._list.getMoveMode()) {
            this._list.setMoveMode(false);
            this._list.setSelectedIndex(this._movedItemIndex);
            return true;
         } else {
            UiApplication.getUiApplication().popScreen(this);
            return true;
         }
      } else if (c == '\n') {
         if (this._list.getMoveMode()) {
            this.completeMove();
            return true;
         } else {
            this.invoke(false);
            return true;
         }
      } else {
         if (c == 127 || c == '\b') {
            String text = this._finder.getText();
            if (text == null || text.length() <= 0) {
               this.delete();
               return true;
            }
         }

         if (this.getFieldWithFocus() != this._scrollManager) {
            this._scrollManager.setFocus();
         }

         return super.keyChar(c, status, time);
      }
   }

   @Override
   protected final boolean navigationClick(int status, int time) {
      if (super.navigationClick(status, time)) {
         return true;
      } else if ((status & 1073741824) != 0) {
         return false;
      } else if (this._list.getMoveMode()) {
         this.completeMove();
         return true;
      } else {
         this.invoke(false);
         return true;
      }
   }

   @Override
   protected final boolean navigationMovement(int dx, int dy, int status, int time) {
      String text = this._finder.getText();
      boolean noText = text == null || text.length() <= 0;
      if ((status & 1) != 0 && this.getLeafFieldWithFocus() == this._list && !this._list.getMoveMode() && noText && this.isPlaylist()) {
         this._list.setMoveMode(true);
         this._movingCurIndex = this._movedItemIndex = this._list.getSelectedIndex();
      }

      if (this._list.getMoveMode()) {
         int selectedIndex = this._list.getSelectedIndex();
         selectedIndex += dy > 0 ? 1 : -1;
         if (selectedIndex >= 0 && selectedIndex < this._list.getSize()) {
            this._movingCurIndex = selectedIndex;
            this._list.setSelectedIndex(this._movingCurIndex);
         }

         return true;
      } else {
         return super.navigationMovement(dx, dy, status, time);
      }
   }

   @Override
   public final void doLayout() {
      super.doLayout();
      Font font = this._list.getFont();
      int fontHeightInPts = font.getHeight(2);
      int fontHeightInPxs = font.getHeight();
      if (this._height > 0) {
         Font newFont = font.derive(0, fontHeightInPts - 1, 2);
         this._padding = 4;
         fontHeightInPxs += newFont.getHeight() + 1;
      } else {
         this._padding = 6;
      }

      this._list.setRowHeight(fontHeightInPxs + this._padding);
   }

   private final void addToPlaylist(MediaInfo[] media, Object context) {
      if (media != null) {
         PlaylistSelectionPopup popup = new PlaylistSelectionPopup(media, context);
         UiApplication.getUiApplication().pushModalScreen(popup);
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final void delete() {
      Field field = this.getLeafFieldWithFocus();
      if (field instanceof Object) {
         MediaInfo media = (MediaInfo)this.getSelectedMedia();
         if (media != null) {
            boolean deleteFile = false;
            if (this._playlist != null) {
               Playlistable item = null;
               if (media instanceof Object) {
                  item = (Playlistable)media;
               }

               if (item != null) {
                  Dialog dialog = (Dialog)(new Object(4, ExplorerResources.getString(194), -1, null, 0));
                  deleteFile = dialog.doModal() == 0;
                  if (deleteFile) {
                     this._playlist.removeMedia(item, false);
                     this._dirty = true;
                     return;
                  }
               }
            } else {
               String location = media.getLocation();
               if (!MediaUtilities.canDelete(location)) {
                  return;
               }

               deleteFile = Dialog.ask(2) == 3;
               if (!deleteFile) {
                  return;
               }

               try {
                  FileUtilities.delete(location, true);
                  return;
               } catch (Throwable var8) {
                  String errorString = null;
                  if (ioe instanceof Object) {
                     errorString = ((FileIOException)ioe).getMessage();
                  }

                  if (errorString == null) {
                     errorString = ExplorerResources.getString(170);
                  }

                  Dialog.alert(errorString);
                  return;
               }
            }
         }
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final void invoke(boolean results) {
      Field field = this.getLeafFieldWithFocus();
      if (field instanceof FolderList) {
         FolderList folderList = (FolderList)field;
         FileItemField item = folderList.getSelectedItem();
         if (item instanceof AliasFileItemField) {
            AliasFileItemField afif = (AliasFileItemField)item;
            afif.invoke(null);
            return;
         }
      }

      if (field instanceof Object) {
         int index = this.getSelectedIndex();
         if (index >= 0) {
            Object selectedObject = this.getSelectedMedia();
            String location = null;
            if (selectedObject instanceof Object) {
               location = ((MediaInfo)selectedObject).getLocation();
               SelectionListener listener = this.getSelectionListener();
               if (listener != null) {
                  boolean selectLocation = false;
                  if (listener instanceof SelectMediaVerb) {
                     SelectMediaVerb selectionListener = (SelectMediaVerb)listener;
                     selectLocation = selectionListener.isFlagSet(4);
                  }

                  if (results) {
                     int size = this._keywordList.size();
                     Object[] selectedItems = new Object[size];

                     for (int i = 0; i < size; i++) {
                        if (selectLocation) {
                           selectedItems[i] = ((MediaInfo)this._keywordList.getAt(i)).getLocation();
                        } else {
                           selectedItems[i] = this._keywordList.getAt(i);
                        }
                     }

                     listener.selected(selectedItems);
                  } else if (selectLocation) {
                     listener.selected(location);
                  } else {
                     listener.selected(selectedObject);
                  }

                  this.close();
                  return;
               }

               String playingurl = MediaPlayerState.getMediaPlayerURL();
               if (playingurl != null) {
                  playingurl = FileUtilities.makeFileURL(playingurl);
                  if (location.regionMatches(true, 0, playingurl, 0, playingurl.length())) {
                     MediaLauncher.showPlayer();
                     return;
                  }
               }
            }

            ContextObject context = (ContextObject)(new Object());
            ContextObject.put(context, -1477447097671931650L, this);
            Integer trackIndex = (Integer)(new Object(index));
            ContextObject.put(context, -4054673099568009991L, trackIndex);
            if (location != null && (this.isRingtone() || this.isVideo() || this.isVoicenote())) {
               context.setFlag(39);
               if (this.isVideo()) {
                  context.setFlag(40);
               }
            }

            if (selectedObject instanceof Object) {
               ContextObject.put(context, -4656037551219504382L, new Object(((Video)selectedObject).getBookmark()));
            }

            boolean var15 = false /* VF: Semaphore variable */;

            try {
               var15 = true;
               M3UPlaylist e = new Object();
               if (this._tracks != null) {
                  for (int i = 0; i < this._tracks.length; i++) {
                     ((M3UPlaylist)e).addUrl(this._tracks[i].getLocation(), this._tracks[i].getName(), -1);
                  }
               } else if (results) {
                  int size = this._keywordList.size();

                  for (int i = 0; i < size; i++) {
                     MediaInfo media = (MediaInfo)this._keywordList.getAt(i);
                     ((M3UPlaylist)e).addUrl(media.getLocation(), media.getName(), -1);
                  }
               } else {
                  for (int i = 0; i < this._collection.size(); i++) {
                     MediaInfo media = (MediaInfo)this._collection.getAt(i);
                     ((M3UPlaylist)e).addUrl(media.getLocation(), media.getName(), -1);
                  }
               }

               InputConnection connection = ((M3UPlaylist)e).openConnection("/");
               MediaLauncher.launch(connection, context);
               synchronized (this) {
                  this.wait(1500);
                  var15 = false;
               }
            } finally {
               if (var15) {
                  String msg = ExplorerResources.getString(202);
                  Dialog.alert(msg);
                  return;
               }
            }
         }
      }
   }

   private final EncodedImage extractImage() {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: aconst_null
      // 001: astore 1
      // 002: aload 0
      // 003: getfield net/rim/device/apps/internal/explorer/Media/TrackListScreen._album Lnet/rim/device/apps/internal/explorer/MediaLibrary/Album;
      // 006: ifnull 01a
      // 009: aload 0
      // 00a: getfield net/rim/device/apps/internal/explorer/Media/TrackListScreen._album Lnet/rim/device/apps/internal/explorer/MediaLibrary/Album;
      // 00d: ifnull 01c
      // 010: aload 0
      // 011: getfield net/rim/device/apps/internal/explorer/Media/TrackListScreen._album Lnet/rim/device/apps/internal/explorer/MediaLibrary/Album;
      // 014: invokevirtual net/rim/device/apps/internal/explorer/MediaLibrary/Album.getName ()Ljava/lang/String;
      // 017: ifnonnull 01c
      // 01a: aconst_null
      // 01b: areturn
      // 01c: aload 0
      // 01d: getfield net/rim/device/apps/internal/explorer/Media/TrackListScreen._keywordList Lnet/rim/device/api/collection/util/KeywordFilterList;
      // 020: invokeinterface net/rim/device/api/collection/ReadableList.size ()I 1
      // 025: ifgt 02b
      // 028: goto 113
      // 02b: aload 0
      // 02c: getfield net/rim/device/apps/internal/explorer/Media/TrackListScreen._keywordList Lnet/rim/device/api/collection/util/KeywordFilterList;
      // 02f: bipush 0
      // 030: invokeinterface net/rim/device/api/collection/ReadableList.getAt (I)Ljava/lang/Object; 2
      // 035: checkcast java/lang/Object
      // 038: invokeinterface net/rim/device/apps/internal/explorer/MediaLibrary/MediaInfo.getLocation ()Ljava/lang/String; 1
      // 03d: astore 2
      // 03e: aload 2
      // 03f: invokestatic net/rim/device/internal/io/file/FileUtilities.getPathURL (Ljava/lang/String;)Ljava/lang/String;
      // 042: astore 3
      // 043: new java/lang/Object
      // 046: dup
      // 047: invokespecial java/lang/StringBuffer.<init> ()V
      // 04a: aload 3
      // 04b: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 04e: ldc_w "folder.jpg"
      // 051: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 054: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 057: astore 4
      // 059: aconst_null
      // 05a: astore 5
      // 05c: aconst_null
      // 05d: astore 6
      // 05f: aload 4
      // 061: invokestatic javax/microedition/io/Connector.open (Ljava/lang/String;)Ljavax/microedition/io/Connection;
      // 064: checkcast java/lang/Object
      // 067: astore 5
      // 069: aload 5
      // 06b: invokeinterface javax/microedition/io/file/FileConnection.openInputStream ()Ljava/io/InputStream; 1
      // 070: astore 6
      // 072: aload 6
      // 074: invokestatic net/rim/device/api/io/IOUtilities.streamToBytes (Ljava/io/InputStream;)[B
      // 077: astore 7
      // 079: aload 7
      // 07b: ifnull 0b5
      // 07e: aload 7
      // 080: bipush 0
      // 081: aload 7
      // 083: arraylength
      // 084: aconst_null
      // 085: bipush 90
      // 087: bipush 90
      // 089: invokestatic net/rim/device/api/system/Display.getWidth ()I
      // 08c: invokestatic net/rim/device/apps/internal/browser/util/ImageConverter.convertAndScale ([BIILjava/lang/String;III)Lnet/rim/device/api/system/EncodedImage;
      // 08f: astore 1
      // 090: aload 1
      // 091: ifnull 0b5
      // 094: aload 1
      // 095: astore 8
      // 097: aload 5
      // 099: ifnull 0a3
      // 09c: aload 5
      // 09e: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 0a3: aload 6
      // 0a5: ifnull 0b2
      // 0a8: aload 6
      // 0aa: invokevirtual java/io/InputStream.close ()V
      // 0ad: goto 0b2
      // 0b0: astore 9
      // 0b2: aload 8
      // 0b4: areturn
      // 0b5: aload 5
      // 0b7: ifnull 0c1
      // 0ba: aload 5
      // 0bc: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 0c1: aload 6
      // 0c3: ifnull 113
      // 0c6: aload 6
      // 0c8: invokevirtual java/io/InputStream.close ()V
      // 0cb: goto 113
      // 0ce: astore 7
      // 0d0: goto 113
      // 0d3: astore 7
      // 0d5: aload 5
      // 0d7: ifnull 0e1
      // 0da: aload 5
      // 0dc: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 0e1: aload 6
      // 0e3: ifnull 113
      // 0e6: aload 6
      // 0e8: invokevirtual java/io/InputStream.close ()V
      // 0eb: goto 113
      // 0ee: astore 7
      // 0f0: goto 113
      // 0f3: astore 10
      // 0f5: aload 5
      // 0f7: ifnull 101
      // 0fa: aload 5
      // 0fc: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 101: aload 6
      // 103: ifnull 110
      // 106: aload 6
      // 108: invokevirtual java/io/InputStream.close ()V
      // 10b: goto 110
      // 10e: astore 11
      // 110: aload 10
      // 112: athrow
      // 113: aload 0
      // 114: getfield net/rim/device/apps/internal/explorer/Media/TrackListScreen._keywordList Lnet/rim/device/api/collection/util/KeywordFilterList;
      // 117: invokeinterface net/rim/device/api/collection/ReadableList.size ()I 1
      // 11c: bipush 1
      // 11d: isub
      // 11e: istore 2
      // 11f: iload 2
      // 120: ifge 126
      // 123: goto 24e
      // 126: aload 0
      // 127: getfield net/rim/device/apps/internal/explorer/Media/TrackListScreen._keywordList Lnet/rim/device/api/collection/util/KeywordFilterList;
      // 12a: iload 2
      // 12b: invokeinterface net/rim/device/api/collection/ReadableList.getAt (I)Ljava/lang/Object; 2
      // 130: checkcast java/lang/Object
      // 133: invokeinterface net/rim/device/apps/internal/explorer/MediaLibrary/MediaInfo.getLocation ()Ljava/lang/String; 1
      // 138: astore 3
      // 139: aconst_null
      // 13a: astore 4
      // 13c: aconst_null
      // 13d: astore 5
      // 13f: aload 3
      // 140: invokestatic javax/microedition/media/Manager.createPlayer (Ljava/lang/String;)Ljavax/microedition/media/Player;
      // 143: astore 4
      // 145: aload 4
      // 147: ifnull 160
      // 14a: aload 4
      // 14c: invokeinterface javax/microedition/media/Player.realize ()V 1
      // 151: aload 4
      // 153: ldc_w "net.rim.device.api.media.control.BinaryMetaDataControl"
      // 156: invokeinterface javax/microedition/media/Controllable.getControl (Ljava/lang/String;)Ljavax/microedition/media/Control; 2
      // 15b: checkcast java/lang/Object
      // 15e: astore 5
      // 160: aload 4
      // 162: ifnull 1a2
      // 165: aload 4
      // 167: invokeinterface javax/microedition/media/Player.close ()V 1
      // 16c: goto 1a2
      // 16f: astore 6
      // 171: aload 4
      // 173: ifnull 1a2
      // 176: aload 4
      // 178: invokeinterface javax/microedition/media/Player.close ()V 1
      // 17d: goto 1a2
      // 180: astore 6
      // 182: aload 4
      // 184: ifnull 1a2
      // 187: aload 4
      // 189: invokeinterface javax/microedition/media/Player.close ()V 1
      // 18e: goto 1a2
      // 191: astore 12
      // 193: aload 4
      // 195: ifnull 19f
      // 198: aload 4
      // 19a: invokeinterface javax/microedition/media/Player.close ()V 1
      // 19f: aload 12
      // 1a1: athrow
      // 1a2: aload 5
      // 1a4: ifnonnull 1aa
      // 1a7: goto 248
      // 1aa: aload 5
      // 1ac: invokeinterface net/rim/device/api/media/control/BinaryMetaDataControl.getMetaDataObjects ()[Lnet/rim/device/api/media/MetaDataObject; 1
      // 1b1: astore 6
      // 1b3: bipush 0
      // 1b4: istore 7
      // 1b6: iload 7
      // 1b8: aload 6
      // 1ba: arraylength
      // 1bb: if_icmplt 1c1
      // 1be: goto 248
      // 1c1: aload 6
      // 1c3: iload 7
      // 1c5: aaload
      // 1c6: ifnull 242
      // 1c9: aload 6
      // 1cb: iload 7
      // 1cd: aaload
      // 1ce: invokevirtual net/rim/device/api/media/MetaDataObject.getPictureType ()I
      // 1d1: bipush -1
      // 1d3: if_icmpeq 242
      // 1d6: aload 6
      // 1d8: iload 7
      // 1da: aaload
      // 1db: invokevirtual net/rim/device/api/media/MetaDataObject.getData ()[B
      // 1de: astore 8
      // 1e0: aload 8
      // 1e2: ifnonnull 1e8
      // 1e5: goto 242
      // 1e8: aload 8
      // 1ea: bipush 0
      // 1eb: aload 8
      // 1ed: arraylength
      // 1ee: aconst_null
      // 1ef: bipush 90
      // 1f1: bipush 90
      // 1f3: invokestatic net/rim/device/api/system/Display.getWidth ()I
      // 1f6: invokestatic net/rim/device/apps/internal/browser/util/ImageConverter.convertAndScale ([BIILjava/lang/String;III)Lnet/rim/device/api/system/EncodedImage;
      // 1f9: astore 1
      // 1fa: aload 1
      // 1fb: ifnonnull 201
      // 1fe: goto 242
      // 201: aload 1
      // 202: invokevirtual net/rim/device/api/system/EncodedImage.getWidth ()I
      // 205: istore 9
      // 207: aload 1
      // 208: invokevirtual net/rim/device/api/system/EncodedImage.getHeight ()I
      // 20b: istore 10
      // 20d: iload 9
      // 20f: bipush 5
      // 211: if_icmplt 21b
      // 214: iload 10
      // 216: bipush 5
      // 218: if_icmpge 220
      // 21b: aconst_null
      // 21c: astore 1
      // 21d: goto 242
      // 220: iload 9
      // 222: bipush 90
      // 224: if_icmpge 23c
      // 227: iload 10
      // 229: bipush 90
      // 22b: if_icmpge 23c
      // 22e: aload 1
      // 22f: ldc_w 65536
      // 232: invokevirtual net/rim/device/api/system/EncodedImage.setScaleX32 (I)V
      // 235: aload 1
      // 236: ldc_w 65536
      // 239: invokevirtual net/rim/device/api/system/EncodedImage.setScaleY32 (I)V
      // 23c: aload 1
      // 23d: ifnull 242
      // 240: aload 1
      // 241: areturn
      // 242: iinc 7 1
      // 245: goto 1b6
      // 248: iinc 2 -1
      // 24b: goto 11f
      // 24e: aconst_null
      // 24f: areturn
      // try (68 -> 76): 77 null
      // try (80 -> 88): 89 null
      // try (42 -> 68): 91 null
      // try (92 -> 100): 101 null
      // try (42 -> 68): 103 null
      // try (91 -> 92): 103 null
      // try (104 -> 112): 113 null
      // try (103 -> 104): 103 null
      // try (136 -> 148): 153 null
      // try (136 -> 148): 159 null
      // try (136 -> 148): 165 null
      // try (153 -> 154): 165 null
      // try (159 -> 160): 165 null
      // try (165 -> 166): 165 null
   }

   public TrackListScreen(Object context) {
      super(562949953421312L);

      label26:
      try {
         NO_ART = ThemeManager.getActiveTheme().getImage("noartsmall");
      } finally {
         break label26;
      }

      this.setTag(ThemeUtilities.SCREEN_TAG);
      Manager manager = this.getMainManager();
      if (manager != null) {
         manager.setTag(ThemeUtilities.SCREEN_TAG);
      }

      this._collection = MediaLibrary.getInstance().getTrackCollection();
      this._contextInfo = (ContextInfo)context;
      this.initialize();
      this.setHelp(32247);
   }

   private final void buildSubset() {
      int type = this._contextInfo.getType();
      this._height = Integer.MAX_VALUE;
      if (type != 1) {
         Artist artist = (Artist)this._contextInfo.getData(2);
         Album album = (Album)this._contextInfo.getData(4);
         Genre genre = (Genre)this._contextInfo.getData(8);
         PlaylistItem playlist = (PlaylistItem)this._contextInfo.getData(16);
         String[] keywords = new Object[0];
         boolean shouldReturn = false;
         boolean buildTransient = false;
         switch (type) {
            case 3:
               Arrays.append(keywords, artist.getKeywords());
               Arrays.append(keywords, artist.getPrefixedKeywords());
               this._height = -1;
               break;
            case 5:
               this._album = album;
               Arrays.append(keywords, album.getPrefixedKeywords());
               this._height = -1;
               if (album.getName() != null) {
                  buildTransient = true;
               }
               break;
            case 7:
               this._album = album;
               Arrays.append(keywords, artist.getKeywords());
               Arrays.append(keywords, artist.getPrefixedKeywords());
               Arrays.append(keywords, album.getPrefixedKeywords());
               this._height = -1;
               if (album.getName() != null) {
                  buildTransient = true;
               }
               break;
            case 9:
               Arrays.append(keywords, genre.getKeywords());
               Arrays.append(keywords, genre.getPrefixedKeywords());
               break;
            case 11:
               Arrays.append(keywords, genre.getKeywords());
               Arrays.append(keywords, genre.getPrefixedKeywords());
               Arrays.append(keywords, artist.getKeywords());
               Arrays.append(keywords, artist.getPrefixedKeywords());
               this._height = -1;
               break;
            case 13:
               this._album = album;
               Arrays.append(keywords, genre.getKeywords());
               Arrays.append(keywords, genre.getPrefixedKeywords());
               Arrays.append(keywords, album.getPrefixedKeywords());
               this._height = -1;
               if (album.getName() != null) {
                  buildTransient = true;
               }
               break;
            case 14:
               this._album = album;
               Arrays.append(keywords, genre.getKeywords());
               Arrays.append(keywords, genre.getPrefixedKeywords());
               Arrays.append(keywords, artist.getKeywords());
               Arrays.append(keywords, artist.getPrefixedKeywords());
               Arrays.append(keywords, album.getPrefixedKeywords());
               this._height = -1;
               if (album.getName() != null) {
                  buildTransient = true;
               }
               break;
            case 17:
               this._playlist = playlist;
               this._collection = this._playlist.getCollection();
               return;
            case 32:
               this._collection = MediaLibrary.getInstance().getVideoCollection();
               this._height = -1;
               return;
            case 65:
               this._collection = MediaLibrary.getInstance().getRingtoneCollection();
               this._height = -1;
               return;
            case 256:
               this._collection = MediaLibrary.getInstance().getVoiceNotesCollection();
               return;
            case 513:
               this._collection = MediaLibrary.getInstance().getPreloadedTrackCollection();
               break;
            case 544:
               this._collection = MediaLibrary.getInstance().getVideoCollection();
               Arrays.add(keywords, FilterConstants.PRELOADED_PREFIX);
               this._height = -1;
               shouldReturn = true;
               break;
            case 577:
               this._collection = MediaLibrary.getInstance().getRingtoneCollection();
               Arrays.add(keywords, FilterConstants.PRELOADED_PREFIX);
               this._height = -1;
               shouldReturn = true;
               break;
            case 1056:
               this._collection = MediaLibrary.getInstance().getVideoCollection();
               Arrays.add(keywords, FilterConstants.USERLOADED_PREFIX);
               this._height = -1;
               shouldReturn = true;
               break;
            case 1089:
               this._collection = MediaLibrary.getInstance().getRingtoneCollection();
               Arrays.add(keywords, FilterConstants.USERLOADED_PREFIX);
               this._height = -1;
               shouldReturn = true;
         }

         if (this.isExternal()) {
            this._keywordList = this._collection.getKeywordFilterListInstance(null);
         } else {
            this._keywordList = this._collection.getKeywordFilterList();
         }

         if (keywords.length > 0) {
            this._collection.addCriteria(this._keywordList, keywords);
         }

         if (!shouldReturn) {
            MediaInfoCollection transientCollection = null;
            if (buildTransient) {
               transientCollection = (MediaInfoCollection)(new Object(TrackNumberComparator.getInstance()));
            } else {
               this._tracks = new Object[0];
            }

            for (int i = 0; i < this._keywordList.size(); i++) {
               MediaInfo track = null;

               label120:
               try {
                  track = (MediaInfo)this._keywordList.getAt(i);
               } finally {
                  break label120;
               }

               if (track != null) {
                  if (buildTransient) {
                     transientCollection.add(track);
                  } else {
                     Arrays.add(this._tracks, track);
                  }
               }
            }

            if (buildTransient) {
               this._collection.addCriteria(this._keywordList, null);
               this._collection = transientCollection;
               this._keywordList = null;
            }
         }
      }
   }

   private final String getTitle() {
      if (this.isVideo()) {
         return ExplorerResources.getString(139);
      } else if (this.isRingtone()) {
         return ExplorerResources.getString(138);
      } else if (this.isVoicenote()) {
         return ExplorerResources.getString(174);
      } else if (this.isPlaylist()) {
         String playlistName = this._playlist.getName();
         return ((StringBuffer)(new Object())).append(ExplorerResources.getString(198)).append(playlistName).toString();
      } else {
         return ExplorerResources.getString(0);
      }
   }

   private final Manager drawAlbumArt() {
      BitmapField bitmap = null;
      HorizontalFieldManager titleManager = null;
      if (NO_ART == null) {
         NO_ART = ThemeManager.getActiveTheme().getImage("noart");
      }

      if ((this._contextInfo.getType() & 4) != 0) {
         bitmap = (BitmapField)(new Object(null, 36028809903865856L));
         bitmap.setReplacementForCorruptImage(NO_ART);
         titleManager = (HorizontalFieldManager)(new Object(1152921504606846976L));
         bitmap.setPadding(3, 7, 3, 3);
         titleManager.add(bitmap);
         titleManager.setTag(ThemeUtilities.ALBUM_TITLE_TAG);
         KeywordFilterList keywordList = this._keywordList;
         Album album = null;
         String albumTitle = null;
         synchronized (keywordList) {
            int size = keywordList.size();
            if (size > 0) {
               album = ((Track)keywordList.getAt(0)).getAlbum();
               if (album != null) {
                  albumTitle = album.toString();
               }
            }
         }

         if (albumTitle != null) {
            AlbumTitleManager atm = new AlbumTitleManager(albumTitle, 90);
            VerticalFieldManager vfm = (VerticalFieldManager)(new Object(51539607552L));
            vfm.add(atm);
            titleManager.add(vfm);
         }

         if (album != null) {
            EncodedImage albumArt = album.getArt();
            if (albumArt != null) {
               ImageConverter.scaleImage(albumArt, 90, 90, 90, 90);
               bitmap.setImage(albumArt);
            } else {
               bitmap.setImage(NO_ART);
            }

            this._imageExtractor = new TrackListScreen$ExtractImageRunnable(this, bitmap);
         }
      }

      return titleManager;
   }

   private final boolean isExternal() {
      return this._contextInfo.isExternal();
   }

   private final SelectionListener getSelectionListener() {
      Object item = this._contextInfo.getItem();
      return !(item instanceof SelectionListener) ? null : (SelectionListener)item;
   }

   private final boolean canDelete(MediaInfo media) {
      return this._playlist != null || MediaUtilities.canDelete(media.getLocation());
   }

   private final String getDefaultPath() {
      if (this.isVoicenote()) {
         return FileUtilities.getDefaultPathForMIMEType("audio/amr");
      } else {
         return this.isVideo() ? FileUtilities.getDefaultPath(3) : "";
      }
   }

   private final int setFolderListView(FolderList folderList) {
      ExplorerRegistry registry = ExplorerRegistry.getInstance();
      int sortProperty = 0;
      Comparator comparator = FileItemComparator.getInstance(sortProperty);
      SortedReadableList shortcutList = (SortedReadableList)(new Object(comparator));
      AliasFileEntry[] globalEntries = registry.getGlobalAlias(0);
      String path = this.getDefaultPath();
      if (globalEntries != null) {
         int i = globalEntries.length;

         while (--i >= 0) {
            if (globalEntries[i].isActiveInDirectory(path, (FileSelectionFilter)(new Object()))) {
               shortcutList.elementAdded(null, new AliasFileItemField(globalEntries[i]));
               if (this.isVideo()) {
                  this._videoCameraAlias = globalEntries[i];
               }
            }
         }
      }

      folderList.setCurrentView(path, shortcutList);
      return shortcutList.size();
   }

   private final String getEmptyString() {
      if (this.isVideo()) {
         return ExplorerResources.getString(163);
      } else if (this.isRingtone()) {
         return ExplorerResources.getString(162);
      } else {
         return this.isVoicenote() ? ExplorerResources.getString(175) : ExplorerResources.getString(144);
      }
   }

   private final int getSelectedIndex() {
      MediaInfo media = (MediaInfo)this.getSelectedMedia();
      if (media != null) {
         return this._tracks != null ? Arrays.getIndex(this._tracks, media) : this._collection.getIndex(media);
      } else {
         return -1;
      }
   }

   private final Object getSelectedMedia() {
      Field field = this.getLeafFieldWithFocus();
      if (!(field instanceof Object)) {
         return null;
      }

      KeywordFilterCollectionListField kList = (KeywordFilterCollectionListField)field;
      return kList.getSelectedElement();
   }

   @Override
   protected final void onUiEngineAttached(boolean attached) {
      super.onUiEngineAttached(attached);
      if (!attached) {
         this.cleanUp();
         String entry = FileExplorerApp.getEntry();
         if (entry != null && entry.equals("voicenote")) {
            System.exit(0);
            return;
         }
      } else if (this._imageExtractor != null) {
         this._imageExtractor.start();
      }
   }

   private final void record() {
      ExplorerRegistry registry = ExplorerRegistry.getInstance();
      AliasFileEntry[] globalEntries = registry.getGlobalAlias(0);
      String path = this.getDefaultPath();
      if (globalEntries != null) {
         int i = globalEntries.length;

         while (--i >= 0) {
            if (globalEntries[i].isActiveInDirectory(path, (FileSelectionFilter)(new Object()))) {
               globalEntries[i].getVerb().invoke(null);
               return;
            }
         }
      }
   }

   private final int translateIndex(int index) {
      if (this._list.getMoveMode()) {
         if (index == this._movingCurIndex) {
            return this._movedItemIndex;
         }

         if (index < this._movingCurIndex && index >= this._movedItemIndex) {
            return index + 1;
         }

         if (index > this._movingCurIndex && index <= this._movedItemIndex) {
            index--;
         }
      }

      return index;
   }

   private final void completeMove() {
      Playlistable moved = (Playlistable)this._collection.getAt(this._movedItemIndex);
      int newSelection = this._movedItemIndex;
      if (this._playlist != null && this._movingCurIndex != this._movedItemIndex) {
         this._playlist.moveMedia(moved, this._movingCurIndex, false);
         this._dirty = true;
         newSelection = this._movingCurIndex;
      }

      this._list.setMoveMode(false);
      this._list.setSelectedIndex(newSelection);
      this._movingCurIndex = this._movedItemIndex = -1;
   }
}
