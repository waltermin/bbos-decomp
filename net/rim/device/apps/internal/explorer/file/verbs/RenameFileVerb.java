package net.rim.device.apps.internal.explorer.file.verbs;

import javax.microedition.io.file.FileConnection;
import net.rim.device.api.io.file.FileIOException;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.apps.api.framework.file.FileDialog;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.explorer.file.FileItemField;
import net.rim.device.apps.internal.explorer.file.resource.ExplorerResources;
import net.rim.device.cldc.io.utility.URIDecoder;
import net.rim.device.internal.io.file.FileUtilities;

public final class RenameFileVerb extends Verb {
   private Object _context;
   private Screen _screenToClose;

   public RenameFileVerb(Object obj, Screen screenToClose) {
      super(591109, ExplorerResources.getResourceBundleFamily(), 45);
      this._context = obj;
      this._screenToClose = screenToClose;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final Object invoke(Object obj) {
      FileItemField file = null;
      if (obj == null && this._context != null) {
         obj = this._context;
      }

      if (!(obj instanceof FileItemField)) {
         if (obj instanceof String) {
            file = new FileItemField((String)obj);
         } else if (obj instanceof ContextObject) {
            Object stringObject = ContextObject.get(obj, 2765042845091913199L);
            if (stringObject instanceof String) {
               file = new FileItemField((String)stringObject);
            }
         }
      } else {
         file = (FileItemField)obj;
      }

      if (file != null && file.canDelete()) {
         FileConnection fileConn = null;
         boolean var87 = false /* VF: Semaphore variable */;

         label1046: {
            label1027: {
               Object var110;
               label1028: {
                  label1029: {
                     label1030: {
                        try {
                           try {
                              var87 = true;
                              long e = 134217728;
                              if (file.isDirectory()) {
                                 e |= 32768;
                              }

                              FileDialog renameDialog = new FileDialog(file.getPath(), file.getName(), file.getMediaType(), ExplorerResources.getString(45), e);
                              int retVal = renameDialog.doModal();
                              String newFileURL = renameDialog.getURL();
                              if (file.isDirectory() && !FileUtilities.isDirectory(newFileURL)) {
                                 newFileURL = newFileURL + '/';
                              }

                              String newFilename = FileUtilities.getName(newFileURL);
                              String oldFileURL = file.getURL();
                              if (retVal == -1) {
                                 var87 = false;
                                 break label1027;
                              }

                              if (URIDecoder.decode(oldFileURL, "UTF-8", false).equals(URIDecoder.decode(newFileURL, "UTF-8", false))) {
                                 var87 = false;
                                 break label1027;
                              }

                              if (this._screenToClose != null) {
                                 this._screenToClose.close();
                              }

                              if (oldFileURL.length() == newFileURL.length() && oldFileURL.regionMatches(true, 0, newFileURL, 0, newFileURL.length())) {
                                 FileUtilities.rename(oldFileURL, newFilename);
                                 var110 = newFileURL;
                                 var87 = false;
                                 break label1028;
                              }

                              if (FileUtilities.checkFileExists(newFileURL)) {
                                 if (4 != Dialog.ask(3, ExplorerResources.getString(47))) {
                                    var110 = null;
                                    var87 = false;
                                    break label1029;
                                 }

                                 FileUtilities.delete(newFileURL, true);
                              } else if (!file.canWrite() && 4 != Dialog.ask(3, ExplorerResources.getString(124))) {
                                 var110 = null;
                                 var87 = false;
                                 break label1030;
                              }

                              if (file.isDirectory()) {
                                 FileUtilities.rename(file.getURL(), newFilename.substring(0, newFilename.length() - 1), true);
                              } else {
                                 FileUtilities.moveFile(file.getURL(), newFileURL, null, true);
                              }

                              var110 = newFileURL;
                              var87 = false;
                           } catch (Throwable var104) {
                              String errorString = null;
                              if (!(e instanceof FileIOException)) {
                                 errorString = ExplorerResources.getString(50);
                              } else {
                                 errorString = ((FileIOException)e).getMessage();
                              }

                              Dialog.alert(errorString);
                              var87 = false;
                              break label1046;
                           }
                        } finally {
                           if (var87) {
                              if (fileConn != null) {
                                 label956:
                                 try {
                                    fileConn.close();
                                 } finally {
                                    break label956;
                                 }
                              }
                           }
                        }

                        if (fileConn != null) {
                           try {
                              fileConn.close();
                           } finally {
                              return var110;
                           }
                        }

                        return var110;
                     }

                     if (fileConn != null) {
                        try {
                           fileConn.close();
                        } finally {
                           return var110;
                        }
                     }

                     return var110;
                  }

                  if (fileConn != null) {
                     try {
                        fileConn.close();
                     } finally {
                        return var110;
                     }
                  }

                  return var110;
               }

               if (fileConn != null) {
                  try {
                     fileConn.close();
                  } finally {
                     return var110;
                  }
               }

               return var110;
            }

            if (fileConn != null) {
               try {
                  fileConn.close();
                  return null;
               } finally {
                  ;
               }
            }

            return null;
         }

         if (fileConn != null) {
            try {
               fileConn.close();
               return null;
            } finally {
               ;
            }
         }
      } else {
         Dialog.alert(ExplorerResources.getString(48));
      }

      return null;
   }
}
