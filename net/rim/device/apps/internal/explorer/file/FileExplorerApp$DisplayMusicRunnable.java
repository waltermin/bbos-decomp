package net.rim.device.apps.internal.explorer.file;

import net.rim.device.apps.internal.explorer.Media.MusicLibraryScreen;

final class FileExplorerApp$DisplayMusicRunnable implements Runnable {
   private final FileExplorerApp this$0;

   FileExplorerApp$DisplayMusicRunnable(FileExplorerApp _1) {
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      if (!(this.this$0.getActiveScreen() instanceof MusicLibraryScreen)) {
         this.this$0.closeAllScreens(new MusicLibraryScreen(null));
      }

      this.this$0.requestForeground();
   }
}
