package net.rim.device.apps.internal.blackberryemail.header;

import net.rim.device.api.collection.util.KeywordFilterList;
import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.ObjectGroup;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.XYEdges;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.component.BitmapField;
import net.rim.device.api.ui.component.Status;
import net.rim.device.apps.api.addressbook.AddressBookComboField;
import net.rim.device.apps.api.addressbook.AddressCardModel;
import net.rim.device.apps.api.addressbook.FriendlyNameAddressModel;
import net.rim.device.apps.api.framework.model.AddressVerifier;
import net.rim.device.apps.api.framework.model.AddressVerifierAwareField;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.PersistableRIMModel;
import net.rim.device.apps.api.framework.verb.DefaultVerbProvider;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.framework.verb.VerbCombiner;
import net.rim.device.apps.internal.addressbook.lookup.ALPConfiguration;
import net.rim.device.apps.internal.addressbook.lookup.ALPManager;
import net.rim.device.apps.internal.addressbook.lookup.Request;
import net.rim.device.apps.internal.blackberryemail.email.EmailBuilder;
import net.rim.device.apps.internal.blackberryemail.email.EmailEditorScreen;
import net.rim.device.apps.internal.blackberryemail.resources.EmailResources;
import net.rim.vm.Array;

public class EmailComposeComboField extends AddressBookComboField implements AddressVerifierAwareField {
   private int _headerType;
   private EmailComposeComboField$MessageType _messageType;
   private EmailHeaderModel _model;
   private EmailEditorScreen _editor;
   private Object _context;
   private ALPManager _lookup;
   private Request _request;
   private String _translatedText = "";
   private boolean _userChangedSelection = false;
   private boolean _suspendUpdates = false;
   private AddressVerifier _addressVerifier = null;
   private Field _addressTrustIndicatorField;
   public static final int EMAIL_MESSAGE = 0;
   public static final int PIN_MESSAGE = 1;
   private static final Bitmap UNTRUSTED_ADDRESS_BITMAP = Bitmap.getBitmapResource("net_rim_bb_framework_api", "untrustedaddress.png");
   private static final XYEdges DROP_MARGIN = (XYEdges)(new Object(0, 5, 0, 0));
   private static final int VISIBLE_ROWS = 2;
   private static final int SCROLL_PADDING = 3;

   public Verb getVerbs(Object createContext, Verb[] verbs) {
      if (this._lookup == null) {
         return null;
      }

      if (this.getText().length() == 0) {
         return null;
      }

      Array.resize(verbs, 1);
      verbs[0] = new EmailComposeComboField$LookupVerb(this);
      return null;
   }

   protected EmailComposeComboField$EmailComposeEditable getEmailEditable() {
      return (EmailComposeComboField$EmailComposeEditable)this.getEditable();
   }

   @Override
   public void verifyAddress(Object context) {
      if (!this._model.hasFreeFormAddress()) {
         Field f = this.getField(0);
         if (f instanceof Object) {
            ((AddressVerifierAwareField)f).verifyAddress(null);
         }
      } else {
         if (!this.isAddressTrusted()) {
            if (this._addressTrustIndicatorField == null) {
               BitmapField iconField = (BitmapField)(new Object(UNTRUSTED_ADDRESS_BITMAP));
               iconField.setSpace(1, 0);
               this._addressTrustIndicatorField = iconField;
               this.insert(this._addressTrustIndicatorField, 0);
               return;
            }
         } else if (this._addressTrustIndicatorField != null) {
            this.delete(this._addressTrustIndicatorField);
            this._addressTrustIndicatorField = null;
         }
      }
   }

   @Override
   protected void editFocusGained() {
      this.ensureDropListHasRoom();
   }

   @Override
   protected XYEdges getDropMargin() {
      return DROP_MARGIN;
   }

   private boolean setAddressFromCard(AddressCardModel card, PersistableRIMModel subAddress, boolean maintainFocus) {
      if (this._messageType.isAppropriateInnerModel(subAddress)
         && !EmailBuilder.modelIsAGroupWithAllInvalidAddresses(subAddress, ContextObject.getFlag(this._context, 94))) {
         this._model.setInsideModel(subAddress, card);
         if (!this._model.hasFreeFormAddress()) {
            ContextObject contextObject = ContextObject.clone(this._context);
            contextObject.setFlag(1);
            Field newField = this._model.getField(contextObject);
            this.replaceMyselfWith(newField);
            this.releaseSureTypeVariantsMemory();
         }

         if (!this._model.isUnresolved()) {
            this.getEmailEditor().handleRecipientAdded(this._model);
         }

         this.addAnotherInputField(maintainFocus);
         return true;
      } else {
         this.warnOfNoAddresses();
         return false;
      }
   }

   private boolean setAddressFromString(String address, boolean submit, boolean maintainFocus) {
      if (!submit && this._model.hasFreeFormAddress()) {
         PersistableRIMModel insideModel = this._model.getInsideModel();
         if (insideModel instanceof Object && !ObjectGroup.isInGroup(insideModel)) {
            FriendlyNameAddressModel fnam = (FriendlyNameAddressModel)insideModel;
            fnam.setAddress(address);
            return true;
         }
      }

      if (submit) {
         this.verifyAddress(null);
      }

      if (address.length() == 0) {
         return false;
      }

      PersistableRIMModel addressModel = this._messageType.createAddress(address);
      return addressModel == null ? false : this.setAddressFromCard(null, addressModel, maintainFocus);
   }

   private boolean setAddressWithChoice(AddressCardModel card) {
      Verb[] verbs = new Object[0];

      for (int i = 0; i < card.size(); i++) {
         Verb innerVerb = null;
         Object subObj = card.getAt(i);
         if (this._messageType.isAppropriateInnerModel(subObj)) {
            FriendlyNameAddressModel item = (FriendlyNameAddressModel)subObj;
            innerVerb = new EmailComposeComboField$SetInsideModelVerb(this, item, card);
            Array.resize(verbs, verbs.length + 1);
            verbs[verbs.length - 1] = card.wrapToUpdateLastUsedEntry(item, innerVerb);
         }
      }

      if (verbs == null || verbs.length == 0) {
         this.warnOfNoAddresses();
         return false;
      }

      if (verbs.length == 1) {
         verbs[0].invoke(null);
         return true;
      }

      VerbCombiner combiner = this._messageType.createVerbCombiner();
      if (combiner == null) {
         return false;
      }

      DefaultVerbProvider defaultVerbProvider = (DefaultVerbProvider)(new Object(card));
      Verb defaultVerb = defaultVerbProvider.getDefaultVerb(verbs);
      Verb wrapperVerb = combiner.createWrapperVerb(verbs, defaultVerb);
      return wrapperVerb.invoke(null) != null;
   }

   private void warnOfNoAddresses() {
      String warningBase = EmailResources.getString(186);
      Object[] parms = new Object[]{this._messageType.toString()};
      String warning = MessageFormat.format(warningBase, parms);
      Status.show(warning, 1500);
   }

   private void replaceMyselfWith(Field newField) {
      this.getEmailEditor();
      Manager manager = this.getManager();
      if (manager != null) {
         int oldScroll = manager.getVerticalScroll();
         boolean hadFocus = super._hasFocus;
         this.deleteAll();
         this._addressTrustIndicatorField = null;
         if (newField != null) {
            this.add(newField);
            newField.setDirty(true);
         }

         if (manager.isStyle(281474976710656L)) {
            manager.setVerticalScroll(oldScroll);
         }

         if (hadFocus && newField != null) {
            newField.setFocus();
         }

         this.updateLayout();
      }
   }

   private void addAnotherInputField(boolean maintainFocus) {
      EmailEditorScreen editor = this.getEmailEditor();
      if (editor != null) {
         Field oldFocus = null;
         if (maintainFocus) {
            oldFocus = editor.getModelFieldWithFocus();
         }

         boolean added = editor.addBlankHeader(this._headerType, true);
         if (!added && !maintainFocus) {
            Field blankHeader = editor.findBlankHeader(this._headerType);
            if (blankHeader != null) {
               blankHeader.setFocus();
            }
         }

         if (maintainFocus && oldFocus != null) {
            oldFocus.setFocus();
         }
      }
   }

   private boolean isBlank() {
      return this._model.isBlank();
   }

   private void ensureDropListHasRoom() {
      if (this.getManager().isValidLayout()) {
         XYRect rect = this.getExtent();
         int y = rect.height;
         int visibleHeight = 2 * (rect.height + 3);
         if (this.isBlank()) {
            y = -rect.height;
            visibleHeight = 4 * (rect.height + 3);
         }

         this.getScreen().ensureRegionVisible(this, 0, y, rect.width, visibleHeight);
      }
   }

   private void createLookupModel() {
      if (this._lookup != null) {
         this._request = this._lookup.createRequest(this.convertToKeywordString(this.getText()), super._sortOrder, -8892319056465090102L, null);
         if (this._request != null) {
            this._request.setContext(this._context);
            PersistableRIMModel lookupModel = (PersistableRIMModel)this._lookup.createModelFromRequest(this._request);
            if (lookupModel != null) {
               this.setAddressFromCard(null, lookupModel, true);
            }
         }
      }
   }

   private void useLookupModel(Request request) {
      request.setContext(this._context);
      PersistableRIMModel lookupModel = (PersistableRIMModel)this._lookup.createModelFromRequest(request);
      if (lookupModel != null) {
         this.setAddressFromCard(null, lookupModel, false);
         this.getEmailEditor().updateLookupFields();
      }
   }

   private String convertToKeywordString(String text) {
      StringBuffer buffer = (StringBuffer)(new Object(text));

      for (int i = text.length() - 1; i >= 0; i--) {
         switch (text.charAt(i)) {
            case '@':
               buffer.setCharAt(i, ' ');
               break;
         }
      }

      return buffer.toString();
   }

   private EmailEditorScreen getEmailEditor() {
      if (this._editor == null) {
         this._editor = (EmailEditorScreen)this.getScreen();
      }

      return this._editor;
   }

   public EmailComposeComboField(int headerType, int messageTypeTag, EmailHeaderModel model, Object context) {
      super(HeaderTypes.getStringForHeaderType(headerType));
      String label = HeaderTypes.getStringForHeaderType(headerType);
      this.setEditable(new EmailComposeComboField$EmailComposeEditable(this, "", label));
      EmailComposeComboField$ComposeListCallback listCallback = new EmailComposeComboField$ComposeListCallback(this);
      this.getList().setCallback(listCallback);
      this.getList().setFocusListener(listCallback);
      this.setController(new EmailComposeComboField$ComposeController(this));
      this._headerType = headerType;
      this._messageType = new EmailComposeComboField$MessageType(this, messageTypeTag);
      this._model = model;
      this._context = context;
      this._addressVerifier = (AddressVerifier)ContextObject.get(context, 9120441889802231811L);
      if (model != null) {
         PersistableRIMModel insideModel = model.getInsideModel();
         if (insideModel instanceof Object) {
            String incomingAddress = ((FriendlyNameAddressModel)insideModel).getAddress();
            this.setText(incomingAddress);
            this.setDirty(false);
         }
      }

      if (ALPConfiguration.isActive()) {
         this._lookup = ALPConfiguration.getManager();
      }
   }

   private boolean isAddressTrusted() {
      return this._addressVerifier == null ? true : this._addressVerifier.isAddressTrusted(this._translatedText, null);
   }

   @Override
   protected void editEnter() {
      this.setAddressFromString(this.getText(), true, true);
   }

   @Override
   protected boolean incrementalLayout(int index, int added, int deleted) {
      int newFieldCount = this.getFieldCount();
      if (added == 0 && deleted >= 1 && newFieldCount == 0) {
         return true;
      } else if (added == 1 && deleted == 0 && index == 0 && newFieldCount == 1) {
         Field newField = this.getField(0);
         this.layoutChild(newField, this.getWidth(), this.getHeight());
         this.setPositionChild(newField, 0, 0);
         return true;
      } else {
         return super.incrementalLayout(index, added, deleted);
      }
   }

   @Override
   protected void addressSelected(Object selected, int type) {
      this.hideDropList();
      if (selected != null) {
         this._suspendUpdates = true;
         if (!(selected instanceof Object)) {
            if (selected instanceof Object) {
               this.setAddressWithChoice((AddressCardModel)selected);
            } else {
               if (!(selected instanceof Object)) {
                  throw new Object("Unknown type in drop list");
               }

               this.setAddressFromCard(null, (PersistableRIMModel)selected, false);
            }
         } else {
            ((Verb)selected).invoke(this._context);
         }

         this._suspendUpdates = false;
      }
   }

   static KeywordFilterList access$000(EmailComposeComboField x0) {
      return x0._filteredList;
   }

   static KeywordFilterList access$100(EmailComposeComboField x0) {
      return x0._filteredList;
   }

   static KeywordFilterList access$600(EmailComposeComboField x0) {
      return x0._filteredList;
   }

   static KeywordFilterList access$700(EmailComposeComboField x0) {
      return x0._filteredList;
   }

   static KeywordFilterList access$800(EmailComposeComboField x0) {
      return x0._filteredList;
   }
}
