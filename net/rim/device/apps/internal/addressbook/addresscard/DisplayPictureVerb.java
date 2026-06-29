package net.rim.device.apps.internal.addressbook.addresscard;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.system.JPEGEncodedImage;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.Status;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.util.FactoryUtil;
import net.rim.device.apps.api.addressbook.AddressBookServices;
import net.rim.device.apps.api.addressbook.AddressCardModel;
import net.rim.device.apps.api.addressbook.AddressSelectionContext;
import net.rim.device.apps.api.addressbook.CompanyInfoModel;
import net.rim.device.apps.api.addressbook.DisplayPictureModel;
import net.rim.device.apps.api.addressbook.PersonNameModel;
import net.rim.device.apps.api.framework.file.ExplorerServices;
import net.rim.device.apps.api.framework.file.FileSelector;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.Recognizer;
import net.rim.device.apps.api.framework.registration.RecognizerRepository;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.ui.ZoomImageCropper;
import net.rim.device.apps.api.utility.editor.EditorUsingRIMModelFactory;
import net.rim.device.apps.internal.addressbook.BlackBerryAddressBook;
import net.rim.device.apps.internal.addressbook.resources.AddressBookResources;
import net.rim.device.internal.io.file.FileUtilities;

public final class DisplayPictureVerb extends Verb {
   private int _type;
   private FileSelector _fileSelector;
   private AddressCardModel _addressCard;
   static final int ADD_PICTURE_VERB = 0;
   static final int REPLACE_PICTURE_VERB = 1;
   static final int SAVE_PICTURE_VERB = 2;
   static final int SET_PICTURE_VERB = 3;
   static final int SET_AS_PICTURE_VERB = 4;
   static final int DELETE_PICTURE_VERB = 5;
   private static String _lastUsedFolder = "/store/home/user/pictures/";

   public DisplayPictureVerb(int type, int menuOrdering) {
      super(menuOrdering);
      this._type = type;
      this._fileSelector = new FileSelector(_lastUsedFolder, new ContextObject(39), 1, null);
      this._fileSelector.setSampleFolder("/store/samples/contacts/");
   }

   @Override
   public final String toString() {
      switch (this._type) {
         case -1:
            return "";
         case 0:
         case 3:
         default:
            return AddressBookResources.getString(900);
         case 1:
            return AddressBookResources.getString(901);
         case 2:
            return AddressBookResources.getString(902);
         case 4:
            return AddressBookResources.getString(903);
         case 5:
            return AddressBookResources.getString(904);
      }
   }

   @Override
   public final Object invoke(Object parameter) {
      EncodedImage image = null;
      byte[] imageData = null;
      ContextObject context = null;
      if (parameter instanceof ContextObject) {
         context = (ContextObject)parameter;
         this._addressCard = (AddressCardModel)context.get(3696141428889703675L);
         if (this._addressCard instanceof CompressedAddressCardModel) {
            this._addressCard = AddressCardCache.resolve((CompressedAddressCardModel)this._addressCard);
         }

         Object object = context.get(2765042845091913199L);
         if (!(object instanceof String)) {
            imageData = (byte[])context.get(8849067667159082262L);
         } else {
            String filename = (String)object;
            filename = "file://" + filename;
            image = FileUtilities.getEncodedImage(filename);
            if (image != null) {
               imageData = image.getData();
            }
         }
      }

      switch (this._type) {
         case -1:
            break;
         case 0:
         case 1:
         default:
            this.getAndSetPicture();
            return null;
         case 2:
            AddressBitmapField abf = (AddressBitmapField)Ui.getUiEngine().getActiveScreen().getLeafFieldWithFocus();
            this.saveImage(abf.getImage());
            return null;
         case 3:
            image = this.cropAndCompressImage(this.getImageFromFileSystem());
            if (image != null) {
               this.updateAddressCard(image);
               return null;
            }
            break;
         case 4:
            if (imageData != null) {
               image = this.cropAndCompressImage(EncodedImage.createEncodedImage(imageData, 0, imageData.length));
               if (image != null) {
                  this.getAddressToUpdate();
                  if (this._addressCard != null) {
                     this.updateAddressCard(image);
                  }
               }
            }
            break;
         case 5:
            this.setBitmapField(null);
            return null;
      }

      return null;
   }

   private final void getAndSetPicture() {
      EncodedImage image = this.getImageFromFileSystem();
      if (image != null) {
         image = this.cropAndCompressImage(image);
         if (image != null) {
            image.setScale(2);
            this.setBitmapField(image);
         }
      }
   }

   private final void getAddressToUpdate() {
      Verb addressSelectionVerb = AddressBookServices.getAddressSelectionVerb(0);
      if (addressSelectionVerb != null) {
         Recognizer recognizer = RecognizerRepository.getRecognizers(-3124646573404667739L);
         AddressSelectionContext selectionContext = new AddressSelectionContext(AddressBookResources.getString(903), "", null, recognizer, null);
         this._addressCard = (AddressCardModel)addressSelectionVerb.invoke(selectionContext);
         if (this._addressCard instanceof CompressedAddressCardModel) {
            this._addressCard = AddressCardCache.resolve((CompressedAddressCardModel)this._addressCard);
            return;
         }
      } else {
         this._addressCard = null;
      }
   }

   private final void updateAddressCard(EncodedImage image) {
      if (image != null) {
         DisplayPictureModelImpl displayPictureModel = (DisplayPictureModelImpl)FactoryUtil.createInstance(2940120466515154418L, image.getData());
         if (displayPictureModel.getDisplayPicture() != null && this._addressCard != null) {
            this._addressCard = AddressCardUtilities.expandGroup(this._addressCard);
            Bitmap originalBitmap = null;
            DisplayPictureModel oldImage = this._addressCard.getContactPicture(new ContextObject(11));
            if (oldImage != null) {
               originalBitmap = oldImage.getDisplayBitmap();
            }

            AddressCardModel newEntry = new AddressCardModelImpl(this._addressCard);
            boolean updateAddress = false;
            if (originalBitmap == null) {
               newEntry.add(displayPictureModel);
               updateAddress = true;
            } else {
               Dialog d = new Dialog(3, AddressBookResources.getString(907), -1, originalBitmap, 0);
               int response = d.doModal();
               if (response == 4) {
                  newEntry.remove(oldImage);
                  newEntry.add(displayPictureModel);
                  updateAddress = true;
               }
            }

            if (updateAddress) {
               newEntry = (AddressCardModel)((AddressCardModelImpl)newEntry).makeReadOnly();
               BlackBerryAddressBook.getAddressBook().forceUpdateAddressCard(this._addressCard, newEntry);
               Dialog.alert(AddressBookResources.getString(908));
            }
         }
      }
   }

   private final String getDefaultFilename() {
      String defaultFilename = "";
      if (this._addressCard != null) {
         PersonNameModel name = this._addressCard.getName();
         if (name != null) {
            String firstName = name.getFirstName();
            String lastName = name.getLastName();
            if (firstName != null) {
               defaultFilename = defaultFilename + firstName;
            }

            if (lastName != null) {
               defaultFilename = defaultFilename + lastName;
            }
         } else {
            CompanyInfoModel company = this._addressCard.getCompanyInfo();
            if (company != null) {
               defaultFilename = defaultFilename + company.getCompanyName();
            }
         }
      }

      if (defaultFilename == null) {
         defaultFilename = "IMG" + this._addressCard.getUID();
      }

      return FileUtilities.makeValidFilename(defaultFilename);
   }

   private final EncodedImage getImageFromFileSystem() {
      EncodedImage image = null;
      String fileURL = this._fileSelector.selectFile(_lastUsedFolder);
      if (fileURL != null) {
         fileURL = FileUtilities.makeFileURL(fileURL);
         image = FileUtilities.getEncodedImage(fileURL);
         _lastUsedFolder = FileUtilities.getPath(fileURL);
      }

      return image;
   }

   private final void setBitmapField(EncodedImage image) {
      Screen screen = Ui.getUiEngine().getActiveScreen();
      if (screen instanceof EditorUsingRIMModelFactory) {
         EditorUsingRIMModelFactory editScreen = (EditorUsingRIMModelFactory)screen;
         Field field = editScreen.findField(new DisplayPictureModelFactory());
         if (field instanceof VerticalFieldManager) {
            VerticalFieldManager vfm = (VerticalFieldManager)field;
            field = vfm.getField(1);
            if (field instanceof AddressBitmapField) {
               AddressBitmapField bitmapField = (AddressBitmapField)field;
               bitmapField.setImage(image);
               bitmapField.setDirty(true);
            }
         }
      }
   }

   private final EncodedImage cropAndCompressImage(EncodedImage image) {
      if (image == null) {
         return null;
      }

      ZoomImageCropper zis = new ZoomImageCropper(image, 72, 96, 1);
      UiApplication.getUiApplication().pushModalScreen(zis);
      if (zis.isCancelled()) {
         return null;
      }

      Bitmap bitmap = zis.getBitmap();
      int qualityFactor = 75;

      do {
         image = JPEGEncodedImage.encode(bitmap, qualityFactor);
         qualityFactor -= 5;
      } while (image.getLength() > 3072 && qualityFactor >= 50);

      return image;
   }

   private final boolean saveImage(EncodedImage image) {
      if (image != null && image.getData() != null) {
         String defaultFilename = this.getDefaultFilename();
         if (image.getImageType() == 3) {
            defaultFilename = defaultFilename + ".jpg";
         }

         InputStream input = new ByteArrayInputStream(image.getData(), image.getOffset(), image.getLength());
         if (input != null) {
            String filename = ExplorerServices.saveInputStream(defaultFilename, input, 1, true, false);
            if (filename != null) {
               Status.show(AddressBookResources.getString(906));
               return true;
            }
         }

         return false;
      } else {
         Status.show(AddressBookResources.getString(1102));
         return false;
      }
   }
}
