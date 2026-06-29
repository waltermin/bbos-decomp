package net.rim.device.apps.internal.options.items;

import net.rim.device.api.collection.WritableSet;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.internal.options.resources.OptionsResources;
import net.rim.device.internal.i18n.CommonResource;
import net.rim.device.internal.ui.component.SimpleInputDialog;

public final class CustomDictUnitEditor {
   private ContextObject _context;
   private CustomDictUnitModel _model;
   private WritableSet _customDict;
   private SimpleInputDialog _mainScreen;

   public CustomDictUnitEditor(WritableSet customDict) {
      this._customDict = customDict;
   }

   public final Object open(String initialString) {
      this._model = null;
      this._context = new ContextObject(0, 2, 6);
      return this.open(CommonResource.getString(13), initialString);
   }

   public final Object open(CustomDictUnitModel model) {
      this._model = model;
      this._context = new ContextObject(0, 2);
      String entry = "";
      if (this._model != null) {
         entry = (String)this._model.getEntry();
      }

      return this.open(CommonResource.getString(16), entry);
   }

   private final Object open(String appendToTitle, String initialString) {
      StringBuffer titleBuffer = new StringBuffer(OptionsResources.getString(1491));
      titleBuffer.append(':');
      titleBuffer.append(' ');
      titleBuffer.append(appendToTitle);
      this._mainScreen = new CustomDictUnitEditor$CustomDictEditorScreen(this, titleBuffer.toString(), initialString);
      this._mainScreen.show();
      return null;
   }
}
