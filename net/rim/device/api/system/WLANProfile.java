package net.rim.device.api.system;

public final class WLANProfile {
   public int _profileID;
   public String _ssid;
   public int _band;
   public int _flags;
   public int _ipAddress;
   public int _subnetMask;
   public int _gatewayIPAddress;
   public int _primaryDNS;
   public int _secondaryDNS;
   public String _domainSuffix;
   public int _authMode;
   public int _innerAuthMode;
   public byte[][][] _wepKey;
   public int _wepKeySize;
   public int _wepTxIndex;
   public byte[] _wpaPSKExpandedPMK;
   public String _username;
   public String _password;
   public byte[] _caCertificate;
   public String _serverSubject;
   public String _serverSAN;
   public byte[] _clientCertificate;
   public byte[] _clientKey;
   public byte[] _tokenSerial;
   public byte[] _imsi;
   public String _ssidForAssociation;
   public int _authFamily;
   public int _authLifetime;
   public int _roamingThreshold;
   public int _wpsPin;
   public byte[] _smartCardPin;
}
