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
            System.out.println(((StringBuffer)(new Object("_networkId = "))).append(String.valueOf(this._networkId)).toString());
            System.out.println(((StringBuffer)(new Object("_needLongNameCountryCode = "))).append(String.valueOf(this._needLongNameCountryCode)).toString());
            System.out.println(((StringBuffer)(new Object("_needShortNameCountryCode = "))).append(String.valueOf(this._needShortNameCountryCode)).toString());
            System.out.println(((StringBuffer)(new Object("_longNameEncoding = "))).append(String.valueOf(this._longNameEncoding)).toString());
            System.out.println(((StringBuffer)(new Object("_shortNameEncoding = "))).append(String.valueOf(this._shortNameEncoding)).toString());
            System.out.println(((StringBuffer)(new Object("_longNameEncodedLength = "))).append(String.valueOf(this._longNameEncodedLength)).toString());
            System.out.println(((StringBuffer)(new Object("_shortNameEncodedLength = "))).append(String.valueOf(this._shortNameEncodedLength)).toString());
         }

         if (this._longNameEncoded != null && this._longNameEncoded.length > 0) {
            if (RibbonApi._logONSState) {
               System.out.println(((StringBuffer)(new Object("_longNameEncoded = "))).append((String)(new Object(this._longNameEncoded))).toString());
            }

            String newLongName = SMSService.decodeSMSData(this._longNameEncoding, this._longNameEncoded);
            this._longNameEncoded = null;
            if (newLongName != null && !newLongName.equals(this._longName)) {
               this._longName = newLongName;
               longChanged = true;
               if (RibbonApi._logONSState) {
                  System.out.println(((StringBuffer)(new Object("_longName = "))).append(this._longName).toString());
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
               System.out.println(((StringBuffer)(new Object("_shortNameEncoded = "))).append((String)(new Object(this._shortNameEncoded))).toString());
            }

            String newShortName = SMSService.decodeSMSData(this._shortNameEncoding, this._shortNameEncoded);
            this._shortNameEncoded = null;
            if (newShortName != null && !newShortName.equals(this._shortName)) {
               this._shortName = newShortName;
               shortChanged = true;
               if (RibbonApi._logONSState) {
                  System.out.println(((StringBuffer)(new Object("_shortName = "))).append(this._shortName).toString());
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
            System.out
               .println(
                  ((StringBuffer)(new Object("NITZ ignored: ")))
                     .append(String.valueOf(netId))
                     .append(" != ")
                     .append(String.valueOf(this._networkId))
                     .toString()
               );
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
      return this._needLongNameCountryCode && countryInitials != null && countryInitials.length() > 0
         ? ((StringBuffer)(new Object())).append(this._longName).append(countryInitials).toString()
         : this._longName;
   }

   protected final String getShortName(String countryInitials) {
      return this._needShortNameCountryCode && countryInitials != null && countryInitials.length() > 0
         ? ((StringBuffer)(new Object())).append(this._shortName).append(countryInitials).toString()
         : this._shortName;
   }
}
