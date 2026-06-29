package net.rim.plazmic.internal.mediaengine.model.smil.v0_0.contentregistry.serviceprovider;

import java.io.InputStream;
import javax.microedition.io.HttpConnection;
import net.rim.device.api.util.StringUtilities;
import net.rim.plazmic.internal.mediaengine.model.smil.v0_0.ui.ActiveRichTextFieldWrapper;

public class PlainTextServiceProvider extends UIComponentServiceProvider {
   private static final int NUM_SERVICES = 3;

   public PlainTextServiceProvider() {
      super(3);
      super._fieldStyle = 9007199254740992L;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public void createServices(InputStream is, String type, HttpConnection conn) {
      byte[] data = this.bufferData(is).toByteArray();
      String encoding = conn.getRequestProperty("charset");
      if (encoding == null) {
         encoding = conn.getEncoding();
      }

      String text;
      if (encoding == null) {
         text = StringUtilities.decodeBOM(data, 0, data.length);
      } else {
         boolean var9 = false /* VF: Semaphore variable */;

         label26:
         try {
            var9 = true;
            text = (String)(new Object(data, encoding));
            var9 = false;
         } finally {
            if (var9) {
               System.out.println(((StringBuffer)(new Object("Unsupported charset encoding: "))).append(encoding).toString());
               text = StringUtilities.decodeBOM(data, 0, data.length);
               break label26;
            }
         }
      }

      this.createServices(text);
   }

   public void createServices(String text) {
      ActiveRichTextFieldWrapper uiComponent = new ActiveRichTextFieldWrapper(text, super._fieldStyle);
      this.setService("Model", text);
      this.setService("UI_Component", uiComponent);
   }
}
