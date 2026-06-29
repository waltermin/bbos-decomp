package net.rim.device.apps.api.messaging.messagelist;

import net.rim.device.api.collection.IntRangedActionTarget;
import net.rim.device.api.collection.NotificationSuspension;
import net.rim.device.api.collection.ReadableList;
import net.rim.device.api.system.RIMPersistentStore;
import net.rim.device.apps.api.framework.model.ActionProvider;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.messaging.FolderHierarchies;
import net.rim.device.apps.api.ui.CommonResources;

public final class DeleteMultipleItemsVerb extends Verb {
   private int _type;
   private int _startIndex;
   private int _endIndex;
   private RIMModel[] _selectedItems;
   private Object _target;
   private Object _context;
   public static final int DELETE_MULTISELECTION = 0;
   public static final int DELETE_PRIOR = 1;
   private static final int ALL = 0;
   private static final int ALL_SAVED = 1;
   private static final int ALL_SAVED_TOO = 2;
   private static final int[][][] PROMPT_TABLE = new int[][][]{
      (int[][])({3001, 3006, 3009, -804651005, 3002, 3007, 3010, 1866989824, 727916, -1569758719, 1929445476, 996951338}),
      (int[][])({3002, 3007, 3010, 1866989824, 727916, -1569758719, 1929445476, 996951338, 1929445514, 996951338, 7618954, -1910540799})
   };
   private static final int SELECTED_ITEM_DELETE_OPTIMIZATION_THRESHOLD = 64;

   public DeleteMultipleItemsVerb(int ordering, int type, int menuResId) {
      super(ordering, CommonResources.getResourceBundle(), menuResId);
      this._type = type;
   }

   public final void setParameters(int startIndex, int endIndex, Object target, Object context) {
      this._startIndex = startIndex;
      this._endIndex = endIndex;
      this._target = target;
      this._context = context;
   }

   public final void setParameters(int[] selectedIndices, Object target, Object context) {
      this._selectedItems = RangeActionVerb.getSelectedItems(selectedIndices, target);
      this._target = target;
      this._context = context;
   }

   public final void setParameters(RIMModel[] selectedItems, Object target, Object context) {
      this._selectedItems = selectedItems;
      this._target = target;
      this._context = context;
   }

   public final void resetParameters() {
      this._startIndex = -1;
      this._endIndex = -1;
      this._selectedItems = null;
      this._target = null;
      this._context = null;
   }

   private final boolean doPrompt(boolean yesNo, int promptType) {
      throw new RuntimeException("cod2jar: array load: unknown element");
   }

   private final boolean proceedWithDeleteOfMainMessages(ContextObject context) {
      boolean confirm = true;
      if (this._type != 1) {
         context.setFlag(73);

         for (int i = 0; i < this._selectedItems.length; i++) {
            RIMModel var10000 = this._selectedItems[i];
            if (this._selectedItems[i] instanceof DeleteConfirmationModel) {
               DeleteConfirmationModel confirmer = (DeleteConfirmationModel)var10000;
               if (!confirmer.proceedWithDelete(context, true)) {
                  return false;
               }
            }
         }

         confirm = context.getFlag(73);
      }

      return !confirm || this.doPrompt(false, 0);
   }

   @Override
   public final Object invoke(Object parameter) {
      if (this._target == null) {
         return null;
      }

      if (this._type == 1) {
         if (this._startIndex == -1 || this._endIndex == -1) {
            return null;
         }
      } else if (this._selectedItems == null) {
         return null;
      }

      ContextObject context = ContextObject.clone(this._context);
      boolean doDelete = false;
      boolean deleteMessageAndSaved = false;
      if (ContextObject.getFlag(this._context, 52)) {
         if (this.doPrompt(false, 1)) {
            doDelete = true;
         }
      } else if (this.proceedWithDeleteOfMainMessages(context)) {
         doDelete = true;
         if ((ContextObject.getFlag(this._context, 22) || ContextObject.getFlag(this._context, 78)) && this.rangeContainsSavedItems() && this.doPrompt(true, 2)
            )
          {
            deleteMessageAndSaved = true;
         }
      }

      if (doDelete) {
         if (deleteMessageAndSaved) {
            context.setFlag(52);
         }

         this.performDeleteOnAppropriateItems(context);
      }

      return null;
   }

   private final boolean rangeContainsSavedItems() {
      synchronized (FolderHierarchies.getLockObject()) {
         if (this._target instanceof Object) {
            ReadableList list = (ReadableList)this._target;
            Object context = this._context;
            if (this._type == 1) {
               int start = Math.max(0, this._startIndex);
               int end = Math.min(list.size(), this._endIndex);
               int i = end;

               while (--i >= start) {
                  try {
                     RIMModel model = (RIMModel)list.getAt(i);
                     if (model instanceof Object) {
                        ActionProvider actionProvider = (ActionProvider)model;
                        if (actionProvider.perform(3103370408204507200L, context)) {
                           return true;
                        }
                     }
                  } finally {
                     return false;
                  }
               }

               return false;
            } else {
               int numberOfIndices = this._selectedItems.length;

               for (int i = 0; i < numberOfIndices; i++) {
                  try {
                     RIMModel model = this._selectedItems[i];
                     if (model instanceof Object) {
                        ActionProvider actionProvider = (ActionProvider)model;
                        if (actionProvider.perform(3103370408204507200L, context)) {
                           return true;
                        }
                     }
                  } finally {
                     return false;
                  }
               }

               return false;
            }
         } else {
            return false;
         }
      }
   }

   private final void performDeleteOnAppropriateItems(Object context) {
      if (this._type == 1) {
         synchronized (FolderHierarchies.getLockObject()) {
            if (this._target instanceof Object) {
               IntRangedActionTarget target = (IntRangedActionTarget)this._target;
               target.apply(this._startIndex, this._endIndex, -3967872215949752466L, context);
            }

            this.resetParameters();
         }
      } else {
         boolean useOptimizationForSelectedItemDelete = this._selectedItems != null && this._selectedItems.length > 64;
         if (useOptimizationForSelectedItemDelete) {
            synchronized (RIMPersistentStore.getSynchObject()) {
               this.performDeleteMultiSelect(context);
            }
         } else {
            this.performDeleteMultiSelect(context);
         }
      }
   }

   private final void performDeleteMultiSelect(Object context) {
      synchronized (FolderHierarchies.getLockObject()) {
         ContextObject contextObject = this.getContextObjectForDeleteMultiSelect(context);
         NotificationSuspension ns = null;
         if (this._target instanceof Object) {
            ns = (NotificationSuspension)this._target;
            ns.suspendNotification(contextObject);
         }

         ContextObject co = (ContextObject)context;
         co.setFlag(103);
         RangeActionVerb.performMultiSelectAction(this._selectedItems, 6780594967363292755L, context);
         this.resetParameters();
         if (ns != null) {
            ns.resumeNotification(contextObject);
         }
      }
   }

   private final ContextObject getContextObjectForDeleteMultiSelect(Object initialContext) {
      if (initialContext == null) {
         return null;
      } else if (ContextObject.getFlag(initialContext, 62)) {
         ContextObject contextObject = ContextObject.clone(initialContext);
         contextObject.clearFlag(62);
         return contextObject;
      } else {
         return ContextObject.castOrCreate(initialContext);
      }
   }
}
