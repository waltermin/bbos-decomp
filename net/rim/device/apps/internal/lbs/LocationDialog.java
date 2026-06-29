package net.rim.device.apps.internal.lbs;

import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.FontRegistry;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.component.ActiveRichTextField;
import net.rim.device.api.ui.component.BitmapField;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.container.FlowFieldManager;
import net.rim.device.api.ui.container.PopupScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.util.FactoryUtil;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.framework.model.VerbProvider;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.apps.internal.lbs.resources.LBSResources;
import net.rim.device.internal.ui.component.VerticalSpacerField;

final class LocationDialog extends PopupScreen implements FieldChangeListener {
   private ButtonField _okField;
   private Field _firstField;
   private BitmapField _ratingField;
   private ActiveRichTextField _descriptionField;
   private ActiveRichTextField _addressField;
   private ActiveRichTextField _cityStateField;
   private ActiveRichTextField _countryPostalCodeField;
   private ActiveRichTextField _phoneField;
   private ActiveRichTextField _faxField;
   private ActiveRichTextField _urlField;
   private ActiveRichTextField _emailField;
   private FlowFieldManager _hfm = new FlowFieldManager(12884901888L);
   private Location _location = new Location();

   LocationDialog(Location location, String[] legalNotices) {
      super(new VerticalFieldManager(1153220571769602048L), 196608);
      this._location = location;
      this.applyTheme();
      this.addTitle(location._label);
      this.addRating(location._rating);
      this.addDescription(location._description);
      this.addAddress(location._address);
      this.addCityState(location._city, location._region);
      this.addCountryPostalCode(location._country, location._postalCode);
      this.addPhone(location._phone);
      this.addFax(location._fax);
      this.addURL(location._url);
      this.addEmail(location._email);
      this.addLegalNotices(legalNotices);
      this.addButtons();
      if (this._firstField != null) {
         this._firstField.setFocus();
      } else {
         this._okField.setFocus();
      }
   }

   private final void addTitle(String title) {
      if (title != null && title.length() > 0) {
         ActiveRichTextField labelField = new ActiveRichTextField(title);
         labelField.setFont(Font.getDefault().derive(1));
         this.add(labelField);
         this.add(new SeparatorField());
         this._firstField = labelField;
      }
   }

   private final void addRating(double rating) {
      if (rating >= 0L && rating != Double.MIN_VALUE) {
         Bitmap star = Bitmap.getBitmapResource("star.gif");
         int starWidth = star.getWidth();
         int starHeight = star.getHeight();
         int numStars = (int)rating;
         int numHalfStars = (int)(rating + 4602678819172646912L) - numStars;
         int numEmptyStars = 5 - numStars - numHalfStars;
         int width = 5 * (starWidth + 1);
         int height = starHeight + 2;
         Bitmap stars = new Bitmap(197, width, height);
         stars.createAlpha(2);
         Graphics graphics = new Graphics(stars);
         graphics.setGlobalAlpha(0);
         graphics.clear();
         graphics.setGlobalAlpha(255);
         int x = 0;
         int y = 1;

         for (int i = 0; i < numStars; i++) {
            graphics.drawBitmap(x, y, starWidth, starHeight, star, 0, 0);
            x += starWidth + 1;
         }

         if (numHalfStars == 1) {
            Bitmap starHalf = Bitmap.getBitmapResource("starHalf.gif");
            graphics.drawBitmap(x, y, starWidth, starHeight, starHalf, 0, 0);
            x += starWidth + 1;
         }

         if (numEmptyStars > 0) {
            Bitmap starEmpty = Bitmap.getBitmapResource("starEmpty.gif");

            for (int i = 0; i < numEmptyStars; i++) {
               graphics.drawBitmap(x, y, starWidth, starHeight, starEmpty, 0, 0);
               x += starWidth + 1;
            }
         }

         this._ratingField = new BitmapField(stars);
         if (this._firstField == null) {
            this._firstField = this._ratingField;
         }

         this.add(this._ratingField);
      }
   }

   private final void addDescription(String description) {
      if (description != null && description != "") {
         this._descriptionField = new ActiveRichTextField(description);
         if (this._firstField == null) {
            this._firstField = this._descriptionField;
         }

         this.add(this._descriptionField);
      }
   }

   private final void addAddress(String address) {
      if (address != null && !address.equals("")) {
         this._addressField = new ActiveRichTextField(address);
         this.add(this._addressField);
         if (this._firstField == null) {
            this._firstField = this._addressField;
         }
      }
   }

   private final void addCityState(String city, String state) {
      if (city != null && !city.equals("") && state != null && !state.equals("")) {
         this._cityStateField = new ActiveRichTextField(city + ", " + state);
      } else if (city != null && !city.equals("")) {
         this._cityStateField = new ActiveRichTextField(city);
      } else if (state != null && !state.equals("")) {
         this._cityStateField = new ActiveRichTextField(state);
      }

      if (this._cityStateField != null) {
         this.add(this._cityStateField);
         if (this._firstField == null) {
            this._firstField = this._cityStateField;
         }
      }
   }

   private final void addCountryPostalCode(String country, String postalCode) {
      if (country != null && !country.equals("") && postalCode != null && !postalCode.equals("")) {
         this._countryPostalCodeField = new ActiveRichTextField(country + ", " + postalCode);
      } else if (country != null && !country.equals("")) {
         this._countryPostalCodeField = new ActiveRichTextField(country);
      } else if (postalCode != null && !postalCode.equals("")) {
         this._countryPostalCodeField = new ActiveRichTextField(postalCode);
      }

      if (this._countryPostalCodeField != null) {
         this.add(this._countryPostalCodeField);
         if (this._firstField == null) {
            this._firstField = this._countryPostalCodeField;
         }
      }
   }

   private final void addPhone(String phone) {
      if (phone != null && !phone.equals("")) {
         this._phoneField = new ActiveRichTextField("Phone: " + phone);
         this.add(this._phoneField);
         if (this._firstField == null) {
            this._firstField = this._phoneField;
         }
      }
   }

   private final void addFax(String fax) {
      if (fax != null && !fax.equals("")) {
         this._faxField = new ActiveRichTextField("Fax: " + fax);
         this.add(this._faxField);
         if (this._firstField == null) {
            this._firstField = this._faxField;
         }
      }
   }

   private final void addURL(String url) {
      if (url != null && !url.equals("")) {
         this._urlField = new ActiveRichTextField("Web Page: " + url);
         this.add(this._urlField);
         if (this._firstField == null) {
            this._firstField = this._urlField;
         }
      }
   }

   private final void addEmail(String email) {
      if (email != null && !email.equals("")) {
         this._emailField = new ActiveRichTextField("Email: " + email);
         this.add(this._emailField);
         if (this._firstField == null) {
            this._firstField = this._emailField;
         }
      }
   }

   private final void addLegalNotices(String[] legalNotices) {
      if (legalNotices != null && legalNotices.length > 0) {
         int spaceHeight = Font.getDefault().derive(1).getHeight() >> 1;
         this.add(new VerticalSpacerField(spaceHeight));
         this.add(new SeparatorField(65536));
         this.add(new VerticalSpacerField(spaceHeight));

         for (int i = 0; i < legalNotices.length; i++) {
            this.add(new LabelField(legalNotices[i]));
            this.add(new VerticalSpacerField(spaceHeight));
         }
      }
   }

   @Override
   protected final void applyTheme() {
      super.applyTheme();
      int size = Ui.convertSize(FontRegistry.DEFAULT_SIZE, 3, 0);
      this.setFont(null);
      Font font = this.getFont();
      if (font.getHeight() > size && size > 0) {
         this.setFont(font.derive(font.getStyle(), size));
      }
   }

   private final void addButtons() {
      this._okField = new ButtonField(CommonResources.getString(117), 65536);
      this._okField.setChangeListener(this);
      this._hfm.add(this._okField);
      this.add(this._hfm);
   }

   public final void doModal() {
      Ui.getUiEngine().pushModalScreen(this);
   }

   @Override
   public final void fieldChanged(Field field, int context) {
      if (this.isDisplayed()) {
         if (field == this._okField) {
            this.close();
         }
      }
   }

   @Override
   protected final boolean keyDown(int keycode, int time) {
      int key = Keypad.key(keycode);
      if (key == 17 && this.getFieldWithFocus() == this._phoneField) {
         this.makePhoneCall();
         return true;
      } else if (key == 27) {
         this.close();
         return true;
      } else {
         return super.keyDown(keycode, time);
      }
   }

   private final void makePhoneCall() {
      boolean addressExists = this._location._address != null && this._location._address.length() > 0;
      boolean phoneExists = this._location._phone != null && this._location._phone.length() > 0;
      if (addressExists && phoneExists) {
         ContextObject tempContext = new ContextObject();
         tempContext.reset();
         if (this._location._phone != null) {
            long contextId = 253;
            tempContext.put(contextId, this._location._phone);
            RIMModel numberModel = (RIMModel)FactoryUtil.createInstance(3797587162219887872L, tempContext);
            if (numberModel == null) {
               throw new RuntimeException();
            }

            ContextObject contextObject = new ContextObject(34);
            ContextObject.setFlag(contextObject, 20);
            contextObject.put(253, this._location._phone);
            Verb[] verbs = new Verb[0];
            Verb defaultVerb = ((VerbProvider)numberModel).getVerbs(contextObject, verbs);
            if (Dialog.ask(3, MessageFormat.format(LBSResources.getString(286), new String[]{this._location._phone}), 4) == 4) {
               defaultVerb.invoke(contextObject);
            }
         }
      }
   }
}
