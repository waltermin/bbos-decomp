package net.rim.device.apps.internal.blackberryemail.header;

import net.rim.device.api.collection.Collection;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FocusChangeListener;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.apps.api.addressbook.AddressBookComboField$AddressBookCallback;
import net.rim.device.apps.api.addressbook.AddressBookOptions;
import net.rim.device.apps.api.addressbook.AddressBookServices;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.addressbook.lookup.Request;

public class EmailComposeComboField$ComposeListCallback extends AddressBookComboField$AddressBookCallback implements FocusChangeListener {
   private Verb _addFreeFormVerb;
   private Verb _lookupVerb;
   private final EmailComposeComboField this$0;

   public EmailComposeComboField$ComposeListCallback(EmailComposeComboField _1) {
      super(_1);
      this.this$0 = _1;
      this._addFreeFormVerb = new EmailComposeComboField$AddFreeFormVerb(this.this$0);
      this._lookupVerb = new EmailComposeComboField$LookupVerb(this.this$0);
   }

   @Override
   public synchronized void update() {
      Collection collection = AddressBookServices.getAddressBook().getAddressBookCollection();
      if (collection != null) {
         synchronized (collection) {
            synchronized (EmailComposeComboField.access$000(this.this$0)) {
               int searchSize = EmailComposeComboField.access$100(this.this$0).size();
               int translationSize = 0;
               if (this.this$0._translatedText.length() > 0) {
                  translationSize = 1;
               }

               int lookupSize = 0;
               if (this.this$0._lookup != null && this.this$0.getText().length() > 0) {
                  lookupSize = 1;
               }

               int galSize = this.getGALSize();
               int listSize = translationSize + lookupSize + galSize + searchSize;
               int selection = this.this$0.getList().getSelectedIndex();
               if (!this.this$0._userChangedSelection || selection >= translationSize + lookupSize + galSize) {
                  selection = this.chooseSelection(translationSize, lookupSize, galSize, searchSize);
               }

               this.this$0.getList().setSize(listSize, selection);
               if (listSize > 0) {
                  this.this$0.showDropList();
               } else {
                  this.this$0.hideDropList();
               }
            }
         }
      }
   }

   private int chooseSelection(int translationSize, int lookupSize, int galSize, int searchSize) {
      if (searchSize > 0) {
         return translationSize + lookupSize + galSize;
      } else if (translationSize > 0 && this.this$0._messageType.shouldPutFocusOnTranslation()) {
         return galSize;
      } else if (lookupSize > 0 && this.this$0._messageType.shouldPutFocusOnLookup()) {
         return translationSize + galSize;
      } else if (translationSize > 0 && lookupSize > 0) {
         AddressBookOptions options = AddressBookServices.getAddressBookOptions();
         byte preference = options != null ? options.getComposePreference() : 0;
         return preference == 2 ? translationSize + galSize : galSize;
      } else {
         return 0;
      }
   }

   @Override
   public synchronized Object get(ListField listField, int index) {
      if (index < 0) {
         return null;
      }

      Collection collection = AddressBookServices.getAddressBook().getAddressBookCollection();
      if (collection == null) {
         return null;
      }

      synchronized (collection) {
         Object var10000;
         synchronized (EmailComposeComboField.access$600(this.this$0)) {
            int searchSize = EmailComposeComboField.access$700(this.this$0).size();
            int translationSize = 0;
            if (this.this$0._translatedText.length() > 0) {
               translationSize = 1;
            }

            int lookupSize = 0;
            if (this.this$0._lookup != null && this.this$0.getText().length() > 0) {
               lookupSize = 1;
            }

            int galSize = this.getGALSize();
            int totalSize = searchSize + translationSize + galSize + lookupSize;
            if (index >= totalSize) {
               return null;
            }

            if (index < galSize) {
               return new EmailComposeComboField$UseLookupVerb(this.this$0, (Request)this.this$0._lookup.getAt(index));
            }

            if (index < galSize + translationSize) {
               return this._addFreeFormVerb;
            }

            if (index < galSize + translationSize + lookupSize) {
               return this._lookupVerb;
            }

            var10000 = EmailComposeComboField.access$800(this.this$0).getAt(index - translationSize - lookupSize - galSize);
         }

         return var10000;
      }
   }

   private int getGALSize() {
      int size = 0;
      if (this.this$0._lookup != null) {
         size = this.this$0._lookup.size();
      }

      return size;
   }

   @Override
   public void focusChanged(Field field, int eventType) {
      if (eventType == 2) {
         this.this$0._userChangedSelection = true;
      }
   }
}
