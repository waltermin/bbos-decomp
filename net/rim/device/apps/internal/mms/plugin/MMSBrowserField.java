package net.rim.device.apps.internal.mms.plugin;

import java.io.DataInputStream;
import javax.microedition.io.InputConnection;
import net.rim.device.api.browser.field.BrowserContentBaseImpl;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.internal.mms.api.MMSMessageModel;
import net.rim.device.apps.internal.mms.model.MMSMessageModelBuilder;
import net.rim.device.apps.internal.mms.service.MMSProtocolDataUnit;
import net.rim.device.apps.internal.mms.ui.MMSViewField;

final class MMSBrowserField extends VerticalFieldManager {
   // $VF: Could not inline inconsistent finally blocks
   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public MMSBrowserField(BrowserContentBaseImpl browserContent, InputConnection inputConnection) {
      super(3460171888704094208L);
      browserContent.setTitle("MMS Content");
      DataInputStream inputstream = null;
      boolean var291 = false /* VF: Semaphore variable */;

      label1488: {
         try {
            label1480:
            try {
               var291 = true;
               inputstream = inputConnection.openDataInputStream();
               MMSProtocolDataUnit ioe = new MMSProtocolDataUnit(inputstream);
               MMSMessageModelBuilder builder = new MMSMessageModelBuilder();
               builder.inheritDefaultReporting();
               builder.update(ioe);
               MMSMessageModel message = builder.getResult();
               ContextObject context = new Object();
               ((ContextObject)context).setFlag(74);
               ((ContextObject)context).clearFlag(0);
               ((ContextObject)context).setFlag(9);
               this.add(new MMSViewField(message, (ContextObject)context));
               var291 = false;
               break label1488;
            } catch (Throwable var324) {
               System.out.println("IOException during browser view.");
               System.out.println(e.toString());
               var291 = false;
               break label1480;
            }
         } finally {
            if (var291) {
               boolean var256 = false /* VF: Semaphore variable */;

               label1450: {
                  label1449: {
                     try {
                        label1447:
                        try {
                           var256 = true;
                           if (inputConnection != null) {
                              inputConnection.close();
                              var256 = false;
                           } else {
                              var256 = false;
                           }
                           break label1449;
                        } catch (Throwable var316) {
                           System.out.println(ioe.toString());
                           var256 = false;
                           break label1447;
                        }
                     } finally {
                        if (var256) {
                           label1437:
                           try {
                              if (inputstream != null) {
                                 inputstream.close();
                              }
                           } catch (Throwable var313) {
                              System.out.println(ioe.toString());
                              break label1437;
                           }
                        }
                     }

                     try {
                        if (inputstream != null) {
                           inputstream.close();
                        }
                        break label1450;
                     } catch (Throwable var314) {
                        System.out.println(ioe.toString());
                        break label1450;
                     }
                  }

                  label1443:
                  try {
                     if (inputstream != null) {
                        inputstream.close();
                     }
                  } catch (Throwable var315) {
                     System.out.println(ioe.toString());
                     break label1443;
                  }
               }
            }
         }

         boolean var170 = false /* VF: Semaphore variable */;

         label1474: {
            try {
               label1472:
               try {
                  var170 = true;
                  if (inputConnection != null) {
                     inputConnection.close();
                     var170 = false;
                  } else {
                     var170 = false;
                  }
                  break label1474;
               } catch (Throwable var322) {
                  System.out.println(ioe.toString());
                  var170 = false;
                  break label1472;
               }
            } finally {
               if (var170) {
                  label1454:
                  try {
                     if (inputstream != null) {
                        inputstream.close();
                     }
                  } catch (Throwable var318) {
                     System.out.println(ioe.toString());
                     break label1454;
                  }
               }
            }

            try {
               if (inputstream != null) {
                  inputstream.close();
                  return;
               }

               return;
            } catch (Throwable var309) {
               System.out.println(ioe.toString());
               return;
            }
         }

         try {
            if (inputstream != null) {
               inputstream.close();
               return;
            }

            return;
         } catch (Throwable var310) {
            System.out.println(ioe.toString());
            return;
         }
      }

      boolean var84 = false /* VF: Semaphore variable */;

      label1466: {
         try {
            label1464:
            try {
               var84 = true;
               if (inputConnection != null) {
                  inputConnection.close();
                  var84 = false;
               } else {
                  var84 = false;
               }
               break label1466;
            } catch (Throwable var320) {
               System.out.println(ioe.toString());
               var84 = false;
               break label1464;
            }
         } finally {
            if (var84) {
               label1458:
               try {
                  if (inputstream != null) {
                     inputstream.close();
                  }
               } catch (Throwable var319) {
                  System.out.println(ioe.toString());
                  break label1458;
               }
            }
         }

         try {
            if (inputstream != null) {
               inputstream.close();
               return;
            }

            return;
         } catch (Throwable var311) {
            System.out.println(ioe.toString());
            return;
         }
      }

      try {
         if (inputstream != null) {
            inputstream.close();
            return;
         }
      } catch (Throwable var312) {
         System.out.println(ioe.toString());
         return;
      }
   }
}
