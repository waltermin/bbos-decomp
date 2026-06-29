package net.rim.device.apps.internal.messaging.search;

import java.util.Enumeration;
import net.rim.device.api.collection.ReadableList;
import net.rim.device.api.ui.Field;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.FieldProvider;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.framework.model.Recognizer;
import net.rim.device.apps.api.framework.registration.RIMModelFactory;
import net.rim.device.apps.api.framework.registration.VerbRepository;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.apps.api.ui.SystemEnabledMenu;
import net.rim.device.apps.api.utility.editor.EditorUsingRIMModelFactory;
import net.rim.device.internal.i18n.CommonResource;

public final class SearchEditScreen extends EditorUsingRIMModelFactory {
   private Verb _cancelSearchVerb;
   private Verb _saveSearchVerb;
   private Verb _newSearchVerb;
   private Verb _executeSearchVerb;
   private Verb _recallSearchVerb;
   private Verb _lastSearchVerb;
   private boolean _fromRibbon;
   private MessageSearchImpl _search;
   public static final int INDENT_AMOUNT;

   public SearchEditScreen(MessageSearchImpl search, boolean fromRibbon) {
      super(new Object(0, 56), CommonResources.getString(9136), search.getCollectionId(), 10000);
      this.setDefaultClose(false);
      this._search = search;
      this._saveSearchVerb = new SearchEditScreen$SaveSearchVerb(this);
      this._newSearchVerb = new SearchEditScreen$NewSearchVerb(this);
      this._executeSearchVerb = new SearchEditScreen$ExecuteSearchVerb(this);
      this._recallSearchVerb = new SearchEditScreen$RecallSearchVerb(this);
      this._lastSearchVerb = new SearchEditScreen$LastSearchVerb(this);
      this._cancelSearchVerb = new CancelVerb(true, fromRibbon, this, this._saveSearchVerb, CommonResource.getString(10003));
      this._fromRibbon = fromRibbon;
      this.setLeaveScreenVerb(this._cancelSearchVerb);
   }

   @Override
   public final void setModel(Object model) {
      ContextObject.put(super._context, 254, model);
      super.setModel(model);
   }

   static final FilterModel getDefaultSearch() {
      return new FilterModel();
   }

   @Override
   protected final void makeMenu(SystemEnabledMenu menu, int instance) {
      super.makeMenu(menu, instance);
      menu.add(this._saveSearchVerb);
      menu.add(this._executeSearchVerb);
      menu.add(this._newSearchVerb);
      menu.add(this._recallSearchVerb);
      menu.add(this._lastSearchVerb);
      VerbRepository vr = VerbRepository.getVerbRepository(-2516548103282563172L);
      if (vr != null) {
         menu.add(vr.getVerbs(null));
      }

      if (menu.getDefaultVerb() == null) {
         Field f = this.getLeafFieldWithFocus();
         if (f instanceof Object || f.isMuddy()) {
            menu.setDefault(this._executeSearchVerb);
         }
      }
   }

   private final ReadableList copyOfChanges() {
      FilterModel modelHolder = new FilterModel();
      Enumeration e = this.getFieldsFromEdit();

      while (e.hasMoreElements()) {
         Field f = (Field)e.nextElement();
         RIMModel m = (RIMModel)f.getCookie();
         if (f.isDirty()) {
            RIMModelFactory[] factories = this.getModelFactories();

            for (int i = 0; i < factories.length; i++) {
               Recognizer r = factories[i];
               if (r.recognize(m)) {
                  RIMModel newModel = (RIMModel)factories[i].createInstance(null);
                  FieldProvider fp = (FieldProvider)newModel;
                  if (fp.grabDataFromField(f, null)) {
                     modelHolder.add(newModel);
                  }
                  break;
               }
            }
         } else {
            modelHolder.add(m);
         }
      }

      return modelHolder;
   }

   @Override
   public final boolean onSave() {
      this._saveSearchVerb.invoke(null);
      return false;
   }
}
