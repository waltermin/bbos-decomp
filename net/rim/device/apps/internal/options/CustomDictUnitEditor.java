package net.rim.device.apps.internal.options;

import net.rim.device.api.system.Application;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.internal.i18n.CommonResource;
import net.rim.device.internal.ui.component.SimpleInputDialog;
import net.rim.tid.awt.im.InputContext;
import net.rim.tid.im.customWordRepository.ja.JapaneseCustomWord;

public final class CustomDictUnitEditor {
   private ContextObject _context;
   protected CustomDictUnitModel _model;
   private SimpleInputDialog _mainScreen;

   public final Object open(Object initialObject) {
      this._model = null;
      this._context = new ContextObject(0, 2, 6);
      return this.open(CommonResource.getString(13), initialObject);
   }

   public final Object open(CustomDictUnitModel model) {
      this._model = model;
      this._context = new ContextObject(0, 2);
      return this.open(CommonResource.getString(16), model);
   }

   private final Object open(String appendToTitle, Object initialObject) {
      StringBuffer titleBuffer = new StringBuffer(OptionsResources.getString(1491));
      titleBuffer.append(':');
      titleBuffer.append(' ');
      titleBuffer.append(appendToTitle);
      String entry = null;
      if (InputContext.getInstance().getActiveInputMethodID() == 16384) {
         entry = "";
      } else if (initialObject == null) {
         entry = "";
      } else if (!(initialObject instanceof String)) {
         if (initialObject instanceof CustomDictUnitModel) {
            Object obj = ((CustomDictUnitModel)initialObject).getEntry();
            if (!(obj instanceof String)) {
               if (obj instanceof JapaneseCustomWord) {
                  this._mainScreen = this.createMainScreen("", "");
                  if (this._mainScreen instanceof CustomDictUnitEditor$JapaneseCustomDictEditorScreen) {
                     ((CustomDictUnitEditor$JapaneseCustomDictEditorScreen)this._mainScreen).init((JapaneseCustomWord)obj);
                  }
               }
            } else {
               entry = (String)obj;
            }
         }
      } else {
         entry = (String)initialObject;
      }

      if (entry != null) {
         this._mainScreen = this.createMainScreen(titleBuffer.toString(), entry);
         if (!entry.equals("")) {
            Application.getApplication().invokeLaterInternal(new CustomDictUnitEditor$1(this), 1, false);
         }
      }

      if (this._mainScreen != null) {
         this._mainScreen.show();
      }

      return null;
   }

   private final SimpleInputDialog createMainScreen(String title, String initialString) {
      switch (CustomWordlistScreen.getScreenType()) {
         case 0:
            return null;
         case 1:
         default:
            return new CustomDictUnitEditor$EuropeanCustomDictEditorScreen(this, title, initialString);
         case 2:
            return new CustomDictUnitEditor$ChineseCustomDictEditorScreen(this, title, initialString);
         case 3:
            return new CustomDictUnitEditor$JapaneseCustomDictEditorScreen(this, title, initialString);
      }
   }
}
