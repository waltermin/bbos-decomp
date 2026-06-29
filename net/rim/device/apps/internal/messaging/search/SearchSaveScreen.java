package net.rim.device.apps.internal.messaging.search;

import java.util.Enumeration;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Status;
import net.rim.device.api.util.FactoryUtil;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.FieldProvider;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.framework.model.Recognizer;
import net.rim.device.apps.api.framework.registration.RIMModelFactory;
import net.rim.device.apps.api.framework.registration.RIMModelFactoryRepository;
import net.rim.device.apps.api.framework.registration.RecognizerRepository;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.ui.SystemEnabledMenu;
import net.rim.device.apps.api.utility.editor.EditorUsingRIMModelFactory;
import net.rim.device.apps.internal.commonmodels.title.TitleModelImpl;
import net.rim.device.apps.internal.messaging.search.resources.SearchResources;

public final class SearchSaveScreen extends EditorUsingRIMModelFactory {
   private boolean _fromRibbon;
   private Verb _saveVerb;
   char _initialHotKey;
   private MessageSearchImpl _search;
   private Object _objectToReplace;
   private boolean _searchSaved;

   protected SearchSaveScreen(MessageSearchImpl search, boolean fromRibbon, Object objectToReplace) {
      super(new Object(0, 56), SearchResources.getString(9), 7820085525428081380L, 10000);
      this._search = search;
      this._objectToReplace = objectToReplace;
      this._fromRibbon = fromRibbon;
      this._saveVerb = new SearchSaveScreen$SaveVerb(this);
   }

   final Recognizer getTitleRecognizer() {
      return RecognizerRepository.getRecognizers(-4904857078378172834L);
   }

   @Override
   public final boolean isDataValid() {
      if (this.validateDataFromEdit()) {
         SearchCollection sc = (SearchCollection)this._search.getCollection();
         FilterModel matchingFilterModel = sc.find((FilterModel)this.getModel());
         if (matchingFilterModel != null && matchingFilterModel != this._objectToReplace) {
            Status.show(SearchResources.getString(44));
            return false;
         } else {
            return true;
         }
      } else {
         Status.show(SearchResources.getString(14));
         return false;
      }
   }

   @Override
   public final void setModel(Object model) {
      ContextObject.put(super._context, 254, model);
      super.setModel(model);
      FilterModel m = (FilterModel)model;
      if (m._titleModel == null) {
         TitleModelImpl titleModel = (TitleModelImpl)FactoryUtil.createInstance(-4904857078378172834L, null);
         m._titleModel = titleModel;
      }

      this.insertModel(m._titleModel);
      if (m._shortCutKey == null) {
         RIMModelFactory keyFactory = ShortCutKeyModelFactory.getInstance();
         ShortCutKeyModel keyModel = (ShortCutKeyModel)keyFactory.createInstance(null);
         m._shortCutKey = keyModel;
      }

      this.insertModel(m._shortCutKey);
      this._initialHotKey = m.getShortCutKey();
   }

   public final Object run() {
      this.setFocus(this.getTitleRecognizer());
      return super.go();
   }

   @Override
   protected final void makeMenu(SystemEnabledMenu menu, int instance) {
      super.makeMenu(menu, instance);
      menu.add(this._saveVerb);
      MenuItem defaultMenuItem = menu.getDefault();
      if (this.isMuddy()) {
         menu.setDefault(this._saveVerb);
      } else {
         if (this.isDirty()) {
            if (defaultMenuItem != null && defaultMenuItem.getPriority() < 150) {
               menu.setDefault(defaultMenuItem);
               return;
            }

            if (menu.getDefaultVerb() == null) {
               menu.setDefault(this._saveVerb);
            }
         }
      }
   }

   private static final void popToMsgList() {
      UiApplication ui_app = UiApplication.getUiApplication();

      while (ui_app.getScreenCount() > 1) {
         ui_app.popScreen(ui_app.getActiveScreen());
      }
   }

   @Override
   protected final boolean onSavePrompt() {
      boolean result = super.onSavePrompt();
      if (result) {
         result = !this._searchSaved;
      }

      return result;
   }

   @Override
   public final void save() {
      FilterModel newModel = null;
      char finalHotKey = 0;
      Enumeration e = this.getFieldsFromEdit();
      Recognizer titleRecognizer = this.getTitleRecognizer();
      Recognizer keyRecognizer = ShortCutKeyModelFactory.getInstance();
      this.getModel();
      newModel = new FilterModel();

      while (e.hasMoreElements()) {
         Field f = (Field)e.nextElement();
         RIMModel m = (RIMModel)f.getCookie();
         if (titleRecognizer.recognize(m)) {
            newModel._titleModel = (TitleModelImpl)m;
         } else if (keyRecognizer.recognize(m)) {
            FieldProvider fp = (FieldProvider)m;
            KeyChoiceField cf = (KeyChoiceField)f;
            int ix = cf.getSelectedIndex();
            if (ix > 0) {
               ShortCutKeyModel keyModel = (ShortCutKeyModel)m;
               newModel._shortCutKey = keyModel;
               finalHotKey = keyModel.getShortCutKey();
            } else {
               newModel._shortCutKey = null;
            }
         } else if (f.isDirty()) {
            RIMModelFactory[] factories = RIMModelFactoryRepository.getModelFactories(this._search.getCollectionId());

            for (int i = 0; i < factories.length; i++) {
               Recognizer r = factories[i];
               if (r.recognize(m)) {
                  RIMModel newCopy = (RIMModel)factories[i].createInstance(null);
                  FieldProvider fp = (FieldProvider)newCopy;
                  fp.grabDataFromField(f, null);
                  newModel.add(newCopy);
                  break;
               }
            }
         } else {
            newModel.add(m);
         }
      }

      if (this._objectToReplace != null) {
         newModel.setUID(((FilterModel)this._objectToReplace).getUID());
      }

      newModel = FilterModel.createGroup(newModel);
      if (newModel != null) {
         if (this._objectToReplace == null) {
            this._search.getCollection().add(newModel);
         } else {
            this._search.getCollection().update(this._objectToReplace, newModel);
         }

         this._searchSaved = true;
         this._search.returnHotKey(this._initialHotKey, true);
         this._search.takeHotKey(finalHotKey, new SearchVerb(newModel), true);
         popToMsgList();
         newModel.performSearch(this._search, false, this._fromRibbon, super._context);
      }
   }

   static final boolean access$000(SearchSaveScreen x0) {
      return x0.onSave();
   }
}
