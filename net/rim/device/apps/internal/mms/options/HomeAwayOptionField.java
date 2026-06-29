package net.rim.device.apps.internal.mms.options;

import net.rim.device.api.ui.component.ObjectChoiceField;
import net.rim.device.api.util.Arrays;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.apps.internal.mms.resources.MMSResources;

class HomeAwayOptionField extends ObjectChoiceField implements MMSOptionsScreen$Saveable {
   private int[] _modes;
   public static final int CHOICETYPE_ALWAYS_NEVER = 0;
   public static final int CHOICETYPE_ALWAYS_NEVER_HOMEONLY = 1;
   public static final int CHOICETYPE_ALWAYS_NEVER_HOMEONLY_UNSPECIFIED = 2;
   public static final int CHOICETYPE_READONLY = 3;

   protected void saveMode(int _1) {
      throw null;
   }

   @Override
   public void saveOption() {
      if (this.isDirty()) {
         this.saveMode(this._modes[this.getSelectedIndex()]);
      }
   }

   private HomeAwayOptionField(String prompt, int[] modes, int initialMode) {
      super(prompt, getChoices(modes), Math.max(0, Arrays.getIndex(modes, initialMode)));
      this._modes = modes;
   }

   public HomeAwayOptionField(int choiceType, String prompt, int initialMode) {
      this(prompt, getChoices(choiceType, initialMode), initialMode);
   }

   private static int[] getChoices(int choiceType, int initialMode) {
      switch (choiceType) {
         case -1:
            return null;
         case 0:
         default:
            return new int[]{0, 1, -804651005, 0, 1, 2, -804651004, 0};
         case 1:
            return new int[]{0, 1, 2, -804651004, 0, 1, 2, -1, -805044213, 775162112, 774909491, 3420721};
         case 2:
            return new int[]{
               0, 1, 2, -1, -805044213, 775162112, 774909491, 3420721, -805044199, 1699878656, 1918985587, 1226860643, 1867325550, 1852795252, 1685343264, 46
            };
         case 3:
            return new int[]{initialMode};
      }
   }

   private static String[] getChoices(int[] modes) {
      int count = modes.length;
      String[] choices = new String[count];

      for (int idx = 0; idx < count; idx++) {
         choices[idx] = getModeString(modes[idx]);
      }

      return choices;
   }

   private static String getModeString(int mode) {
      switch (mode) {
         case -2:
            return null;
         case -1:
            return "Unspecified";
         case 0:
            return MMSResources.getString(35);
         case 1:
         default:
            return CommonResources.getString(2014);
         case 2:
            return MMSResources.getString(37);
      }
   }
}
