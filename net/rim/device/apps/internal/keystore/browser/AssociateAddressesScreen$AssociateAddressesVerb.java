package net.rim.device.apps.internal.keystore.browser;

import net.rim.device.api.i18n.ResourceBundleFamily;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.apps.api.addressbook.EmailAddressModel;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.api.crypto.AddressBookUtilities;
import net.rim.device.internal.i18n.CommonResource;
import net.rim.device.internal.ui.component.SimpleChoiceDialog;
import net.rim.device.internal.ui.component.SimpleInputDialog;

class AssociateAddressesScreen$AssociateAddressesVerb extends Verb {
   private int _type;
   private int _index;
   private final AssociateAddressesScreen this$0;
   public static final int ADD = 1;
   public static final int EDIT = 2;
   public static final int DELETE = 3;
   public static final int SAVE = 4;

   public AssociateAddressesScreen$AssociateAddressesVerb(AssociateAddressesScreen _1, int type, int ordering, ResourceBundleFamily rb, int rbKey) {
      super(ordering, rb, rbKey);
      this.this$0 = _1;
      this._type = type;
   }

   public void setIndex(int index) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   @Override
   public Object invoke(Object parameter) {
      switch (this._type) {
         case 0:
            break;
         case 1:
         default:
            EmailAddressModel[] emailAddressModels = AddressBookUtilities.getEmailAddresses(null, KeyStoreBrowserResources.getString(6085));
            if (emailAddressModels != null) {
               for (int i = 0; i < emailAddressModels.length; i++) {
                  String newAddress = emailAddressModels[i].getAddress();
                  if (!this.this$0.addressExists(newAddress)) {
                     this.this$0._associatedAddressesListField.insert(0, newAddress);
                     this.this$0._associatedAddressesListField.setDirty(true);
                  }
               }
            }
            break;
         case 2:
            SimpleInputDialog dialog = (SimpleInputDialog)(new Object(8, KeyStoreBrowserResources.getString(6086)));
            dialog.setText((String)this.this$0._associatedAddressesListField.get(this.this$0._associatedAddressesListField, this._index));
            dialog.show();
            if (dialog.getCloseReason() == 0) {
               this.this$0._associatedAddressesListField.delete(this._index);
               this.this$0._associatedAddressesListField.insert(this._index, dialog.getText());
               this.this$0._associatedAddressesListField.setSelectedIndex(this._index);
               this.this$0._associatedAddressesListField.setDirty(true);
               return null;
            }
            break;
         case 3:
            if (SimpleChoiceDialog.askYesNoQuestion(
               KeyStoreBrowserResources.getString(6087),
               (String)this.this$0._associatedAddressesListField.get(this.this$0._associatedAddressesListField, this._index)
            )) {
               this.this$0._associatedAddressesListField.delete(this._index);
               this.this$0._associatedAddressesListField.setDirty(true);
               return null;
            }
            break;
         case 4:
            try {
               this.this$0.save();
               this.this$0.setDirty(false);
               this.this$0.close();
               return null;
            } finally {
               Dialog.inform(CommonResource.getString(10018));
               return null;
            }
      }

      return null;
   }
}
