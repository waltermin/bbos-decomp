package net.rim.device.apps.internal.explorer.file.bluetooth;

import java.io.InputStream;
import java.io.OutputStream;
import javax.bluetooth.DataElement;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.ServiceRecord;
import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import javax.obex.HeaderSet;
import javax.obex.Operation;
import javax.obex.ServerRequestHandler;
import net.rim.device.api.io.MIMETypeAssociations;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.KeyListener;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.GaugeField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.container.PopupScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.apps.api.framework.file.FileDialog;
import net.rim.device.apps.internal.explorer.file.resource.ExplorerResources;
import net.rim.device.cldc.io.btgoep.BluetoothGOEPServerConnection;
import net.rim.device.internal.i18n.CommonResource;
import net.rim.device.internal.io.file.FileUtilities;

public final class ServiceConnection extends ServerRequestHandler implements Runnable, FieldChangeListener, KeyListener {
   private VerticalFieldManager _vfm = (VerticalFieldManager)(new Object(1152921504606846976L));
   private PopupScreen _popupScreen = (PopupScreen)(new Object(this._vfm));
   private GaugeField _gaugeField = (GaugeField)(new Object("", 0, 100, 0, 4));
   private LabelField _gaugePlaceholder;
   private LabelField _labelField = (LabelField)(new Object(ExplorerResources.getString(20), 12884901952L));
   private ButtonField _cancelField = (ButtonField)(new Object(CommonResource.getString(10044), 12884901888L));
   private BluetoothGOEPServerConnection _notifier;
   private SetProgressRunnable _setProgressRunnable;
   private SetStatusRunnable _statusRunnable;
   private boolean _stopped;
   private static final int PACKET_SIZE;

   public ServiceConnection() {
      this._cancelField.setChangeListener(this);
      this._statusRunnable = new SetStatusRunnable(this._labelField);
      this._setProgressRunnable = new SetProgressRunnable(this._gaugeField);
      this._gaugePlaceholder = (LabelField)(new Object(""));
      this._vfm.add((Field)(new Object(ExplorerResources.getString(31), 12884901888L)));
      this._vfm.add((Field)(new Object("")));
      this._vfm.add(this._labelField);
      this._vfm.add(this._gaugePlaceholder);
      this._vfm.add(this._cancelField);
      this._popupScreen.addKeyListener(this);
   }

   @Override
   public final void fieldChanged(Field field, int context) {
      if (field == this._cancelField) {
         this.stop();
      }
   }

   private final void stop() {
      if (!this._stopped) {
         this._stopped = true;
         this._setProgressRunnable.setProgress(100);
         this._statusRunnable.setStatus(ExplorerResources.getString(26));
         this.dismiss();
      }
   }

   @Override
   public final boolean keyChar(char key, int status, int time) {
      if (key == 27) {
         this.stop();
         return true;
      } else {
         return false;
      }
   }

   @Override
   public final boolean keyDown(int keycode, int time) {
      return false;
   }

   @Override
   public final boolean keyUp(int keycode, int time) {
      return false;
   }

   @Override
   public final boolean keyRepeat(int keycode, int time) {
      return false;
   }

   @Override
   public final boolean keyStatus(int keycode, int time) {
      return false;
   }

   private final void dismiss() {
      Application.getApplication().invokeLater(new ServiceConnection$1(this));
      Application.getApplication().invokeLater(new ServiceConnection$2(this), 2000, false);
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void run() {
      boolean var6 = false /* VF: Semaphore variable */;

      label28:
      try {
         var6 = true;
         Application.getApplication().invokeLater(new ServiceConnection$3(this));
         this._notifier = (BluetoothGOEPServerConnection)Connector.open("btgoep://localhost:1105;name=File Transfer Agent");
         LocalDevice ex = LocalDevice.getLocalDevice();
         ServiceRecord serviceRecord = ex.getRecord(this._notifier);
         DataElement element = new Object(48);
         DataElement element1 = new Object(48);
         ((DataElement)element1).addElement((DataElement)(new Object(24, new Object("1105", true))));
         ((DataElement)element1).addElement((DataElement)(new Object(9, 256)));
         ((DataElement)element).addElement((DataElement)element1);
         serviceRecord.setAttributeValue(9, (DataElement)element);
         element = new Object(48);
         ((DataElement)element).addElement((DataElement)(new Object(8, 255)));
         serviceRecord.setAttributeValue(771, (DataElement)element);
         this._statusRunnable.setStatus(ExplorerResources.getString(27));
         if (!this._stopped) {
            this._notifier.acceptAndOpen(this);
            return;
         }

         var6 = false;
      } finally {
         if (var6) {
            if (!this._stopped) {
               this._statusRunnable.setStatus(ExplorerResources.getString(28));
               this.dismiss();
            }
            break label28;
         }
      }
   }

   @Override
   public final int onConnect(HeaderSet request, HeaderSet reply) {
      this._statusRunnable.setStatus(ExplorerResources.getString(29));
      Application.getApplication().invokeAndWait(new ServiceConnection$4(this));
      return super.onConnect(request, reply);
   }

   @Override
   public final void onDisconnect(HeaderSet request, HeaderSet reply) {
      this._stopped = true;
      this.dismiss();
      super.onDisconnect(request, reply);
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final int onPut(Operation op) {
      InputStream btIn = null;
      OutputStream fileOut = null;
      FileConnection fc = null;
      boolean success = false;
      boolean var1212 = false /* VF: Semaphore variable */;
      boolean var1249 = false /* VF: Semaphore variable */;

      short contentLength;
      label9412: {
         short offset;
         label9413: {
            label9414: {
               int bytesRead;
               label9415: {
                  short buffer;
                  label9416: {
                     short fileSize;
                     label9417: {
                        short defaultPath;
                        try {
                           try {
                              var1249 = true;
                              var1212 = true;
                              this._statusRunnable.setStatus(ExplorerResources.getString(30));
                              HeaderSet ex = op.getReceivedHeaders();
                              if (ex == null) {
                                 contentLength = 192;
                                 var1212 = false;
                                 var1249 = false;
                                 break label9414;
                              }

                              Long contentLengthx = (Long)ex.getHeader(195);
                              String name = (String)ex.getHeader(1);
                              if (contentLengthx != null && name != null) {
                                 String defaultPathx = FileUtilities.getDefaultPathForMIMEType(MIMETypeAssociations.getMIMEType(name));
                                 FileDialog fd = (FileDialog)(new Object(
                                    defaultPathx, name, MIMETypeAssociations.getMediaType(name), ExplorerResources.getString(93), 134217728
                                 ));
                                 fd.setPromptForOverwrite(true);
                                 Application app = Application.getApplication();
                                 int invokeLaterId = app.invokeLater(new ServiceConnection$5(this, fd), 1000, true);
                                 boolean var1323 = false /* VF: Semaphore variable */;

                                 try {
                                    var1323 = true;
                                    app.invokeAndWait(new ServiceConnection$6(this, fd));
                                    var1323 = false;
                                 } finally {
                                    if (var1323) {
                                       app.cancelInvokeLater(invokeLaterId);
                                    }
                                 }

                                 app.cancelInvokeLater(invokeLaterId);
                                 if (this._stopped) {
                                    throw new Object();
                                 }

                                 if (fd.getSelectedValue() == 0) {
                                    String url = fd.getURL();
                                    fc = (FileConnection)Connector.open(url);
                                    long fileSizex = contentLengthx;
                                    if (fileSizex >= 0 && fileSizex <= 10485760) {
                                       boolean var1286 = false /* VF: Semaphore variable */;

                                       try {
                                          var1286 = true;
                                          FileUtilities.checkSpaceAvailable(fileSizex, fc);
                                          var1286 = false;
                                       } finally {
                                          if (var1286) {
                                             offset = 205;
                                             var1212 = false;
                                             var1249 = false;
                                             break label9413;
                                          }
                                       }

                                       if (fc.exists()) {
                                          fc.delete();
                                       }

                                       fc.create();
                                       btIn = op.openInputStream();
                                       fileOut = fc.openOutputStream();
                                       byte[] bufferx = new byte[2048];
                                       long offsetx = 0;

                                       while (offsetx < fileSizex) {
                                          if (this._stopped) {
                                             throw new Object();
                                          }

                                          bytesRead = btIn.read(bufferx);
                                          if (bytesRead == -1) {
                                             throw new Object();
                                          }

                                          fileOut.write(bufferx, 0, bytesRead);
                                          this._setProgressRunnable.setProgress((int)(offsetx * 100 / fileSizex));
                                          offsetx += bytesRead;
                                       }

                                       success = true;
                                       bytesRead = 160;
                                       var1212 = false;
                                       var1249 = false;
                                       break label9415;
                                    }

                                    buffer = 205;
                                    var1212 = false;
                                    var1249 = false;
                                    break label9416;
                                 }

                                 this._stopped = true;
                                 fileSize = 195;
                                 var1212 = false;
                                 var1249 = false;
                                 break label9417;
                              }

                              defaultPath = 192;
                              var1212 = false;
                              var1249 = false;
                           } finally {
                              if (var1249) {
                                 contentLength = 192;
                                 var1212 = false;
                                 break label9412;
                              }
                           }
                        } finally {
                           if (var1212) {
                              this._setProgressRunnable.setProgress(100);
                              int resource;
                              if (this._stopped) {
                                 resource = 26;
                              } else if (success) {
                                 resource = 25;
                              } else {
                                 resource = 24;
                              }

                              this._statusRunnable.setStatus(ExplorerResources.getString(resource));
                              if (btIn != null) {
                                 label9234:
                                 try {
                                    btIn.close();
                                 } finally {
                                    break label9234;
                                 }
                              }

                              if (fileOut != null) {
                                 label9230:
                                 try {
                                    fileOut.close();
                                 } finally {
                                    break label9230;
                                 }
                              }

                              if (!success && fc != null) {
                                 label9225:
                                 try {
                                    fc.delete();
                                 } finally {
                                    break label9225;
                                 }
                              }

                              if (fc != null) {
                                 label9221:
                                 try {
                                    fc.close();
                                 } finally {
                                    break label9221;
                                 }
                              }

                              if (!success) {
                                 this.dismiss();
                              }
                           }
                        }

                        this._setProgressRunnable.setProgress(100);
                        int resource;
                        if (this._stopped) {
                           resource = 26;
                        } else if (success) {
                           resource = 25;
                        } else {
                           resource = 24;
                        }

                        this._statusRunnable.setStatus(ExplorerResources.getString(resource));
                        if (btIn != null) {
                           label9369:
                           try {
                              btIn.close();
                           } finally {
                              break label9369;
                           }
                        }

                        if (fileOut != null) {
                           label9365:
                           try {
                              fileOut.close();
                           } finally {
                              break label9365;
                           }
                        }

                        if (!success && fc != null) {
                           label9360:
                           try {
                              fc.delete();
                           } finally {
                              break label9360;
                           }
                        }

                        if (fc != null) {
                           label9356:
                           try {
                              fc.close();
                           } finally {
                              break label9356;
                           }
                        }

                        if (!success) {
                           this.dismiss();
                        }

                        return defaultPath;
                     }

                     this._setProgressRunnable.setProgress(100);
                     int resource;
                     if (this._stopped) {
                        resource = 26;
                     } else if (success) {
                        resource = 25;
                     } else {
                        resource = 24;
                     }

                     this._statusRunnable.setStatus(ExplorerResources.getString(resource));
                     if (btIn != null) {
                        label9351:
                        try {
                           btIn.close();
                        } finally {
                           break label9351;
                        }
                     }

                     if (fileOut != null) {
                        label9347:
                        try {
                           fileOut.close();
                        } finally {
                           break label9347;
                        }
                     }

                     if (!success && fc != null) {
                        label9342:
                        try {
                           fc.delete();
                        } finally {
                           break label9342;
                        }
                     }

                     if (fc != null) {
                        label9338:
                        try {
                           fc.close();
                        } finally {
                           break label9338;
                        }
                     }

                     if (!success) {
                        this.dismiss();
                     }

                     return fileSize;
                  }

                  this._setProgressRunnable.setProgress(100);
                  int resource;
                  if (this._stopped) {
                     resource = 26;
                  } else if (success) {
                     resource = 25;
                  } else {
                     resource = 24;
                  }

                  this._statusRunnable.setStatus(ExplorerResources.getString(resource));
                  if (btIn != null) {
                     label9333:
                     try {
                        btIn.close();
                     } finally {
                        break label9333;
                     }
                  }

                  if (fileOut != null) {
                     label9329:
                     try {
                        fileOut.close();
                     } finally {
                        break label9329;
                     }
                  }

                  if (!success && fc != null) {
                     label9324:
                     try {
                        fc.delete();
                     } finally {
                        break label9324;
                     }
                  }

                  if (fc != null) {
                     label9320:
                     try {
                        fc.close();
                     } finally {
                        break label9320;
                     }
                  }

                  if (!success) {
                     this.dismiss();
                  }

                  return buffer;
               }

               this._setProgressRunnable.setProgress(100);
               int resource;
               if (this._stopped) {
                  resource = 26;
               } else if (success) {
                  resource = 25;
               } else {
                  resource = 24;
               }

               this._statusRunnable.setStatus(ExplorerResources.getString(resource));
               if (btIn != null) {
                  label9309:
                  try {
                     btIn.close();
                  } finally {
                     break label9309;
                  }
               }

               if (fileOut != null) {
                  label9305:
                  try {
                     fileOut.close();
                  } finally {
                     break label9305;
                  }
               }

               if (!success && fc != null) {
                  label9300:
                  try {
                     fc.delete();
                  } finally {
                     break label9300;
                  }
               }

               if (fc != null) {
                  label9296:
                  try {
                     fc.close();
                  } finally {
                     break label9296;
                  }
               }

               if (!success) {
                  this.dismiss();
               }

               return bytesRead;
            }

            this._setProgressRunnable.setProgress(100);
            int resource;
            if (this._stopped) {
               resource = 26;
            } else if (success) {
               resource = 25;
            } else {
               resource = 24;
            }

            this._statusRunnable.setStatus(ExplorerResources.getString(resource));
            if (btIn != null) {
               label9291:
               try {
                  btIn.close();
               } finally {
                  break label9291;
               }
            }

            if (fileOut != null) {
               label9287:
               try {
                  fileOut.close();
               } finally {
                  break label9287;
               }
            }

            if (!success && fc != null) {
               label9282:
               try {
                  fc.delete();
               } finally {
                  break label9282;
               }
            }

            if (fc != null) {
               label9278:
               try {
                  fc.close();
               } finally {
                  break label9278;
               }
            }

            if (!success) {
               this.dismiss();
            }

            return contentLength;
         }

         this._setProgressRunnable.setProgress(100);
         int resource;
         if (this._stopped) {
            resource = 26;
         } else if (success) {
            resource = 25;
         } else {
            resource = 24;
         }

         this._statusRunnable.setStatus(ExplorerResources.getString(resource));
         if (btIn != null) {
            label9273:
            try {
               btIn.close();
            } finally {
               break label9273;
            }
         }

         if (fileOut != null) {
            label9269:
            try {
               fileOut.close();
            } finally {
               break label9269;
            }
         }

         if (!success && fc != null) {
            label9264:
            try {
               fc.delete();
            } finally {
               break label9264;
            }
         }

         if (fc != null) {
            label9260:
            try {
               fc.close();
            } finally {
               break label9260;
            }
         }

         if (!success) {
            this.dismiss();
         }

         return offset;
      }

      this._setProgressRunnable.setProgress(100);
      int resource;
      if (this._stopped) {
         resource = 26;
      } else if (success) {
         resource = 25;
      } else {
         resource = 24;
      }

      this._statusRunnable.setStatus(ExplorerResources.getString(resource));
      if (btIn != null) {
         label9255:
         try {
            btIn.close();
         } finally {
            break label9255;
         }
      }

      if (fileOut != null) {
         label9251:
         try {
            fileOut.close();
         } finally {
            break label9251;
         }
      }

      if (!success && fc != null) {
         label9246:
         try {
            fc.delete();
         } finally {
            break label9246;
         }
      }

      if (fc != null) {
         label9242:
         try {
            fc.close();
         } finally {
            break label9242;
         }
      }

      if (!success) {
         this.dismiss();
      }

      return contentLength;
   }
}
