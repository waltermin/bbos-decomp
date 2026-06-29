package net.rim.device.apps.internal.options.items;

import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.autotext.AutoText;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.internal.options.resources.OptionsResources;
import net.rim.device.internal.i18n.CommonResource;

public final class AutoTextUnitEditor {
   private ContextObject _context;
   private AutoTextUnitModel _model;
   private Object _editedAutoTextItem;
   private MainScreen _mainScreen;
   private static AutoText _autoTextEngine = AutoText.getAutoText();
   private static final String[] CASING_FIELD_CHOICES = new Object[]{OptionsResources.getString(300), OptionsResources.getString(301)};

   public final Object open(String initialReplacedString) {
      this._model = null;
      this._context = (ContextObject)(new Object(0, 2, 6));
      return this.open(CommonResource.getString(13), initialReplacedString, null, 0, 0);
   }

   public final Object open(AutoTextUnitModel model) {
      this._model = model;
      this._context = (ContextObject)(new Object(0, 2));
      Object entry = model.getEntry();
      return this.open(
         CommonResource.getString(16),
         _autoTextEngine.getReplacedString(entry),
         _autoTextEngine.getReplacementStringPattern(entry),
         _autoTextEngine.getReplacementCase(entry),
         _autoTextEngine.getLocaleCode(entry)
      );
   }

   private final Object open(
      String appendToTitle, String initialReplacedString, String initialReplacementStringPattern, int initialCasing, int initialLocaleCode
   ) {
      StringBuffer titleBuffer = (StringBuffer)(new Object(OptionsResources.getString(302)));
      titleBuffer.append(':');
      titleBuffer.append(' ');
      titleBuffer.append(appendToTitle);
      this._mainScreen = this.createMainScreen(titleBuffer.toString(), initialReplacedString, initialReplacementStringPattern, initialCasing, initialLocaleCode);
      UiApplication.getUiApplication().pushModalScreen(this._mainScreen);
      return this._editedAutoTextItem;
   }

   private final MainScreen createMainScreen(
      String title, String initialReplacedString, String initialReplacementStringPattern, int initialCasing, int initialLocaleCode
   ) {
      return new AutoTextUnitEditor$AutoTextEditorScreen(this, title, initialReplacedString, initialReplacementStringPattern, initialCasing, initialLocaleCode);
   }
}
