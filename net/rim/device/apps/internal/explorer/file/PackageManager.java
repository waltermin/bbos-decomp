package net.rim.device.apps.internal.explorer.file;

import net.rim.device.apps.api.framework.registration.VerbFactoryRepository;
import net.rim.device.internal.io.file.FileSystem;
import net.rim.device.internal.io.file.FileUtilities;

public final class PackageManager {
   public static final void registerOnceOnSystemStart() {
      if (FileSystem.isFileSystemSupported(1)) {
         FileUtilities.ensureDirectoryExists("file:///store/home/user/videos/");
         FileUtilities.ensureDirectoryExists("file:///store/home/user/music/");
      }

      ExplorerVerbFactory fileSelectionVerbFactory = new ExplorerVerbFactory();
      VerbFactoryRepository.addFactory(-2843135760572915788L, fileSelectionVerbFactory);
   }
}
