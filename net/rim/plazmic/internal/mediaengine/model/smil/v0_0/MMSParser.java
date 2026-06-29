package net.rim.plazmic.internal.mediaengine.model.smil.v0_0;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.i18n.ResourceBundleFamily;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Field;
import net.rim.device.apps.internal.mms.resources.MMSResources;
import net.rim.device.internal.ui.component.AnimatedBitmapField;
import net.rim.plazmic.internal.mediaengine.event.EventLogic;
import net.rim.plazmic.internal.mediaengine.model.smil.v0_0.timing.MediaObject;
import net.rim.plazmic.internal.mediaengine.model.smil.v0_0.timing.Par;
import net.rim.plazmic.internal.mediaengine.model.smil.v0_0.timing.TimeContainer;
import net.rim.plazmic.internal.mediaengine.model.smil.v0_0.ui.ActiveRichTextFieldWrapper;
import net.rim.plazmic.internal.mediaengine.model.smil.v0_0.ui.ScalableBitmapField;
import net.rim.plazmic.internal.mediaengine.model.smil.v0_0.ui.Slide;
import net.rim.plazmic.internal.mediaengine.model.smil.v0_0.ui.SlideManager;
import net.rim.plazmic.internal.mediaengine.model.smil.v0_0.ui.VideoField;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class MMSParser extends SMILParser {
   private int _rootLayoutBackgroundColor = -1;
   private int _imageRegionBackgroundColor = -1;
   private int _textRegionBackgroundColor = -1;
   private SlideManager _slideManager;
   private Slide _currentSlide;

   @Override
   public SMILModel parse(Document dom) {
      MMSModel model = new MMSModel();
      this.parse(dom, model);
      model.setSlideManager(this._slideManager);
      if (model.hasAudio() || model.hasVideo()) {
         this._slideManager.addVolumeControl();
      }

      return model;
   }

   @Override
   protected void parseRootLayout(Element layout, SMILModel model) {
      super.parseRootLayout(layout, model);
      Region rootLayout = model.getRootLayout();
      rootLayout.setWidth(Display.getWidth(), false);
      rootLayout.setHeight(Display.getHeight(), false);
      this._rootLayoutBackgroundColor = rootLayout.getBackgroundColor();
      this._slideManager = new SlideManager(0);
      rootLayout.add(this._slideManager);
   }

   @Override
   protected void parseRegion(Element region, SMILModel model) {
      super.parseRegion(region, model);
      Region rootLayout = model.getRootLayout();
      Region smilRegion = (Region)rootLayout.getField(1);
      rootLayout.delete(smilRegion);
      String name = this.getRegionName(region);
      int backgroundColor = smilRegion.getBackgroundColor();
      if (name.equals("Text")) {
         this._textRegionBackgroundColor = backgroundColor == -1 ? this._rootLayoutBackgroundColor : backgroundColor;
      } else if (name.equals("Image")) {
         this._imageRegionBackgroundColor = backgroundColor == -1 ? this._rootLayoutBackgroundColor : backgroundColor;
      } else {
         if (this._imageRegionBackgroundColor == -1) {
            this._imageRegionBackgroundColor = this._rootLayoutBackgroundColor;
         }

         if (this._textRegionBackgroundColor == -1) {
            this._textRegionBackgroundColor = this._rootLayoutBackgroundColor;
         }
      }
   }

   protected String getRegionName(Element region) {
      String name = "";
      if (region.hasAttribute("regionName")) {
         return region.getAttribute("regionName");
      }

      if (region.hasAttribute("id")) {
         name = region.getAttribute("id");
      }

      return name;
   }

   @Override
   protected void parseParAttributes(Element element, Par par, TimeContainer parent, EventLogic logic) {
      super.parseParAttributes(element, par, parent, logic);
      this._currentSlide = new Slide(0, this._imageRegionBackgroundColor, this._textRegionBackgroundColor);
      this._slideManager.addSlide(par.getId(), this._currentSlide);
   }

   @Override
   protected void parseMediaObjectFields(Element element, MediaObject mediaObject, EventLogic logic, SMILModel model) {
      super.parseMediaObjectFields(element, mediaObject, logic, model);
      if (!mediaObject.realizedSuccessfully()) {
         if (element.getTagName().equals("img") || element.getAttribute("region").equalsIgnoreCase("Image")) {
            mediaObject.setURI(MMSResources.getBrokenImageURI());
            mediaObject.realize();
            this._currentSlide.setSlideStyle(1);
         } else if ((element.getTagName().equals("text") || element.getAttribute("region").equalsIgnoreCase("Text")) && mediaObject.getAlt().length() == 0) {
            ResourceBundleFamily mmsResources = ResourceBundle.getBundle(8432718016989017157L, "net.rim.device.apps.internal.resource.MMS");
            String msg = mmsResources.getString(129);
            mediaObject.setAlt(msg);
            mediaObject.realize();
         }
      }

      Field uiComponent = mediaObject.getUIComponent();
      if (uiComponent instanceof ScalableBitmapField || uiComponent instanceof VideoField) {
         this._currentSlide.setImageComponent(uiComponent);
         mediaObject.setRegion(this._currentSlide.getImageRegion());
         if (uiComponent instanceof AnimatedBitmapField) {
            AnimatedBitmapField animatedBitmapField = (AnimatedBitmapField)uiComponent;
            if (animatedBitmapField.isAnimated()) {
               model.setHasAnimation(true);
               return;
            }
         }
      } else if (uiComponent instanceof ActiveRichTextFieldWrapper) {
         this._currentSlide.setTextComponent((ActiveRichTextFieldWrapper)uiComponent);
         mediaObject.setRegion(this._currentSlide.getTextRegion());
      }
   }
}
