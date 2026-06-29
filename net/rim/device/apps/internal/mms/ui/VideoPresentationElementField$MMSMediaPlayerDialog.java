package net.rim.device.apps.internal.mms.ui;

import net.rim.device.api.ui.ContextMenu;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.theme.Tag;
import net.rim.device.apps.api.ribbon.RibbonBanner;
import net.rim.device.apps.internal.mms.api.AttachmentDataProvider;
import net.rim.device.apps.internal.mms.api.MMSAttachment;
import net.rim.device.internal.ui.component.PopupDialog;

class VideoPresentationElementField$MMSMediaPlayerDialog extends PopupDialog {
   private Field _banner;
   private VideoPresentationElementField$MMSMediaPlayerDialog$MMSMediaPlayerDialogManager _MMSMediaPlayerDialogManager = new VideoPresentationElementField$MMSMediaPlayerDialog$MMSMediaPlayerDialogManager(
      this
   );
   private static final Tag EXPLORER_BANNER_TAG = Tag.create("explorer-banner");

   public VideoPresentationElementField$MMSMediaPlayerDialog(
      MMSAttachment attachment, boolean isEditable, AttachmentDataProvider attachmentProvider, boolean isForwardLocked
   ) {
      super((Manager)(new Object()));
      this.setBorder(0, 0, 0, 0);
      this.setMargin(0, 0, 0, 0);
      this.setPadding(0, 0, 0, 0);
      this.add(this._MMSMediaPlayerDialogManager);
      this._banner = RibbonBanner.getInstance().getStatusBanner("", 3);
      this._banner.setTag(EXPLORER_BANNER_TAG);
      this._MMSMediaPlayerDialogManager.add(this._banner);
      BrowserPresentationElementField bpef = new BrowserPresentationElementField(attachment, isEditable, attachmentProvider, isForwardLocked);
      this._MMSMediaPlayerDialogManager.add(bpef);
   }

   @Override
   protected boolean keyChar(char key, int status, int time) {
      switch (key) {
         case '\u001b':
            this.close(-1);
         default:
            return super.keyChar(key, status, time);
      }
   }

   @Override
   public boolean onMenu(int instance) {
      Menu menu = this.getMenu(instance);
      menu.setAlignment(4294967296L, 34359738368L);
      menu.show();
      this.onMenuDismissed(menu);
      ContextMenu.getInstance().setTarget(null);
      Menu.setTargetScreen(null);
      return true;
   }

   @Override
   public Menu getMenu(int instance) {
      Menu menu = (Menu)(new Object(65536));
      Menu.setTargetScreen(this);
      menu.setInstance(instance);
      this.makeMenu(menu, instance);
      return menu;
   }

   @Override
   protected void makeMenu(Menu menu, int instance) {
      VideoPresentationElementField$MMSCloseVerb cv = new VideoPresentationElementField$MMSCloseVerb(this);
      MenuItem mi = (MenuItem)(new Object(cv, 0));
      menu.add(mi);
   }
}
