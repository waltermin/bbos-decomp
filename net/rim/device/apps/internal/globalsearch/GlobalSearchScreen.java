package net.rim.device.apps.internal.globalsearch;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.BackdoorKeyListener;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.system.PersistentContentListener;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.AutoTextEditField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.component.ObjectChoiceField;
import net.rim.device.api.ui.text.TextFilter;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.search.GlobalSearchRegistry;
import net.rim.device.apps.api.search.SearchCriterion;
import net.rim.device.apps.api.search.Searchable;
import net.rim.device.apps.api.search.criteria.IdSearchModel;
import net.rim.device.apps.api.search.criteria.OrSearchCriterion;
import net.rim.device.apps.api.search.criteria.PhoneNumberSearchModel;
import net.rim.device.apps.api.search.criteria.TextSearchModel;
import net.rim.device.apps.api.ui.AppsMainScreen;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.apps.api.ui.SystemEnabledMenu;
import net.rim.device.apps.internal.messaging.search.criteria.NameSearchModel;

final class GlobalSearchScreen extends AppsMainScreen implements BackdoorKeyListener, PersistentContentListener {
   private AutoTextEditField _searchString;
   private AutoTextEditField _nameString;
   private EditField _idString;
   private SearchableCheckboxField[] _searchableCheckboxArray;
   private ObjectChoiceField _combineUsing;
   private ObjectChoiceField _searchForAll;
   private boolean _advancedMode;
   private boolean _developmentMode;
   private Application _application = Application.getApplication();
   private boolean _displayIcons;
   private Verb _searchVerb;
   private static ResourceBundle _rb = ResourceBundle.getBundle(1181520400124563677L, "net.rim.device.apps.internal.resource.GlobalSearch");

   public GlobalSearchScreen() {
      super(65536);
      this.setHelp("searching");
      this.initialize();
   }

   public GlobalSearchScreen(String text, String name) {
      this();
      this._searchString.setText(text);
      this._nameString.setText(name);
   }

   private final void initialize() {
      this.deleteAll();
      PersistentContent.addWeakListener(this);
      this.setTitle(StringUtilities.removeChars(_rb.getString(0), "̲"));
      this._searchString = (AutoTextEditField)(new Object(CommonResources.getString(9134), null, 1000000, 4503601774854144L));
      this.add(this._searchString);
      this._nameString = (AutoTextEditField)(new Object(CommonResources.getString(2002), null, 1000000, 4503601774854144L));
      this.add(this._nameString);
      if (this._advancedMode) {
         this._idString = (EditField)(new Object(_rb.getString(15), null, 1000000, 4503601774854144L));
         this._idString.setFilter((TextFilter)(new Object(1)));
         this.add(this._idString);
      }

      if (this._developmentMode) {
         this.add((Field)(new Object()));
         this._searchForAll = (ObjectChoiceField)(new Object(_rb.getString(12), new Object[]{_rb.getString(13), _rb.getString(14)}, 0));
         this.add(this._searchForAll);
         this._combineUsing = (ObjectChoiceField)(new Object(_rb.getString(9), new Object[]{_rb.getString(10), _rb.getString(11)}, 1));
         this.add(this._combineUsing);
      }

      this.add((Field)(new Object()));
      long[] ids = GlobalSearchRegistry.getRegistrationIds();
      int numIds = ids.length;
      int[] priorities = new int[numIds];
      this._searchableCheckboxArray = new SearchableCheckboxField[numIds];
      this._displayIcons = Graphics.isColor();

      for (int i = 0; i < numIds; i++) {
         long id = ids[i];
         Searchable s = GlobalSearchRegistry.getSearchable(id);
         priorities[i] = s.getPriority(id, null);
         EncodedImage icon = s.getIcon(id);
         this._displayIcons = this._displayIcons & (icon != null && !icon.isMonochrome());
         this._searchableCheckboxArray[i] = new SearchableCheckboxField(s, id, this._developmentMode);
      }

      Arrays.sort(priorities, 0, numIds, this._searchableCheckboxArray);

      for (int i = 0; i < numIds; i++) {
         this.add(this._searchableCheckboxArray[i]);
      }

      this._searchVerb = new GlobalSearchScreen$SearchVerb(this);
   }

   @Override
   public final boolean openProductionBackdoor(int backdoorCode) {
      if (backdoorCode == 1094997581) {
         this._advancedMode = !this._advancedMode;
         this.initialize();
         return true;
      } else {
         return super.openProductionBackdoor(backdoorCode);
      }
   }

   @Override
   public final boolean openDevelopmentBackdoor(int backdoorCode) {
      if (backdoorCode == 1145394765) {
         this._developmentMode = !this._developmentMode;
         this.initialize();
         return true;
      } else {
         return super.openDevelopmentBackdoor(backdoorCode);
      }
   }

   private final boolean doesStringContainNumber(String str) {
      for (int i = str.length() - 1; i >= 0; i--) {
         if (Character.isDigit(str.charAt(i))) {
            return true;
         }
      }

      return false;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final void performSearch() {
      Object ticket = PersistentContent.getTicket();
      if (ticket != null) {
         ticket.hashCode();
      }

      String searchString = this._searchString.getText().trim();
      String nameString = this._nameString.getText().trim();
      SearchCriterion[] orCriteria = new Object[0];
      if (searchString.length() != 0) {
         TextSearchModel textCriteria = (TextSearchModel)(new Object());
         textCriteria.setMatchAllPatterns(!this._developmentMode || this._searchForAll.getSelectedIndex() == 0);
         textCriteria.setValue(searchString);
         Arrays.add(orCriteria, textCriteria);
         if (this.doesStringContainNumber(searchString)) {
            PhoneNumberSearchModel numberCriteria = (PhoneNumberSearchModel)(new Object());
            numberCriteria.setValue(searchString);
            Arrays.add(orCriteria, numberCriteria);
         }
      }

      if (nameString.length() != 0) {
         NameSearchModel nameCriteria = (NameSearchModel)(new Object());
         nameCriteria.setNameString(nameString);
         Arrays.add(orCriteria, nameCriteria);
      }

      if (this._idString != null) {
         String idString = this._idString.getText().trim();
         if (idString.length() != 0) {
            IdSearchModel idCriteria = (IdSearchModel)(new Object());
            boolean var11 = false /* VF: Semaphore variable */;

            try {
               var11 = true;
               idCriteria.setValue(Integer.parseInt(idString));
               var11 = false;
            } finally {
               if (var11) {
                  Dialog.alert(_rb.getString(16));
                  return;
               }
            }

            Arrays.add(orCriteria, idCriteria);
         }
      }

      if (orCriteria.length == 0) {
         Dialog.alert(_rb.getString(2));
      } else {
         SearchCriterion[] sc;
         if (this._developmentMode && this._combineUsing.getSelectedIndex() == 0) {
            sc = orCriteria;
         } else {
            OrSearchCriterion osc = (OrSearchCriterion)(new Object());
            osc.setValue(orCriteria);
            sc = new Object[1];
            sc[0] = osc;
         }

         SearchableWrapper[] searchablesArray = new SearchableWrapper[0];
         boolean somethingSelectedForSearch = false;

         for (int i = this._searchableCheckboxArray.length - 1; i >= 0; i--) {
            SearchableWrapper s = this._searchableCheckboxArray[i].getSearchableWrapper();
            if (s != null) {
               Arrays.add(searchablesArray, s);
               somethingSelectedForSearch = true;
            }
         }

         if (!somethingSelectedForSearch) {
            Dialog.alert(_rb.getString(3));
         } else {
            GlobalSearchResultsScreen _resultsScreen = new GlobalSearchResultsScreen(this._displayIcons);
            UiApplication.getUiApplication().pushScreen(_resultsScreen);
            _resultsScreen.performSearch(searchablesArray, sc);
         }
      }
   }

   @Override
   protected final void makeMenu(SystemEnabledMenu menu, int instance) {
      super.makeMenu(menu, instance);
      menu.add(this._searchVerb, this.isDirty() ? 150 : Integer.MAX_VALUE);
      boolean checkboxSelected = false;
      boolean checkboxNotSelected = false;

      for (int i = this._searchableCheckboxArray.length - 1; i >= 0; i--) {
         if (this._searchableCheckboxArray[i].getChecked()) {
            checkboxSelected = true;
         } else {
            checkboxNotSelected = true;
         }
      }

      if (checkboxNotSelected) {
         menu.add(new GlobalSearchScreen$SelectVerb(this, true));
      }

      if (checkboxSelected) {
         menu.add(new GlobalSearchScreen$SelectVerb(this, false));
      }

      Verb defaultVerb = null;
      Field fieldWithFocus = this.getFieldWithFocus();
      if (fieldWithFocus == this._nameString) {
         Verb selectNameVerb = (Verb)(new Object(this._nameString));
         menu.add(selectNameVerb);
         if (!this._nameString.isMuddy()) {
            defaultVerb = selectNameVerb;
         }
      }

      if (defaultVerb == null && fieldWithFocus.isMuddy()) {
         defaultVerb = this._searchVerb;
      }

      menu.setDefault(defaultVerb);
   }

   private final void selectAll(boolean markSelected) {
      for (int i = this._searchableCheckboxArray.length - 1; i >= 0; i--) {
         this._searchableCheckboxArray[i].setChecked(markSelected);
      }
   }

   @Override
   public final boolean onClose() {
      this.setDirty(false);
      return super.onClose();
   }

   @Override
   public final void persistentContentStateChanged(int state) {
      if (state == 2
         && (this._searchString.getTextLength() != 0 || this._nameString.getTextLength() != 0 || this._idString != null && this._idString.getTextLength() != 0)
         )
       {
         this._application.invokeLater(new GlobalSearchScreen$1(this));
      }
   }

   @Override
   public final void persistentContentModeChanged(int generation) {
   }
}
