package net.rim.blackberry.api.pdap;

import net.rim.device.apps.api.addressbook.AddressCardModel;
import net.rim.device.apps.api.addressbook.GroupAddressCardModel;
import net.rim.device.apps.internal.commonmodels.categories.CategoriesModel;
import net.rim.device.apps.internal.commonmodels.categories.CategoryList;

class ContactComparator$CategoryMatchProvider extends ContactComparator$MatchProvider {
   private String _category;
   private int _categoryID;
   private boolean _nothingMatches;
   private final ContactComparator this$0;

   public ContactComparator$CategoryMatchProvider(ContactComparator _1, String category) {
      this.this$0 = _1;
      this._category = category;
      if (category != null) {
         this._categoryID = CategoryList.getInstance().getCategoryId(category);
         if (this._categoryID == -1) {
            this._nothingMatches = true;
         }
      }
   }

   @Override
   public boolean comparesGroups() {
      return false;
   }

   @Override
   public boolean matches(ContactImpl contact) {
      return false;
   }

   @Override
   public boolean matches(GroupAddressCardModel groupAddressCard) {
      return false;
   }

   @Override
   public boolean matches(AddressCardModel addressCard) {
      if (this._nothingMatches) {
         return false;
      }

      CategoriesModel categoriesModel = this.getCategoriesModel(addressCard);
      if (this._category == null) {
         if (categoriesModel == null) {
            return true;
         }

         int[] categoryIds = new int[0];
         categoriesModel.getCategoryIds(categoryIds);
         if (categoryIds.length == 0) {
            return true;
         }
      } else if (categoriesModel != null) {
         int[] categoryIds = new int[0];
         categoriesModel.getCategoryIds(categoryIds);

         for (int i = categoryIds.length - 1; i >= 0; i--) {
            if (categoryIds[i] == this._categoryID) {
               return true;
            }
         }
      }

      return false;
   }

   private CategoriesModel getCategoriesModel(AddressCardModel addressCard) {
      for (int i = addressCard.size() - 1; i >= 0; i--) {
         Object o = addressCard.getAt(i);
         if (o instanceof Object) {
            CategoriesModel categoriesModel = (CategoriesModel)o;
            return categoriesModel;
         }
      }

      return null;
   }
}
