package net.rim.device.apps.internal.explorer.file.menu;

import javax.microedition.io.file.FileConnection;
import net.rim.device.api.io.file.FileIOException;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.apps.internal.explorer.file.FileItemField;
import net.rim.device.apps.internal.explorer.file.resource.ExplorerResources;
import net.rim.device.internal.i18n.CommonResource;
import net.rim.device.internal.io.file.FileUtilities;

public final class DeleteMenuItem extends MenuItem {
   private FileItemField _file;
   private Screen _screenToClose;
   private boolean _success;

   public DeleteMenuItem(FileItemField file, Screen screenToClose) {
      super(CommonResource.getBundle(), 17, 591107, Integer.MAX_VALUE);
      this._file = file;
      this._screenToClose = screenToClose;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void run() {
      if (this._file.canDelete()) {
         FileConnection fc = null;
         boolean var27 = false /* VF: Semaphore variable */;

         label379: {
            try {
               try {
                  var27 = true;
                  fc = this._file.getFileConnection();
                  boolean ioe = false;
                  String var38 = null;
                  if (fc != null) {
                     if (fc.isDirectory() && fc.list("*", true).hasMoreElements()) {
                        var38 = ExplorerResources.getString(117);
                        ioe = true;
                     } else if (!fc.canWrite()) {
                        var38 = ExplorerResources.getString(118);
                        ioe = true;
                     }
                  }

                  boolean deleteFile;
                  if (ioe) {
                     deleteFile = Dialog.ask(3, var38, -1) == 4;
                  } else {
                     deleteFile = Dialog.ask(2) == 3;
                  }

                  if (deleteFile) {
                     if (this._screenToClose != null) {
                        this._screenToClose.close();
                     }

                     FileUtilities.delete(this._file.getURL(), true);
                     this._success = true;
                     var27 = false;
                  } else {
                     var27 = false;
                  }
                  break label379;
               } catch (Throwable var36) {
                  label375: {
                     String errorString = null;
                     if (ioe instanceof Object) {
                        errorString = ((FileIOException)ioe).getMessage();
                     }

                     if (errorString == null) {
                        errorString = ExplorerResources.getString(50);
                     }

                     Dialog.alert(errorString);
                     var27 = false;
                     break label375;
                  }
               }
            } finally {
               if (var27) {
                  if (fc != null) {
                     label337:
                     try {
                        fc.close();
                     } finally {
                        break label337;
                     }
                  }
               }
            }

            if (fc != null) {
               try {
                  fc.close();
                  return;
               } finally {
                  return;
               }
            }

            return;
         }

         if (fc != null) {
            try {
               fc.close();
               return;
            } finally {
               return;
            }
         }
      }
   }

   public final boolean getSuccess() {
      return this._success;
   }
}
