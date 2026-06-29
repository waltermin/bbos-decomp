package net.rim.device.apps.internal.explorer.Media;

import net.rim.device.api.ui.UiApplication;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.internal.explorer.MediaLibrary.ContextInfo;
import net.rim.device.apps.internal.explorer.file.ExploreManager;
import net.rim.device.apps.internal.explorer.file.MediaFolderListener;
import net.rim.device.apps.internal.explorer.file.RootFileItemField;
import net.rim.device.apps.internal.explorer.file.resource.ExplorerResources;

final class PictureLibraryScreen extends MediaLibraryScreen {
   ContextObject _context;
   MediaFolderListener _folderListener;

   public PictureLibraryScreen(ContextInfo context) {
      super(context);
   }

   @Override
   protected final void initialize() {
      this._context = (ContextObject)(new Object());
      ContextObject.put(this._context, -1002650280265073678L, new Object(1, 2048));
      this._folderListener = new MediaFolderListener(super.ITEMS[0], 1, this._context);
   }

   @Override
   protected final String[] getItems() {
      return ExplorerResources.getStringArray(149);
   }

   @Override
   protected final String getTitle() {
      return ExplorerResources.getString(150);
   }

   @Override
   protected final void invoke() {
      super.invoke();
      int index = this.getSelectedIndex() - this.getNowPlaying();
      if (index >= 0) {
         RootFileItemField rootField = null;
         switch (index) {
            case -1:
               break;
            case 0:
            default:
               rootField = this._folderListener.getRootItem();
               break;
            case 1:
               rootField = new RootFileItemField("/store/samples/pictures/", super.ITEMS[index]);
         }

         if (rootField != null) {
            this._context.put(2765042845091913199L, rootField);
            PictureExploreScreen screen = new PictureExploreScreen(this._context);
            ExploreManager manager = screen.getExploreManager();
            this._folderListener.setManager(manager);
            UiApplication.getUiApplication().pushScreen(screen);
         }
      }
   }
}
