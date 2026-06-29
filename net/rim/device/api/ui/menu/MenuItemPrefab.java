package net.rim.device.api.ui.menu;

import net.rim.device.api.system.Clipboard;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.InvokableAction;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.util.IntHashtable;
import net.rim.device.internal.i18n.CommonResource;

public class MenuItemPrefab extends MenuItem implements FieldChangeListener {
   private int _id;
   private static final int RSRC_ID_OFFSET;
   private static final int ORDINAL_OFFSET;
   private static final int PRIORITY_OFFSET;
   private static final int DATA_WIDTH;
   private static final int[] DATA = new int[]{
      1,
      196640,
      100,
      3,
      196672,
      200,
      4,
      196688,
      300,
      6,
      196704,
      400,
      7,
      196720,
      500,
      10,
      196736,
      350,
      10039,
      196944,
      200,
      10168,
      197024,
      300,
      10038,
      197040,
      400,
      10038,
      197040,
      400,
      10038,
      197040,
      400,
      10038,
      197040,
      400,
      10038,
      197040,
      400,
      10038,
      197040,
      400,
      9,
      268501008,
      1073741823,
      18,
      332288,
      1000,
      10111,
      196721,
      500,
      10112,
      196721,
      500,
      10125,
      268501009,
      1500,
      -804651005,
      4,
      5,
      6,
      -804651004,
      7,
      3,
      1,
      2,
      51,
      4408146,
      4801362,
      5391186,
      5526098,
      1867317504,
      13919606,
      1829528321,
      424025994,
      16781677,
      -1972564893,
      2036419615,
      1661010167,
      1418358028,
      1281702760,
      -1501289105,
      16807074,
      38616944,
      -682312344,
      2329784,
      2781953,
      1126855425,
      1835998069,
      1979777215,
      8556063,
      -1910540799,
      2036419714,
      1979777271,
      1384287775,
      1979777272,
      -729641441,
      16807045,
      -2104615050,
      16778984,
      1701539702,
      477389633,
      1870004480,
      -313301653,
      7672653,
      12956929,
      -926567167,
      67109888,
      1189879657,
      513809249,
      526976000,
      274942444,
      482591910,
      526976000,
      274942444,
      482591910,
      191925064,
      207816192,
      1750370925,
      1867277627,
      -1566145499,
      1812332660,
      100689154,
      1097138796,
      1769108596,
      1812332642,
      -1002347262,
      40633856,
      460669797,
      12545391,
      1694657542,
      1864070467,
      7585645,
      1694657542,
      100726866,
      1399128684,
      1695315181,
      326108672,
      1400139270,
      1248355,
      638058504,
      1482714233,
      2032535552,
      5857388,
      2121868808,
      103286784,
      424047726,
      134219496,
      1296003112,
      -1598199806,
      459634033,
      671613043,
      1867524723,
      1724251507,
      1091043358,
      1764296547,
      -2111406494,
      134218323,
      688350017,
      1132356201,
      134272956,
      688350017,
      1316905577,
      134243628,
      688350017,
      1350460009,
      1953369185,
      1665206272,
      1651058951,
      2121224830,
      1665206272,
      1651058951,
      -1014279298,
      7618844,
      123945224,
      2120378665,
      482577235,
      7643814,
      123945224,
      2120378665,
      134272852,
      688350017,
      -1199676823,
      1950442715,
      1665206272,
      1651058951,
      1692121214,
      7643814,
      1952661768,
      134279452,
      192177729,
      1091043514,
      1343854190,
      16610159,
      1886404872,
      1665778381,
      1091043344,
      1769108596,
      1411596130,
      1091043543,
      1769108596,
      1399144290,
      1091043330,
      134265460,
      409797442,
      423757824,
      1107820774,
      2032592409,
      1107820670,
      1292923967,
      771873,
      1950302728,
      134266640,
      1868981570,
      134265356,
      -1334546622,
      -954118803,
      1107820555,
      1280464321,
      272828416,
      673393270,
      -1071895949,
      272828416,
      -1205654922,
      192195904,
      1956816499,
      524486656,
      1806525036
   };
   private static IntHashtable _itemCache = new IntHashtable();

   MenuItemPrefab(int id) {
      super(CommonResource.getBundle(), DATA[3 * id + 0], DATA[3 * id + 1], DATA[3 * id + 2]);
      this._id = id;
   }

   public static MenuItemPrefab get(int id) {
      MenuItemPrefab item = (MenuItemPrefab)_itemCache.get(id);
      if (item == null) {
         item = new MenuItemPrefab(id);
         _itemCache.put(id, item);
      }

      return item;
   }

   public static MenuItemPrefab get(InvokableAction action) {
      int actionId = action.getActionId();
      MenuItemPrefab item = (MenuItemPrefab)_itemCache.get(actionId);
      if (item == null) {
         item = new MenuItemPrefabInvokableAction(action);
         _itemCache.put(actionId, item);
      }

      return item;
   }

   @Override
   public int getPriority() {
      return super.getPriority();
   }

   static int getDefaultPriority(int id) {
      return 3 * id < DATA.length ? DATA[3 * id + 2] : -1;
   }

   @Override
   public void run() {
      Field target = this.getTarget();
      Screen screen = Menu.getTargetScreen();
      switch (this._id) {
         case 0:
         case 6:
         case 7:
         case 8:
         case 9:
         case 10:
         case 11:
         case 12:
         case 13:
            break;
         case 1:
         default:
            target.selectionCopy(Clipboard.getClipboard());
            target.select(false);
            Clipboard.getClipboard().setNotYetPasted(true);
            return;
         case 2:
            target.selectionCut(Clipboard.getClipboard());
            target.select(false);
            Clipboard.getClipboard().setNotYetPasted(true);
            return;
         case 3:
            target.paste(Clipboard.getClipboard());
            target.select(false);
            Clipboard.getClipboard().setNotYetPasted(false);
            return;
         case 4:
            target.select(true);
            return;
         case 5:
            target.select(false);
            return;
         case 14:
            Clipboard.getClipboard().setNotYetPasted(false);
            screen.onClose();
            return;
         case 15:
            if (screen.doSaveInternal()) {
               screen.close();
               return;
            }
            break;
         case 16:
            screen.setScrollBehaviourSelect(true);
            return;
         case 17:
            screen.setScrollBehaviourSelect(false);
            return;
         case 18:
            screen.onMenu(1073741824);
            screen.setScrollBehaviourSelect(false);
      }
   }

   @Override
   public void fieldChanged(Field field, int context) {
   }
}
