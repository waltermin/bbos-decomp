package net.rim.device.apps.internal.options.items;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import net.rim.device.api.io.IOUtilities;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.apps.api.framework.file.FileSelector;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.internal.io.file.FileUtilities;

class SoftTokensOptionsItem$InstallTokenVerb extends Verb {
   private final SoftTokensOptionsItem this$0;

   public SoftTokensOptionsItem$InstallTokenVerb(SoftTokensOptionsItem _1) {
      super(16986368, SoftTokensOptionsItem._rb.getFamily(), 0);
      this.this$0 = _1;
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public Object invoke(Object parameter) {
      if (this.this$0._fileSelector == null) {
         this.this$0._fileSelector = (FileSelector)(new Object("/SDCard/", 0, new SoftTokensOptionsItem$InstallTokenVerb$1(this)));
      }

      String name = this.this$0._fileSelector.selectFile("/SDCard/");
      if (name != null) {
         name = FileUtilities.makeFileURL(name);

         try {
            FileConnection fconn = (FileConnection)Connector.open(name);
            if (fconn.canRead()) {
               byte[] seedBytes = IOUtilities.streamToBytes(fconn.openInputStream());
               if (seedBytes != null) {
                  String seedString = (String)(new Object(seedBytes));
                  this.this$0.saveToken(seedString, "", true);
                  Dialog.inform(this.this$0.getString(8));
                  return null;
               }
            }
         } catch (Throwable var7) {
            Dialog.alert(ioe.getMessage());
            return null;
         }
      }

      return null;
   }
}
