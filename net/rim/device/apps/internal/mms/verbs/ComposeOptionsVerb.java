package net.rim.device.apps.internal.mms.verbs;

import java.util.Enumeration;
import java.util.Hashtable;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.mms.ui.ComposeOptionsScreen;
import net.rim.device.apps.internal.mms.ui.MMSEditorScreen;
import net.rim.device.internal.i18n.CommonResource;

public final class ComposeOptionsVerb extends Verb {
   private MMSEditorScreen _editorScreen;

   public ComposeOptionsVerb(MMSEditorScreen editorScreen) {
      super(16978176, CommonResource.getBundle(), 20);
      this._editorScreen = editorScreen;
   }

   @Override
   public final Object invoke(Object parameter) {
      int estimatedDataSize = this._editorScreen.getAttachmentDataProvider().getTotalAttachmentDataSize();
      ComposeOptionsScreen optionsScreen = new ComposeOptionsScreen(this._editorScreen, estimatedDataSize);
      UiApplication.getUiApplication().pushModalScreen(optionsScreen);
      Hashtable result = optionsScreen.getResult();
      if (result != null) {
         Enumeration names = result.keys();

         while (names.hasMoreElements()) {
            String name = (String)names.nextElement();
            this._editorScreen.setAttribute(name, (String)result.get(name));
         }
      }

      return null;
   }
}
