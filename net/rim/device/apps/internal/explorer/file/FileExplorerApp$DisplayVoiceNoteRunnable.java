package net.rim.device.apps.internal.explorer.file;

import net.rim.device.api.ui.Screen;
import net.rim.device.apps.internal.explorer.Media.TrackListScreen;
import net.rim.device.apps.internal.explorer.MediaLibrary.ContextInfo;

final class FileExplorerApp$DisplayVoiceNoteRunnable implements Runnable {
   private final FileExplorerApp this$0;

   FileExplorerApp$DisplayVoiceNoteRunnable(FileExplorerApp _1) {
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      Screen screen = this.this$0.getActiveScreen();
      TrackListScreen voiceNoteScreen = null;
      boolean isVoicenote = false;
      if (screen instanceof TrackListScreen) {
         voiceNoteScreen = (TrackListScreen)screen;
         isVoicenote = voiceNoteScreen.isVoicenote();
      }

      if (voiceNoteScreen == null || !isVoicenote) {
         ContextInfo contextInfo = new ContextInfo(256);
         this.this$0.closeAllScreens(new TrackListScreen(contextInfo));
      }

      this.this$0.requestForeground();
   }
}
