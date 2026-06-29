package net.rim.device.apps.internal.phone.pattern;

import net.rim.device.api.system.PersistentContentListener;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.util.IntEnumeration;
import net.rim.device.api.util.IntHashtable;
import net.rim.device.api.util.IntVector;
import net.rim.vm.Persistable;

final class SmartDialingOptions$PersistedSmartDialingOptions implements Persistable, PersistentContentListener {
   int _countryCode;
   int _nationalPhoneNumberLength;
   boolean _enableSmartDialing;
   boolean _autoAppendNDD;
   String _areaCode;
   boolean _allowPromptForAreaCode;
   String _corporatePhoneNumber;
   IntHashtable _standardExtensionsAdditionalTones = new IntHashtable();
   IntHashtable _corporateExtensionsAdditionalTones = new IntHashtable();
   int _corporateExtensionLength;
   int[] _corporateExtensionLengthExclusions;
   String _formattingString;

   SmartDialingOptions$PersistedSmartDialingOptions() {
      this.reset();
   }

   final void reset() {
      this._countryCode = SmartDialingOptions.getDefaultCountryCode();
      this._nationalPhoneNumberLength = SmartDialingOptions.getDefaultNationalPhoneNumberLength(this._countryCode);
      this._enableSmartDialing = true;
      this._autoAppendNDD = SmartDialingOptions.getDefaultAutoAppendNDDForDialing();
      this._areaCode = null;
      this._allowPromptForAreaCode = true;
      this._corporatePhoneNumber = "";
      this._corporateExtensionsAdditionalTones.clear();
      this._standardExtensionsAdditionalTones.clear();
      this._corporateExtensionLength = 0;
      this._corporateExtensionLengthExclusions = new int[0];
      this._formattingString = null;
   }

   public final int[] getAdditionalTonesNetworks() {
      IntVector vector = new IntVector();
      vector.addElement(RadioInfo.getNetworkType());
      IntEnumeration networks = this._corporateExtensionsAdditionalTones.keys();

      while (networks.hasMoreElements()) {
         int network = networks.nextElement();
         if (!vector.contains(network)) {
            vector.addElement(network);
         }
      }

      networks = this._standardExtensionsAdditionalTones.keys();

      while (networks.hasMoreElements()) {
         int network = networks.nextElement();
         if (!vector.contains(network)) {
            vector.addElement(network);
         }
      }

      return vector.getArray();
   }

   public final String getCorporateExtensionsAdditionalTones(int network) {
      String tones = (String)this._corporateExtensionsAdditionalTones.get(network);
      if (tones == null) {
         tones = SmartDialingOptions.getDefaultExtensionsAdditionalTonesCorporate(network);
         this._corporateExtensionsAdditionalTones.put(network, tones);
      }

      return tones;
   }

   public final void setCorporateExtensionsAdditionalTones(int network, String tones) {
      if (tones == null) {
         tones = "";
      }

      this._corporateExtensionsAdditionalTones.put(network, tones);
   }

   public final String getStandardExtensionsAdditionalTones(int network) {
      String tones = (String)this._standardExtensionsAdditionalTones.get(network);
      if (tones == null) {
         tones = SmartDialingOptions.getDefaultExtensionsAdditionalTonesStandard(network);
         this._standardExtensionsAdditionalTones.put(network, tones);
      }

      return tones;
   }

   public final void setStandardExtensionsAdditionalTones(int network, String tones) {
      if (tones == null) {
         tones = "";
      }

      this._standardExtensionsAdditionalTones.put(network, tones);
   }

   @Override
   public final void persistentContentStateChanged(int state) {
   }

   @Override
   public final void persistentContentModeChanged(int generation) {
   }
}
