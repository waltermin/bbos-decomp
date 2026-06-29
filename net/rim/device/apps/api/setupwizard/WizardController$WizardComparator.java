package net.rim.device.apps.api.setupwizard;

import net.rim.device.api.util.Comparator;

class WizardController$WizardComparator implements Comparator {
   private int[] _rank1 = new int[]{0, 0, -805044213, 775162112, 774909491, 3420721, -805044199, 1699878656};
   private int[] _rank2 = new int[]{0, 0, -805044213, 775162112, 774909491, 3420721, -805044199, 1699878656};

   private void getRank(WizardPage page, int[] rank) {
      WizardCategory cat = page.getCategory();
      if (cat == null) {
         rank[0] = page.getPriority();
         rank[1] = 0;
      } else {
         rank[0] = cat.getPriority();
         rank[1] = page.getPriority();
      }
   }

   @Override
   public int compare(Object o1, Object o2) {
      this.getRank((WizardPage)o1, this._rank1);
      this.getRank((WizardPage)o2, this._rank2);
      int result = this._rank1[0] - this._rank2[0];
      if (result == 0) {
         result = this._rank1[1] - this._rank2[1];
      }

      return result;
   }
}
