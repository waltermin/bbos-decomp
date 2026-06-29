package net.rim.device.apps.api.framework.verb;

import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.Comparator;
import net.rim.device.apps.api.framework.model.ContextObject;

public class PopupVerbWrapper extends Verb {
   private String _description;
   private String _prompt;
   protected Verb[] _verbs;
   private Verb _defaultVerb;
   private String[] _descriptions;
   private boolean _showHint;
   private static Comparator _verbOrderingComparator = new VerbOrderingComparator();

   public PopupVerbWrapper(String menuDescription, int ordering, Verb[] verbs) {
      super(ordering);
      int count = verbs.length;
      String[] descriptions = new String[count];

      for (int i = 0; i < count; i++) {
         descriptions[i] = verbs[i].toString();
      }

      this.initialize(menuDescription, menuDescription, verbs, descriptions, verbs[0], false);
   }

   public PopupVerbWrapper(String menuDescription, String prompt, int ordering, Verb[] verbs, String[] descriptions, Verb defaultVerb) {
      super(ordering);
      this.initialize(menuDescription, prompt, verbs, descriptions, defaultVerb, false);
   }

   public PopupVerbWrapper(String menuDescription, String prompt, int ordering, Verb[] verbs, String[] descriptions, Verb defaultVerb, boolean showHint) {
      super(ordering);
      this.initialize(menuDescription, prompt, verbs, descriptions, defaultVerb, showHint);
   }

   private void initialize(String menuDescription, String prompt, Verb[] verbs, String[] descriptions, Verb defaultVerb, boolean showHint) {
      this._description = menuDescription;
      this._prompt = prompt;
      this._verbs = new Verb[verbs.length];
      this._descriptions = descriptions;
      this._defaultVerb = defaultVerb;
      this._showHint = showHint;
      System.arraycopy(verbs, 0, this._verbs, 0, verbs.length);
   }

   @Override
   public String toString() {
      return this._description;
   }

   public void resetDescription(ContextObject context) {
      if (this._verbs != null && this._verbs.length > 0) {
         Verb sourceVerb = this._verbs[0];
         this._description = context == null ? sourceVerb.toString() : sourceVerb.toString(context);
      }
   }

   @Override
   public Object invoke(Object context) {
      int defaultChoiceIndex = 0;
      Arrays.sort(this._verbs, 0, this._verbs.length, this._descriptions, _verbOrderingComparator);
      if (this._defaultVerb != null) {
         for (int i = this._verbs.length - 1; i >= 0; i--) {
            if (this._defaultVerb == this._verbs[i]) {
               defaultChoiceIndex = i;
            }
         }
      }

      Object result = null;
      int index = this.promptUser(this._prompt, this._descriptions, defaultChoiceIndex);
      if (index != -1 && index >= 0 && index < this._verbs.length) {
         result = this._verbs[index].invoke(context);
      }

      return result;
   }

   protected int promptUser(String prompt, String[] _descriptions, int defaultChoiceIndex) {
      PopupVerbWrapperSelectionDialog d = new PopupVerbWrapperSelectionDialog(prompt, _descriptions, defaultChoiceIndex, this._showHint);
      d.setIcon(ThemeManager.getThemeAwareImage("dialog_question"));
      return d.doModal();
   }
}
