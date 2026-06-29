package net.rim.device.apps.internal.browser.img;

import net.rim.device.api.browser.field.BrowserContentBaseImpl;
import net.rim.device.api.browser.field.RenderingOptions;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.apps.api.ui.VerbMenuItem;
import net.rim.device.apps.api.ui.ZoomBitmapField;
import net.rim.device.apps.internal.browser.verbs.FullImageVerb;
import net.rim.device.apps.internal.browser.verbs.SaveImageVerb;
import net.rim.device.apps.internal.browser.verbs.ShowUrlVerb;

final class ImageRenderer$ZoomManager extends Manager {
   private ZoomBitmapField _field;
   private String _imageUrl;
   private BrowserContentBaseImpl _browserContent;
   private boolean _transcoded;
   private EncodedImage _img;
   private String _fileSystemFilename;

   public ImageRenderer$ZoomManager(
      ZoomBitmapField field, String url, BrowserContentBaseImpl content, boolean transcoded, EncodedImage img, String fileSystemFilename
   ) {
      super(0);
      this._field = field;
      this._imageUrl = url;
      this._browserContent = content;
      this._transcoded = transcoded;
      this._img = img;
      this._fileSystemFilename = fileSystemFilename;
   }

   @Override
   protected final void sublayout(int width, int height) {
      this.setVirtualExtent(width, height);
      this.setExtent(width, height);
      this.layoutChild(this._field, width, height);
   }

   @Override
   protected final void makeMenu(Menu contextMenu, int instance) {
      if (this._browserContent != null) {
         RenderingOptions renderingOptions = this._browserContent.getRenderingOptions();
         if (this._fileSystemFilename != null) {
            if (renderingOptions.getPropertyWithBooleanValue(4550690918222697397L, 40, true)
               && Graphics.isColor()
               && this._img != null
               && this._img.getData() != null) {
               contextMenu.add(new SetAsHomescreenBackgroundMenuItem(this._fileSystemFilename));
               return;
            }
         } else if (this._imageUrl != null) {
            if (renderingOptions.getPropertyWithBooleanValue(4550690918222697397L, 30, false)) {
               contextMenu.add((MenuItem)(new Object(new ShowUrlVerb(null, this._imageUrl, this._browserContent, 2), Integer.MAX_VALUE)));
            }

            if (this._transcoded && renderingOptions.getPropertyWithBooleanValue(4550690918222697397L, 38, false)) {
               VerbMenuItem menuItem = (VerbMenuItem)(new Object(new FullImageVerb(this._imageUrl, this._browserContent), 15));
               contextMenu.add(menuItem);
            }

            if (Graphics.isColor()
               && (renderingOptions.getPropertyWithBooleanValue(4550690918222697397L, 26, true) || this._field.isProtected())
               && this._img != null
               && this._img.getData() != null) {
               contextMenu.add((MenuItem)(new Object(new SaveImageVerb(this._imageUrl, this._img, this._field.isProtected()), Integer.MAX_VALUE)));
            }
         }
      }
   }
}
