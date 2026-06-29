package net.rim.device.apps.internal.diagnostic;

import net.rim.device.api.i18n.SimpleDateFormat;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.util.Persistable;

public final class Report implements Persistable {
   long startTimeStamp = -1;
   long endTimeStamp = -1;
   private Object serverName1;
   private Object emailAddress1;
   int emailService1 = 0;
   private Object serverName2;
   private Object emailAddress2;
   int emailService2 = 0;
   private Object pin;
   private Object msisdn;
   private Object deviceType;
   private Object appVersion;
   private Object platform;
   private Object serviceBook;
   int freeSpace = -1;
   int radioActivation = 0;
   int signalLevel = -99999;
   private Object networkType;
   private Object network;
   private Object ip;
   int icmpPing = 0;
   int bbReg = 0;
   int mdpPing = 0;
   int pin2pinPing = 0;

   Report() {
      this.setServerName1(null);
      this.setServerName2(null);
      this.setEmailAddress1(null);
      this.setEmailAddress2(null);
      this.setPin(null);
      this.setMsisdn(null);
      this.setDeviceType(null);
      this.setAppVersion(null);
      this.setPlatform(null);
      this.setServiceBook(null);
      this.setNetworkType(null);
      this.setNetwork(null);
      this.setIp(null);
   }

   public final String getIp() {
      return PersistentContent.decodeString(this.ip);
   }

   public final void setIp(String str) {
      if (str == null) {
         this.ip = PersistentContent.encode(" Unknown", false, true);
      } else {
         this.ip = PersistentContent.encode(str, false, true);
      }
   }

   public final String getNetwork() {
      return PersistentContent.decodeString(this.network);
   }

   public final void setNetwork(String str) {
      if (str == null) {
         this.network = PersistentContent.encode(" Unknown", false, true);
      } else {
         this.network = PersistentContent.encode(str, false, true);
      }
   }

   public final String getNetworkType() {
      return PersistentContent.decodeString(this.networkType);
   }

   public final void setNetworkType(String str) {
      if (str == null) {
         this.networkType = PersistentContent.encode(" Unknown", false, true);
      } else {
         this.networkType = PersistentContent.encode(str, false, true);
      }
   }

   public final String getServiceBook() {
      return PersistentContent.decodeString(this.serviceBook);
   }

   public final void setServiceBook(String str) {
      if (str == null) {
         this.serviceBook = PersistentContent.encode(" Unknown", false, true);
      } else {
         this.serviceBook = PersistentContent.encode(str, false, true);
      }
   }

   public final String getPlatform() {
      return PersistentContent.decodeString(this.platform);
   }

   public final void setPlatform(String str) {
      if (str == null) {
         this.platform = PersistentContent.encode(" Unknown", false, true);
      } else {
         this.platform = PersistentContent.encode(str, false, true);
      }
   }

   public final String getAppVersion() {
      return PersistentContent.decodeString(this.appVersion);
   }

   public final void setAppVersion(String str) {
      if (str == null) {
         this.appVersion = PersistentContent.encode(" Unknown", false, true);
      } else {
         this.appVersion = PersistentContent.encode(str, false, true);
      }
   }

   public final String getDeviceType() {
      return PersistentContent.decodeString(this.deviceType);
   }

   public final void setDeviceType(String str) {
      if (str == null) {
         this.deviceType = PersistentContent.encode(" Unknown", false, true);
      } else {
         this.deviceType = PersistentContent.encode(str, false, true);
      }
   }

   public final String getMsisdn() {
      return PersistentContent.decodeString(this.msisdn);
   }

   public final void setMsisdn(String str) {
      if (str == null) {
         this.msisdn = PersistentContent.encode(" Unknown", false, true);
      } else {
         this.msisdn = PersistentContent.encode(str, false, true);
      }
   }

   public final String getPin() {
      return PersistentContent.decodeString(this.pin);
   }

   public final void setPin(String str) {
      if (str == null) {
         this.pin = PersistentContent.encode(" Unknown", false, true);
      } else {
         this.pin = PersistentContent.encode(str, false, true);
      }
   }

   public final String getEmailAddress1() {
      return PersistentContent.decodeString(this.emailAddress1);
   }

   public final void setEmailAddress1(String str) {
      if (str == null) {
         this.emailAddress1 = PersistentContent.encode(" Unknown", false, true);
      } else {
         this.emailAddress1 = PersistentContent.encode(str, false, true);
      }
   }

   public final String getEmailAddress2() {
      return PersistentContent.decodeString(this.emailAddress2);
   }

   public final void setEmailAddress2(String str) {
      if (str == null) {
         this.emailAddress2 = PersistentContent.encode(" Unknown", false, true);
      } else {
         this.emailAddress2 = PersistentContent.encode(str, false, true);
      }
   }

   public final String getServerName1() {
      return PersistentContent.decodeString(this.serverName1);
   }

   public final void setServerName1(String str) {
      if (str == null) {
         this.serverName1 = PersistentContent.encode(" Unknown", false, true);
      } else {
         this.serverName1 = PersistentContent.encode(str, false, true);
      }
   }

   public final String getServerName2() {
      return PersistentContent.decodeString(this.serverName2);
   }

   public final void setServerName2(String str) {
      if (str == null) {
         this.serverName2 = PersistentContent.encode(" Unknown", false, true);
      } else {
         this.serverName2 = PersistentContent.encode(str, false, true);
      }
   }

   @Override
   public final String toString() {
      StringBuffer str = (StringBuffer)(new Object());
      SimpleDateFormat formatter = (SimpleDateFormat)(new Object("MMMMM dd, yyyy hh:mm aaa"));
      str.append("Starting Time Stamp: ");
      str.append(formatter.format(new Object(this.startTimeStamp)));
      str.append("\n");
      str.append("Ending Time Stamp: ");
      if (this.endTimeStamp == -1) {
         str.append("Incomplete");
      } else {
         str.append(formatter.format(new Object(this.endTimeStamp)));
      }

      str.append("\n");
      str.append("PIN: ");
      str.append(this.getPin());
      str.append("\n");
      str.append("MSISDN: ");
      str.append(this.getMsisdn());
      str.append("\n");
      str.append("Device Type: BlackBerry ");
      str.append(this.getDeviceType());
      str.append("\n");
      str.append("Application Version: ");
      str.append(this.getAppVersion());
      str.append("\n");
      str.append("Platform Version: ");
      str.append(this.getPlatform());
      str.append("\n");
      str.append("Service Books: ");
      str.append(this.getServiceBook());
      str.append("\n");
      str.append("Free File Space: ");
      if (this.freeSpace != -1) {
         str.append(this.freeSpace);
         str.append(" bytes");
      } else {
         str.append("Unknown");
      }

      str.append("\n");
      str.append("Radio Data Activation: ");
      str.append(this.radioActivation == 0 ? "No" : "Yes");
      str.append("\n");
      str.append("Signal Level: ");
      if (this.signalLevel == -99999) {
         str.append("Unknown");
      } else {
         str.append(this.signalLevel);
         str.append(" dBm");
      }

      str.append("\n");
      str.append("Radio Access: ");
      str.append(this.getNetworkType());
      str.append("\n");
      str.append("Network: ");
      str.append(this.getNetwork());
      str.append("\n");
      str.append("IP Address: ");
      str.append(this.getIp());
      str.append("\n");
      str.append("ICMP Ping Echo: ");
      str.append(this.icmpPing == 0 ? "No" : "Yes");
      str.append("\n");
      str.append("BlackBerry Registration: ");
      str.append(this.bbReg == 0 ? "No" : "Yes");
      str.append("\n");
      str.append("Connected to BlackBerry: ");
      str.append(this.mdpPing == 0 ? "No" : "Yes");
      str.append("\n");
      str.append("BlackBerry PIN Email: ");
      str.append(this.pin2pinPing == 0 ? "No" : "Yes");
      str.append("\n");
      if (!this.getServerName1().equalsIgnoreCase(" Unknown")) {
         str.append("Server Name: ");
         str.append(this.getServerName1());
         str.append("\n");
         str.append("Email Address: ");
         str.append(this.getEmailAddress1());
         str.append("\n");
         str.append(((StringBuffer)(new Object("Connection to "))).append(this.getEmailAddress1()).append(" : ").toString());
         str.append(this.emailService1 == 0 ? "No" : "Yes");
         str.append("\n");
      }

      if (!this.getServerName2().equalsIgnoreCase(" Unknown")) {
         str.append("Server Name: ");
         str.append(this.getServerName2());
         str.append("\n");
         str.append("Email Address: ");
         str.append(this.getEmailAddress2());
         str.append("\n");
         str.append(((StringBuffer)(new Object("Connection to "))).append(this.getEmailAddress2()).append(" : ").toString());
         str.append(this.emailService2 == 0 ? "No" : "Yes");
         str.append("\n");
      }

      return str.toString();
   }
}
