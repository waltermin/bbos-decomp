package net.rim.device.apps.internal.explorer.file;

import java.lang.ref.WeakReference;
import java.util.Enumeration;
import net.rim.device.api.system.Application;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.api.framework.file.FileSelectionFilter;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.internal.explorer.file.resource.ExplorerResources;
import net.rim.device.internal.io.file.FileIndexService;
import net.rim.device.internal.io.file.FileUtilities;
import net.rim.device.internal.io.file.FilteredFolderListener;
import net.rim.vm.Array;

public final class MediaFolderListener implements FilteredFolderListener {
   private MediaAliasFolderEntry[] _aliasEntries = new MediaAliasFolderEntry[0];
   private WeakReference _managerRef;
   private String _rootDir;
   private String _rootDisplayName;
   private RootFileItemField _rootItem;
   private FileConnectionHolder _initialView;
   private int _mediaType;
   private int _rootView;
   private Object _context;
   private FileIndexService _indexService;
   private static final String SAMPLES_ROOT_FOLDER = "/store/samples/";
   private static final String PICTURES_SAMPLE_FOLDER = "/store/samples/pictures/";
   private static final String CONTACTS_SAMPLE_FOLDER = "/store/samples/contacts/";
   private static final String RINGTONES_SAMPLE_FOLDER = "/store/samples/ringtones/";
   private static final String MUSIC_SAMPLES_FOLDER = "/store/samples/music/";
   private static final String VIDEO_SAMPLE_FOLDER = "/store/samples/videos/";

   public final synchronized void setManager(ExploreManager manager) {
      this._managerRef = new WeakReference(manager);
      this._initialView = manager.getCurrentView();
   }

   public final synchronized ExploreManager getManager() {
      return this._managerRef != null ? (ExploreManager)this._managerRef.get() : null;
   }

   public final RootFileItemField getRootItem() {
      if (this._rootItem == null) {
         this._rootItem = new RootFileItemField(this._rootDir, this._rootDisplayName);
         this._rootItem.update(this._rootDir, this._aliasEntries);
      }

      return this._rootItem;
   }

   final MediaAliasFolderEntry createAliasForPath(String path) {
      String[] localizedPaths = ExplorerResources.getTokenizedStringArrayElement(0, 13, this._rootView);
      boolean isSample = StringUtilities.startsWithIgnoreCase(path, "/store/samples/", 1701707776);
      int i = localizedPaths.length;

      String displayName;
      while (true) {
         if (--i < 0) {
            displayName = FileUtilities.getDisplayBaseName(path);
            break;
         }

         if (isSample ? StringUtilities.startsWithIgnoreCase(localizedPaths[i], "/store/samples/", 1701707776) : path.equalsIgnoreCase(localizedPaths[i])) {
            String[] localizedNames = ExplorerResources.getTokenizedStringArrayElement(13, this._rootView);
            displayName = localizedNames[i];
            break;
         }
      }

      MediaAliasFolderEntry entry = new MediaAliasFolderEntry(displayName, new MediaFolderListener$AliasFolderVerb(this, path), path, this._mediaType);
      if (this._rootItem != null) {
         this._rootItem.addAlias(entry);
      }

      return entry;
   }

   @Override
   public final void folderAdded(String pathURL) {
      String path = pathURL.substring(7);
      if (!StringUtilities.startsWithIgnoreCase(path, this._rootDir, 1701707776)
         && (!StringUtilities.startsWithIgnoreCase(path, "/store/samples/", 1701707776) || this.showSamplesFolder(path))) {
         boolean initialViewChanging = false;
         String defaultPath = FileUtilities.getDefaultPath(this._mediaType);
         if (StringUtilities.startsWithIgnoreCase(path, defaultPath, 1701707776)) {
            this.register();
            initialViewChanging = true;
         } else {
            label49: {
               int i = this._aliasEntries.length;

               while (--i >= 0) {
                  String checkFolder = this._aliasEntries[i].getPath();
                  if (StringUtilities.startsWithIgnoreCase(checkFolder, path, 1701707776)) {
                     if (!path.equals(checkFolder)) {
                        if (this._rootItem != null) {
                           this._rootItem.removeAlias(this._aliasEntries[i]);
                        }

                        this._aliasEntries[i] = this.createAliasForPath(path);
                        initialViewChanging = true;
                     }
                     break label49;
                  }

                  if (StringUtilities.startsWithIgnoreCase(path, checkFolder, 1701707776)) {
                     break label49;
                  }
               }

               Arrays.add(this._aliasEntries, this.createAliasForPath(path));
               initialViewChanging = true;
            }
         }

         if (initialViewChanging) {
            this.onInitialViewChanging();
         }
      }
   }

   @Override
   public final void folderDeleted(String pathURL) {
      String path = pathURL.substring(7);
      boolean initialViewChanging = false;
      String previousRoot = this._rootDir;
      if (path.equalsIgnoreCase(previousRoot)) {
         this.register();
         initialViewChanging = true;
      } else {
         int i = this._aliasEntries.length;

         while (--i >= 0) {
            if (path.equalsIgnoreCase(this._aliasEntries[i].getPath())) {
               if (this._rootItem != null) {
                  this._rootItem.removeAlias(this._aliasEntries[i]);
                  initialViewChanging = true;
               }

               Arrays.removeAt(this._aliasEntries, i);
            }
         }
      }

      if (initialViewChanging) {
         this.onInitialViewChanging();
      }
   }

   private final void register() {
      Array.resize(this._aliasEntries, 0);
      this._rootDir = FileUtilities.getDefaultPath(this._mediaType);
      if (this._indexService != null) {
         Enumeration folderList = this._indexService.metaDataFolders(this._mediaType);

         while (folderList.hasMoreElements()) {
            this.folderAdded((String)folderList.nextElement());
         }
      }

      if (this.selectingFolder()) {
         String deviceMemoryDefaultPath = FileIndexService.getDefaultMediaFolderForFileSystem(this._mediaType, 3, -1);
         if (StringUtilities.compareToIgnoreCase(deviceMemoryDefaultPath, this._rootDir, 1701707776) != 0) {
            int i = this._aliasEntries.length;

            String path;
            do {
               if (--i < 0) {
                  Arrays.add(this._aliasEntries, this.createAliasForPath(deviceMemoryDefaultPath));
                  break;
               }

               path = this._aliasEntries[i].getPath();
            } while (StringUtilities.compareToIgnoreCase(deviceMemoryDefaultPath, path, 1701707776) != 0);
         }
      }

      if (this._rootItem != null) {
         this._rootItem.update(this._rootDir, this._aliasEntries);
      }
   }

   private final boolean selectingFolder() {
      int selectionAttribs = 0;
      if (ContextObject.getFlag(this._context, 5)) {
         Object obj = ContextObject.get(this._context, -1002650280265073678L);
         if (!(obj instanceof FileSelectionFilter)) {
            selectionAttribs = ContextObject.getIntegerData(this._context, ContextObject.getIntegerData(this._context, 0));
         } else {
            selectionAttribs = ((FileSelectionFilter)obj).getSelectFilter();
         }
      }

      return (selectionAttribs & 1024) != 0;
   }

   private final boolean showSamplesFolder(String path) {
      Object obj = ContextObject.get(this._context, -1002650280265073678L);
      if (obj instanceof FileSelectionFilter) {
         FileSelectionFilter filter = (FileSelectionFilter)obj;
         if ((filter.getSelectFilter() & 8192) == 0) {
            return false;
         }

         String sampleFolder = filter.getSampleFolder();
         if (sampleFolder != null) {
            if (StringUtilities.startsWithIgnoreCase(sampleFolder, "file://", 1701707776)) {
               sampleFolder = sampleFolder.substring(7);
            }

            return StringUtilities.strEqualIgnoreCase(path, sampleFolder, 1701707776);
         }

         if (filter.isSelectForwardUnlockedOn() && !filter.isSelectForwardLockedOn()) {
            return false;
         }
      }

      switch (this._rootView) {
         case -1:
            return false;
         case 0:
         default:
            return StringUtilities.strEqualIgnoreCase(path, "/store/samples/", 1701707776);
         case 1:
            return StringUtilities.strEqualIgnoreCase(path, "/store/samples/pictures/", 1701707776);
         case 2:
            return StringUtilities.strEqualIgnoreCase(path, "/store/samples/ringtones/", 1701707776);
         case 3:
            return StringUtilities.strEqualIgnoreCase(path, "/store/samples/videos/", 1701707776);
         case 4:
            return StringUtilities.strEqualIgnoreCase(path, "/store/samples/music/", 1701707776);
      }
   }

   public MediaFolderListener(String rootDisplayName, int mediaType, Object context) {
      this(rootDisplayName, mediaType, FileSelectionFilter.getDefaultRootViewForMediaType(mediaType), context);
   }

   public MediaFolderListener(String rootDisplayName, int mediaType, int rootView, Object context) {
      this._rootDisplayName = rootDisplayName;
      this._mediaType = mediaType;
      this._rootView = rootView;
      this._context = context;
      this._indexService = FileIndexService.getService();
      if (this._indexService != null) {
         this._indexService.addFilteredFolderListener(this, mediaType);
      }

      this.register();
   }

   public MediaFolderListener(String rootDisplayName, int mediaType) {
      this(rootDisplayName, mediaType, FileSelectionFilter.getDefaultRootViewForMediaType(mediaType), null);
   }

   private final void onInitialViewChanging() {
      ExploreManager manager = this.getManager();
      if (manager != null && manager.getCurrentView() == this._initialView) {
         Application app = manager.getScreen().getApplication();
         if (app != null) {
            app.invokeLater(new MediaFolderListener$1(this));
         }
      }
   }
}
