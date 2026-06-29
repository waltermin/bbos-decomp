package net.rim.device.apps.internal.explorer.file.verbs;

import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.explorer.Media.PictureExploreScreen;
import net.rim.device.apps.internal.explorer.Media.TrackListScreen;
import net.rim.device.apps.internal.explorer.MediaLibrary.ContextInfo;
import net.rim.device.apps.internal.explorer.file.ExploreManager;
import net.rim.device.apps.internal.explorer.file.ExploreScreen;
import net.rim.device.apps.internal.explorer.file.resource.ExplorerResources;

public final class FileExplorerVerb extends Verb {
   private ContextObject _context;

   public FileExplorerVerb(Object ctx) {
      super(0, ExplorerResources.getResourceBundleFamily(), 91);
      this._context = ContextObject.castOrCreate(ctx);
   }

   private static final Screen createViewMediaScreen(int mediaContextValue) {
      ContextInfo contextInfo = (ContextInfo)(new Object(mediaContextValue));
      contextInfo.setExternal(true);
      return new TrackListScreen(contextInfo);
   }

   @Override
   public final Object invoke(Object parameter) {
      ContextObject context = this._context;
      if (parameter instanceof Object) {
         this._context.put(2765042845091913199L, parameter);
      } else if (parameter instanceof Object) {
         context = (ContextObject)parameter;
         Integer invokeMenuItem = (Integer)ContextObject.get(context, -8073278814961745892L);
         if (invokeMenuItem != null) {
            new ExploreManager(null, context, false, 0);
            return null;
         }
      }

      Integer rootViewInteger = (Integer)ContextObject.get(context, 3941043584844673548L);
      int view;
      if (rootViewInteger != null) {
         view = rootViewInteger;
      } else {
         view = 0;
      }

      Screen screen;
      switch (view) {
         case 127:
            screen = new ExploreScreen(context);
            break;
         case 128:
         default:
            screen = new PictureExploreScreen(context);
            break;
         case 129:
            screen = createViewMediaScreen(256);
            break;
         case 130:
            screen = createViewMediaScreen(32);
            break;
         case 131:
            screen = createViewMediaScreen(1);
            break;
         case 132:
            screen = createViewMediaScreen(256);
      }

      UiApplication.getUiApplication().pushScreen(screen);
      return null;
   }
}
