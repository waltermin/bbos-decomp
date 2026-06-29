package net.rim.device.apps.internal.secureemail;

import net.rim.device.apps.api.search.SearchCriterion;
import net.rim.device.apps.api.search.criteria.OrSearchCriterion;
import net.rim.device.apps.api.search.criteria.TextSearchModel;
import net.rim.device.apps.internal.messaging.search.MessageSearchableSubItem;

class EncodingActionMessageSearchableSubItem implements MessageSearchableSubItem {
   private static final long ID = 1998100331012273891L;

   @Override
   public long[] getSubItemIds(boolean advancedMode) {
      return new long[]{
         1998100331012273891L,
         72168242854101248L,
         11948397161159014L,
         8503078132182382081L,
         7741536031146863471L,
         -4200449977755808667L,
         -5404037383061176064L,
         66462630402902213L
      };
   }

   @Override
   public String getName(long id) {
      return id != 1998100331012273891L ? null : SecureEmailResources.getString(159);
   }

   @Override
   public void modifySearchCriteria(long id, SearchCriterion[] searchCriteria) {
      if (id == 1998100331012273891L) {
         TextSearchModel textSearchModel = this.locateTextSearchModel(searchCriteria);
         if (textSearchModel != null) {
            EncodingActionSearchModel encodingActionModel = new EncodingActionSearchModel();
            encodingActionModel.setAllowedEncodingActions(3);
            textSearchModel.addSubCriterion(encodingActionModel);
         }
      }
   }

   private TextSearchModel locateTextSearchModel(SearchCriterion[] searchCriteria) {
      for (SearchCriterion currentCriterion : searchCriteria) {
         if (currentCriterion instanceof Object) {
            return (TextSearchModel)currentCriterion;
         }

         if (currentCriterion instanceof Object) {
            OrSearchCriterion orCriterion = (OrSearchCriterion)currentCriterion;
            TextSearchModel textSearchModel = this.locateTextSearchModel((Object[])orCriterion.getValue());
            if (textSearchModel != null) {
               return textSearchModel;
            }
         }
      }

      return null;
   }
}
