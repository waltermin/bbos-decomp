package net.rim.tools.compiler;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Enumeration;
import java.util.Vector;
import net.rim.device.api.crypto.Digest;
import net.rim.device.internal.io.PushRegistryHelper;
import net.rim.device.internal.system.MIDletSecurity;
import net.rim.device.internal.system.MIDletSecurityConstants;
import net.rim.tools.compiler.exec.CharacterHelper;
import net.rim.tools.compiler.io.StructuredInputStream;
import net.rim.tools.compiler.types.ClassType;
import net.rim.tools.compiler.util.CompileException;
import net.rim.tools.compiler.util.CompilerProperties;
import net.rim.tools.compiler.util.FileHelper;
import net.rim.tools.compiler.util.StringHelper;
import net.rim.tools.compiler.util.Tokenizer;
import net.rim.tools.compiler.vm.Constants;
import net.rim.tools.jar.Manifest;

public class JadSupport extends CompilerProperties implements Constants {
   private boolean _interesting;
   private Vector _applets = (Vector)(new Object());
   private ResourceFile _manifest;
   private Vector _resourceBinaries;
   private GenerateResources _generateResources;
   private byte[] _midletPolicy;
   private byte[] _signerCertEncoding;

   public JadSupport(CompilerProperties properties) {
      this._resourceBinaries = properties.getVector("rapc_resourceBinaries");
   }

   public int getJarSize() {
      String value = (String)this.get(ResourceIds.jadRequired[1]);
      int size = 0;
      if (value != null) {
         try {
            return Integer.parseInt(value);
         } finally {
            return size;
         }
      } else {
         return size;
      }
   }

   public ResourceFile getManifest() {
      return this._manifest;
   }

   public int getNumApplets() {
      return this._applets.size();
   }

   public Applet getApplet(int index) {
      return (Applet)this._applets.elementAt(index);
   }

   private static String nextToken(Tokenizer tokenizer) {
      String token = null;

      try {
         if (tokenizer.hasMoreTokens()) {
            token = tokenizer.nextToken();
            if (token != null) {
               token = StringHelper.trim(token);
            }

            if (token != null) {
               if (token.equals(",")) {
                  token = null;
               } else {
                  if (tokenizer.hasMoreTokens()) {
                     tokenizer.nextToken();
                  }

                  if (token.length() == 0) {
                     return null;
                  }
               }
            }
         }
      } finally {
         ;
      }

      return token;
   }

   private void processMidletAttribute(int index, String line) {
      Tokenizer tokenizer = new Tokenizer(line, ",", true);
      String name = nextToken(tokenizer);
      String iconName = nextToken(tokenizer);
      String className = nextToken(tokenizer);
      Applet applet = null;
      if (index < this.getNumApplets()) {
         applet = this.getApplet(index);
         if (name != null) {
            applet.setName(name);
         }

         if (className != null) {
            applet.setClassName(className);
         }
      } else {
         applet = new Applet(name, className);
         this._applets.addElement(applet);
      }

      if (iconName != null) {
         applet.setIconName(iconName, 0);
      }

      String tag = ((StringBuffer)(new Object())).append(ResourceIds.iconTag).append("Count-").append(index + 1).toString();
      String value = this.getProperty(tag);
      if (value != null) {
         try {
            int count = Integer.parseInt(value);

            for (int i = 0; i < count; i++) {
               tag = ((StringBuffer)(new Object())).append(ResourceIds.iconTag).append(index + 1).append("-").append(i + 1).toString();
               iconName = this.getProperty(tag);
               if (iconName != null) {
                  applet.setIconName(iconName, i + 1);
               }
            }
         } finally {
            return;
         }
      }
   }

   private void parseAttributes(CompilerProperties properties, String[] tags, boolean required, boolean override) {
      for (String tag : tags) {
         String value = properties.getProperty(tag);
         if (value == null) {
            if (required) {
               throw new CompileException(907, null, ((StringBuffer)(new Object("Descriptor missing required attribute: "))).append(tag).toString());
            }
         } else {
            properties.remove(tag);
            if (override) {
               this.setProperty(tag, value);
            } else {
               String oldValue = this.getProperty(tag);
               if (oldValue == null) {
                  this.setProperty(tag, value);
               } else if (!oldValue.equals(value)) {
                  throw new CompileException(
                     905,
                     null,
                     ((StringBuffer)(new Object("Descriptor duplicate attribute mismatch: '")))
                        .append(tag)
                        .append("' old value: '")
                        .append(oldValue)
                        .append("' new value: '")
                        .append(value)
                        .append("'")
                        .toString()
                  );
               }
            }
         }
      }
   }

   private String parseAttributeOrdinals(CompilerProperties properties, String[] tags, int ordinal) {
      String result = null;
      int num = tags.length;

      for (int i = 0; i < num; i++) {
         String tag = ((StringBuffer)(new Object())).append(tags[i]).append(ordinal + 1).toString();
         String value = properties.getProperty(tag);
         if (value != null) {
            properties.remove(tag);
            this.setProperty(tag, value);
            if (i == 0) {
               result = value;
            } else if (i == 1) {
               try {
                  int count = Integer.parseInt(value);

                  for (int j = 0; j < count; j++) {
                     tag = ((StringBuffer)(new Object())).append(ResourceIds.iconTag).append(ordinal + 1).append("-").append(j + 1).toString();
                     value = properties.getProperty(tag);
                     if (value != null) {
                        properties.remove(tag);
                        this.setProperty(tag, value);
                     }
                  }
               } finally {
                  continue;
               }
            }
         }
      }

      return result;
   }

   private void setRemainingProperties(CompilerProperties properties) {
      Enumeration tags = properties.keys();

      while (tags.hasMoreElements()) {
         String tag = (String)tags.nextElement();
         String value = properties.getProperty(tag);
         this.setProperty(tag, value);
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public void parseManifest(Compiler compiler, String jarName, Manifest manifest) {
      CompilerProperties properties = new CompilerProperties();
      byte[] manifestBytes = null;

      label45:
      try {
         manifestBytes = manifest.getBytes();
         properties.load(manifest.getString());
      } catch (Throwable var9) {
         compiler.generateWarning(false, jarName, ((StringBuffer)(new Object("Found malformed manifest in jar file: "))).append(ioe.toString()).toString());
         break label45;
      }

      if (manifestBytes != null) {
         this._manifest = new ResourceFile("META-INF/MANIFEST.MF", manifestBytes, false);
         this.parseAttributes(properties, ResourceIds.manifestVersion, false, false);
         this.parseAttributes(properties, ResourceIds.required, true, false);
         this.parseAttributes(properties, ResourceIds.manifestRequired, false, false);
         this.parseAttributes(properties, ResourceIds.optional, false, false);
         int index = 0;

         while (true) {
            String name = this.parseAttributeOrdinals(properties, ResourceIds.ordinalPrefix, index);
            if (name == null) {
               this.setRemainingProperties(properties);
               this._interesting = true;
               return;
            }

            if (name.length() > 0) {
               this.processMidletAttribute(index, name);
            }

            index++;
         }
      }
   }

   private void securityProblemDoNotInstall(int resultCode, String msg, String parm) {
      if (parm != null) {
         msg = ((StringBuffer)(new Object())).append(msg).append(parm).toString();
      }

      throw new CompileException(resultCode, null, msg);
   }

   private void checkSecurityTagsNotPresent(CompilerProperties jadProperties) {
      String badTag = null;
      Enumeration tags = jadProperties.keys();

      while (tags.hasMoreElements()) {
         String tag = (String)tags.nextElement();
         if (tag.startsWith("MIDlet-Certificate-")) {
            badTag = tag;
            break;
         }
      }

      if (badTag != null) {
         this.securityProblemDoNotInstall(905, "Cannot use security attribute: ", badTag);
      }
   }

   private boolean equalsManifestValue(String jad, String manifest, boolean okIfInJadOnly) {
      if (jad != null) {
         return manifest != null ? jad.equals(manifest) : okIfInJadOnly;
      } else {
         return true;
      }
   }

   public void checkSecurity(CompilerProperties jadProperties) {
      String sig = jadProperties.getProperty("MIDlet-Jar-RSA-SHA1");
      if (sig == null) {
         this.checkSecurityTagsNotPresent(jadProperties);
      } else {
         Enumeration e = jadProperties.keys();

         while (e.hasMoreElements()) {
            String key = (String)e.nextElement();
            String jadValue = jadProperties.getProperty(key);
            String manifestValue = this.getProperty(key);
            if (!this.equalsManifestValue(jadValue, manifestValue, true)) {
               this.securityProblemDoNotInstall(905, ((StringBuffer)(new Object())).append(key).append(" in JAD does not match manifest").toString(), null);
            }
         }

         String perms = jadProperties.getProperty("MIDlet-Permissions");
         String optPerms = jadProperties.getProperty("MIDlet-Permissions-Opt");
         String manifestPerms = this.getProperty("MIDlet-Permissions");
         String manifestOptPerms = this.getProperty("MIDlet-Permissions-Opt");
         if (perms == null) {
            perms = manifestPerms;
         }

         if (optPerms == null) {
            optPerms = manifestOptPerms;
         }

         if (!this.equalsManifestValue(perms, manifestPerms, false) || !this.equalsManifestValue(optPerms, manifestOptPerms, false)) {
            this.securityProblemDoNotInstall(905, "Permissions in manifest do not match JAD permissions", null);
         }

         int index = 1;
         String jadPushAttributeValue = jadProperties.getProperty(
            ((StringBuffer)(new Object())).append(PushRegistryHelper.MIDLET_PUSH_PROPERTY_NAME_PREFIX).append(index).toString()
         );

         for (String manifestPushAttributeValue = this.getProperty(
               ((StringBuffer)(new Object())).append(PushRegistryHelper.MIDLET_PUSH_PROPERTY_NAME_PREFIX).append(index).toString()
            );
            manifestPushAttributeValue != null || jadPushAttributeValue != null;
            manifestPushAttributeValue = this.getProperty(
               ((StringBuffer)(new Object())).append(PushRegistryHelper.MIDLET_PUSH_PROPERTY_NAME_PREFIX).append(index).toString()
            )
         ) {
            if (jadPushAttributeValue == null) {
               jadPushAttributeValue = manifestPushAttributeValue;
            }

            if (!this.equalsManifestValue(jadPushAttributeValue, manifestPushAttributeValue, true)) {
               this.securityProblemDoNotInstall(905, "Push attribute in JAD does not match manifest", null);
            }

            String[] values = PushRegistryHelper.getPushPropertyValues(jadPushAttributeValue);
            if (!PushRegistryHelper.isPushTransportPermissionRequested(values[0], perms)) {
               this.securityProblemDoNotInstall(910, "Push connection not permitted", null);
            }

            jadPushAttributeValue = jadProperties.getProperty(
               ((StringBuffer)(new Object())).append(PushRegistryHelper.MIDLET_PUSH_PROPERTY_NAME_PREFIX).append(++index).toString()
            );
         }

         String jadContentHandlerAttributeValue = jadProperties.getProperty("MicroEdition-Handler-1");
         if (jadContentHandlerAttributeValue != null && (perms == null || perms.indexOf("javax.microedition.content.ContentHandler") == -1)) {
            this.securityProblemDoNotInstall(910, "Content Handler permissions not requested", null);
         }
      }
   }

   byte[] getPolicy() {
      return this._midletPolicy;
   }

   byte[] getSignerCertEncoding() {
      return this._signerCertEncoding;
   }

   private void processPerms(String perms, byte[] policy, boolean optPerm) {
      if (perms != null) {
         Tokenizer tokenizer = new Tokenizer(perms, ",", false);

         while (tokenizer.hasMoreTokens()) {
            String permName = tokenizer.nextToken();
            if (permName == null) {
               return;
            }

            int perm = MIDletSecurity.adjustPolicy(policy, permName, optPerm);
            if (perm == 40 && !optPerm) {
               this.securityProblemDoNotInstall(910, "unknown permission: ", permName);
            }
         }
      }
   }

   private byte[] processPerms(String perms, String optPerms) {
      byte[] policy = MIDletSecurity.createPolicy();
      this.processPerms(perms, policy, false);
      this.processPerms(optPerms, policy, true);
      return policy;
   }

   public void verifySignature(Digest digest) {
      String sig = this.getProperty("MIDlet-Jar-RSA-SHA1");
      if (sig != null) {
         String perms = this.getProperty("MIDlet-Permissions");
         String optPerms = this.getProperty("MIDlet-Permissions-Opt");
         byte[] desiredPolicy = this.processPerms(perms, optPerms);
         byte[] domainPolicy = null;
         byte[] signerCertEncoding = new byte[0];
         int n = 1;

         label50:
         while (true) {
            Vector v = (Vector)(new Object());
            int m = 1;

            while (true) {
               String tag = ResourceIds.getMIDletCertificateTag(n, m);
               String value = this.getProperty(tag);
               if (value == null) {
                  m = v.size();
                  if (m == 0 && n > 1) {
                     break label50;
                  }

                  String[] certs = new Object[m];
                  v.copyInto(certs);
                  byte[] domainCopy = new byte[0];
                  int status = MIDletSecurity.checkMIDletSignature(digest, sig, certs, domainCopy, signerCertEncoding);
                  if (status == 0) {
                     domainPolicy = domainCopy;
                     break label50;
                  }

                  n++;
                  break;
               }

               v.addElement(value);
               m++;
            }
         }

         if (domainPolicy == null) {
            this.securityProblemDoNotInstall(907, "Signature could not be verified", null);
         }

         byte[] checkPolicy = MIDletSecurity.updateDesiredPolicy(desiredPolicy, domainPolicy);
         if (checkPolicy == null) {
            this.securityProblemDoNotInstall(907, "Could not create a midlet policy", null);
         } else if (checkPolicy.length == 1) {
            this.securityProblemDoNotInstall(910, "required permission denied: ", MIDletSecurityConstants.MIDletPermissions[checkPolicy[0]]);
            checkPolicy = null;
         }

         if (checkPolicy != null) {
            this._midletPolicy = checkPolicy;
         }

         if (signerCertEncoding != null) {
            this._signerCertEncoding = signerCertEncoding;
         }
      }
   }

   public void parseJad(Compiler compiler, String fileName) {
      CompilerProperties properties = new CompilerProperties();
      InputStream in = null;
      in = compiler.getHost().openInput(fileName);
      String contents = CharacterHelper.utf8ToString(StructuredInputStream.readFully(in, -1, fileName));
      properties.load(contents);
      this.parseJad(properties);
   }

   public void parseJad(String jadString) {
      CompilerProperties properties = new CompilerProperties();
      properties.load(jadString);
      this.parseJad(properties);
   }

   private void parseJad(CompilerProperties properties) {
      this.checkSecurity(properties);
      this.parseAttributes(properties, ResourceIds.manifestRequired, false, true);
      this.checkAttributes(ResourceIds.manifestRequired, ResourceIds.manifestRequiredAllowed);
      this.parseAttributes(properties, ResourceIds.required, true, false);
      this.parseAttributes(properties, ResourceIds.jadRequired, true, true);
      this.parseAttributes(properties, ResourceIds.optional, false, true);
      int index = 0;

      while (true) {
         String name = this.parseAttributeOrdinals(properties, ResourceIds.ordinalPrefix, index);
         if (name == null) {
            this.setRemainingProperties(properties);
            this._interesting = true;
            return;
         }

         if (name.length() == 0) {
            throw new CompileException(
               907, null, ((StringBuffer)(new Object("Descriptor missing required attribute: "))).append(ResourceIds.ordinalPrefix[0]).toString()
            );
         }

         this.processMidletAttribute(index, name);
         Applet applet = this.getApplet(index);
         int num = applet.getNumIcons();

         for (int i = 0; i < num; i++) {
            ResourceFile rf = applet.getIcon(i);
            if (!rf.isEmpty() && this._resourceBinaries.indexOf(rf) == -1) {
               this._resourceBinaries.addElement(rf);
            }
         }

         index++;
      }
   }

   private void rationalizeApplets(Compiler compiler, boolean makingMIDlet, boolean convertPNG, Vector potentialMIDlets) {
      int count = this.getNumApplets();

      for (int i = count - 1; i >= 0; i--) {
         Applet applet = this.getApplet(i);
         int numIconNames = applet.getNumIconNames();

         for (int j = 0; j < numIconNames; j++) {
            String iconName = applet.getIconName(j);
            if (iconName != null) {
               if (iconName.charAt(0) == '/') {
                  iconName = iconName.substring(1);
               }

               ImageFile icon = null;
               int index = -1;

               for (index = this._resourceBinaries.size() - 1; index >= 0; index--) {
                  Object obj = this._resourceBinaries.elementAt(index);
                  if (obj.equals(iconName)) {
                     break;
                  }
               }

               if (index != -1) {
                  icon = (ImageFile)this._resourceBinaries.elementAt(index);
               }

               if (icon != null) {
                  applet.setIcon(icon, j);
               }
            }
         }
      }

      if (makingMIDlet) {
         for (int i = count - 1; i >= 0; i--) {
            Applet applet = this.getApplet(i);
            String className = applet.getClassName();
            if (className == null) {
               this._applets.removeElementAt(i);
            } else {
               ClassType classType = compiler.findClassType(className);
               if (!classType.is(128)) {
                  throw new CompileException(907, null, ((StringBuffer)(new Object("MIDlet class is not public: "))).append(className).toString());
               }
            }
         }

         if (this.getNumApplets() == 0) {
            throw new CompileException(907, null, "Descriptor missing required attribute: MIDlet-1");
         }
      }
   }

   public void fixupProperties(Compiler compiler, boolean makingMIDlet, boolean convertPNG, Vector potentialMIDlets) {
      this.rationalizeApplets(compiler, makingMIDlet, convertPNG, potentialMIDlets);
      if (this.getProperty(ResourceIds.required[0]) == null) {
         this.setProperty(ResourceIds.required[0], "unnamed");
      }

      if (this.getProperty(ResourceIds.required[1]) == null) {
         this.setProperty(ResourceIds.required[1], "0.0");
      }

      if (this.getProperty(ResourceIds.required[2]) == null) {
         this.setProperty(ResourceIds.required[2], "anonymous");
      }

      if (this.getProperty(ResourceIds.manifestVersion[0]) == null) {
         this.setProperty(ResourceIds.manifestVersion[0], "1.0");
      }

      if (this.getProperty(ResourceIds.manifestRequired[0]) == null) {
         this.setProperty(ResourceIds.manifestRequired[0], "MIDP-2.0");
      }

      if (this.getProperty(ResourceIds.manifestRequired[1]) == null) {
         this.setProperty(ResourceIds.manifestRequired[1], "CLDC-1.1");
      }

      if (this.getProperty(ResourceIds.jadRequired[0]) == null) {
         this.setProperty(ResourceIds.jadRequired[0], "unknown");
      }
   }

   private void generateAttributes(PrintStream out) {
      Enumeration tags = this.keys();

      while (tags.hasMoreElements()) {
         String tag = (String)tags.nextElement();
         String value = this.getProperty(tag);
         if (value != null) {
            out.print(tag);
            out.print(": ");
            out.println(value);
         }
      }
   }

   private void checkAttributes(String[] tags, String[][] allowed) {
      int num = tags.length;

      for (int i = 0; i < num; i++) {
         String tag = tags[i];
         String value = this.getProperty(tag);
         if (value == null) {
            throw new CompileException(907, null, ((StringBuffer)(new Object("Application missing required attribute: "))).append(tag).toString());
         }

         if (allowed != null) {
            if (value.indexOf(45) == -1) {
               throw new CompileException(
                  907, null, ((StringBuffer)(new Object("Application has malformed attribute: "))).append(tag).append(": ").append(value).toString()
               );
            }

            String[] values = allowed[i];
            int n = values.length;
            int offset = 0;

            while (true) {
               String token = null;
               int space = value.indexOf(32, offset);
               if (space == -1) {
                  token = value.substring(offset);
                  offset = space;
               } else {
                  token = value.substring(offset, space);
                  offset = space + 1;
               }

               boolean ok = false;

               for (int j = 0; j < n && !ok; j++) {
                  ok = token.equals(values[j]);
               }

               if (!ok) {
                  throw new CompileException(
                     908, null, ((StringBuffer)(new Object("Application has malformed attribute: "))).append(tag).append(": ").append(value).toString()
                  );
               }

               if (offset == -1) {
                  break;
               }
            }
         }
      }
   }

   public void generateManifest(boolean makingMIDlet) {
      if ((this._interesting || makingMIDlet) && this._manifest == null) {
         ByteArrayOutputStream bout = (ByteArrayOutputStream)(new Object());
         if (makingMIDlet) {
            this.checkAttributes(ResourceIds.required, (Object[][])null);
            this.checkAttributes(ResourceIds.manifestRequired, ResourceIds.manifestRequiredAllowed);
         }

         PrintStream out = (PrintStream)(new Object(bout));
         this.generateAttributes(out);
         out.close();
         byte[] data = bout.toByteArray();
         this._manifest = new ResourceFile("META-INF/MANIFEST.MF", data, false);
      }
   }

   public String getResourceClassName(Compiler compiler, String fileName) {
      this._generateResources = new GenerateResources(compiler, fileName, this, this._resourceBinaries);
      return this._generateResources.getClassName();
   }

   public ClassType generateResourceClass() {
      if (!this._interesting && this._resourceBinaries.size() <= 0) {
         return null;
      }

      Vector exts = (Vector)(new Object());
      int num = this._resourceBinaries.size();

      for (int i = num - 1; i >= 0; i--) {
         ResourceFile rf = (ResourceFile)this._resourceBinaries.elementAt(i);
         String extension = FileHelper.extractExtension(rf.getRelativeName());
         if (extension != null && exts.indexOf(extension) == -1) {
            exts.addElement(extension);
         }
      }

      StringBuffer ebuf = (StringBuffer)(new Object());
      num = exts.size();

      for (int var6 = 0; var6 < num; var6++) {
         ebuf.append((String)exts.elementAt(var6));
         ebuf.append('\n');
      }

      exts = null;
      StringBuffer lbuf = (StringBuffer)(new Object());
      return this._generateResources.generateResourceClass(ebuf.toString(), lbuf.toString());
   }
}
