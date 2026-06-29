package net.rim.device.apps.internal.videorecorder;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.api.framework.file.AliasFileEntry;
import net.rim.device.apps.api.framework.file.FileSelectionFilter;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.internal.io.file.FileUtilities;
import net.rim.device.internal.io.file.RootRegister;

final class VideoRecorderMain$VideoCameraAliasFileEntry extends AliasFileEntry {
   private VideoRecorderMain$VideoCameraAliasFileEntry(String name, Verb verb, String imageName) {
   }

   @Override
   public final boolean isActiveInDirectory(String path, FileSelectionFilter filter) {
      RootRegister register = RootRegister.getInstance();
      if (!ShowVideoCameraApp.isVideoCameraInForegroundProcess()
         && !ITPolicy.getBoolean(47, 2, false)
         && register.isCardMounted()
         && !filter.isSelectWriteable()) {
         switch (filter.getMediaType()) {
            case 0:
               String videoDomainFolder = FileUtilities.getDefaultPath(3);
               if (!StringUtilities.regionMatches(path, true, 0, videoDomainFolder, 0, videoDomainFolder.length(), 1701707776)
                  && !StringUtilities.strEqualIgnoreCase(path, VideoRecorderOptions.getOptions().getDestinationFolder(), 1701707776)) {
                  return false;
               }
            case 3:
               FileConnection fileConn = null;

               try {
                  fileConn = (FileConnection)Connector.open(FileUtilities.makeFileURL(path));
                  return fileConn != null && fileConn.canWrite();
               } finally {
                  if (fileConn != null) {
                     try {
                        fileConn.close();
                        return false;
                     } finally {
                        return false;
                     }
                  }

                  return false;
               }
         }
      }

      return false;
   }

   VideoRecorderMain$VideoCameraAliasFileEntry(String x0, Verb x1, String x2, VideoRecorderMain$1 x3) {
      this(x0, x1, x2);
   }
}
