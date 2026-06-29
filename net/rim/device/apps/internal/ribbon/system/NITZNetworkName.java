package net.rim.device.apps.internal.ribbon.system;

import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.util.MathUtilities;
import net.rim.device.apps.api.ribbon.RibbonApi;
import net.rim.device.apps.internal.sms.SMSService;
import net.rim.device.internal.system.RadioInternal;

final class NITZNetworkName {
   private int _networkId = 0;
   private boolean _needLongNameCountryCode = false;
   private boolean _needShortNameCountryCode = false;
   private int _longNameEncoding = 0;
   private int _shortNameEncoding = 0;
   private int _longNameEncodedLength = 0;
   private int _shortNameEncodedLength = 0;
   private byte[] _longNameEncoded = null;
   private byte[] _shortNameEncoded = null;
   private String _longName = null;
   private String _shortName = null;

   final boolean queryNewNameData(int longLength, int shortLength) {
      this._shortNameEncodedLength = MathUtilities.clamp(0, shortLength, 30);
      this._longNameEncodedLength = MathUtilities.clamp(0, longLength, 30);
      if (this._shortNameEncodedLength == 0 && this._longNameEncodedLength == 0) {
         return false;
      }

      boolean shortChanged = false;
      boolean longChanged = false;
      if (!RadioInternal.getServingNetworkNameString(this)) {
         if (RibbonApi._logONSState) {
            System.out.println("NITZ data extraction failed!");
         }
      } else {
         this._networkId = RadioInfo.convertNetworkId(this._networkId);
         if (RibbonApi._logONSState) {
            System.out.println("NITZ data:");
            System.out.println("_networkId = " + String.valueOf(this._networkId));
            System.out.println("_needLongNameCountryCode = " + String.valueOf(this._needLongNameCountryCode));
            System.out.println("_needShortNameCountryCode = " + String.valueOf(this._needShortNameCountryCode));
            System.out.println("_longNameEncoding = " + String.valueOf(this._longNameEncoding));
            System.out.println("_shortNameEncoding = " + String.valueOf(this._shortNameEncoding));
            System.out.println("_longNameEncodedLength = " + String.valueOf(this._longNameEncodedLength));
            System.out.println("_shortNameEncodedLength = " + String.valueOf(this._shortNameEncodedLength));
         }

         if (this._longNameEncoded != null && this._longNameEncoded.length > 0) {
            if (RibbonApi._logONSState) {
               System.out.println("_longNameEncoded = " + new String(this._longNameEncoded));
            }

            String newLongName = SMSService.decodeSMSData(this._longNameEncoding, this._longNameEncoded);
            this._longNameEncoded = null;
            if (newLongName != null && !newLongName.equals(this._longName)) {
               this._longName = newLongName;
               longChanged = true;
               if (RibbonApi._logONSState) {
                  System.out.println("_longName = " + this._longName);
               }
            }
         } else if (this._longName != null) {
            longChanged = true;
            this._longName = null;
            if (RibbonApi._logONSState) {
               System.out.println("_longName = null");
            }
         }

         if (this._shortNameEncoded != null && this._shortNameEncoded.length > 0) {
            if (RibbonApi._logONSState) {
               System.out.println("_shortNameEncoded = " + new String(this._shortNameEncoded));
            }

            String newShortName = SMSService.decodeSMSData(this._shortNameEncoding, this._shortNameEncoded);
            this._shortNameEncoded = null;
            if (newShortName != null && !newShortName.equals(this._shortName)) {
               this._shortName = newShortName;
               shortChanged = true;
               if (RibbonApi._logONSState) {
                  System.out.println("_shortName = " + this._shortName);
               }
            }
         } else if (this._shortName != null) {
            shortChanged = true;
            this._shortName = null;
            if (RibbonApi._logONSState) {
               System.out.println("_shortName = null");
            }
         }
      }

      return shortChanged || longChanged;
   }

   protected final boolean longNameIsValid(int netId) {
      if (this._longName == null) {
         if (RibbonApi._logONSState) {
            System.out.println("NITZ long name is null");
         }

         return false;
      } else if (netId != this._networkId) {
         if (RibbonApi._logONSState) {
            System.out.println("NITZ ignored: " + String.valueOf(netId) + " != " + this._networkId);
         }

         return false;
      } else {
         return true;
      }
   }

   protected final boolean shortNameIsValid(int netId) {
      if (this._shortName == null) {
         if (RibbonApi._logONSState) {
            System.out.println("NITZ short name is null");
         }

         return false;
      } else {
         return netId == this._networkId;
      }
   }

   protected final String getLongName(String countryInitials) {
      return this._needLongNameCountryCode && countryInitials != null && countryInitials.length() > 0 ? this._longName + countryInitials : this._longName;
   }

   protected final String getShortName(String countryInitials) {
      return this._needShortNameCountryCode && countryInitials != null && countryInitials.length() > 0 ? this._shortName + countryInitials : this._shortName;
   }
}
