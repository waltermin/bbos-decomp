package net.rim.device.apps.internal.addressbook.addresscard;

import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.component.BasicEditField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.ui.theme.Tag;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.IntHashtable;
import net.rim.device.apps.api.addressbook.AddressBookServices;
import net.rim.device.apps.api.addressbook.AddressCardModel;
import net.rim.device.apps.api.addressbook.CompanyInfoModel;
import net.rim.device.apps.api.addressbook.MailingAddressModel;
import net.rim.device.apps.api.addressbook.PersonNameModel;
import net.rim.device.apps.api.addressbook.SelectionListener;
import net.rim.device.apps.api.framework.model.CompoundRecognizer;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.framework.model.Recognizer;
import net.rim.device.apps.api.framework.model.VerbProvider;
import net.rim.device.apps.api.framework.registration.RecognizerRepository;
import net.rim.device.apps.api.framework.registration.VerbFactory;
import net.rim.device.apps.api.framework.registration.VerbFactoryRepository;
import net.rim.device.apps.api.framework.registration.VerbRepository;
import net.rim.device.apps.api.framework.verb.LastUsedDefaultVerbProvider;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.quickcontact.QuickContactItem;
import net.rim.device.apps.api.quickcontact.QuickContactList;
import net.rim.device.apps.api.quickcontact.QuickContactList$Listener;
import net.rim.device.apps.api.ui.ExitVerb;
import net.rim.device.apps.api.ui.SystemEnabledMenu;
import net.rim.device.apps.api.ui.TwoColumnVerticalFieldManager;
import net.rim.device.apps.api.utility.framework.ControllerUtilities;
import net.rim.device.apps.api.utility.framework.SubmemberUtilities;
import net.rim.device.apps.api.utility.viewer.ViewReadableListRIMModel;
import net.rim.device.apps.internal.addressbook.resources.AddressBookResources;
import net.rim.device.apps.internal.addressbook.ui.DeleteEntryVerb;
import net.rim.device.apps.internal.commonmodels.categories.CategoriesModel;
import net.rim.device.apps.internal.commonmodels.categories.DisplayCategoriesForFieldVerb;
import net.rim.device.apps.internal.commonmodels.title.TitleModel;
import net.rim.device.apps.internal.phone.api.verbs.SpeedDialVerb;
import net.rim.device.apps.internal.phone.model.PhoneNumberModel;
import net.rim.device.apps.internal.profiles.Overrides;
import net.rim.device.internal.io.file.FileUtilities;
import net.rim.device.internal.ui.UiInternal;
import net.rim.vm.Array;

final class ViewAddressCardScreen extends ViewReadableListRIMModel implements QuickContactList$Listener {
   AddressCardModel _acm;
   AddressCardModel _originalACM;
   SelectionListener _selectionListener;
   private Field _categoriesField;
   private boolean _resetWhenExposed;
   private Field[] _fieldList;
   boolean _infoVisible;
   private static IntHashtable _hotkeyToRecognizerTable = new IntHashtable();
   static final long IGNORE_MODEL = -8746885042893430564L;
   private static final int[] _hotkeyResId = new int[]{
      1500,
      1501,
      1502,
      1503,
      1504,
      1505,
      1506,
      1507,
      1508,
      1064304896,
      -1117257139,
      -1934043308,
      1929445411,
      1929445418,
      7618858,
      1160409857,
      1099578220,
      1979777140,
      6646639,
      1802466817,
      1952661861,
      1979777052,
      1097165679,
      -1026723728,
      1644910241,
      1870004480,
      1698981227,
      1819631974,
      1964199284,
      3896393,
      1802466817,
      185683045,
      1870004480,
      -917281429,
      1644910241,
      -977993472
   };
   private static final long[] _hotkeyRecognizerId = new long[]{
      -2985347935260258684L,
      8414046446004092553L,
      7064935308737611579L,
      -442687637293762776L,
      6627402073208639065L,
      2862138288634470671L,
      -8069221209051907189L,
      4246852237058296601L,
      2940120466515154418L,
      6445941260297L,
      6451040880093L,
      6459630814687L,
      6468220749281L,
      6476810683875L,
      -4798582872163221248L,
      8286904942023202644L,
      32722747872313386L,
      4722652495454302977L,
      28547099113095284L,
      8386618834943964673L,
      4712290711578411036L,
      7064835693218721904L,
      7297068808352956672L,
      8436171689426248038L,
      7741536031144113225L,
      8031608085159169125L,
      7064835693328164203L,
      -4200449974624517888L,
      8620731269382344L,
      174521084325030918L,
      -6749649617552063643L,
      8379339642776191077L,
      8187655183636234299L,
      2740440407616480629L,
      55004590929874031L,
      4640206385446070280L,
      576581747774410902L,
      -7633234801415878360L,
      513721691243836261L,
      -2901370120189769728L,
      7091244199978272768L,
      32722689094013822L,
      8081821378512371976L,
      19221376258952511L,
      7157176080017867016L,
      -5083328948436200692L,
      4685995847067391077L,
      5288836821928342636L,
      2711269677348908806L,
      7929559577212223620L,
      7901147893407791377L,
      8407447238983549805L,
      8088755203783960075L,
      8102047232267832688L,
      576510910670095053L,
      8247344640278164545L,
      576697508672004713L,
      7295658126394815553L,
      1760066611508675155L,
      5020498503982516224L,
      7251430952148759154L,
      7885551662998749310L,
      -8449805099208433068L,
      5797449644422725747L,
      -4881048774837439395L,
      7084117942455501015L,
      7018408412169945962L,
      7378378365674482805L,
      63922610104989025L,
      8389209267074581512L,
      576526122938078029L,
      6229723435961312580L
   };

   ViewAddressCardScreen(Object context, AddressCardModel model) {
      super(null, null, 0, null);
      this.setDefaultClose(false);
      this.setSupportClickAndHoldKeyEvents(true);
      super._context = ContextObject.clone(context);
      ContextObject.setFlag(super._context, 11);
      ContextObject.setFlag(super._context, 37);
      ContextObject.setFlag(super._context, 107);
      ContextObject.setFlag(super._context, 128);
      ContextObject.put(super._context, 252, model);
      ContextObject.put(super._context, 244, "contacts");
      this._originalACM = (AddressCardModel)ContextObject.get(context, -4055106280780392421L);
      if (this._originalACM == null) {
         this._originalACM = model;
      }

      this.setModel(model);
      this.setLeaveScreenVerb(new ExitVerb(0, null));
      Recognizer r = RecognizerRepository.getRecognizers(5149066071290992769L);
      if (SubmemberUtilities.getFirstSubmember(model, r) == null) {
         this.setTitle(new LabelField(AddressBookResources.getString(402)));
      }

      this._selectionListener = (SelectionListener)ContextObject.get(context, 1021178189941494075L);
   }

   @Override
   public final void setModel(Object model) {
      this._acm = (AddressCardModel)model;
      super.setModel(model);
      this._categoriesField = this.findField(RecognizerRepository.getRecognizers(-537018776823173138L));
      int uid = this._acm.getUID();
      String customTuneName = Overrides.getInstance().getCustomTune(uid);
      if (customTuneName != null) {
         customTuneName = FileUtilities.getName(FileUtilities.getDisplayName(customTuneName));
         int dotIndex = customTuneName.indexOf(46);
         if (dotIndex != -1) {
            customTuneName = customTuneName.substring(0, dotIndex);
         }

         this.add(new SeparatorField());
         this.add(new BasicEditField(AddressBookResources.getString(1734), customTuneName, customTuneName.length(), 27021597764222976L));
      }
   }

   @Override
   protected final void organizeFields(Field[] fields, int[] orders) {
      int count = fields.length;
      this._fieldList = new Field[count];
      System.arraycopy(fields, 0, this._fieldList, 0, count);
      if (ContextObject.getFlag(super._context, 128)) {
         TwoColumnVerticalFieldManager commFields = new TwoColumnVerticalFieldManager(1152921504606846976L, Display.getWidth() / 2);
         TwoColumnVerticalFieldManager imFields = new TwoColumnVerticalFieldManager(1152921504606846976L, Display.getWidth() / 2);
         VerticalFieldManager otherVFM = new VerticalFieldManager(1152921504606846976L);
         Field nameField = null;
         Field companyField = null;
         Field titleField = null;
         Field pictureField = null;
         int dest = 0;

         for (int i = 0; i < count; i++) {
            Field field = fields[i];
            Object model = field.getCookie();
            if (orders[i] >= 3000 && orders[i] <= 4000) {
               commFields.add(field);
            } else if (orders[i] >= 4100 && orders[i] <= 4200) {
               imFields.add(field);
            } else if (model instanceof PersonNameModel) {
               nameField = field;
            } else if (model instanceof CompanyInfoModel) {
               companyField = field;
            } else if (model instanceof TitleModel) {
               titleField = field;
            } else if (model instanceof DisplayPictureModelImpl) {
               pictureField = field;
            } else if (model instanceof MailingAddressModel) {
               fields[dest] = fields[i];
               orders[dest] = orders[i];
               dest++;
            } else {
               otherVFM.add(field);
            }
         }

         Array.resize(fields, dest);
         Array.resize(orders, dest);
         HorizontalFieldManager hfm = new HorizontalFieldManager(1152921504606846976L);
         VerticalFieldManager vfm = new VerticalFieldManager(1152921504606846976L);
         if (pictureField != null) {
            pictureField.setTag(Tag.create("addressbook-picture"));
            hfm.add(pictureField);
         }

         hfm.add(vfm);
         hfm.setTag(Tag.create("addressbook-block-name"));
         if (nameField != null) {
            vfm.add(nameField);
            nameField.setTag(Tag.create("addressbook-name"));
         }

         if (companyField != null) {
            vfm.add(companyField);
            if (nameField != null) {
               companyField.setTag(Tag.create("addressbook-company"));
            } else {
               companyField.setTag(Tag.create("addressbook-name"));
            }
         }

         if (titleField != null) {
            vfm.add(titleField);
            titleField.setTag(Tag.create("addressbook-title"));
         }

         int insertionPt = 0;
         Arrays.insertAt(fields, hfm, insertionPt);
         Arrays.insertAt(orders, 1100, insertionPt);
         insertionPt++;
         if (commFields.getFieldCount() > 0) {
            commFields.setTag(Tag.create("addressbook-communication"));
            Arrays.insertAt(fields, commFields, insertionPt);
            Arrays.insertAt(orders, 3000, insertionPt);
            insertionPt++;
         }

         if (imFields.getFieldCount() > 0) {
            imFields.setTag(Tag.create("addressbook-communication"));
            Arrays.insertAt(fields, imFields, insertionPt);
            Arrays.insertAt(orders, 4100, insertionPt);
            insertionPt++;
         }

         if (otherVFM.getFieldCount() > 0) {
            otherVFM.setTag(Tag.create("addressbook-other"));
            Arrays.add(fields, otherVFM);
            Arrays.add(orders, 5500);
         }
      }
   }

   @Override
   protected final void makeMenu(SystemEnabledMenu menu, int instance) {
      super.makeMenu(menu, instance);
      if (instance != 65537) {
         Field focusedField = this.getModelFieldWithFocus();
         RIMModel focusedModel = null;
         if (focusedField != null) {
            Object cookie = focusedField.getCookie();
            if (cookie instanceof RIMModel) {
               focusedModel = (RIMModel)cookie;
            }
         }

         if (!ContextObject.getFlag(super._context, 108)
            && !ContextObject.getFlag(super._context, 54)
            && AddressBookServices.getAddressCard(this._acm.getUID()) != null) {
            menu.add(new DeleteEntryVerb(this._originalACM, true));
         }

         if ((ContextObject.getFlag(super._context, 18) || ContextObject.getFlag(super._context, 78)) && !ContextObject.getFlag(super._context, 114)) {
            super._defaultVerb = new AddressCardVerb(2, 611328, this._acm, new UpdateAddressCardVerb(this._originalACM), this);
            menu.add(super._defaultVerb);
         }

         if (ContextObject.getFlag(super._context, 54)) {
            Object[] matches = AddressBookServices.lookup(this._acm, 1);
            if (matches != null && matches.length > 0 && matches[0] != null) {
               super._defaultVerb = new UpdateAttachmentInAddressBookVerb(matches[0], this._acm);
            } else {
               super._defaultVerb = new AddAttachmentToAddressBookVerb(this._acm);
            }

            menu.add(super._defaultVerb);
         }

         if (instance == 0) {
            if (super._defaultVerb == null) {
               super._defaultVerb = super._leaveScreenVerb;
            }

            VerbRepository verbRepository = VerbRepository.getVerbRepository(-5544721730296222436L);
            menu.add(verbRepository.getVerbs(null));
            VerbFactory[] verbFactories = VerbFactoryRepository.getVerbFactories(-6650104226833963074L);
            if (focusedModel != null && verbFactories != null && verbFactories.length > 0) {
               ContextObject verbFactoryContext = new ContextObject(11);
               verbFactoryContext.put(252, this._acm);
               verbFactoryContext.put(250, focusedModel);
               if (ContextObject.get(super._context, -4055106280780392421L) != null) {
                  verbFactoryContext.put(-4055106280780392421L, this._originalACM);
               }

               for (int i = verbFactories.length - 1; i >= 0; i--) {
                  menu.add(verbFactories[i].getVerbs(verbFactoryContext));
               }
            }

            if (this._categoriesField != null && !(this.getModelFieldWithFocus().getCookie() instanceof CategoriesModel)) {
               menu.add(new DisplayCategoriesForFieldVerb(this._categoriesField));
            }

            if (ContextObject.getFlag(super._context, 18) && ContextObject.getFlag(super._context, 114)) {
               Object existingACM = AddressBookServices.reverseLookup(this._acm);
               if (existingACM == null || existingACM instanceof CompressedAddressCardModel) {
                  Object lookupRequest = ContextObject.get(super._context, 113);
                  menu.add(new AddressCardVerb(3, 611328, this._originalACM, new AddLookupResultVerb(lookupRequest), this));
               }
            }
         }

         if (menu.getDefaultVerb() != null) {
            super._defaultVerb = menu.getDefaultVerb();
         }

         Recognizer verbRecognizer = (Recognizer)ContextObject.get(super._context, -409744358660961448L);
         if (ContextObject.getFlag(super._context, 43)) {
            menu.setDefault(super._defaultVerb);
         } else {
            if (this._selectionListener != null) {
               Verb[] verbs = new Verb[0];
               if (focusedModel != null && this._selectionListener.canSelect(focusedModel)) {
                  super._defaultVerb = this._selectionListener.getVerbs(verbs, focusedModel, new ContextObject(37));
               } else if (this._selectionListener.canSelect(this._acm)) {
                  super._defaultVerb = this._selectionListener.getVerbs(verbs, this._acm, new ContextObject(37));
               }

               if (verbRecognizer != null) {
                  for (int i = 0; i < verbs.length; i++) {
                     if (verbRecognizer.recognize(verbs[i])) {
                        menu.add(verbs[i]);
                     }
                  }
               } else {
                  menu.add(verbs);
               }
            }

            boolean originalEditNotAllowed = ContextObject.getFlag(super._context, 85);
            boolean originalInViewFlag = ContextObject.getFlag(super._context, 37);
            boolean originalAddressBookFlag = ContextObject.getFlag(super._context, 18);
            if (focusedModel != null) {
               ContextObject.put(super._context, -8746885042893430564L, focusedModel);
               ContextObject.put(super._context, 254, focusedModel);
            }

            ContextObject.setFlag(super._context, 85, 37, 18);
            ContextObject.setPrivateFlag(super._context, -2774095533296287874L, 0);
            Verb[] verbs = new Verb[0];
            Verb defaultVerb = null;
            if (this._acm instanceof VerbProvider) {
               VerbProvider verbProvider = (VerbProvider)this._acm;
               defaultVerb = verbProvider.getVerbs(super._context, verbs);
            }

            menu.add(verbs);
            if (defaultVerb != null) {
               super._defaultVerb = defaultVerb;
            }

            ContextObject.remove(super._context, -2774095533296287874L);
            if (!originalEditNotAllowed) {
               ContextObject.clearFlag(super._context, 85);
            }

            menu.setDefault(super._defaultVerb);
            menu.coalesce(-3072555018635390988L, new LastUsedDefaultVerbProvider(this._acm, super._context));
            ContextObject.remove(super._context, -8746885042893430564L);
            if (!originalInViewFlag) {
               ContextObject.clearFlag(super._context, 37);
            }

            if (!originalAddressBookFlag) {
               ContextObject.clearFlag(super._context, 18);
               return;
            }
         }
      }
   }

   @Override
   protected final boolean handleSendKey() {
      return super.handleSendKey() || ControllerUtilities.invokeSendKeyVerb(this._acm);
   }

   @Override
   protected final boolean keyDown(int keycode, int time) {
      if (super.keyDown(keycode, time)) {
         return true;
      }

      char key = UiInternal.map(keycode);
      Recognizer recognizer = (Recognizer)_hotkeyToRecognizerTable.get(key);
      if (recognizer != null) {
         Field topLevelField = this.getModelFieldWithFocus();
         int index = Arrays.getIndex(this._fieldList, topLevelField);
         if (index != -1) {
            int startIndex = index;
            int fieldCount = this._fieldList.length;

            do {
               if (++index == fieldCount) {
                  index = 0;
               }

               if (recognizer.recognize(this._fieldList[index].getCookie())) {
                  this._fieldList[index].setFocus();
                  return true;
               }
            } while (index != startIndex);
         }
      }

      return false;
   }

   @Override
   protected final void onExposed() {
      if (this._resetWhenExposed) {
         this._resetWhenExposed = false;
         this.setModel(this.getModel());
      }

      super.onExposed();
   }

   @Override
   protected final void onUiEngineAttached(boolean attached) {
      if (attached) {
         QuickContactList.getInstance().addListener(this);
      } else {
         QuickContactList.getInstance().removeListener(this);
      }

      super.onUiEngineAttached(attached);
   }

   @Override
   protected final boolean keyClickedAndHeld(int keycode, int time) {
      Field focusedField = this.getLeafFieldWithFocus();
      if (focusedField != null) {
         Object cookie = focusedField.getCookie();
         if (cookie instanceof PhoneNumberModel) {
            PhoneNumberModel pnm = (PhoneNumberModel)cookie;
            QuickContactList ql = QuickContactList.getInstance();
            if (pnm.canSpeedDial() && ql.getQuickContactKey(pnm) == 0 && ql.getQuickContactItem(keycode) == null) {
               SpeedDialVerb.assignSpeedDial(Keypad.map(keycode), keycode, cookie, false);
               return true;
            }
         }
      }

      return super.keyClickedAndHeld(keycode, time);
   }

   @Override
   public final void onQuickContactListEvent(int eventId, int index, QuickContactItem item) {
      if (item != null) {
         for (int i = this._fieldList.length - 1; i >= 0; i--) {
            Field field = this._fieldList[i];
            Object cookie = field.getCookie();
            if (item.matchAddress(cookie, null)) {
               if (!this.isVisible()) {
                  this._resetWhenExposed = true;
                  return;
               }

               this.setModel(this.getModel());
               return;
            }
         }
      }
   }

   @Override
   protected final boolean openProductionBackdoor(int backDoor) {
      switch (backDoor) {
         case 1447642454:
            return super.openProductionBackdoor(backDoor);
         case 1447642455:
         default:
            if (!this._infoVisible) {
               this.insertInfo();
            }

            return true;
      }
   }

   private final void insertInfo() {
      VerticalFieldManager vfm = new VerticalFieldManager();
      vfm.add(new LabelField("RefId: " + this._acm.getUID(), 18014398509481984L));
      vfm.add(new SeparatorField());
      this.insert(vfm, 0);
      vfm.setFocus();
      this._infoVisible = true;
   }

   static {
      for (int i = 0; i < _hotkeyResId.length; i++) {
         String resource = AddressBookResources.getString(_hotkeyResId[i]);
         Recognizer recognizer = RecognizerRepository.getRecognizers(_hotkeyRecognizerId[i]);
         if (resource != null && recognizer != null) {
            int len = resource.length();

            for (int j = 0; j < len; j++) {
               int key = resource.charAt(j);
               Recognizer existing = (Recognizer)_hotkeyToRecognizerTable.get(key);
               if (existing == null) {
                  _hotkeyToRecognizerTable.put(key, recognizer);
               } else if (existing instanceof TemporaryCompoundRecognizer) {
                  CompoundRecognizer cr = (CompoundRecognizer)existing;
                  cr.addRecognizer(recognizer);
               } else {
                  _hotkeyToRecognizerTable.remove(key);
                  CompoundRecognizer cr = new TemporaryCompoundRecognizer();
                  cr.addRecognizer(existing);
                  cr.addRecognizer(recognizer);
                  _hotkeyToRecognizerTable.put(key, cr);
               }
            }
         }
      }
   }
}
