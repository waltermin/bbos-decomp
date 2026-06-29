package net.rim.device.apps.internal.standardcalculator;

import net.rim.device.api.system.Clipboard;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.ui.menu.MenuHandler;
import net.rim.device.api.ui.menu.MenuItemCallback;
import net.rim.device.api.ui.theme.Tag;
import net.rim.device.apps.api.ui.AppsMainScreen;
import net.rim.device.apps.api.ui.SystemEnabledMenu;

final class CalculatorScreen extends AppsMainScreen implements MenuHandler {
   private CalculatorUI _calculatorUI = new CalculatorUI(this, 0);
   private static Tag TAG = Tag.create("client");
   protected static final String ID = "calculator";
   private static final int[] ToMetric = new int[]{
      401,
      402,
      403,
      404,
      405,
      406,
      506,
      407,
      508,
      -805044223,
      1979777214,
      16813599,
      67119734,
      15474537,
      1952661768,
      1750361529,
      134243643,
      409797442,
      -1572730880,
      134250084,
      1659333699,
      6570095,
      -1967373560,
      1174929523,
      1191706812,
      1567103090,
      1208483955,
      1635214400,
      2036419596,
      1409810679,
      1460142295,
      -1089994501,
      -1310128128,
      151024744,
      167800876,
      7473920
   };
   private static final int[] FromMetric = new int[]{
      301,
      302,
      303,
      304,
      305,
      306,
      408,
      409,
      509,
      -805044219,
      1718183726,
      10,
      -804651007,
      51,
      -804913147,
      4456513,
      4849735,
      76,
      -804913142,
      5439553,
      4587588,
      4718663,
      4915274,
      524364,
      -804913147,
      4522065,
      5570644,
      79,
      -804913142,
      5701713,
      5374021,
      5832788,
      4784213,
      5242959,
      -804913147,
      4391002
   };

   CalculatorScreen() {
      super(562949953421312L);
      this.setHelp("calculator");
      this.setTag(TAG);
      VerticalFieldManager vfm = new VerticalFieldManager();
      vfm.setTag(TAG);
      vfm.setId("calculator");
      vfm.add(this._calculatorUI);
      this.add(new CenteringFieldManager(vfm));
      this._calculatorUI.initialize();
   }

   @Override
   protected final boolean navigationClick(int status, int time) {
      return this._calculatorUI.navigationClick(status);
   }

   @Override
   protected final boolean navigationUnclick(int status, int time) {
      return this._calculatorUI.navigationUnclick();
   }

   @Override
   protected final boolean navigationMovement(int dx, int dy, int status, int time) {
      return this._calculatorUI.moveFocusRectangle(dx, dy);
   }

   @Override
   protected final void makeMenu(SystemEnabledMenu menu, int instance) {
      DeciFloat displayFP = new DeciFloat(this._calculatorUI.getText());
      DeciFloat clipBoardFP = new DeciFloat(Clipboard.getClipboard().toString().trim());
      if (!displayFP.isZero()) {
         menu.add(new MenuItemCallback(this, CalculatorUI._commonResources, 3, 196672, 200));
      }

      if (!clipBoardFP.isZero()) {
         menu.add(new MenuItemCallback(this, CalculatorUI._commonResources, 6, 196704, 400));
      }

      if (this._calculatorUI._displayFP == null || !this._calculatorUI._displayFP.isInvalid()) {
         int[] data = new int[]{
            400,
            1135104,
            0,
            300,
            1135360,
            0,
            -804650999,
            401,
            402,
            403,
            404,
            405,
            406,
            506,
            407,
            508,
            -805044223,
            1979777214,
            16813599,
            67119734,
            15474537,
            1952661768,
            1750361529,
            134243643
         };
         MenuItemCallback.add(menu, this, CalculatorUI._resources, data);
      }
   }

   private final void menuConversion(int[] options) {
      Menu submenu = new Menu();
      MenuItemCallback.add(submenu, this, CalculatorUI._resources, options, 0, 0);
      submenu.show();
   }

   @Override
   public final void menuInvoke(int id, MenuItem item) {
      switch (id) {
         case 0:
            this._calculatorUI.runTests();
            return;
         case 3:
            Clipboard.getClipboard().put(this._calculatorUI.getText());
            return;
         case 6:
            this._calculatorUI.setText(Clipboard.getClipboard().toString().trim());
            Clipboard.getClipboard().setNotYetPasted(false);
            return;
         case 300:
            this.menuConversion(FromMetric);
            return;
         case 400:
            this.menuConversion(ToMetric);
            return;
         default:
            this._calculatorUI.doConversion(id);
      }
   }
}
