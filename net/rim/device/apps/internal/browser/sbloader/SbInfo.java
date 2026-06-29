package net.rim.device.apps.internal.browser.sbloader;

class SbInfo {
   private String _books = "";
   private String _devices = "";
   private String _service = "";
   private String _efgid = "";
   private String _sapsoldto = "";
   private String _regex = "";
   private String _region = "";

   public SbInfo(String efgid, String region, String books, String devices, String service, String sapsoldto, String regex) {
      this._books = books;
      this._devices = devices;
      this._service = service;
      this._efgid = efgid;
      this._sapsoldto = sapsoldto;
      this._regex = regex;
      this._region = region;
   }

   public String getRegex() {
      return this._regex;
   }

   public String getSapSoldTo() {
      return this._sapsoldto;
   }

   public String getBooks() {
      return this._books;
   }

   public String getDevices() {
      return this._devices;
   }

   public String getService() {
      return this._service;
   }

   public String getEfgid() {
      return this._efgid;
   }

   public String getRegion() {
      return this._region;
   }
}
