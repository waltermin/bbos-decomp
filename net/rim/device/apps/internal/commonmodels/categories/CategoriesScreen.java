package net.rim.device.apps.internal.commonmodels.categories;

import net.rim.device.api.collection.Collection;
import net.rim.device.api.collection.CollectionListener;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.CheckboxField;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.util.Arrays;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.options.SaveableMainScreenOptionsListItem;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.apps.api.utility.framework.VerbToMenu;
import net.rim.vm.Array;

final class CategoriesScreen extends SaveableMainScreenOptionsListItem implements CollectionListener, FieldChangeListener {
   private CheckboxField[] _checkboxes = new CheckboxField[0];
   private int[] _selectedCategoryIds;
   private byte _mode;
   private boolean _changesSaved;
   private UiApplication _uiApp;
   static final byte VIEW = 0;
   static final byte SELECT = 1;
   static final byte FILTER = 2;

   CategoriesScreen(int[] selectedCategoryIds, byte mode) {
      super(null);
      int titleId;
      switch (mode) {
         case 0:
            titleId = 9103;
            mode = 0;
            break;
         case 1:
         default:
            titleId = 9104;
            break;
         case 2:
            titleId = 9118;
      }

      this.setDisplayName(CommonResources.getString(titleId));
      this._mode = mode;
      this._selectedCategoryIds = selectedCategoryIds != null ? Arrays.copy(selectedCategoryIds) : new int[0];
      this._uiApp = UiApplication.getUiApplication();
   }

   @Override
   protected final void populateMainScreen(MainScreen mainScreen) {
      this.populateCategoryCheckboxes();
      this.addCategoriesToMainScreen(mainScreen);
      CategoryList.getInstance().addCollectionListener(this);
   }

   private final void populateCategoryCheckboxes() {
      CategoryList categoryList = CategoryList.getInstance();
      synchronized (categoryList) {
         int numCategories = categoryList.size();
         Array.resize(this._checkboxes, numCategories);

         for (int i = 0; i < numCategories; i++) {
            CheckboxField checkbox = this.createCategoryCheckbox((CategoryModel)categoryList.getAt(i));
            this._checkboxes[i] = checkbox;
         }
      }
   }

   private final CheckboxField createCategoryCheckbox(CategoryModel category) {
      CheckboxField checkbox = new CheckboxField(category.getName(), this.isInSelectedCategoryIds(category.getId()));
      if (this._mode == 2) {
         checkbox.setChangeListener(this);
      }

      checkbox.setEditable(this._mode != 0);
      checkbox.setCookie(category);
      return checkbox;
   }

   private final void addCategoriesToMainScreen(MainScreen mainScreen) {
      int numCategories = this._checkboxes.length;
      boolean focusSet = false;
      if (numCategories != 0) {
         for (int i = 0; i < numCategories; i++) {
            CheckboxField checkbox = this._checkboxes[i];
            mainScreen.add(checkbox);
            if (this._mode == 2 && !focusSet && checkbox.getChecked()) {
               checkbox.setFocus();
               focusSet = true;
            }
         }
      } else {
         mainScreen.add(new CategoriesScreen$NoCategoriesLabelField());
      }
   }

   private final boolean isInSelectedCategoryIds(int id) {
      for (int i = this._selectedCategoryIds.length - 1; i >= 0; i--) {
         if (id == this._selectedCategoryIds[i]) {
            return true;
         }
      }

      return false;
   }

   @Override
   protected final Verb getSaveVerb() {
      return this._mode == 1 && this._checkboxes.length > 0 ? super.getSaveVerb() : null;
   }

   @Override
   protected final boolean save() {
      CategoryList.getInstance().removeCollectionListener(this);
      if (this._mode != 0) {
         this.saveSelectedCategories();
      }

      return super.save();
   }

   private final void saveSelectedCategories() {
      int numSelectedCategories = 0;
      Array.resize(this._selectedCategoryIds, 0);

      for (int i = 0; i < this._checkboxes.length; i++) {
         CheckboxField checkbox = this._checkboxes[i];
         if (checkbox.getChecked()) {
            Array.resize(this._selectedCategoryIds, numSelectedCategories + 1);
            this._selectedCategoryIds[numSelectedCategories++] = ((CategoryModel)checkbox.getCookie()).getId();
         }
      }

      this._changesSaved = true;
   }

   @Override
   protected final boolean discard() {
      CategoryList.getInstance().removeCollectionListener(this);
      return super.discard();
   }

   @Override
   protected final void addScreenVerbs(VerbToMenu verbToMenu, int instance) {
      super.addScreenVerbs(verbToMenu, instance);
      if (this._mode != 0) {
         for (int i = 0; i < this._checkboxes.length; i++) {
            if (this._checkboxes[i].getChecked()) {
               verbToMenu.addVerb(new CategoriesScreen$ClearCategoryCheckboxesVerb(this));
               break;
            }
         }
      }

      verbToMenu.addVerb(new CategoriesScreen$AddCategoryVerb());
   }

   @Override
   protected final Verb addCurrentItemVerbs(VerbToMenu verbToMenu, int instance) {
      Verb defaultVerb = super.addCurrentItemVerbs(verbToMenu, instance);
      Field fieldWithFocus = super._mainScreen.getLeafFieldWithFocus();
      if (fieldWithFocus != null) {
         Object cookie = fieldWithFocus.getCookie();
         if (cookie instanceof CategoryModel) {
            CategoryModel category = (CategoryModel)cookie;
            verbToMenu.addVerb(new CategoriesScreen$DeleteCategoryVerb(category.getId()));
         }
      }

      return defaultVerb;
   }

   final boolean changesSaved() {
      return this._changesSaved;
   }

   @Override
   protected final void open() {
      this._changesSaved = false;
      super._mainScreen = this.createMainScreen();
      this._uiApp.pushModalScreen(super._mainScreen);
   }

   public final void getSelectedCategoryIds(int[] selectedCategoryIds) {
      if (selectedCategoryIds != null) {
         Array.resize(selectedCategoryIds, this._selectedCategoryIds.length);
         System.arraycopy(this._selectedCategoryIds, 0, selectedCategoryIds, 0, this._selectedCategoryIds.length);
      }
   }

   private final void clearCheckboxes(CheckboxField excludedCheckbox) {
      for (int i = 0; i < this._checkboxes.length; i++) {
         CheckboxField checkbox = this._checkboxes[i];
         if (checkbox != excludedCheckbox && checkbox.getChecked()) {
            checkbox.setChecked(false);
            checkbox.setDirty(true);
         }
      }
   }

   @Override
   public final boolean keyChar(char key, int status, int time) {
      if (key == 127 || Keypad.getAltedChar(key) == 127) {
         Field fieldWithFocus = super._mainScreen.getLeafFieldWithFocus();
         if (fieldWithFocus != null) {
            Object cookie = fieldWithFocus.getCookie();
            if (cookie instanceof CategoryModel) {
               CategoryModel category = (CategoryModel)cookie;
               new CategoriesScreen$DeleteCategoryVerb(category.getId()).invoke(null);
               return true;
            }
         }
      }

      return super.keyChar(key, status, time);
   }

   @Override
   public final void elementAdded(Collection collection, Object element) {
      CategoryList categoryList = CategoryList.getInstance();
      CategoryModel category = (CategoryModel)element;
      int categoryIndex = categoryList.getIndex(category);
      if (categoryIndex != -1) {
         boolean wasEmpty = this._checkboxes.length == 0;
         CheckboxField checkbox = this.createCategoryCheckbox(category);
         Arrays.insertAt(this._checkboxes, checkbox, categoryIndex);
         this._uiApp.invokeLater(new CategoriesScreen$1(this, wasEmpty, checkbox, categoryIndex));
      }
   }

   @Override
   public final void elementRemoved(Collection collection, Object element) {
      CheckboxField checkboxToRemove = null;

      for (int i = 0; i < this._checkboxes.length; i++) {
         if (this._checkboxes[i].getCookie() == element) {
            checkboxToRemove = this._checkboxes[i];
            Arrays.removeAt(this._checkboxes, i);
            break;
         }
      }

      if (checkboxToRemove != null) {
         if (this._mode == 2 && checkboxToRemove.getChecked()) {
            this.saveSelectedCategories();
         }

         CheckboxField finalCheckboxToRemove = checkboxToRemove;
         this._uiApp.invokeLater(new CategoriesScreen$2(this, finalCheckboxToRemove));
      }
   }

   @Override
   public final void elementUpdated(Collection collection, Object oldElement, Object newElement) {
   }

   @Override
   public final void reset(Collection collection) {
      this.populateCategoryCheckboxes();
      this._uiApp.invokeLater(new CategoriesScreen$3(this));
   }

   @Override
   public final void fieldChanged(Field field, int context) {
      if (context != Integer.MIN_VALUE) {
         this.clearCheckboxes((CheckboxField)field);
         this.save();
         this.getCloseVerb().invoke(null);
      }
   }

   static final MainScreen access$000(CategoriesScreen x0) {
      return x0._mainScreen;
   }

   static final MainScreen access$100(CategoriesScreen x0) {
      return x0._mainScreen;
   }

   static final MainScreen access$200(CategoriesScreen x0) {
      return x0._mainScreen;
   }

   static final MainScreen access$400(CategoriesScreen x0) {
      return x0._mainScreen;
   }

   static final MainScreen access$500(CategoriesScreen x0) {
      return x0._mainScreen;
   }

   static final MainScreen access$600(CategoriesScreen x0) {
      return x0._mainScreen;
   }

   static final Verb access$1000(CategoriesScreen x0) {
      return x0.getCloseVerb();
   }
}
