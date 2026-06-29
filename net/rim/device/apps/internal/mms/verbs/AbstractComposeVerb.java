package net.rim.device.apps.internal.mms.verbs;

import net.rim.device.api.memorycleaner.MemoryCleanerDaemon;
import net.rim.device.api.memorycleaner.MemoryCleanerListener;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.apps.api.addressbook.AddressBookServices;
import net.rim.device.apps.api.addressbook.AddressSelectionContext;
import net.rim.device.apps.api.addressbook.FriendlyNameAddressModel;
import net.rim.device.apps.api.framework.model.CompoundRecognizer;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.PersistableRIMModel;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.framework.model.Recognizer;
import net.rim.device.apps.api.framework.model.VerbDescriptionProvider;
import net.rim.device.apps.api.framework.registration.RecognizerRepository;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.messaging.messagelist.ShowMessageAppVerb;
import net.rim.device.apps.api.utility.general.Copyable;
import net.rim.device.apps.api.utility.general.SetParameter;
import net.rim.device.apps.internal.blackberryemail.resources.EmailResources;
import net.rim.device.apps.internal.mms.MMSUtilities;
import net.rim.device.apps.internal.mms.api.MMSMessageModel;
import net.rim.device.apps.internal.mms.options.MMSTransportServiceBook;
import net.rim.device.apps.internal.mms.resources.MMSResources;
import net.rim.device.apps.internal.mms.ui.MMSEditorScreen;

class AbstractComposeVerb extends ShowMessageAppVerb implements SetParameter, Copyable, MemoryCleanerListener {
   protected PersistableRIMModel _address;
   protected boolean _useSmartDialing;
   protected PersistableRIMModel _addressCard;
   private ContextObject _context;

   protected String getAddressDescription(Object context) {
      String description = getAddressDescription(this._address, this._addressCard, this._context);
      if (description == null) {
         PersistableRIMModel address = (PersistableRIMModel)ContextObject.get(context, 254);
         PersistableRIMModel addressCard = (PersistableRIMModel)ContextObject.get(context, 252);
         if (address != null || addressCard != null) {
            description = getAddressDescription(address, addressCard, (ContextObject)context);
         }
      }

      return description;
   }

   @Override
   public void setParameter(Object context) {
      this._context = ContextObject.clone(context);
      if (this._context.getFlag(7)) {
         super._ordering = 328000;
      }

      this._address = (PersistableRIMModel)this._context.get(247);
      this._useSmartDialing = this._context.getFlag(117);
      this._addressCard = (PersistableRIMModel)this._context.get(252);
      if (this._addressCard != null) {
         this._useSmartDialing = true;
      }
   }

   protected void resolveAddress() {
      int PHONE_NUMBER_RECOGNIZER = 0;
      Recognizer[] recognizers = new Recognizer[]{
         RecognizerRepository.getRecognizers(3797587162219887872L), RecognizerRepository.getRecognizers(-2985347935260258684L)
      };
      CompoundRecognizer compoundRecognizer = new CompoundRecognizer(recognizers);
      Verb addressSelectionVerb = AddressBookServices.getAddressSelectionVerb(3797587162219887872L);
      if (addressSelectionVerb != null) {
         Verb[] useOnceVerbs = new Verb[]{new MMSUseOnceVerb()};
         AddressSelectionContext selectionContext = new AddressSelectionContext(
            MMSResources.getString(111), MMSResources.getString(10), null, compoundRecognizer, useOnceVerbs
         );
         ContextObject phoneNumberSelectionContext = new ContextObject();
         phoneNumberSelectionContext.setFlag(42, 34);
         phoneNumberSelectionContext.put(6609423255094033855L, new Integer(12759082));
         selectionContext.setContext(phoneNumberSelectionContext);
         selectionContext.setUseEntryPrefixes(new String[]{MMSResources.getString(9)});
         this._address = (PersistableRIMModel)addressSelectionVerb.invoke(selectionContext);
         if (recognizers[PHONE_NUMBER_RECOGNIZER].recognize(this._address)) {
            Object selectedSource = selectionContext.getSelectedSource();
            if (selectedSource != null && selectedSource != this._address) {
               this._useSmartDialing = true;
            }
         }

         PersistableRIMModel addressCard = this._addressCard;
         if (addressCard == null) {
            addressCard = (PersistableRIMModel)selectionContext.getSelectedSource();
         }

         if (addressCard != null && this._address instanceof FriendlyNameAddressModel) {
            FriendlyNameAddressModel friendlyModel = (FriendlyNameAddressModel)this._address;
            if (friendlyModel.getFriendlyName() == null) {
               String friendlyName = getAddressDescription(this._address, addressCard, phoneNumberSelectionContext);
               if (friendlyName != null && friendlyModel instanceof Copyable) {
                  friendlyModel = (FriendlyNameAddressModel)((Copyable)friendlyModel).copy();
                  friendlyModel.setFriendlyName(friendlyName);
                  this._address = friendlyModel;
               }
            }
         }
      }
   }

   @Override
   public boolean cleanNow(int event) {
      if (event == 10 && this._address != null) {
         this._address = null;
         return true;
      } else {
         return false;
      }
   }

   @Override
   public String getDescription() {
      return null;
   }

   @Override
   public Object copy() {
      throw null;
   }

   @Override
   public Object doInvoke(Object context) {
      MMSMessageModel message = (MMSMessageModel)ContextObject.get(context, -7651695713744129224L);
      if (!MMSUtilities.canSend()) {
         int msgid = MMSTransportServiceBook.hasMMSServiceRecord() ? 14 : 62;
         Dialog.alert(MMSResources.getString(msgid));
      }

      ContextObject.setFlag(context, 0);
      MMSEditorScreen editor = new MMSEditorScreen(context);
      editor.setModel(message);
      return editor.go();
   }

   @Override
   public RIMModel getRIMModel() {
      return this._address != null ? this._address : this._addressCard;
   }

   @Override
   public final String toString() {
      return this.toString(this._context);
   }

   protected static String trimSubject(String subject) {
      String prefixRE = EmailResources.getString(60);
      String prefixFW = EmailResources.getString(59);

      while (true) {
         subject = subject.trim();
         if (subjectStartsWith(subject, prefixRE)) {
            subject = subject.substring(prefixRE.length());
         } else {
            if (!subjectStartsWith(subject, prefixFW)) {
               return subject;
            }

            subject = subject.substring(prefixFW.length());
         }
      }
   }

   private static boolean subjectStartsWith(String subject, String prefix) {
      int prefixLength = prefix.length();
      return subject.length() < prefixLength ? false : subject.regionMatches(true, 0, prefix, 0, prefixLength);
   }

   @Override
   public String toString(Object _1) {
      throw null;
   }

   public AbstractComposeVerb(int ordering) {
      super(ordering);
      MemoryCleanerDaemon.addWeakListener(this, false);
   }

   private static String getAddressDescription(PersistableRIMModel address, PersistableRIMModel addressCard, ContextObject context) {
      if (address == null) {
         return null;
      }

      if (!(address instanceof VerbDescriptionProvider)) {
         return address.toString();
      }

      VerbDescriptionProvider verbDescriptionProvider = (VerbDescriptionProvider)address;
      if (addressCard != null) {
         context.put(252, addressCard);
      }

      return verbDescriptionProvider.getVerbDescription(context);
   }
}
