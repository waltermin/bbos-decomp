package net.rim.device.apps.internal.addressbook.lookup;

import net.rim.device.api.collection.util.KeywordFilterList;
import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.CollectionListField;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.component.ListFieldCallback;
import net.rim.device.apps.api.addressbook.SelectionListener;
import net.rim.device.apps.api.addressbook.UseEntryVerb;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.PaintProvider;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.framework.model.VerbProvider;
import net.rim.device.apps.api.framework.verb.DefaultVerbProvider;
import net.rim.device.apps.api.framework.verb.LastUsedDefaultVerbProvider;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.ui.KeywordFilteredScreen;
import net.rim.device.apps.api.ui.SystemEnabledMenu;
import net.rim.device.apps.api.utility.editor.EditorUsingRIMModelFactory;
import net.rim.device.apps.internal.addressbook.resources.AddressBookResources;

public class SearchViewScreen extends KeywordFilteredScreen implements ListFieldCallback {
   private ContextObject _baseContext;
   private ContextObject _verbContext;
   private Request _request;
   private SearchViewScreen$SearchKeywordFilterCollectionListField _list;
   private UiApplication _app = UiApplication.getUiApplication();
   private ALPManager _manager = ALPConfiguration.getManager();
   private SelectionListener _selectionListener;

   public SearchViewScreen(Request r, KeywordFilterList filterList, Object context, SelectionListener selectionListener) {
      super(null, null, new SearchViewScreen$SearchKeywordFilterCollectionListField(filterList, null), false, null);
      this._baseContext = ContextObject.castOrCreate(context);
      this._verbContext = ContextObject.clone(this._baseContext);
      this._verbContext.setFlag(2, 3);
      this._verbContext.clearFlag(43);
      this._verbContext.setFlag(18);
      this._verbContext.setFlag(4);
      this._request = r;
      this._selectionListener = selectionListener;
      this.updateTitle();
      this._list = (SearchViewScreen$SearchKeywordFilterCollectionListField)this.getListField();
      this._list._searchViewScreen = this;
      this._list.setCallback(this);
      this._list.setSize(r.getIncludedMatches(), r._selectedItem);
   }

   private void updateTitle() {
      Request r = this._request;
      int included_matches = r.getIncludedMatches();
      int available_matches = r.getAvailableMatches();
      Object[] title_parms = new Object[]{r._search, new Integer(included_matches), null};
      int title_resId;
      if (included_matches < available_matches) {
         title_resId = 1720;
         title_parms[2] = new Integer(available_matches);
      } else if (included_matches == 1) {
         title_resId = 1724;
      } else {
         title_resId = 1719;
      }

      this.setBaseTitle(MessageFormat.format(AddressBookResources.getString(title_resId), title_parms));
   }

   @Override
   public void drawListRow(ListField listField, Graphics graphics, int index, int y, int width) {
      CollectionListField collectionListField = (CollectionListField)listField;
      Object element = collectionListField.getElementAt(index);
      if (element != null) {
         if (element instanceof PaintProvider) {
            ((PaintProvider)element).paint(graphics, 0, y, width, 100, this._verbContext);
            return;
         }
      } else if (index == this._list.getExtraRowCount()) {
         graphics.drawText(AddressBookResources.getString(305), 0, y, 4, width);
      }
   }

   @Override
   public int getPreferredWidth(ListField listField) {
      return this.getPreferredWidth();
   }

   @Override
   public Object get(ListField listField, int index) {
      try {
         return this._request.getAddress(index);
      } finally {
         ;
      }
   }

   @Override
   public int indexOfList(ListField listField, String prefix, int start) {
      return -1;
   }

   @Override
   protected boolean invokeAction(int action) {
      boolean handled = super.invokeAction(action);
      if (!handled) {
         switch (action) {
            case 1:
               this.getKeywordFilterList().waitForComplete();
               handled = this.invokeDefaultMenuItem(0);
         }
      }

      return handled;
   }

   @Override
   protected void makeMenu(SystemEnabledMenu menu, int instance) {
      super.makeMenu(menu, instance);
      if (instance == 0) {
         ContextObject context = this._verbContext;
         context.setFlag(116);
         context.put(113, this._request);
         Verb defaultVerb = null;
         Object element = this._list.getSelectedElement();
         Object modelUser = null;
         if (this._request != null) {
            modelUser = ContextObject.get(this._request.getContext(), -6581931217101110672L);
         }

         boolean fromEmailCompose = modelUser instanceof EditorUsingRIMModelFactory;
         menu.add(new SearchViewScreen$CancelScreenVerb(this, null));
         Verb continueVerb = null;
         if (element != null) {
            if (fromEmailCompose) {
               continueVerb = new SearchViewScreen$ContinueVerb(this);
            } else {
               continueVerb = this.getSimpleViewVerbInstance(413984, 1708);
            }

            menu.add(continueVerb);
            menu.add(this.getSimpleViewVerbInstance(414000, 1706));
            menu.add(this.getSimpleViewVerbInstance(414032, 1705));
            if (this._request != null && this._request.getIncludedMatches() > 1) {
               menu.add(this.getSimpleViewVerbInstance(414016, 1707));
            }
         }

         Verb createSearchVerb = this.getSimpleViewVerbInstance(413952, 204);
         menu.add(createSearchVerb);
         if (this._request != null && this._request.moreAvailable()) {
            menu.add(this.getSimpleViewVerbInstance(414048, 1721));
         }

         menu.add(new RequestVerb(this._request, 414080, 1704, true, null));
         if (element instanceof VerbProvider) {
            VerbProvider vp = (VerbProvider)element;
            Verb[] tmp = new Verb[0];
            context.setFlag(114);
            Verb tmpDefault = vp.getVerbs(context, tmp);
            context.clearFlag(114);
            menu.add(tmp);
            if (tmpDefault != null && defaultVerb == null) {
               defaultVerb = tmpDefault;
            }
         }

         if (this._selectionListener != null && this._selectionListener.canSelect(element)) {
            Verb[] verba = new Verb[0];
            defaultVerb = this._selectionListener.getVerbs(verba, element, context);
            menu.add(verba);
         }

         if (defaultVerb == null || fromEmailCompose) {
            if (continueVerb != null) {
               defaultVerb = continueVerb;
            } else {
               defaultVerb = createSearchVerb;
            }
         }

         menu.setDefault(defaultVerb);
         DefaultVerbProvider defaultVerbProvider = null;
         if (element instanceof RIMModel) {
            defaultVerbProvider = new LastUsedDefaultVerbProvider((RIMModel)element);
         }

         menu.coalesce(-3072555018635390988L, defaultVerbProvider);
      }
   }

   @Override
   protected boolean keyChar(char key, int status, int time) {
      Verb verb = null;
      if (key == 27) {
         String pattern = this.getSearchPattern();
         if (pattern != null && pattern.length() != 0) {
            this.setSearchPattern(null);
            return true;
         }

         verb = new SearchViewScreen$CancelScreenVerb(this, null);
      } else if (key == 127) {
         this.getKeywordFilterList().waitForComplete();
         verb = this.getSimpleViewVerbInstance(414032, 1705);
      } else if (key == '\n') {
         this.getKeywordFilterList().waitForComplete();
         this.invokeDefaultMenuItem(0);
      }

      if (verb != null) {
         this.invokeVerb(verb, this._baseContext);
         return true;
      } else {
         return super.keyChar(key, status, time);
      }
   }

   @Override
   protected Object invokeVerb(Verb verb, Object parameter) {
      int verbOrdering = verb.getOrdering();
      if (this._request.needsResolving()
         && this._request.getAvailableMatches() > 1
         && (verbOrdering >= 1245184 && verbOrdering <= 1284505 || verbOrdering >= 327936 && verbOrdering <= 330137)) {
         this._manager.resolveRequestItem(this._request, this._list.getSelectedElement());
      }

      return super.invokeVerb(verb, parameter);
   }

   @Override
   protected void verbInvoked(Verb verb, Object context, Object result) {
      super.verbInvoked(verb, context, result);
      if (!(verb instanceof UseEntryVerb) && this._selectionListener != null && this._selectionListener.hasSelectedObject()) {
         this.terminateScreen();
         this.terminateScreen();
      }
   }

   private void terminateScreen() {
      this._app.popScreen(this);
   }

   private SearchViewScreen$SimpleViewVerb getSimpleViewVerbInstance(int ordering, int resId) {
      return new SearchViewScreen$SimpleViewVerb(this, ordering, resId, null);
   }

   static Object access$500(SearchViewScreen x0) {
      return x0.getSelectedElement();
   }

   static Object access$800(SearchViewScreen x0) {
      return x0.getSelectedElement();
   }
}
