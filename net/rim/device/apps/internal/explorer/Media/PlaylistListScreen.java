package net.rim.device.apps.internal.explorer.Media;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.KeywordFilterCollectionListField;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.apps.api.framework.model.ActionProvider;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.ui.PopupStatus;
import net.rim.device.apps.api.ui.SystemEnabledMenu;
import net.rim.device.apps.internal.explorer.Media.dialogs.NewPlaylistPopup;
import net.rim.device.apps.internal.explorer.Media.verbs.MediaVerb;
import net.rim.device.apps.internal.explorer.MediaLibrary.ContextInfo;
import net.rim.device.apps.internal.explorer.MediaLibrary.MediaInfo;
import net.rim.device.apps.internal.explorer.MediaLibrary.MediaInfoCollection;
import net.rim.device.apps.internal.explorer.MediaLibrary.MediaLibrary;
import net.rim.device.apps.internal.explorer.MediaLibrary.PlaylistItem;
import net.rim.device.apps.internal.explorer.MediaLibrary.SmartlistItem;
import net.rim.device.apps.internal.explorer.file.menu.PropertiesMenuItem;
import net.rim.device.apps.internal.explorer.file.resource.ExplorerResources;
import net.rim.device.apps.internal.explorer.file.verbs.RenameFileVerb;
import net.rim.device.internal.i18n.CommonResource;
import net.rim.device.internal.io.file.FileUtilities;

final class PlaylistListScreen extends MediaListScreen {
   private boolean _newPlaylist;
   private String _newPlayListString;

   PlaylistListScreen(Object context) {
      super(context);
   }

   @Override
   protected final MediaInfoCollection getCollection() {
      return MediaLibrary.getInstance().getPlaylistCollection();
   }

   @Override
   protected final String getTitle() {
      return ExplorerResources.getString(146);
   }

   @Override
   protected final String getEmptyString() {
      return ExplorerResources.getString(161);
   }

   @Override
   protected final void buildSubset() {
   }

   @Override
   public final void drawListRow(ListField listField, Graphics graphics, int index, int y, int width) {
      if (index == 0 && this._newPlaylist) {
         graphics.drawText(this._newPlayListString, 5, y + 3);
      } else {
         super.drawListRow(listField, graphics, index, y, width);
      }
   }

   @Override
   protected final void checkToAddAll(KeywordFilterCollectionListField list) {
   }

   @Override
   protected final void checkToAddNew(KeywordFilterCollectionListField list) {
      if (super._contextInfo.getType() == 17) {
         list.setExtraRowCount(1);
         this._newPlaylist = true;
         this._newPlayListString = ExplorerResources.getString(192);
      }
   }

   @Override
   protected final void makeMenu(SystemEnabledMenu menu, int instance) {
      super.makeMenu(menu, instance);
      Object selection = this.getSelectedObject();
      ContextObject context = new ContextObject();
      if (selection instanceof MediaInfo) {
         String location = ((MediaInfo)selection).getLocation();
         context.put(2765042845091913199L, location);
         ContextObject.put(menu.getContext(), 2765042845091913199L, location);
         menu.add(new PropertiesMenuItem((MediaInfo)selection));
         menu.add(new MediaVerb(6068253483645874830L, this, 591107, CommonResource.getBundle(), 17));
         menu.add(new RenameFileVerb(null, null));
      }

      if (FileUtilities.isSDCardMounted() && MediaLibrary.getInstance().getBackdoor()) {
         menu.add(new MediaVerb(-6715256456950413284L, this, 569346, ExplorerResources.getResourceBundleFamily(), 180));
      }
   }

   @Override
   protected final boolean canAddToPlaylist() {
      return false;
   }

   @Override
   protected final boolean keyChar(char c, int status, int time) {
      if (c == 127 || c == '\b') {
         String text = super._finder.getText();
         if (text == null || text.length() <= 0) {
            this.delete();
            return true;
         }
      }

      return super.keyChar(c, status, time);
   }

   @Override
   public final boolean perform(long actionId, Object context) {
      if (actionId == 6068253483645874830L) {
         this.delete();
         return true;
      } else if (actionId == 3712387911241450002L) {
         this.playPlaylist(actionId);
         return true;
      } else if (actionId == -6715256456950413284L) {
         UiApplication.getUiApplication().pushModalScreen(new NewPlaylistPopup(1));
         return true;
      } else {
         return super.perform(actionId, context);
      }
   }

   private final void delete() {
      Field field = this.getLeafFieldWithFocus();
      if (field instanceof ListField) {
         MediaInfo playlist = (MediaInfo)this.getSelectedObject();
         if (playlist != null) {
            String location = playlist.getLocation();
            boolean deleteFile = Dialog.ask(2) == 3;
            if (deleteFile) {
               try {
                  FileUtilities.delete(location, true);
               } finally {
                  return;
               }
            }
         }
      }
   }

   private final void createNewPlaylist() {
      NewPlaylistPopup newList = new NewPlaylistPopup(0);
      Object list = newList.doModal();
      if (list instanceof PlaylistItem) {
         PlaylistItem playlist = (PlaylistItem)list;
         if (playlist != null) {
            ContextInfo contextInfo = ContextInfo.createCopy(super._contextInfo);
            contextInfo.setType(17);
            contextInfo.setData(16, playlist);
            TrackListScreen screen = new TrackListScreen(contextInfo);
            screen.setScreenDirty();
            UiApplication.getUiApplication().pushScreen(screen);
         }
      }
   }

   private final void playPlaylist(long actionId) {
      Field field = this.getLeafFieldWithFocus();
      if (field instanceof ListField) {
         Object item = this.getSelectedObject();
         ContextInfo contextInfo = ContextInfo.createCopy(super._contextInfo);
         ActionProvider action = null;
         if (item instanceof PlaylistItem) {
            contextInfo.setType(17);
            contextInfo.setData(16, (PlaylistItem)item);
            action = new TrackListScreen(contextInfo);
            action.perform(actionId, item);
         } else {
            if (item instanceof SmartlistItem) {
               contextInfo.setType(16);
               contextInfo.setData(16, (SmartlistItem)item);
               ActionProvider var7 = new SmartlistScreen(contextInfo, false);
               var7.perform(actionId, item);
            }
         }
      }
   }

   @Override
   protected final void invoke() {
      Field field = this.getLeafFieldWithFocus();
      if (field instanceof ListField) {
         Object item = this.getSelectedObject();
         ContextInfo contextInfo = ContextInfo.createCopy(super._contextInfo);
         Screen screen = null;
         if (this._newPlaylist && this.getSelectedIndex() == 0) {
            this.createNewPlaylist();
         }

         if (item instanceof PlaylistItem) {
            contextInfo.setType(17);
            contextInfo.setData(16, (PlaylistItem)item);
            screen = new TrackListScreen(contextInfo);
         } else if (item instanceof SmartlistItem) {
            contextInfo.setType(16);
            contextInfo.setData(16, (SmartlistItem)item);
            screen = new SmartlistScreen(contextInfo, false);
         }

         if (screen != null) {
            UiApplication.getUiApplication().pushScreen(screen);
         }
      }
   }

   @Override
   protected final boolean openDevelopmentBackdoor(int backdoorCode) {
      switch (backdoorCode) {
         case 1397576275:
            return super.openDevelopmentBackdoor(backdoorCode);
         case 1397576276:
         default:
            MediaLibrary.getInstance().setBackdoor(true);
            PopupStatus.show("Smartlist mode active!", 750);
            return true;
      }
   }
}
