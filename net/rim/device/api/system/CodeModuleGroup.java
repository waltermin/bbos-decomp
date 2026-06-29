package net.rim.device.api.system;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import net.rim.device.api.i18n.Locale;
import net.rim.device.api.util.CRC32;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.EmptyEnumeration;
import net.rim.device.internal.applicationcontrol.ApplicationControl;
import net.rim.device.internal.system.CodeModuleGroupProperties;
import net.rim.device.internal.system.CodeModuleGroupPropertiesCollection;
import net.rim.vm.TraceBack;

public final class CodeModuleGroup {
   private int _flags;
   private String _groupName;
   private Vector _moduleNames;
   private Vector _dependencies;
   private String _friendlyName;
   private String _description;
   private String _version;
   private String _vendor;
   private String _copyright;
   private CodeModuleGroupProperties _properties;
   private int _currentLocaleCode;
   private Hashtable _friendlyNameTable;
   private Hashtable _descriptionTable;
   private Hashtable _vendorTable;
   private Hashtable _versionTable;
   private Hashtable _copyrightTable;
   private int _handle;
   private String _midletSigner;
   private byte[] _midletSignerHash;
   public static final int FLAG_REQUIRED = 1;
   public static final int FLAG_HIDDEN = 2;
   public static final int FLAG_DEPENDENCY = 4;
   public static final int FLAG_LIBRARY = 8;

   CodeModuleGroup(int handle) {
      this._handle = handle;
      this._moduleNames = new Vector();
      this._dependencies = new Vector();
      this._currentLocaleCode = Locale.getDefault().getCode();
   }

   public CodeModuleGroup(String groupName) {
      this(0);
      if (!net.rim.vm.Memory.isStringAllBytes(groupName)) {
         throw new IllegalArgumentException();
      }

      if (groupName.length() > 256) {
         throw new IllegalArgumentException();
      }

      char[] chars = new char[groupName.length()];
      groupName.getChars(0, chars.length, chars, 0);

      for (int i = 0; i < chars.length; i++) {
         if (chars[i] > 127) {
            throw new IllegalArgumentException();
         }
      }

      this._groupName = groupName;
      CodeModuleGroupPropertiesCollection collection = CodeModuleGroupPropertiesCollection.getInstance();
      int uid = CodeModuleGroupPropertiesCollection.getGroupUID(this._groupName);
      this._properties = (CodeModuleGroupProperties)collection.getSyncObject(uid);
      if (this._properties == null) {
         this._properties = new CodeModuleGroupProperties(uid);
         collection.addSyncObject(this._properties);
      }
   }

   private final void assertPermission() {
      ApplicationControl.assertCMMApiAllowed(true);
   }

   public final String getName() {
      return this._groupName;
   }

   public final Enumeration getModules() {
      return this._moduleNames.elements();
   }

   public final int getHandle() {
      return this._handle;
   }

   public final boolean containsModule(String moduleName) {
      return this._moduleNames.contains(moduleName);
   }

   public final void addModule(String moduleName) {
      this._moduleNames.addElement(moduleName);
   }

   public final Enumeration getDependencies() {
      return this._dependencies.elements();
   }

   public final void addDependency(String groupName) {
      this._dependencies.addElement(groupName);
   }

   public final boolean containsDependency(String groupName) {
      return this._dependencies.contains(groupName);
   }

   public final int getFlags() {
      return this._flags;
   }

   public final void setFlag(int flag, boolean value) {
      if (value) {
         this._flags |= flag;
      } else {
         this._flags &= ~flag;
      }
   }

   public final String getFriendlyName() {
      return this._friendlyName == null ? this._groupName : this._friendlyName;
   }

   public final void setFriendlyName(String friendlyName) {
      this._friendlyName = friendlyName;
      if (this._friendlyNameTable != null) {
         this._friendlyNameTable.put(Locale.get(this._currentLocaleCode), friendlyName);
      }
   }

   public final String getFriendlyName(Locale locale) {
      if (locale == null) {
         throw new NullPointerException();
      }

      if (this._friendlyNameTable == null) {
         this.localize();
      }

      String friendlyName = (String)this._friendlyNameTable.get(locale);
      if (friendlyName == null) {
         friendlyName = this.getFriendlyName();
      }

      return friendlyName;
   }

   public final void setFriendlyName(Locale locale, String friendlyName) {
      if (locale == null) {
         throw new NullPointerException();
      }

      if (this._friendlyNameTable == null) {
         this.localize();
      }

      this._friendlyNameTable.put(locale, friendlyName);
      int localeCode = locale.getCode();
      if (localeCode == this._currentLocaleCode || this._friendlyName == null && this.fuzzyLocaleMatch(localeCode)) {
         this._friendlyName = friendlyName;
      }
   }

   public final Enumeration getFriendlyNameLocales() {
      if (this._friendlyNameTable == null) {
         this.localize();
      }

      return this._friendlyNameTable.keys();
   }

   public final String getDescription() {
      return this._description;
   }

   public final void setDescription(String description) {
      this._description = description;
      if (this._descriptionTable != null) {
         this._descriptionTable.put(Locale.get(this._currentLocaleCode), description);
      }
   }

   public final String getDescription(Locale locale) {
      if (locale == null) {
         throw new NullPointerException();
      }

      if (this._descriptionTable == null) {
         this.localize();
      }

      String description = (String)this._descriptionTable.get(locale);
      if (description == null) {
         description = this._description;
      }

      return description;
   }

   public final void setDescription(Locale locale, String description) {
      if (locale == null) {
         throw new NullPointerException();
      }

      if (this._descriptionTable == null) {
         this.localize();
      }

      this._descriptionTable.put(locale, description);
      int localeCode = locale.getCode();
      if (localeCode == this._currentLocaleCode || this._description == null && this.fuzzyLocaleMatch(localeCode)) {
         this._description = description;
      }
   }

   public final Enumeration getDescriptionLocales() {
      if (this._descriptionTable == null) {
         this.localize();
      }

      return this._descriptionTable.keys();
   }

   public final String getVersion() {
      return this._version;
   }

   public final void setVersion(String version) {
      this._version = version;
      if (this._versionTable != null) {
         this._versionTable.put(Locale.get(this._currentLocaleCode), version);
      }
   }

   public final String getVersion(Locale locale) {
      if (locale == null) {
         throw new NullPointerException();
      }

      if (this._versionTable == null) {
         this.localize();
      }

      String version = (String)this._versionTable.get(locale);
      if (version == null) {
         version = this._version;
      }

      return version;
   }

   public final void setVersion(Locale locale, String version) {
      if (locale == null) {
         throw new NullPointerException();
      }

      if (this._versionTable == null) {
         this.localize();
      }

      this._versionTable.put(locale, version);
      int localeCode = locale.getCode();
      if (localeCode == this._currentLocaleCode || this._version == null && this.fuzzyLocaleMatch(localeCode)) {
         this._version = version;
      }
   }

   public final Enumeration getVersionLocales() {
      if (this._versionTable == null) {
         this.localize();
      }

      return this._versionTable.keys();
   }

   public final String getVendor() {
      return this._vendor;
   }

   public final void setVendor(String vendor) {
      this._vendor = vendor;
      if (this._vendorTable != null) {
         this._vendorTable.put(Locale.get(this._currentLocaleCode), vendor);
      }
   }

   public final String getVendor(Locale locale) {
      if (locale == null) {
         throw new NullPointerException();
      }

      if (this._vendorTable == null) {
         this.localize();
      }

      String vendor = (String)this._vendorTable.get(locale);
      if (vendor == null) {
         vendor = this._vendor;
      }

      return vendor;
   }

   public final void setVendor(Locale locale, String vendor) {
      if (locale == null) {
         throw new NullPointerException();
      }

      if (this._vendorTable == null) {
         this.localize();
      }

      this._vendorTable.put(locale, vendor);
      int localeCode = locale.getCode();
      if (localeCode == this._currentLocaleCode || this._vendor == null && this.fuzzyLocaleMatch(localeCode)) {
         this._vendor = vendor;
      }
   }

   public final Enumeration getVendorLocales() {
      if (this._vendorTable == null) {
         this.localize();
      }

      return this._vendorTable.keys();
   }

   public final String getCopyright() {
      return this._copyright;
   }

   public final void setCopyright(String copyright) {
      this._copyright = copyright;
      if (this._copyrightTable != null) {
         this._copyrightTable.put(Locale.get(this._currentLocaleCode), copyright);
      }
   }

   public final String getCopyright(Locale locale) {
      if (locale == null) {
         throw new NullPointerException();
      }

      if (this._copyrightTable == null) {
         this.localize();
      }

      String copyright = (String)this._copyrightTable.get(locale);
      if (copyright == null) {
         copyright = this._copyright;
      }

      return copyright;
   }

   public final void setCopyright(Locale locale, String copyright) {
      if (locale == null) {
         throw new NullPointerException();
      }

      if (this._copyrightTable == null) {
         this.localize();
      }

      this._copyrightTable.put(locale, copyright);
      int localeCode = locale.getCode();
      if (localeCode == this._currentLocaleCode || this._copyright == null && this.fuzzyLocaleMatch(localeCode)) {
         this._copyright = copyright;
      }
   }

   public final Enumeration getCopyrightLocales() {
      if (this._copyrightTable == null) {
         this.localize();
      }

      return this._copyrightTable.keys();
   }

   public final void setMIDletSigner(String midletSigner) {
      ControlledAccess.verifyRRISignature(TraceBack.getCallingModule(0));
      this._midletSigner = midletSigner;
   }

   public final String getMIDletSigner() {
      ControlledAccess.verifyRRISignature(TraceBack.getCallingModule(0));
      return this._midletSigner;
   }

   public final void setMIDletSignerHash(byte[] sha256MidletSignerHash) {
      ControlledAccess.verifyRRISignature(TraceBack.getCallingModule(0));
      this._midletSignerHash = sha256MidletSignerHash;
   }

   public final byte[] getMIDletSignerHash() {
      ControlledAccess.verifyRRISignature(TraceBack.getCallingModule(0));
      return this._midletSignerHash;
   }

   public final Enumeration getPropertyNames() {
      return this._properties == null ? new EmptyEnumeration() : this._properties.keys();
   }

   public final String getProperty(String name) {
      return this._properties == null ? null : (String)this._properties.get(name);
   }

   public final void setProperty(String name, String value) {
      if (this._properties != null) {
         this._properties.put(name, value);
         CodeModuleGroupPropertiesCollection.getInstance().updateSyncObject(this._properties, this._properties);
      }
   }

   public final void clearProperties() {
      if (this._properties != null && ControlledAccess.verifyRRISignature(TraceBack.getCallingModule(0))) {
         this._properties.clear();
         CodeModuleGroupPropertiesCollection.getInstance().updateSyncObject(this._properties, this._properties);
      }
   }

   private final boolean fuzzyLocaleMatch(int localeCode) {
      return localeCode == 0 || (localeCode & -65536) == (this._currentLocaleCode & -65536);
   }

   private final boolean localize() {
      if (this._handle == 0) {
         this._friendlyNameTable = new Hashtable();
         this._descriptionTable = new Hashtable();
         this._vendorTable = new Hashtable();
         this._versionTable = new Hashtable();
         this._copyrightTable = new Hashtable();
         boolean result = true;
         return result;
      } else {
         return this.load(true);
      }
   }

   final boolean load() {
      return this.load(false);
   }

   final boolean load(boolean localized) {
      byte[] data = CodeModuleGroupManager.getGroupData(this._handle);
      return this.loadFromData(data, localized);
   }

   final boolean loadFromData(byte[] data, boolean localized) {
      DataBuffer buf = new DataBuffer(data, 0, data.length, false);
      if (localized) {
         this._friendlyNameTable = new Hashtable();
         this._descriptionTable = new Hashtable();
         this._vendorTable = new Hashtable();
         this._versionTable = new Hashtable();
         this._copyrightTable = new Hashtable();
      } else {
         this._friendlyNameTable = null;
         this._descriptionTable = null;
         this._vendorTable = null;
         this._versionTable = null;
         this._copyrightTable = null;
      }

      this._moduleNames.removeAllElements();
      this._dependencies.removeAllElements();

      try {
         buf.readInt();
         buf.readInt();
         this._flags = buf.readInt();
         this._groupName = this.readString(data, buf);
         CodeModuleGroupPropertiesCollection collection = CodeModuleGroupPropertiesCollection.getInstance();
         int uid = CodeModuleGroupPropertiesCollection.getGroupUID(this._groupName);
         this._properties = (CodeModuleGroupProperties)collection.getSyncObject(uid);
         if (this._properties == null) {
            this._properties = new CodeModuleGroupProperties(uid);
            collection.addSyncObject(this._properties);
         }

         while (buf.available() >= 3) {
            int type = buf.readShort();
            switch (type) {
               case -1:
                  break;
               case 0:
               default:
                  String sx = this.readString(data, buf);
                  if (sx.length() != 0) {
                     this._moduleNames.addElement(sx);
                  }
                  break;
               case 1:
                  String s = this.readString(data, buf);
                  if (s.length() != 0) {
                     this._dependencies.addElement(s);
                  }
                  break;
               case 2:
                  this.readProperty(data, buf);
                  break;
               case 3:
                  this._friendlyName = this.readLocalizedString(data, buf, this._friendlyName, this._friendlyNameTable);
                  break;
               case 4:
                  this._description = this.readLocalizedString(data, buf, this._description, this._descriptionTable);
                  break;
               case 5:
                  this._version = this.readLocalizedString(data, buf, this._version, this._versionTable);
                  break;
               case 6:
                  this._vendor = this.readLocalizedString(data, buf, this._vendor, this._vendorTable);
                  break;
               case 7:
                  this._copyright = this.readLocalizedString(data, buf, this._copyright, this._copyrightTable);
            }
         }

         return true;
      } catch (IOException ex) {
         return false;
      }
   }

   public static final CodeModuleGroup loadUnpersisted(byte[] data) {
      CodeModuleGroup result = new CodeModuleGroup(0);
      boolean success = result.loadFromData(data, false);
      return success ? result : null;
   }

   private final String readString(byte[] data, DataBuffer buf) {
      int start = buf.getPosition();

      while (buf.readByte() != 0) {
      }

      int end = buf.getPosition();
      return new String(data, start, end - start - 1, "UTF8");
   }

   private final String readLocalizedString(byte[] data, DataBuffer buf, String previousValue, Hashtable localizedTable) {
      int localeCode = buf.readInt();
      String s = this.readString(data, buf);
      if (localizedTable != null) {
         localizedTable.put(Locale.get(localeCode), s);
      }

      return localeCode != this._currentLocaleCode && (previousValue != null || !this.fuzzyLocaleMatch(localeCode)) ? previousValue : s;
   }

   private final void readProperty(byte[] data, DataBuffer buf) {
      String name = this.readString(data, buf);
      String value = this.readString(data, buf);
      if (this._properties != null) {
         String storedValue = (String)this._properties.get(name);
         if (storedValue != null && !storedValue.equals(value) || storedValue == null && value != null) {
            this._properties.put(name, value);
            CodeModuleGroupPropertiesCollection.getInstance().updateSyncObject(this._properties, this._properties);
         }
      }
   }

   public final synchronized boolean store() {
      this.assertPermission();
      DataBuffer buf = new DataBuffer(false);
      buf.writeInt(2060613291);
      buf.writeInt(0);
      buf.writeInt(this._flags);
      this.writeString(buf, this._groupName);
      this.writeVector(buf, 0, this._moduleNames);
      this.writeVector(buf, 1, this._dependencies);
      this.writeLocalizedHashtable(buf, 3, this._friendlyName, this._friendlyNameTable);
      this.writeLocalizedHashtable(buf, 4, this._description, this._descriptionTable);
      this.writeLocalizedHashtable(buf, 5, this._version, this._versionTable);
      this.writeLocalizedHashtable(buf, 6, this._vendor, this._vendorTable);
      this.writeLocalizedHashtable(buf, 7, this._copyright, this._copyrightTable);
      int rem = buf.getLength() & 3;
      if (rem != 0) {
         for (int i = 3 - rem; i >= 0; i--) {
            buf.write(255);
         }
      }

      byte[] data = buf.toArray();
      int crc = CRC32.update(-1, data, 12, data.length - 12);
      data[4] = (byte)crc;
      data[5] = (byte)(crc >>> 8);
      data[6] = (byte)(crc >>> 16);
      data[7] = (byte)(crc >>> 24);
      this._handle = CodeModuleGroupManager.createGroup(data);
      return this._handle > 0;
   }

   public final synchronized void delete() {
      this.assertPermission();
      if (this._handle != 0) {
         deleteGroup(this._handle);
         this._handle = 0;
      }
   }

   private final void writeVector(DataBuffer buf, int type, Vector v) {
      int length = v.size();

      for (int i = 0; i < length; i++) {
         String s = (String)v.elementAt(i);
         buf.writeShort(type);
         this.writeString(buf, s);
      }
   }

   private final void writeProperty(DataBuffer buf, String name, String value) {
      try {
         byte[] nameData = name.getBytes("UTF8");
         byte[] valueData = value.getBytes("UTF8");
         buf.writeShort(2);
         buf.write(nameData);
         buf.write(0);
         buf.write(valueData);
         buf.write(0);
      } catch (UnsupportedEncodingException var6) {
      }
   }

   private final void writeString(DataBuffer buf, String s) {
      if (s != null) {
         try {
            byte[] bytes = s.getBytes("UTF8");
            buf.write(bytes);
            buf.write(0);
         } catch (UnsupportedEncodingException var4) {
         }
      }
   }

   private final void writeLocalizedString(DataBuffer buf, int type, int localeCode, String s) {
      if (s != null) {
         try {
            byte[] bytes = s.getBytes("UTF8");
            buf.writeShort(type);
            buf.writeInt(localeCode);
            buf.write(bytes);
            buf.write(0);
         } catch (UnsupportedEncodingException var6) {
         }
      }
   }

   private final void writeLocalizedHashtable(DataBuffer buf, int type, String s, Hashtable localizedTable) {
      if (localizedTable == null) {
         this.writeLocalizedString(buf, type, 0, s);
      } else {
         s = (String)localizedTable.get(Locale.get(0));
         this.writeLocalizedString(buf, type, 0, s);
         Enumeration locales = localizedTable.keys();

         while (locales.hasMoreElements()) {
            Locale locale = (Locale)locales.nextElement();
            int code = locale.getCode();
            if (code != 0) {
               s = (String)localizedTable.get(locale);
               this.writeLocalizedString(buf, type, code, s);
            }
         }
      }
   }

   @Override
   public final boolean equals(Object o) {
      if (!(o instanceof CodeModuleGroup)) {
         return false;
      }

      CodeModuleGroup cmg = (CodeModuleGroup)o;
      return this._groupName.equals(cmg._groupName);
   }

   public static final CodeModuleGroup load(String groupName) {
      return CodeModuleGroupManager.load(groupName);
   }

   public static final CodeModuleGroup[] loadAll() {
      return CodeModuleGroupManager.loadAll();
   }

   public static final byte[] getGroupData(int handle) {
      return CodeModuleGroupManager.getGroupData(handle);
   }

   public static final int createGroup(byte[] data) {
      return CodeModuleGroupManager.createGroup(data);
   }

   private static final native void deleteGroup(int var0);
}
