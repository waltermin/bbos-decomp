package net.rim.plazmic.internal.mediaengine.model.smil.v0_0.ui;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.FontFamily;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.component.NumericChoiceField;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.util.IntHashtable;
import net.rim.device.api.util.IntVector;
import net.rim.device.apps.api.ui.VerbMenuItem;
import net.rim.device.apps.internal.mms.ui.MMSModelScreen;
import net.rim.plazmic.internal.mediaengine.model.smil.v0_0.SMILPlayer;
import net.rim.plazmic.internal.mediaengine.model.smil.v0_0.SMILVolumeControl;
import net.rim.plazmic.internal.mediaengine.model.smil.v0_0.VolumeChangeListener;
import net.rim.plazmic.internal.mediaengine.model.smil.v0_0.player.Player;
import net.rim.plazmic.internal.mediaengine.model.smil.v0_0.verbs.MMSNextSlideVerb;
import net.rim.plazmic.internal.mediaengine.model.smil.v0_0.verbs.MMSPrevSlideVerb;

public class SlideManager extends Manager implements FieldChangeListener, VolumeChangeListener {
   private IntHashtable _slides;
   private IntVector _ids;
   int _numSlides = 0;
   private int _idSlideInFocus = -1;
   private SMILPlayer _player;
   private final VerbMenuItem _nextVerb;
   private final VerbMenuItem _previousVerb;
   private NumericChoiceField _volumeField;
   private RichTextField _slideNumberField;
   private String _slideNumberLabel;
   private static String VOLUME_FONT_NAME = "System";
   private static String SLIDE_NUM_FONT_NAME = "System";
   private static int GAP = 4;
   private static int BORDER_GAP = 2;

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public void addVolumeControl() {
      if (this._volumeField == null) {
         ResourceBundle mmsResources = ResourceBundle.getBundle(8432718016989017157L, "net.rim.device.apps.internal.resource.MMS");
         String volumeStr = mmsResources.getString(117);
         this._volumeField = (NumericChoiceField)(new Object(volumeStr, 0, 9, 1, SMILPlayer.getVolumeLevel() / 11, 4503608217305088L));
         boolean var5 = false /* VF: Semaphore variable */;

         label24:
         try {
            var5 = true;
            this._volumeField.setFont(FontFamily.forName(VOLUME_FONT_NAME).getFont(0, 14));
            var5 = false;
         } finally {
            if (var5) {
               this._volumeField.setFont(this.getFont().getFontFamily().getFont(0, 14));
               break label24;
            }
         }

         this.add(this._volumeField);
      }

      this._volumeField.setChangeListener(this);
   }

   public int getSlideCount() {
      return this._numSlides;
   }

   public void prevSlide() {
      int curSlideIdx = this._ids.indexOf(this._idSlideInFocus);
      if (curSlideIdx > 0) {
         if (this._player != null) {
            this._player.endPlayback();
         }

         int prevSlideId = this._ids.elementAt(curSlideIdx - 1);
         this.setFocusToSlide(prevSlideId);
         Slide prevSlide = (Slide)this._slides.get(prevSlideId);
         this.animateImageComponent(prevSlide);
      }
   }

   public void setPlayer(SMILPlayer player) {
      this._player = player;
      if (this._volumeField != null) {
         SMILVolumeControl vc = this._player.getVolumeControl();
         vc.setVolumeChangeListener(this);
      }
   }

   public void addSlide(int id, Slide slide) {
      this._slides.put(id, slide);
      this._ids.addElement(id);
      this._numSlides++;
   }

   public void setFocusToSlide(int id) {
      if (id != this._idSlideInFocus) {
         int oldSlideID = this._idSlideInFocus;
         this.addSlideToView(id);
         this.removeSlideFromView(oldSlideID);
         int curSlide = this._ids.indexOf(this._idSlideInFocus) + 1;
         this._slideNumberField
            .setText(((StringBuffer)(new Object())).append(this._slideNumberLabel).append(" ").append(curSlide).append("/").append(this._numSlides).toString());
      }
   }

   public void nextSlide() {
      int curSlideIdx = this._ids.indexOf(this._idSlideInFocus);
      if (curSlideIdx < this._numSlides - 1) {
         if (this._player != null) {
            this._player.endPlayback();
         }

         int nextSlideId = this._ids.elementAt(curSlideIdx + 1);
         this.setFocusToSlide(nextSlideId);
         Slide nextSlide = (Slide)this._slides.get(nextSlideId);
         this.animateImageComponent(nextSlide);
      }
   }

   @Override
   public void volumeChanged(int newLevel) {
      if (this._volumeField != null) {
         this._volumeField.setSelectedIndex(newLevel / 11);
      }
   }

   @Override
   public void fieldChanged(Field field, int context) {
      field = field.getOriginal();
      if (field == this._volumeField) {
         int newVolume = this._volumeField.getSelectedIndex() * 11;
         this._player.getVolumeControl().setLevel(newVolume);
         field.setDirty(false);
      }
   }

   private void addSlideToView(int id) {
      Slide newSlide = (Slide)this._slides.get(id);
      if (newSlide != null) {
         this.add(newSlide);
         this._idSlideInFocus = id;
      } else {
         this._idSlideInFocus = -1;
      }
   }

   @Override
   public void onDisplay() {
      super.onDisplay();
      Object screen = UiApplication.getUiApplication().getActiveScreen();
      if (screen instanceof Object) {
         MMSModelScreen mmsScreen = (MMSModelScreen)screen;
         if (this._volumeField != null) {
            mmsScreen.setVolumeControl(this._player.getVolumeControl());
            return;
         }

         mmsScreen.setVolumeControl(null);
      }
   }

   private void removeSlideFromView(int id) {
      Slide currentSlide = (Slide)this._slides.get(id);
      if (currentSlide != null) {
         this.delete(currentSlide);
      }
   }

   private void animateImageComponent(Slide slide) {
      Field imageComponent = slide.getImageComponent();
      if (imageComponent != null && imageComponent instanceof Object) {
         ScalableBitmapField sbf = (ScalableBitmapField)imageComponent;
         Player player = (Player)(new Object(sbf, 200));
         player.start();
      }
   }

   @Override
   public void sublayout(int availWidth, int availHeight) {
      int slideNumberHeight = this._slideNumberField.getContentHeight();
      int slideNumberPosX = 20;
      int slidePosY = slideNumberHeight + GAP + BORDER_GAP;
      this.layoutChild(this._slideNumberField, availWidth, slideNumberHeight);
      this.setPositionChild(this._slideNumberField, slideNumberPosX, BORDER_GAP);
      if (this._volumeField != null) {
         int volumeHeight = this._volumeField.getPreferredHeight();
         int volumeWidth = this._volumeField.getPreferredWidth() + 1;
         int volumePosX = availWidth - volumeWidth - 20;
         slidePosY = Math.max(slidePosY, volumeHeight + GAP + BORDER_GAP);
         this.layoutChild(this._volumeField, volumeWidth, volumeHeight);
         this.setPositionChild(this._volumeField, volumePosX, BORDER_GAP);
      }

      for (int i = 0; i < this.getFieldCount(); i++) {
         Field child = this.getField(i);
         if (child != this._volumeField && child != this._slideNumberField) {
            this.layoutChild(child, availWidth, availHeight - slidePosY);
            this.setPositionChild(child, 0, slidePosY);
         }
      }

      this.setExtent(availWidth, availHeight);
      this.setVirtualExtent(availWidth, availHeight);
   }

   @Override
   protected void makeMenu(Menu menu, int instance) {
      int curSlideIdx = this._ids.indexOf(this._idSlideInFocus);
      if (curSlideIdx > 0) {
         menu.add(this._previousVerb);
      }

      if (curSlideIdx < this._numSlides - 1) {
         menu.add(this._nextVerb);
      }
   }

   @Override
   protected boolean trackwheelRoll(int amount, int status, int time) {
      if ((status & 1) == 1) {
         if (amount < 0) {
            this.prevSlide();
            return true;
         } else {
            this.nextSlide();
            return true;
         }
      } else {
         return super.trackwheelRoll(amount, status, time);
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public SlideManager(long style) {
      super(style);
      this._slides = (IntHashtable)(new Object());
      this._ids = (IntVector)(new Object());
      this._nextVerb = (VerbMenuItem)(new Object(new MMSNextSlideVerb(this), 500));
      this._previousVerb = (VerbMenuItem)(new Object(new MMSPrevSlideVerb(this), 450));
      this._slideNumberField = (RichTextField)(new Object(36028797018963968L));
      boolean var5 = false /* VF: Semaphore variable */;

      label20:
      try {
         var5 = true;
         this._slideNumberField.setFont(FontFamily.forName(SLIDE_NUM_FONT_NAME).getFont(0, 14));
         var5 = false;
      } finally {
         if (var5) {
            this._slideNumberField.setFont(this.getFont().getFontFamily().getFont(0, 14));
            break label20;
         }
      }

      this.add(this._slideNumberField);
      ResourceBundle mmsResources = ResourceBundle.getBundle(8432718016989017157L, "net.rim.device.apps.internal.resource.MMS");
      this._slideNumberLabel = mmsResources.getString(118);
   }
}
