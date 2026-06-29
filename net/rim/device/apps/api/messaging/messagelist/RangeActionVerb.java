package net.rim.device.apps.api.messaging.messagelist;

import net.rim.device.api.collection.IntRangedActionTarget;
import net.rim.device.api.collection.ReadableList;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.apps.api.framework.model.ActionProvider;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.messaging.FolderHierarchies;
import net.rim.device.apps.api.ribbon.indicators.IndicatorManager;
import net.rim.device.apps.api.ui.CommonResources;

public final class RangeActionVerb extends Verb {
   private long _action;
   private boolean _confirm;
   private int _confirmResId;
   private int[] _confirmChoices;
   private int _defaultConfirmChoice;
   private int _proceedConfirmChoice;
   private int _startIndex;
   private int _endIndex;
   private RIMModel[] _selectedItems;
   private Object _target;
   private Object _context;

   public RangeActionVerb(
      int ordering, long action, boolean confirm, int confirmResId, int[] confirmChoices, int defaultConfirmChoice, int proceedConfirmChoice, int menuResId
   ) {
      super(ordering, CommonResources.getResourceBundle(), menuResId);
      this._action = action;
      this._confirm = confirm;
      this._confirmResId = confirmResId;
      this._confirmChoices = confirmChoices;
      this._defaultConfirmChoice = defaultConfirmChoice;
      this._proceedConfirmChoice = proceedConfirmChoice;
   }

   public final void setParameters(int startIndex, int endIndex, Object target, Object context) {
      this._target = target;
      this._startIndex = startIndex;
      this._endIndex = endIndex;
      this._context = context;
   }

   public final void setParameters(int[] selectedIndices, Object target, Object context) {
      this._selectedItems = getSelectedItems(selectedIndices, target);
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

   public final void setConfirm(boolean confirm) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   public static final RIMModel[] getSelectedItems(int[] selectedIndices, Object target) {
      if (selectedIndices == null) {
         return null;
      }

      RIMModel[] selectedItems = null;
      if (target instanceof ReadableList) {
         ReadableList list = (ReadableList)target;
         int numberOfIndices = selectedIndices.length;
         selectedItems = new RIMModel[numberOfIndices];

         for (int i = numberOfIndices - 1; i >= 0; i--) {
            try {
               selectedItems[i] = (RIMModel)list.getAt(selectedIndices[i]);
            } finally {
               ;
            }
         }
      }

      return selectedItems;
   }

   public static final boolean performMultiSelectAction(RIMModel[] items, long action, Object context) {
      boolean result = false;
      IndicatorManager im = IndicatorManager.getInstance();
      if (im != null) {
         im.suspendIndicatorUpdates();
      }

      result = performActionOnItems(items, action, context);
      if (im != null) {
         im.resumeIndicatorUpdates();
      }

      return result;
   }

   private static final boolean performActionOnItems(RIMModel[] items, long action, Object context) {
      if (items == null) {
         return false;
      }

      int numberOfItems = items.length;
      boolean result = false;

      for (int i = numberOfItems - 1; i >= 0; i--) {
         try {
            RIMModel model = items[i];
            if (model instanceof ActionProvider) {
               ActionProvider actionProvider = (ActionProvider)model;
               boolean success = actionProvider.perform(action, context);
               result = result || success;
            }
         } finally {
            break;
         }
      }

      return result;
   }

   @Override
   public final Object invoke(Object parameter) {
      if (this._target == null) {
         return null;
      }

      if (this._confirm) {
         Object[] choiceStrings = new Object[this._confirmChoices.length];

         for (int i = 0; i < this._confirmChoices.length; i++) {
            choiceStrings[i] = CommonResources.getString(this._confirmChoices[i]);
         }

         int answer = Dialog.ask(CommonResources.getString(this._confirmResId), choiceStrings, this._defaultConfirmChoice);
         if (answer == this._proceedConfirmChoice) {
            this.traverseItems();
            return null;
         }
      } else {
         this.traverseItems();
      }

      return null;
   }

   private final void traverseItems() {
      synchronized (FolderHierarchies.getLockObject()) {
         if (this._selectedItems != null) {
            performMultiSelectAction(this._selectedItems, this._action, this._context);
            this.resetParameters();
         } else {
            if (this._startIndex == -1 || this._endIndex == -1) {
               return;
            }

            if (this._target instanceof IntRangedActionTarget) {
               ((IntRangedActionTarget)this._target).apply(this._startIndex, this._endIndex, this._action, this._context);
            }

            this.resetParameters();
         }
      }
   }
}
