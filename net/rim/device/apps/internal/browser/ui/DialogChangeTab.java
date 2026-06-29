package net.rim.device.apps.internal.browser.ui;

import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.BitmapField;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.container.DialogFieldManager;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.PopupScreen;
import net.rim.device.apps.internal.browser.core.BrowserDaemonRegistry;
import net.rim.device.apps.internal.browser.page.BrowserScreen;
import net.rim.device.apps.internal.browser.resources.BrowserResources;
import net.rim.device.internal.ui.Image;
import net.rim.device.internal.ui.component.ImageField;

public final class DialogChangeTab extends PopupScreen {
   private static final int ICON_WIDTH = 16;
   private static final int ICON_HEIGHT = 16;

   public DialogChangeTab() {
      super(new DialogFieldManager(), 0);
      DialogFieldManager dfm = (DialogFieldManager)this.getDelegate();
      dfm.setMessage(new RichTextField(BrowserResources.getString(898), 36028797018963968L));
      BrowserScreen browserScreen = BrowserDaemonRegistry.getInstance().getBrowserScreen();
      Image defaultPageImage = BrowserIcons.getIcons().getImage(3);
      int numTabs = browserScreen.getNumberOfTabs();

      for (int i = 0; i < numTabs; i++) {
         HorizontalFieldManager hfm = new HorizontalFieldManager();
         EncodedImage img = browserScreen.getTabIcon(i);
         if (img != null) {
            BitmapField bf = new BitmapField();
            bf.setImage(img);
            hfm.add(bf);
         } else {
            ImageField imgField = new ImageField();
            imgField.setImage(defaultPageImage);
            imgField.setPreferredSize(16, 16);
            hfm.add(imgField);
         }

         hfm.add(new TabField(browserScreen.getTabTitle(i), i));
         dfm.addCustomField(hfm);
      }
   }

   public final void show() {
      UiApplication.getUiApplication().pushModalScreen(this);
   }

   @Override
   protected final boolean keyChar(char key, int status, int time) {
      if (super.keyChar(key, status, time)) {
         return true;
      }

      if (key != 27 && key != '\n') {
         return false;
      }

      UiApplication.getUiApplication().popScreen(this);
      return true;
   }

   @Override
   protected final boolean navigationClick(int status, int time) {
      UiApplication.getUiApplication().popScreen(this);
      return true;
   }
}
