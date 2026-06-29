package net.rim.device.apps.internal.addressbook.ui;

import net.rim.device.api.collection.ReadableList;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.Comparator;
import net.rim.device.apps.api.addressbook.AddressSelectionContext;
import net.rim.device.apps.api.addressbook.SelectionListener;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.DefaultProvider;
import net.rim.device.apps.api.framework.model.Recognizer;
import net.rim.device.apps.api.framework.model.VerbDescriptionProvider;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.utility.framework.SubmemberUtilities;
import net.rim.device.apps.api.utility.general.Copyable;
import net.rim.device.apps.internal.addressbook.resources.AddressBookResources;
import net.rim.vm.Array;

final class AddressSelectionVerb extends Verb implements SelectionListener, Copyable {
   private Object _rootObject;
   private Object _selected;
   private Comparator _toStringComparator = new ToStringComparator();
   AddressSelectionContext _context;
   private Recognizer[] _recognizers;

   protected final Object pickFromList(AddressSelectionContext context, Object model, Object[] members) {
      int count = members.length;
      String[] values = new Object[count];
      ContextObject contextObject = ContextObject.castOrCreate(context.getContext());
      boolean oldAddressOnly = contextObject.getFlag(51);
      contextObject.setFlag(51);
      Object oldAddressCard = contextObject.get(252);
      contextObject.put(252, model);

      for (int i = 0; i < count; i++) {
         Object member = members[i];
         if (member instanceof Object) {
            VerbDescriptionProvider provider = (VerbDescriptionProvider)member;
            values[i] = provider.getVerbDescription(contextObject);
         }

         if (values[i] == null) {
            values[i] = member.toString();
         }
      }

      if (!oldAddressOnly) {
         contextObject.clearFlag(51);
      }

      if (oldAddressCard != null) {
         contextObject.put(252, oldAddressCard);
      } else {
         contextObject.remove(252);
      }

      String dialogTitle;
      if ((dialogTitle = context.getPickDialogTitle()) == null) {
         dialogTitle = AddressBookResources.getString(301);
      }

      Arrays.sort(values, 0, count, members, this._toStringComparator);
      int defaultIndex = 0;
      DefaultProvider defaultProvider = null;
      if (this._rootObject instanceof Object) {
         defaultProvider = (DefaultProvider)this._rootObject;
         Object defaultEntry = defaultProvider.getDefault(null, contextObject);

         for (int i = 0; i < count; i++) {
            if (defaultEntry == members[i]) {
               defaultIndex = i;
            }
         }
      }

      int result;
      if (contextObject.getFlag(119)) {
         Dialog d = new AddressSelectionVerb$1(this, dialogTitle, values, null, defaultIndex, null, 0);
         d.setIcon(ThemeManager.getThemeAwareImage("dialog_question"));
         result = d.doModal();
      } else {
         result = Dialog.ask(dialogTitle, values, defaultIndex);
      }

      if (result != -1) {
         if (defaultProvider != null) {
            this._rootObject = defaultProvider.updateDefault(members[result], contextObject);
         }

         return members[result];
      } else {
         return null;
      }
   }

   @Override
   public final boolean select(Object object) {
      return this.select(object, 0);
   }

   @Override
   public final boolean select(Object object, int recognizerIndex) {
      String badAddressError = this._context.getBadAddressError();
      if (badAddressError == null) {
         badAddressError = AddressBookResources.getString(302);
      }

      if (!this.canSelect(object, recognizerIndex)) {
         Dialog.alert(badAddressError);
         this._selected = null;
         return false;
      }

      if (this._rootObject == null) {
         this._rootObject = object;
      }

      if (this._recognizers[recognizerIndex].recognize(object)) {
         this._selected = object;
         return true;
      }

      Object[] members = this.getSubmembers(object, this._recognizers[recognizerIndex]);
      if (members != null && members.length > 0) {
         if (members.length == 1) {
            this._selected = members[0];
            return true;
         }

         this._selected = this.pickFromList(this._context, object, members);
         if (this._selected == null) {
            this._rootObject = null;
         }

         return this._selected != null;
      } else {
         Dialog.alert(badAddressError);
         this._rootObject = null;
         return false;
      }
   }

   @Override
   public final boolean hasSelectedObject() {
      return this._selected != null;
   }

   @Override
   public final boolean canSelect(Object object, int recognizerIndex) {
      if (this._recognizers[recognizerIndex].recognize(object)) {
         return true;
      }

      Object[] members = this.getSubmembers(object, this._recognizers[recognizerIndex]);
      return members != null && members.length > 0;
   }

   @Override
   public final boolean canSelect(Object object) {
      int length = this._recognizers.length;

      for (int i = 0; i < length; i++) {
         if (this.canSelect(object, i)) {
            return true;
         }
      }

      return false;
   }

   @Override
   public final Verb getVerbs(Verb[] verbs, Object selectedElement, Object trackwheelclickContext) {
      Recognizer[] recognizers = this._context.getRecognizers();
      int length = recognizers.length;
      Verb verb = null;
      Verb defaultVerb = null;
      int preferredDefaultIndex = this._context.getPreferredDefaultIndex();
      String[] useEntryPrefixes = this._context.getUseEntryPrefixes();
      String useEntryPrefix = null;

      for (int i = 0; i < length; i++) {
         if (this.canSelect(selectedElement, i)) {
            if (useEntryPrefixes != null && useEntryPrefixes.length > 0) {
               useEntryPrefix = useEntryPrefixes[i];
            }

            Verb var12 = new Object(this, i, selectedElement, useEntryPrefix, trackwheelclickContext);
            Arrays.add(verbs, var12);
            if (defaultVerb == null || i == preferredDefaultIndex) {
               defaultVerb = (Verb)var12;
            }
         }
      }

      return defaultVerb;
   }

   @Override
   public final Object[] getMatches(Object object) {
      Object[] matches = new Object[0];
      int length = this._recognizers.length;

      for (int i = 0; i < length; i++) {
         if (this._recognizers[i].recognize(object)) {
            Array.resize(matches, matches.length + 1);
            matches[matches.length - 1] = object;
         } else {
            Object[] submembers = this.getSubmembers(object, this._recognizers[i]);
            if (submembers != null) {
               int oldLength = matches.length;
               Array.resize(matches, oldLength + submembers.length);
               System.arraycopy(submembers, 0, matches, oldLength, submembers.length);
            }
         }
      }

      return matches;
   }

   @Override
   public final Object copy() {
      return new AddressSelectionVerb();
   }

   @Override
   public final String toString() {
      return "";
   }

   @Override
   public final Object invoke(Object parameter) {
      if (!(parameter instanceof Object)) {
         throw new Object();
      }

      this._context = (AddressSelectionContext)parameter;
      this._context.setSelectedSource(null);
      this._recognizers = this._context.getRecognizers();
      if (this._recognizers == null) {
         throw new Object();
      }

      Object originalContext = this._context.getContext();
      ContextObject contextObject = ContextObject.clone(originalContext);
      this._context.setContext(contextObject);
      contextObject.put(1021178189941494075L, this);
      contextObject.setFlag(5);
      this._selected = null;
      this._rootObject = null;
      Screen selectionScreen = new AddressBookListUI(this._context, this);
      UiApplication.getUiApplication().pushModalScreen(selectionScreen);
      if (this._selected != null && this._selected != this._rootObject) {
         this._context.setSelectedSource(this._rootObject);
      }

      contextObject.clearFlag(5);
      contextObject.remove(1021178189941494075L);
      this._context.setContext(originalContext);
      Object tempSelected = this._selected;
      this._selected = null;
      this._rootObject = null;
      return tempSelected;
   }

   private final Object[] getSubmembers(Object object, Recognizer recognizer) {
      Object[] members = null;
      if (object instanceof Object) {
         ReadableList model = (ReadableList)object;
         members = SubmemberUtilities.getSubmembers(model, recognizer);
      }

      return members;
   }

   AddressSelectionVerb() {
      super(0);
   }
}
