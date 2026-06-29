package net.rim.device.apps.internal.addressbook.addresscard;

import net.rim.device.api.i18n.Locale;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.component.Status;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.ui.theme.Tag;
import net.rim.device.apps.api.addressbook.AddressBookServices;
import net.rim.device.apps.api.addressbook.AddressCardModel;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.ContextObjectWR;
import net.rim.device.apps.api.framework.model.Recognizer;
import net.rim.device.apps.api.framework.profiles.TuneChoiceField;
import net.rim.device.apps.api.framework.profiles.TuneManager;
import net.rim.device.apps.api.framework.registration.VerbRepository;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.ui.SystemEnabledMenu;
import net.rim.device.apps.api.utility.editor.EditorUsingRIMModelFactory;
import net.rim.device.apps.internal.addressbook.BlackBerryAddressBook;
import net.rim.device.apps.internal.addressbook.resources.AddressBookResources;
import net.rim.device.apps.internal.commonmodels.categories.CategoriesModel;
import net.rim.device.apps.internal.commonmodels.categories.DisplayCategoriesForFieldVerb;
import net.rim.device.apps.internal.profiles.Overrides;

final class EditAddressCardScreen extends EditorUsingRIMModelFactory {
   private Verb _saveVerb;
   private Verb _storeAction;
   private AddressCardModel _originalModel;
   private Field _categoriesField;
   private TuneChoiceField _customTuneField;
   private boolean _hasTune;
   private static ContextObjectWR _editContextWR = new ContextObjectWR(11, 0);

   EditAddressCardScreen(Verb storeAction, String titleString) {
      super(_editContextWR.getContextObject(), titleString, -5785746452676094833L, -1);
      this._storeAction = storeAction;
      this._saveVerb = new EditAddressCardScreen$SaveAddressCardVerb(this);
      ContextObject.put(super._context, 1387359264132630355L, Locale.getDefaultForSystem());
      ContextObject.put(super._context, 244, "contacts");
      ContextObject.setFlag(super._context, 128);
   }

   final void setAddressCardModel(Object addressCard, Object compressedAddressCard, boolean isExisting) {
      ContextObject context = this.getContext();
      if (isExisting) {
         this._originalModel = (AddressCardModel)compressedAddressCard;
         context.put(-4055106280780392421L, compressedAddressCard);
      } else {
         context.remove(-4055106280780392421L);
      }

      super.setModel(addressCard);
      this.getContext().put(3696141428889703675L, addressCard);
      this._categoriesField = this.findField(new EditAddressCardScreen$CategoriesModelRecognizer(null));
      int uid = ((AddressCardModel)addressCard).getUID();
      String customTuneName = Overrides.getInstance().getCustomTune(uid);
      if (customTuneName != null) {
         this._hasTune = true;
         this._customTuneField = TuneManager.getTuneManager().getTuneChoiceField(AddressBookResources.getString(1734), customTuneName, null, true);
         this.add(this._customTuneField);
      }
   }

   @Override
   public final void onUiEngineAttached(boolean attached) {
      super.onUiEngineAttached(attached);
      if (attached) {
         this.setFocus((Recognizer)ApplicationRegistry.getApplicationRegistry().waitFor(5149066071290992769L));
      }
   }

   @Override
   protected final boolean handleKeyChar(char key, int status, int time) {
      return key == '\n' ? false : super.handleKeyChar(key, status, time);
   }

   @Override
   protected final boolean handleSendKey() {
      return true;
   }

   @Override
   protected final Manager createManagerForField(Field field, int order) {
      Manager manager = null;
      if (ContextObject.getFlag(super._context, 128)) {
         if (order >= 1100 && order < 2300) {
            manager = new VerticalFieldManager();
            manager.setTag(Tag.create("addressbook-block-name-edit"));
         }

         if (order >= 2400 && order < 3200) {
            manager = new VerticalFieldManager();
            manager.setTag(Tag.create("addressbook-communication-edit"));
         }

         if (order >= 3500 && order < 3600) {
            manager = new VerticalFieldManager();
            manager.setTag(Tag.create("addressbook-communication-edit"));
         }

         if (order >= 4300) {
            manager = new VerticalFieldManager();
            manager.setTag(Tag.create("addressbook-other-edit"));
         }
      }

      return manager;
   }

   @Override
   protected final int getOrderForManagerForField(Field field, int order) {
      if (ContextObject.getFlag(super._context, 128)) {
         if (order >= 1100 && order < 2300) {
            return 1100;
         }

         if (order >= 2400 && order < 3200) {
            return 2400;
         }

         if (order >= 3500 && order < 3600) {
            return 3500;
         }

         if (order >= 4300) {
            return 4300;
         }
      }

      return -1;
   }

   @Override
   protected final void makeMenu(SystemEnabledMenu menu, int instance) {
      if (instance != 65537) {
         super.makeMenu(menu, instance);
         if (instance == 0 && this._categoriesField != null && !(this.getModelFieldWithFocus().getCookie() instanceof CategoriesModel)) {
            menu.add(new DisplayCategoriesForFieldVerb(this._categoriesField));
         }

         menu.add(this._saveVerb);
         VerbRepository verbRepository = VerbRepository.getVerbRepository(-1011653757168863700L);
         menu.add(verbRepository.getVerbs(null));
         VerticalFieldManager vfm = (VerticalFieldManager)this.findField(new DisplayPictureModelFactory());
         if (((AddressBitmapField)vfm.getField(1)).getImage() == null) {
            DisplayPictureVerb dpv = new DisplayPictureVerb(0, 16864581);
            menu.add(dpv);
            if (this.getLeafFieldWithFocus() instanceof AddressBitmapField) {
               menu.setDefault(dpv);
            }
         }

         if (this._customTuneField == null) {
            menu.add(new EditAddressCardScreen$1(this, 16864384));
         } else if (this.getLeafFieldWithFocus() instanceof TuneChoiceField) {
            menu.add(new EditAddressCardScreen$2(this, 16879616));
         }

         if (menu.getDefaultVerb() == null && this.isDirty()) {
            menu.setDefault(this._saveVerb);
         }
      }
   }

   @Override
   public final boolean onSave() {
      if (this.proceedWithSave()) {
         this._saveVerb.invoke(null);
      }

      return false;
   }

   private final boolean proceedWithSave() {
      BlackBerryAddressBook addressBook = BlackBerryAddressBook.getAddressBook();
      Object currentModel = null;
      if (this._originalModel != null) {
         currentModel = addressBook.getAddressCard(this._originalModel.getUID());
      }

      if (this._originalModel != null && currentModel != this._originalModel) {
         Status.show(AddressBookResources.getString(1731));
         return false;
      } else {
         return true;
      }
   }

   private final boolean isDuplicateName(AddressCardModel model) {
      if (!AddressBookServices.checkDuplicateName(model)) {
         return false;
      }

      if (this._originalModel == null) {
         return true;
      }

      Object[] matches = AddressBookServices.lookup(model, 1);
      if (matches != null) {
         for (int i = matches.length - 1; i >= 0; i--) {
            if (matches[i] != this._originalModel) {
               return true;
            }
         }
      }

      return false;
   }

   static final boolean access$300(EditAddressCardScreen x0) {
      return x0.validateDataFromEdit();
   }
}
