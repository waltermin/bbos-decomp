package net.rim.device.apps.internal.phone;

import net.rim.device.api.system.Application;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.component.ListFieldCallback;
import net.rim.device.api.ui.component.ObjectChoiceField;
import net.rim.device.api.ui.component.Status;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.ui.AppsMainScreen;
import net.rim.device.apps.api.ui.Confirmation;
import net.rim.device.apps.api.ui.ExitVerb;
import net.rim.device.apps.api.ui.SystemEnabledMenu;

final class SSQueryScreen extends AppsMainScreen implements SSQueryListener, ListFieldCallback, Confirmation {
   private ListField _serviceStatusField = (ListField)(new Object(3, 36028797018963968L));
   private Verb _closeVerb = ExitVerb.createCloseVerb(0, this);
   private ObjectChoiceField _ssChoiceField;
   private Application _app;
   private boolean _serviceAvailable;
   private boolean _serviceActive;
   private boolean _querying;
   private static String[] QUERY_CHOICES = new String[]{"CLIP", "CLIR", "CFU", "CFB", "CFNRY", "CFNRC", "BAOC", "BOIC", "BOIC_PLMN", "BAIC", "BICROAM"};
   private static final int[] QUERY_TYPES_MAP = new int[]{
      17,
      18,
      33,
      41,
      42,
      43,
      146,
      147,
      148,
      153,
      154,
      -804651003,
      25,
      5,
      3,
      0,
      0,
      51,
      1866858752,
      -1574934772,
      1208025188,
      186346607,
      1866989824,
      1158355820,
      16812662,
      -1972564893,
      186343757,
      -1888719018,
      1711341669,
      16805145,
      38616944,
      -682312344,
      2329784,
      2781953,
      -1910540799,
      1979777154,
      1979777066,
      6646639,
      1802466817,
      1952661861,
      1979777052,
      1281715055,
      16780049,
      16827829
   };
   private static final int NUM_STATUS_FIELD_ENTRIES;
   static final String YES;
   static final String NO;
   static final String PROVISIONED;
   static final String ACTIVE;
   static final String QUERYING;
   static final String IDLE;
   static final String STATUS;
   static final int KEY_POSITION;
   static final int STATUS_POSITION;

   public SSQueryScreen(Application app) {
      super(196608);
      this.setTitle((Field)(new Object("SS Interrogation")));
      this._ssChoiceField = (ObjectChoiceField)(new Object("Choose Query", QUERY_CHOICES));
      this.add(this._ssChoiceField);
      this.add((Field)(new Object()));
      this.add(this._serviceStatusField);
      this._serviceStatusField.setCallback(this);
      this._app = app;
   }

   @Override
   protected final void makeMenu(SystemEnabledMenu menu, int instance) {
      super.makeMenu(menu, instance);
      menu.add(this._closeVerb);
      if (!this._querying) {
         int index = this._ssChoiceField.getSelectedIndex();
         if (index != -1) {
            int queryType = this.getQueryType(index);
            menu.add(new SSQueryVerb(queryType, this, this._app));
         }
      }
   }

   private final int getQueryType(int index) {
      return index >= 0 && index < QUERY_TYPES_MAP.length ? QUERY_TYPES_MAP[index] : -1;
   }

   @Override
   public final boolean confirm(Verb verb, Object context) {
      if (verb == this._closeVerb && this._querying) {
         Status.show("Query in progress.  Please wait.");
         return false;
      } else {
         return true;
      }
   }

   @Override
   public final void drawListRow(ListField listField, Graphics graphics, int index, int y, int width) {
      switch (index) {
         case 0:
         default:
            graphics.drawText("Status: ", 0, y);
            if (this._querying) {
               graphics.drawText("Querying...", 80, y);
               return;
            }

            graphics.drawText("Idle", 80, y);
            return;
         case 1:
            graphics.drawText("Provisioned: ", 0, y);
            graphics.drawText(this._serviceAvailable ? "yes" : "no", 80, y);
            return;
         case 2:
            graphics.drawText("Active: ", 0, y);
            graphics.drawText(this._serviceActive ? "yes" : "no", 80, y);
         case -1:
      }
   }

   @Override
   public final int getPreferredWidth(ListField listField) {
      return 100;
   }

   @Override
   public final Object get(ListField listField, int index) {
      return null;
   }

   @Override
   public final int indexOfList(ListField listField, String prefix, int start) {
      return 0;
   }

   @Override
   public final void queryStarted() {
      this._serviceAvailable = false;
      this._serviceActive = false;
      this._querying = true;
      this.invalidate();
   }

   @Override
   public final void queryFinished(boolean provisioned, boolean active) {
      this._serviceAvailable = provisioned;
      this._serviceActive = active;
      this._querying = false;
      this.invalidate();
   }

   @Override
   public final void queryTimedOut() {
      this._querying = false;
      this._serviceAvailable = false;
      this._serviceAvailable = false;
      this.invalidate();
   }
}
