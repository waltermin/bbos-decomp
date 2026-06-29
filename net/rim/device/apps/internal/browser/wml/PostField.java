package net.rim.device.apps.internal.browser.wml;

import java.util.Vector;
import net.rim.device.api.io.http.HttpHeaders;
import net.rim.device.apps.internal.browser.stack.FormData;
import net.rim.device.apps.internal.browser.stack.MultipartFormData;
import net.rim.device.apps.internal.browser.stack.URLEncodedFormData;
import net.rim.device.apps.internal.browser.util.RendererControl;

final class PostField {
   private Vector _names = (Vector)(new Object());
   private Vector _values = (Vector)(new Object());

   protected PostField() {
   }

   protected final void add(WMLVariable name, WMLVariable value) {
      if (name != null && value != null) {
         this._values.addElement(value);
         this._names.addElement(name);
      }
   }

   protected final FormData getFormData(int enctype, String characterEncoding, HttpHeaders offlineParameters, WMLContextManager wmlContextManager) {
      FormData formData = null;
      Object buffer = null;
      if (enctype == 1) {
         formData = new MultipartFormData(characterEncoding, true);
         buffer = new Object();
      } else {
         formData = new URLEncodedFormData(characterEncoding, true);
         buffer = new Object();
      }

      int numPostFields = this._names.size();

      for (int i = 0; i < numPostFields; i++) {
         String name = ((WMLVariable)this._names.elementAt(i)).getName(wmlContextManager);
         String value = ((WMLVariable)this._values.elementAt(i)).getName(wmlContextManager);
         if (RendererControl.isOfflineQueueHeader(name)) {
            offlineParameters.setProperty(name, value);
         } else {
            formData.append(buffer, name, value);
         }
      }

      formData.setData(buffer);
      return formData;
   }
}
