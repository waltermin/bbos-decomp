package net.rim.device.apps.internal.bis.api.ui;

import net.rim.device.api.math.Fixed32;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.component.BitmapField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.apps.internal.bis.resource.ImageCache;

final class TitleBar extends VerticalFieldManager {
   private LabelField _title;
   private BitmapField _helpImage;
   private TitleBar$TitleAndHelpManager _titleAndHelpManager;
   private static final int TITLEBAR_FONT_SIZE_PT = 7;

   public TitleBar(String title) {
      this._title = new LabelField(title, 4294967360L);
      this._titleAndHelpManager = new TitleBar$TitleAndHelpManager();
      this._titleAndHelpManager.setTitleField(this._title);
      this.add(this._titleAndHelpManager);
   }

   @Override
   protected final void applyTheme() {
      super.applyTheme();
      this.setFont(Font.getDefault().derive(1, 7, 3));
   }

   public final void setTitle(String title) {
      this._title.setText(title);
   }

   public final void setHelpImage(BitmapField image) {
      if (this._helpImage == null) {
         this._helpImage = image;
         int scale = 0;
         int fontHeightInPixels = this.getFont().getHeight(0);
         int imageHeightInPixels = ImageCache.getHelpImage().getHeight();
         if (fontHeightInPixels > imageHeightInPixels) {
            scale = 1;
         } else {
            scale = Fixed32.div(fontHeightInPixels, imageHeightInPixels);
         }

         EncodedImage scaledImage = ImageCache.getHelpImage();
         scaledImage.setScale(scale);
         scaledImage.setDecodeMode(1);
         this._helpImage.setImage(scaledImage);
         this._titleAndHelpManager.setHelpField(this._helpImage);
      }
   }
}
