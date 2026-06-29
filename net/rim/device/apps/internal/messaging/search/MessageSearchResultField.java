package net.rim.device.apps.internal.messaging.search;

import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.component.Status;
import net.rim.device.api.ui.component.VariableHeightListFieldCallback;
import net.rim.device.api.util.Arrays;
import net.rim.device.apps.api.framework.model.ActionProvider;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.HotKeyProvider;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.framework.model.VerbProvider;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.messaging.FolderHierarchies;
import net.rim.device.apps.api.messaging.messagelist.DateSeparator;
import net.rim.device.apps.api.messaging.messagelist.DateSortedSeparatedMessageArray;
import net.rim.device.apps.api.messaging.messagelist.DeleteSingleItemVerb;
import net.rim.device.apps.api.messaging.resources.MessageResources;
import net.rim.device.apps.api.search.SearchResultField;
import net.rim.device.apps.api.ui.VariableHeightCollectionListField;
import net.rim.device.apps.internal.messaging.MessageHotkeys;

final class MessageSearchResultField extends VariableHeightCollectionListField implements SearchResultField, VerbProvider {
   DateSortedSeparatedMessageArray _dateSortedSeparatedItems;
   private MessageSearchResultCollection _searchCollection;

   MessageSearchResultField(DateSortedSeparatedMessageArray list, VariableHeightListFieldCallback callback, MessageSearchResultCollection searchCollection) {
      super(list, callback);
      this._dateSortedSeparatedItems = list;
      this._searchCollection = searchCollection;
   }

   @Override
   public final int getRowHeight(int index) {
      return this._searchCollection != null ? this._searchCollection.getRowHeight(this, index) : 0;
   }

   @Override
   protected final boolean keyChar(char key, int status, int time) {
      Object selectedElement = this.getSelectedElement();
      if (selectedElement instanceof Object) {
         if (key == '\n') {
            return this.openSelectedMessage();
         }

         if (key == 127 || Keypad.getAltedChar(key) == 127) {
            DeleteSingleItemVerb deleteVerb = (DeleteSingleItemVerb)(new Object(611472, 1000));
            deleteVerb.setParameters(selectedElement, null);
            deleteVerb.invoke(null);
            return true;
         }
      }

      return true;
   }

   private final boolean openSelectedMessage() {
      this.getScreen().invokeDefaultMenuItem(0);
      return true;
   }

   @Override
   protected final boolean invokeAction(int action) {
      switch (action) {
         case 1:
            Object selectedElement = this.getSelectedElement();
            if (selectedElement instanceof Object) {
               return this.openSelectedMessage();
            }
         default:
            return super.invokeAction(action);
      }
   }

   private final int checkItemIndex(long action, boolean upward) {
      String warning = null;
      int index = this.checkFolderForItem(action, upward, false, false, false, this.getSelectedIndex());
      if (index != -1) {
         this.setSelectedIndex(index);
      } else {
         index = this.checkFolderForItem(action, upward, upward, !upward, false, this.getSelectedIndex());
         if (index == -1) {
            if (action == 278390328807340479L) {
               warning = MessageResources.getString(115);
            } else if (action == -4201671119995560115L) {
               warning = MessageResources.getString(118);
            } else if (action == -2415955221176628574L) {
               warning = MessageResources.getString(190);
            }
         } else {
            if (index == this.getSelectedIndex()) {
               if (action == 278390328807340479L) {
                  warning = MessageResources.getString(117);
               } else if (action == -4201671119995560115L) {
                  warning = MessageResources.getString(120);
               } else if (action == -2415955221176628574L) {
                  warning = MessageResources.getString(187);
                  index = -1;
               }
            } else if (action == 278390328807340479L) {
               warning = MessageResources.getString(116);
            } else if (action == -4201671119995560115L) {
               warning = MessageResources.getString(119);
            } else if (action == -2415955221176628574L && upward) {
               warning = MessageResources.getString(188);
            } else if (action == -2415955221176628574L && !upward) {
               warning = MessageResources.getString(189);
            }

            if (index != -1) {
               this.setSelectedIndex(index);
            }
         }
      }

      if (warning != null) {
         Status.show(warning, 1000);
      }

      return index;
   }

   private final int checkFolderForItem(
      long action, boolean upward, boolean startFromBottom, boolean startFromTop, boolean scanIncludingCurrentSelection, int modelToStartFrom
   ) {
      boolean foundOne = false;
      int itemToCheck = 0;
      synchronized (FolderHierarchies.getLockObject()) {
         int listFieldSize = this.getSize();
         if (startFromBottom) {
            itemToCheck = listFieldSize;
            foundOne = true;
         } else if (startFromTop) {
            itemToCheck = 0;
            foundOne = true;
         } else if (modelToStartFrom != -1) {
            itemToCheck = modelToStartFrom;
            if (itemToCheck >= 0) {
               foundOne = true;
            }
         }

         foundOne = false;
         int size = this._dateSortedSeparatedItems.size();

         do {
            if (upward) {
               itemToCheck--;
            } else {
               itemToCheck++;
            }

            if (itemToCheck <= 0 || itemToCheck >= size) {
               break;
            }

            RIMModel model = (RIMModel)this._dateSortedSeparatedItems.getAt(itemToCheck);
            if (model instanceof Object) {
               ActionProvider actionProvider = (ActionProvider)model;
               ContextObject context = null;
               if (action == -2415955221176628574L) {
                  context = (ContextObject)(new Object());
                  ContextObject.put(context, -321822713458159100L, this.getSelectedElement());
               }

               foundOne = actionProvider.perform(action, context);
            }
         } while (!foundOne);
      }

      return foundOne ? itemToCheck : -1;
   }

   @Override
   public final boolean keyDown(int keycode, int time) {
      int hotk = MessageHotkeys.map(keycode);
      RIMModel modelCurrentlyOperatedOn = (RIMModel)this.getSelectedElement();
      if (modelCurrentlyOperatedOn instanceof Object) {
         HotKeyProvider d = (HotKeyProvider)modelCurrentlyOperatedOn;
         Object result = d.invokeHotkey(new Object(), hotk);
         if (result != null) {
            return true;
         }
      }

      switch (hotk) {
         case 141:
            this.setSelectedIndex(0);
            return true;
         case 142:
            this.setSelectedIndex(this.getSize() - 1);
            return true;
         case 143:
            this.setSelectedIndex(this._dateSortedSeparatedItems.getNextDateIndex(this.getSelectedIndex()));
            return true;
         case 144:
            this.setSelectedIndex(this._dateSortedSeparatedItems.getPreviousDateIndex(this.getSelectedIndex()));
            return true;
         case 146:
            this.checkItemIndex(278390328807340479L, true);
            return true;
         case 151:
            this.checkItemIndex(-4201671119995560115L, true);
            return true;
         case 185:
            this.checkItemIndex(-2415955221176628574L, true);
            return true;
         case 186:
            this.checkItemIndex(-2415955221176628574L, false);
            return true;
         default:
            return super.keyDown(keycode, time);
      }
   }

   @Override
   public final Object getSelectedObject() {
      return this.getSelectedElement();
   }

   @Override
   public final Verb getVerbs(Object context, Verb[] verbs) {
      Verb defaultVerb = null;
      Object selectedElement = this.getSelectedElement();
      if (selectedElement instanceof Object) {
         ContextObject newContext = ContextObject.clone(context);
         ContextObject.clearFlag(newContext, 2);
         defaultVerb = ((VerbProvider)selectedElement).getVerbs(newContext, verbs);
         DeleteSingleItemVerb deleteVerb = (DeleteSingleItemVerb)(new Object(611472, 1000));
         deleteVerb.setParameters(selectedElement, newContext);
         Arrays.add(verbs, deleteVerb);
         return defaultVerb;
      }

      if (selectedElement instanceof Object) {
         ContextObject newContext = ContextObject.castOrCreate(null);
         ContextObject.put(newContext, 250, selectedElement);
         Verb[] verbArray = new Object[0];
         if (ContextObject.getFlag(context, 81)) {
            defaultVerb = ((DateSeparator)selectedElement).getContextMenuVerbs(verbArray, this.getSelectedIndex(), this._dateSortedSeparatedItems, newContext);
         } else {
            defaultVerb = ((DateSeparator)selectedElement).getDefaultMenuVerbs(verbArray, this.getSelectedIndex(), this._dateSortedSeparatedItems, newContext);
         }

         Arrays.append(verbs, verbArray);
      }

      return null;
   }
}
