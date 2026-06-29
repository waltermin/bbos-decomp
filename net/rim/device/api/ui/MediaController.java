package net.rim.device.api.ui;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.plazmic.internal.mediaengine.service.FocusInteractor;

class MediaController extends Field {
   private MenuItem _activateMenu;
   public static final int ORDINAL_TOP = 0;
   public static final int PRIORITY_LOW = 100;
   private static ResourceBundle _resources = ResourceBundle.getBundle(7570626400829226451L, "net.rim.device.internal.resource.MediaEngine");

   public MediaController(long style) {
      super(style);
   }

   @Override
   protected void layout(int width, int height) {
   }

   @Override
   protected void paint(Graphics graphics) {
   }

   @Override
   protected boolean trackwheelClick(int status, int time) {
      return this.activate();
   }

   @Override
   protected boolean navigationClick(int status, int time) {
      return this.activate();
   }

   private boolean activate() {
      if ((this.getFieldStyle() & 1048576) != 0) {
         FocusInteractor cia = ((MediaField)this.getManager())._focusInteractor;
         if (cia != null) {
            return cia.activateItemInFocus();
         }
      }

      return false;
   }

   @Override
   protected boolean invokeAction(int action) {
      switch (action) {
         case 1:
            return this.activate();
         default:
            return false;
      }
   }

   @Override
   protected void onFocus(int direction) {
      FocusInteractor cia = ((MediaField)this.getManager())._focusInteractor;
      if (cia != null && !cia.hasFocus()) {
         cia.moveFocus(direction > 0 ? 1 : -1, 1, cia.getWrap());
      }
   }

   @Override
   protected void onUnfocus() {
      MediaField mf = (MediaField)this.getManager();
      FocusInteractor cia = mf._focusInteractor;
      if (cia != null) {
         Field inFocus = mf.getFieldWithFocus();
         if (!mf.isForeignObject(inFocus)) {
            cia.setFocusToItem(-1);
         }
      }

      super.onUnfocus();
   }

   @Override
   protected int moveFocus(int amount, int status, int time) {
      FocusInteractor cia = ((MediaField)this.getManager())._focusInteractor;
      if (cia != null) {
         int remaining = cia.moveFocus(amount > 0 ? 1 : -1, Math.abs(amount), cia.getWrap());
         amount = amount > 0 ? remaining : -remaining;
      }

      return amount;
   }

   @Override
   protected boolean keyChar(char character, int status, int time) {
      FocusInteractor cia = ((MediaField)this.getManager())._focusInteractor;
      if (cia != null) {
         if (cia.keyChar(character, status)) {
            return true;
         }

         if (character == '\n') {
            cia.activateItemInFocus();
            return true;
         }
      }

      return super.keyChar(character, status, time);
   }

   @Override
   public ContextMenu getContextMenu() {
      ContextMenu menu = ContextMenu.getInstance();
      menu.setTarget(this);
      if ((this.getFieldStyle() & 2097152) == 0) {
         if (this._activateMenu == null) {
            this._activateMenu = new MediaController$1(this, _resources.getString(0), 0, 100);
         }

         FocusInteractor cia = ((MediaField)this.getManager())._focusInteractor;
         if (cia != null && cia.hasFocus()) {
            menu.addItem(this._activateMenu);
            menu.setDefaultItem(this._activateMenu);
         }
      }

      return menu;
   }

   @Override
   public void getFocusRect(XYRect rect) {
      throw new RuntimeException("cod2jar: tail call (jumpspecial)");
   }
}
