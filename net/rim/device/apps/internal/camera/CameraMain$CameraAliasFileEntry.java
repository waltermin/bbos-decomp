package net.rim.device.apps.internal.camera;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.api.framework.file.AliasFileEntry;
import net.rim.device.apps.api.framework.file.FileSelectionFilter;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.internal.io.file.FileUtilities;

final class CameraMain$CameraAliasFileEntry extends AliasFileEntry {
   private CameraMain$CameraAliasFileEntry(String name, Verb verb, String imageName) {
   }

   @Override
   public final boolean isActiveInDirectory(String path, FileSelectionFilter filter) {
      if (!ShowCameraApp.isCameraInForegroundProcess() && !ITPolicy.getBoolean(47, 1, false) && !filter.isSelectWriteable()) {
         switch (filter.getMediaType()) {
            case -1:
               break;
            case 0:
            default:
               String pictureDomainFolder = FileUtilities.getDefaultPath(1);
               if (!StringUtilities.regionMatches(path, true, 0, pictureDomainFolder, 0, pictureDomainFolder.length(), 1701707776)
                  && !StringUtilities.strEqualIgnoreCase(path, CameraOptions.getOptions().getDestinationFolder(), 1701707776)) {
                  return false;
               }
            case 1:
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

   CameraMain$CameraAliasFileEntry(String x0, Verb x1, String x2, CameraMain$1 x3) {
      this(x0, x1, x2);
   }
}
