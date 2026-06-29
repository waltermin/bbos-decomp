package net.rim.device.api.itpolicy;

public final class WipeITPolicyDirectory {
   private static int WIPE_ALL = -1;
   private static int[] WIPEABLE_POLICY_GROUPS = new int[]{
      40, 38, 36, 249, -805044208, 858927408, 926299444, 1650538808, 1717920867, 51, 4408146, 4801362, 5391186, 5526098, -804782058, 1651663206
   };
   private static int[][] WIPEABLE_POLICY_IDS = new int[][]{WLANITPolicy.WLAN_WIPEABLES, VPNITPolicy.VPN_WIPEABLES, VOIPITPolicy.VOIP_WIPEABLES, {WIPE_ALL}};

   public static final boolean isWipeableGroup(int group) {
      for (int i = 0; i < WIPEABLE_POLICY_GROUPS.length; i++) {
         if (WIPEABLE_POLICY_GROUPS[i] == group) {
            return true;
         }
      }

      return false;
   }

   public static final boolean isWipeableId(int group, int id) {
      int groupLookupId = -1;

      for (int i = 0; i < WIPEABLE_POLICY_GROUPS.length; i++) {
         if (WIPEABLE_POLICY_GROUPS[i] == group) {
            groupLookupId = i;
            break;
         }
      }

      if (groupLookupId == -1) {
         return false;
      }

      int[] wipeableIds = WIPEABLE_POLICY_IDS[groupLookupId];
      if (wipeableIds[0] == WIPE_ALL) {
         return true;
      }

      for (int i = 0; i < wipeableIds.length; i++) {
         if (wipeableIds[i] == id) {
            return true;
         }
      }

      return false;
   }
}
