package net.rim.device.apps.internal.explorer.Media;

import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.util.CharacterUtilities;
import net.rim.device.apps.api.ui.SystemEnabledMenu;
import net.rim.device.apps.internal.explorer.MediaLibrary.ContextInfo;
import net.rim.device.apps.internal.explorer.file.resource.ExplorerResources;

public final class RingtoneLibraryScreen extends MediaLibraryScreen {
   public RingtoneLibraryScreen(ContextInfo context) {
      super(context);
   }

   @Override
   protected final void initialize() {
   }

   @Override
   protected final String[] getItems() {
      return ExplorerResources.getStringArray(137);
   }

   @Override
   protected final String getTitle() {
      return ExplorerResources.getString(138);
   }

   @Override
   protected final boolean keyChar(char c, int status, int time) {
      if (super.keyChar(c, status, time)) {
         return true;
      }

      ContextInfo contextInfo = (ContextInfo)(new Object(super._context.getType() | 65));
      TrackListScreen screen = new TrackListScreen(contextInfo);
      StringBuffer buffer = (StringBuffer)(new Object(1));
      buffer.append(CharacterUtilities.toUpperCase(c));
      screen.setText(buffer.toString());
      UiApplication.getUiApplication().pushScreen(screen);
      return true;
   }

   @Override
   protected final void makeMenu(SystemEnabledMenu menu, int instance) {
      MediaUtilities.addDownloadTunesMenuItems(menu);
      super.makeMenu(menu, instance);
   }

   @Override
   protected final void invoke() {
      super.invoke();
      int index = this.getSelectedIndex() - this.getNowPlaying();
      if (index >= 0) {
         ContextInfo contextInfo = ContextInfo.createCopy(super._context);
         switch (index) {
            case -1:
               break;
            case 0:
            default:
               contextInfo.setType(65);
               break;
            case 1:
               contextInfo.setType(1089);
               break;
            case 2:
               contextInfo.setType(577);
         }

         UiApplication.getUiApplication().pushScreen(new TrackListScreen(contextInfo));
      }
   }
}
