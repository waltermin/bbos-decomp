package net.rim.device.apps.internal.explorer.Media;

import javax.microedition.io.InputConnection;
import net.rim.device.api.collection.Collection;
import net.rim.device.api.collection.CollectionListener;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.CharacterUtilities;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.ui.SystemEnabledMenu;
import net.rim.device.apps.internal.explorer.MediaLibrary.ContextInfo;
import net.rim.device.apps.internal.explorer.MediaLibrary.MediaInfo;
import net.rim.device.apps.internal.explorer.MediaLibrary.MediaInfoCollection;
import net.rim.device.apps.internal.explorer.MediaLibrary.MediaLibrary;
import net.rim.device.apps.internal.explorer.file.FileExplorerApp;
import net.rim.device.apps.internal.explorer.file.resource.ExplorerResources;
import net.rim.device.internal.media.M3UPlaylist;
import net.rim.device.internal.media.MediaOptionsRegistry;

public final class MusicLibraryScreen extends MediaLibraryScreen implements CollectionListener {
   private String item;

   public MusicLibraryScreen(ContextInfo context) {
      super(context);
   }

   @Override
   protected final void initialize() {
      MediaLibrary.getInstance().getPlaylistCollection().addCollectionListener(this);
      this.addNowPlaying(2);
   }

   @Override
   protected final String[] getItems() {
      String[] items = new Object[0];
      Arrays.append(items, ExplorerResources.getStringArray(135));
      this.item = items[4];
      return items;
   }

   @Override
   protected final String getTitle() {
      return ExplorerResources.getString(143);
   }

   @Override
   protected final boolean keyChar(char c, int status, int time) {
      if (super.keyChar(c, status, time)) {
         return true;
      }

      ContextInfo contextInfo = (ContextInfo)(new Object(super._context.getType() | 1));
      TrackListScreen screen = new TrackListScreen(contextInfo);
      StringBuffer buffer = (StringBuffer)(new Object(1));
      buffer.append(CharacterUtilities.toUpperCase(c));
      screen.setText(buffer.toString());
      UiApplication.getUiApplication().pushScreen(screen);
      return true;
   }

   @Override
   protected final void invoke() {
      super.invoke();
      int index = this.getSelectedIndex() - this.getNowPlaying();
      if (index >= 0) {
         int length = super.ITEMS.length - this.getNowPlaying();
         Screen screen = null;
         ContextInfo contextInfo = ContextInfo.createCopy(super._context);
         if (index == 0) {
            contextInfo.setType(1);
            screen = new TrackListScreen(contextInfo);
         } else if (index == 1) {
            contextInfo.setType(2);
            screen = new ArtistListScreen(contextInfo);
         } else if (index == 2) {
            contextInfo.setType(4);
            screen = new AlbumListScreen(contextInfo);
         } else if (index == 3) {
            contextInfo.setType(8);
            screen = new GenreListScreen(contextInfo);
         } else if (index == 4 && length == 7) {
            contextInfo.setType(17);
            screen = new PlaylistListScreen(contextInfo);
         } else if (index == length - 2) {
            contextInfo.setType(513);
            screen = new TrackListScreen(contextInfo);
         } else if (index == length - 1) {
            this.shuffleAllSongs();
         }

         if (screen != null) {
            UiApplication.getUiApplication().pushScreen(screen);
         }
      }
   }

   @Override
   protected final void makeMenu(SystemEnabledMenu menu, int instance) {
      MediaUtilities.addDownloadTunesMenuItems(menu);
      super.makeMenu(menu, instance);
   }

   @Override
   protected final void onUiEngineAttached(boolean attached) {
      super.onUiEngineAttached(attached);
      if (!attached) {
         MediaLibrary.getInstance().getPlaylistCollection().removeCollectionListener(this);
         if (FileExplorerApp.getEntry().equals("music")) {
            System.exit(0);
         }
      }
   }

   @Override
   public final boolean perform(long actionId, Object context) {
      if (actionId == 5803508244060051872L) {
         synchronized (this) {
            this.notifyAll();
            return true;
         }
      } else {
         return super.perform(actionId, context);
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final void shuffleAllSongs() {
      MediaInfoCollection collection = MediaLibrary.getInstance().getTrackCollection();
      if (collection.size() != 0) {
         M3UPlaylist playlist = (M3UPlaylist)(new Object());
         MediaInfo media = null;
         int size = collection.size();

         for (int i = 0; i < size; i++) {
            MediaInfo var14 = collection.getAt(i);
            playlist.addUrl(((MediaInfo)var14).getLocation(), ((MediaInfo)var14).getName(), -1);
         }

         boolean var11 = false /* VF: Semaphore variable */;

         try {
            var11 = true;
            InputConnection var15 = playlist.openConnection("/");
            MediaOptionsRegistry.getInstance().setBoolean(-2846908971875712627L, true);
            ContextObject context = new Object();
            ContextObject.put(context, -1477447097671931650L, this);
            MediaLauncher.launch(var15, context);
            synchronized (this) {
               this.wait(1500);
               var11 = false;
            }
         } finally {
            if (var11) {
               Dialog.alert(ExplorerResources.getString(202));
               return;
            }
         }
      }
   }

   @Override
   public final void elementAdded(Collection collection, Object element) {
      int length = super.ITEMS.length - this.getNowPlaying();
      if (MediaLibrary.getInstance().getPlaylistCollection().size() > 0 && length < 7) {
         Arrays.insertAt(super.ITEMS, this.item, 4 + this.getNowPlaying());
         this.update();
      }
   }

   @Override
   public final void elementRemoved(Collection collection, Object element) {
      int length = super.ITEMS.length - this.getNowPlaying();
      if (MediaLibrary.getInstance().getPlaylistCollection().size() <= 0 && length >= 7) {
         Arrays.removeAt(super.ITEMS, 4 + this.getNowPlaying());
         this.update();
      }
   }

   @Override
   public final void elementUpdated(Collection collection, Object oldElement, Object newElement) {
   }

   @Override
   public final void reset(Collection collection) {
   }
}
