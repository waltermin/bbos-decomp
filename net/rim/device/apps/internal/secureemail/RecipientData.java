package net.rim.device.apps.internal.secureemail;

import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.internal.blackberryemail.header.EmailHeaderModel;

public class RecipientData {
   private String _name;
   private int _type;
   private String[] _addresses;
   private Object _certificateID;
   private EmailHeaderModel _emailHeaderModel;
   private boolean _isExcluded;
   private int _newEncodingAction;
   private RecipientData$CertificateDetails[] _recommendedCertificates;
   private RecipientData$CertificateDetails[] _allowedCertificates;
   private RecipientData$CertificateDetails[] _allCertificates;
   private RecipientData$CertificateDetails[] _selectedCertificates;
   public static final int TYPE_UI_RECIPIENT = 0;
   public static final int TYPE_ALWAYS_BCC_RECIPIENT = 1;
   public static final int TYPE_EMS_RECIPIENT = 2;
   public static final int ADK_RECIPIENT = 3;

   public RecipientData(String name, int type, String[] addresses, EmailHeaderModel emailHeaderModel) {
      this._name = name;
      this._type = type;
      this._addresses = addresses;
      this._emailHeaderModel = emailHeaderModel;
      this._isExcluded = false;
   }

   public RecipientData(String name, int type, Object certificateID, EmailHeaderModel emailHeaderModel) {
      this._name = name;
      this._type = type;
      this._certificateID = certificateID;
      this._emailHeaderModel = emailHeaderModel;
      this._isExcluded = false;
   }

   public String getName() {
      if (this._name != null) {
         return this._name;
      } else {
         return this._addresses != null && this._addresses.length > 0 ? this._addresses[0] : null;
      }
   }

   public int getType() {
      return this._type;
   }

   public String[] getAddresses() {
      return this._addresses;
   }

   public Object getCertificateID() {
      return this._certificateID;
   }

   public EmailHeaderModel getEmailHeaderModel() {
      return this._emailHeaderModel;
   }

   public void setRecommendedCertificates(RecipientData$CertificateDetails[] recommendedCertificates) {
      this._recommendedCertificates = recommendedCertificates;
   }

   public RecipientData$CertificateDetails[] getRecommendedCertificates() {
      return this._recommendedCertificates;
   }

   public void setAllowedCertificates(RecipientData$CertificateDetails[] allowedCertificates) {
      this._allowedCertificates = allowedCertificates;
   }

   public RecipientData$CertificateDetails[] getAllowedCertificates() {
      return this._allowedCertificates;
   }

   public void setAllCertificates(RecipientData$CertificateDetails[] allCertificates) {
      this._allCertificates = allCertificates;
   }

   public RecipientData$CertificateDetails[] getAllCertificates() {
      return this._allCertificates;
   }

   public void setSelectedCertificates(RecipientData$CertificateDetails[] selectedCertificates) {
      this._selectedCertificates = selectedCertificates;
   }

   public RecipientData$CertificateDetails[] getSelectedCertificates() {
      return this._selectedCertificates;
   }

   public void resetCertificateLists() {
      this.setRecommendedCertificates(null);
      this.setAllowedCertificates(null);
      this.setAllCertificates(null);
      this.setSelectedCertificates(null);
   }

   public void setExcluded(int newEncodingAction) {
      this._newEncodingAction = newEncodingAction;
      this._isExcluded = true;
   }

   public boolean isExcluded() {
      return this._isExcluded;
   }

   public int getNewEncodingAction() {
      return this._newEncodingAction;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }

      if (obj instanceof RecipientData) {
         RecipientData recipientData = (RecipientData)obj;
         if (this._emailHeaderModel == recipientData._emailHeaderModel && StringUtilities.strEqual(this._name, recipientData._name)) {
            if (this._addresses == null && recipientData._addresses == null) {
               return true;
            }

            if (this._addresses != null && recipientData._addresses != null && this._addresses.length == recipientData._addresses.length) {
               for (int i = 0; i < this._addresses.length; i++) {
                  if (!StringUtilities.strEqual(this._addresses[i], recipientData._addresses[i])) {
                     return false;
                  }
               }

               return true;
            }
         }
      }

      return false;
   }
}
