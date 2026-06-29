package net.rim.blackberry.api.pdap;

import net.rim.device.apps.api.addressbook.AddressCardModel;
import net.rim.device.apps.api.addressbook.CompanyInfoModel;
import net.rim.device.apps.api.addressbook.FriendlyNameAddressModel;
import net.rim.device.apps.api.addressbook.GroupAddressCardModel;
import net.rim.device.apps.api.addressbook.MailingAddressModel;
import net.rim.device.apps.api.addressbook.PersonNameModel;
import net.rim.device.apps.internal.addressbook.addresscard.WebPageAddressModel;
import net.rim.device.apps.internal.addressbook.userfields.UserFieldsModel;
import net.rim.device.apps.internal.commonmodels.body.BodyModel;
import net.rim.device.apps.internal.commonmodels.title.TitleModel;
import net.rim.device.apps.internal.phone.model.AbstractPhoneNumberModel;
import net.rim.device.apps.internal.phone.model.PhoneNumberModel;

class ContactComparator$StringMatchProvider extends ContactComparator$MatchProvider {
   private String _matchString;

   public ContactComparator$StringMatchProvider(String matchString) {
      this._matchString = matchString;
   }

   @Override
   public boolean comparesGroups() {
      return true;
   }

   @Override
   public boolean matches(ContactImpl contact) {
      return false;
   }

   @Override
   public boolean matches(GroupAddressCardModel groupAddressCard) {
      return this.match(this._matchString, groupAddressCard.getName()) ? true : this.match(this._matchString, String.valueOf(groupAddressCard.getUID()));
   }

   @Override
   public boolean matches(AddressCardModel addressCard) {
      PersonNameModel personalModel = addressCard.getName();
      if (personalModel != null) {
         if (this.match(this._matchString, personalModel.getSalutation())) {
            return true;
         }

         if (this.match(this._matchString, personalModel.getFirstName())) {
            return true;
         }

         if (this.match(this._matchString, personalModel.getLastName())) {
            return true;
         }
      }

      CompanyInfoModel compayInfoModel = addressCard.getCompanyInfo();
      if (compayInfoModel != null && this.match(this._matchString, compayInfoModel.getCompanyName())) {
         return true;
      }

      if (this.match(this._matchString, String.valueOf(addressCard.getUID()))) {
         return true;
      }

      int size = addressCard.size();

      for (int i = 0; i < size; i++) {
         Object model = addressCard.getAt(i);
         if (!(model instanceof Object) && !(model instanceof Object)) {
            if (model instanceof Object) {
               if (this.match(this._matchString, ((FriendlyNameAddressModel)model).getAddress())) {
                  return true;
               }
            } else if (model instanceof Object) {
               if (this.match(this._matchString, ((PhoneNumberModel)model).getValue())) {
                  return true;
               }
            } else if (model instanceof Object) {
               if (this.match(this._matchString, ((BodyModel)model).getText())) {
                  return true;
               }
            } else if (!(model instanceof Object)) {
               if (!(model instanceof Object)) {
                  if (model instanceof Object) {
                     if (this.match(this._matchString, ((FriendlyNameAddressModel)model).getAddress())) {
                        return true;
                     }
                  } else if (model instanceof Object) {
                     if (this.match(this._matchString, ((TitleModel)model).getTitle())) {
                        return true;
                     }
                  } else if (model instanceof Object) {
                     if (this.match(this._matchString, ((WebPageAddressModel)model).getAddress())) {
                        return true;
                     }
                  } else if (model.getClass().getName().equals("net.rim.device.apps.internal.phone.direct.DirectConnectNumberModel")
                     && this.match(this._matchString, ((AbstractPhoneNumberModel)model).getValue())) {
                     return true;
                  }
               } else {
                  MailingAddressModel addressModel = (MailingAddressModel)model;
                  if (this.match(this._matchString, addressModel.getAddressLine1())) {
                     return true;
                  }

                  if (this.match(this._matchString, addressModel.getCity())) {
                     return true;
                  }

                  if (this.match(this._matchString, addressModel.getArea())) {
                     return true;
                  }

                  if (this.match(this._matchString, addressModel.getCountry())) {
                     return true;
                  }

                  if (this.match(this._matchString, addressModel.getZipOrPostalCode())) {
                     return true;
                  }

                  if (this.match(this._matchString, addressModel.getAddressLine2())) {
                     return true;
                  }
               }
            } else {
               UserFieldsModel usersFieldModel = (UserFieldsModel)model;
               if (this.match(this._matchString, usersFieldModel.getUserDefinedField(0))) {
                  return true;
               }

               if (this.match(this._matchString, usersFieldModel.getUserDefinedField(1))) {
                  return true;
               }

               if (this.match(this._matchString, usersFieldModel.getUserDefinedField(2))) {
                  return true;
               }

               if (this.match(this._matchString, usersFieldModel.getUserDefinedField(3))) {
                  return true;
               }
            }
         }
      }

      return false;
   }
}
