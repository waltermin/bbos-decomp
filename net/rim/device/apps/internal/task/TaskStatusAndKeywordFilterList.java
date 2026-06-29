package net.rim.device.apps.internal.task;

import net.rim.device.api.collection.util.KeywordIndexerHelper;
import net.rim.device.api.collection.util.KeywordPrefixSearchResult;
import net.rim.device.api.collection.util.PrefixKeywordFilterList;
import net.rim.device.apps.api.task.TaskModel;

final class TaskStatusAndKeywordFilterList extends PrefixKeywordFilterList {
   TaskUICollection _taskUICollection;
   Object _searchCriteria = null;

   public TaskStatusAndKeywordFilterList(TaskUICollection source, KeywordIndexerHelper helper, boolean firstWordBias) {
      super(source, helper, firstWordBias);
      this._taskUICollection = source;
   }

   @Override
   public final Object[] getElements(KeywordPrefixSearchResult result) {
      synchronized (this) {
         Object[] elements = super.getElements(result);
         if (elements != null && elements.length > 0) {
            Object[] temp = new Object[elements.length];
            int filteredStatus = this._taskUICollection.getStatusToHide();
            int count = 0;

            for (int i = 0; i < elements.length; i++) {
               Object element = elements[i];
               if (element instanceof Object) {
                  TaskModel tm = (TaskModel)element;
                  if (tm.getStatus() != filteredStatus) {
                     temp[count++] = element;
                  }
               }
            }

            elements = temp;
         }

         return elements;
      }
   }

   @Override
   public final void recalculateResults() {
      throw new RuntimeException("cod2jar: tail call (jumpspecial)");
   }

   public final void storeSearchCriteria() {
      this._searchCriteria = super.getCriteria();
   }

   public final void redoSearch() {
      super.setCriteria(this._searchCriteria, null);
   }
}
