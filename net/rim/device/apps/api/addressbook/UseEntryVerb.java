package net.rim.device.apps.api.addressbook;

import net.rim.device.api.ui.UiApplication;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.VerbDescriptionProvider;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.ui.CommonResources;

public final class UseEntryVerb extends Verb {
   private String _description;
   private SelectionListener _selectionListener;
   private Object _selectedEntry;
   private Object _subModel;
   private int _recognizerIndex;
   int _screensToUnroll = 1;

   public UseEntryVerb(SelectionListener selectionListener, Object selectedEntry, String prefix, Object context) {
      this(selectionListener, 0, selectedEntry, prefix, context);
   }

   public UseEntryVerb(SelectionListener selectionListener, int recognizerIndex, Object selectedEntry, String prefix, Object context) {
      super(327936);
      this._selectionListener = selectionListener;
      this._selectedEntry = selectedEntry;
      this._recognizerIndex = recognizerIndex;
      Object oldEntry = null;
      if (context != null) {
         oldEntry = ContextObject.get(context, 252);
         if (selectedEntry != null) {
            ContextObject.put(context, 252, selectedEntry);
         }
      }

      if (prefix == null) {
         this._description = CommonResources.getString(800);
      } else {
         Object[] matches = this._selectionListener.getMatches(this._selectedEntry);
         if (matches != null && matches.length != 0) {
            if (matches[0] instanceof Object) {
               ContextObject contextObject = ContextObject.clone(context);
               contextObject.clearFlag(34);
               VerbDescriptionProvider verbDescriptionProvider = (VerbDescriptionProvider)matches[0];
               String desc = verbDescriptionProvider.getVerbDescription(contextObject);
               if (desc != null) {
                  this._description = ((StringBuffer)(new Object())).append(prefix).append(' ').append(desc).toString();
               }
            }

            if (this._description == null) {
               this._description = ((StringBuffer)(new Object())).append(prefix).append(' ').append(matches[0].toString()).toString();
            }
         } else {
            this._description = prefix;
         }
      }

      if (context != null) {
         if (oldEntry == null) {
            ContextObject.remove(context, 252);
         } else {
            ContextObject.put(context, 252, oldEntry);
         }
      }

      if (ContextObject.getFlag(context, 116)) {
         this._screensToUnroll++;
      }

      if (ContextObject.getFlag(context, 37)) {
         this._screensToUnroll++;
      }

      if (ContextObject.getFlag(context, 123)) {
         this._screensToUnroll++;
      }
   }

   @Override
   public final String toString() {
      return this._description;
   }

   public final void setSubModel(Object subModel) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   @Override
   public final Object invoke(Object parameter) {
      Object entry = this._selectedEntry;
      if (this._subModel != null && this._selectionListener.canSelect(this._subModel, this._recognizerIndex)) {
         entry = this._subModel;
      }

      if (this._selectionListener.select(entry, this._recognizerIndex)) {
         for (int i = 0; i < this._screensToUnroll; i++) {
            UiApplication app = UiApplication.getUiApplication();
            if (app.getScreenCount() > 1) {
               app.popScreen(app.getActiveScreen());
            }
         }
      }

      return null;
   }
}
