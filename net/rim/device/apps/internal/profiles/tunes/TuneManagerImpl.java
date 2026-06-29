package net.rim.device.apps.internal.profiles.tunes;

import java.util.Enumeration;
import java.util.Vector;
import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import javax.microedition.io.file.FileSystemListener;
import net.rim.device.api.io.MIMETypeAssociations;
import net.rim.device.api.io.file.FileSystemJournal;
import net.rim.device.api.io.file.FileSystemJournalEntry;
import net.rim.device.api.io.file.FileSystemJournalListener;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.Branding;
import net.rim.device.api.ui.theme.Theme;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.api.framework.file.FileBrowser;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.VerbProvider;
import net.rim.device.apps.api.framework.profiles.TuneChoiceField;
import net.rim.device.apps.api.framework.profiles.TuneManager;
import net.rim.device.apps.api.framework.profiles.TuneProvider;
import net.rim.device.apps.api.framework.registration.VerbRepository;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.cldc.io.utility.URIDecoder;
import net.rim.device.internal.io.file.FileUtilities;
import net.rim.device.internal.proxy.Proxy;
import net.rim.device.internal.system.AlertPlayer;
import net.rim.vm.Array;

public final class TuneManagerImpl extends TuneManager implements FileSystemListener, FileSystemJournalListener, VerbProvider {
   private int _builtinTuneCount;
   private long _myStoredUSN;
   private String _brandingTuneName;
   private AlertPlayer _brandingTune;
   private int _brandingTuneIndex = -1;
   private TuneManagerImpl$TuneManagerData _tuneManagerData;
   private static final long GUID = 8253404770240635913L;
   private static String[] AUTO_LOAD_PATHS = new String[]{"/store/home/user/ringtones/", "/SDCard/BlackBerry/ringtones/"};
   private static String STORE_RINGTONES_FOLDER = AUTO_LOAD_PATHS[0];
   private static String BUILTIN_RINGTONES_FOLDER = "/store/samples/ringtones/";

   public static final void main(String[] args) {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      ar.put(8253404770240635913L, new TuneManagerImpl());
      VerbRepository.getVerbRepository(-2843135760572915788L).register(new TuneManagerImpl$SetPhoneTuneVerb(), 8830038681865959882L);
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private TuneManagerImpl() {
      byte[] data = Branding.getData(16386);
      if (data != null) {
         boolean var4 = false /* VF: Semaphore variable */;

         label31:
         try {
            var4 = true;
            this._brandingTuneName = new String(data, "UTF-8");
            byte[] p = Branding.getData(16390);
            if (p != null) {
               this._brandingTune = new ContentAlertPlayer(p, new String(Branding.getData(16391), "UTF-8"));
            }

            if (this._brandingTune == null) {
               p = Branding.getData(16388);
               if (p != null) {
                  this._brandingTune = new ContentAlertPlayer(p, "audio/midi");
                  var4 = false;
               } else {
                  var4 = false;
               }
            } else {
               var4 = false;
            }
         } finally {
            if (var4) {
               this._brandingTune = null;
               this._brandingTuneName = null;
               break label31;
            }
         }
      }

      this._tuneManagerData = new TuneManagerImpl$TuneManagerData();
      this.buildTunes();
      this._myStoredUSN = FileSystemJournal.getNextUSN();
      Proxy p = Proxy.getInstance();
      p.addFileSystemListener(this);
      p.addFileSystemJournalListener(this);
   }

   @Override
   public final synchronized AlertPlayer getTune(String name) {
      AlertPlayer alertPlayer = null;
      if (name != null) {
         try {
            alertPlayer = new ContentAlertPlayer(name);
         } finally {
            ;
         }

         if (alertPlayer == null) {
            if (name.equalsIgnoreCase(this._brandingTuneName)) {
               return this._brandingTune;
            }

            alertPlayer = this.getThemeTune(name);
            if (alertPlayer == null) {
               name = this.resolveTune(name);

               try {
                  return new ContentAlertPlayer(name);
               } finally {
                  ;
               }
            }
         }
      }

      return alertPlayer;
   }

   @Override
   public final String resolveTune(String name) {
      if (name != null && this.isLegacy(name)) {
         String[] locations = new String[]{BUILTIN_RINGTONES_FOLDER, STORE_RINGTONES_FOLDER};

         try {
            int length = locations.length;
            boolean resolved = false;

            for (int index = 0; index < length; index++) {
               FileConnection fc = (FileConnection)Connector.open("file://" + locations[index]);
               Enumeration listing = fc.list(name + "*", false);

               while (listing.hasMoreElements() && !resolved) {
                  String item = (String)listing.nextElement();
                  if (this.isRecognizedType(item)) {
                     try {
                        name = FileUtilities.encodeString(locations[index] + item);
                        resolved = true;
                     } finally {
                        continue;
                     }
                  }
               }

               if (resolved) {
                  break;
               }
            }
         } finally {
            return name;
         }
      }

      return name;
   }

   @Override
   public final String getBuiltInTuneFileName(int id) {
      if (id < 0) {
         return null;
      } else {
         return this._builtinTuneCount > 0 && id < this._tuneManagerData._builtInNames.length ? this._tuneManagerData._builtInNames[id] : null;
      }
   }

   @Override
   public final String[] getAllTuneFileNames() {
      String[] themeTuneNames = this.getThemeTuneNames();
      return themeTuneNames == null ? this._tuneManagerData._allTuneFileNames : this.combineNameArrays(this._tuneManagerData._allTuneFileNames, themeTuneNames);
   }

   private final String[] combineNameArrays(String[] namesA, String[] namesB) {
      int countA = namesA.length;
      int countB = namesB.length;
      String[] names = new String[countA + countB];

      for (int idx = countA - 1; idx >= 0; idx--) {
         names[idx] = namesA[idx];
      }

      for (int idx = countB - 1; idx >= 0; idx--) {
         names[countA + idx] = namesB[idx];
      }

      return names;
   }

   private final void initBuiltInTunes() {
      String[] tuneNames = null;
      ApplicationRegistry registry = ApplicationRegistry.getApplicationRegistry();
      Object o = registry.get(6675644780973176756L);
      if (o instanceof TuneProvider) {
         TuneProvider tuneProvider = (TuneProvider)o;
         tuneNames = tuneProvider.getTuneFilenames();
      }

      if (tuneNames != null && tuneNames.length > 0) {
         this._tuneManagerData._builtInNames = tuneNames;
         this._builtinTuneCount = tuneNames.length;
      } else {
         this._builtinTuneCount = 0;
      }

      if (this._brandingTune != null && this._brandingTuneName != null) {
         if (this._tuneManagerData._builtInNames == null) {
            this._tuneManagerData._builtInNames = new String[0];
         }

         Arrays.add(this._tuneManagerData._builtInNames, this._brandingTuneName);
         this._brandingTuneIndex = this._builtinTuneCount;
      }
   }

   @Override
   public final int getBrandingTuneIndex() {
      return this._brandingTuneIndex;
   }

   private final boolean isRecognizedType(String file) {
      return MIMETypeAssociations.getMediaType(file) == 2;
   }

   @Override
   public final String getDisplayName(String tuneFileName) {
      String tempName = URIDecoder.decode(FileUtilities.getName(tuneFileName), "UTF-8", false);
      int dotIndex = tempName.indexOf(46);
      return dotIndex == -1 ? tempName : tempName.substring(0, dotIndex);
   }

   @Override
   public final String getLegacyName(String tuneFileName) {
      String tempName = FileUtilities.getName(tuneFileName);
      if (tuneFileName.indexOf("/samples/") != -1) {
         int dotIndex = tempName.indexOf(46);
         if (dotIndex != -1) {
            return tempName.substring(0, dotIndex);
         }
      }

      return URIDecoder.decode(tempName, "UTF-8", false);
   }

   private final boolean isLegacy(String tuneFileName) {
      return tuneFileName.indexOf("/") == -1;
   }

   @Override
   public final int getIndex(String[] fileNames, String tuneFileName) {
      return this.getIndex(fileNames, tuneFileName, this.isLegacy(tuneFileName));
   }

   private final int getIndex(String[] fileNames, String tuneFileName, boolean isLegacy) {
      if (!isLegacy) {
         for (int i = fileNames.length - 1; i >= 0; i--) {
            if (StringUtilities.strEqualIgnoreCase(fileNames[i], tuneFileName)) {
               return i;
            }
         }
      } else {
         for (int i = fileNames.length - 1; i >= 0; i--) {
            if (StringUtilities.strEqualIgnoreCase(this.getLegacyName(fileNames[i]), tuneFileName)) {
               return i;
            }
         }
      }

      return -1;
   }

   private final void initAllTuneNames() {
      if (this._tuneManagerData._allTuneFileNames != null) {
         this._tuneManagerData._allTuneFileNames = null;
      }

      this._tuneManagerData._allTuneFileNames = new String[0];
      if (this._brandingTune != null && this._brandingTuneName != null) {
         Arrays.add(this._tuneManagerData._allTuneFileNames, this._brandingTuneName);
      }

      for (int i = 0; i < AUTO_LOAD_PATHS.length; i++) {
         try {
            FileConnection fc = (FileConnection)Connector.open("file://" + AUTO_LOAD_PATHS[i]);
            Enumeration listItems = fc.list();

            while (listItems.hasMoreElements()) {
               String nextItem = (String)listItems.nextElement();
               if (this.isRecognizedType(nextItem)) {
                  Arrays.add(this._tuneManagerData._allTuneFileNames, AUTO_LOAD_PATHS[i] + FileUtilities.encodeString(nextItem));
               }
            }
         } finally {
            continue;
         }
      }

      if (this._builtinTuneCount > 0) {
         int startIndex = this._tuneManagerData._allTuneFileNames.length;
         Array.resize(this._tuneManagerData._allTuneFileNames, startIndex + this._builtinTuneCount);
         System.arraycopy(this._tuneManagerData._builtInNames, 0, this._tuneManagerData._allTuneFileNames, startIndex, this._builtinTuneCount);
      }
   }

   @Override
   public final boolean isBrandingTune(String name) {
      return this._brandingTuneName != null && name != null && this._brandingTuneName.equals(name);
   }

   @Override
   public final String getBrandingTuneFileName() {
      return this._brandingTuneName;
   }

   @Override
   public final boolean isBuiltinTune(String fileName) {
      boolean builtInTune = false;
      if (this._builtinTuneCount > 0) {
         builtInTune = this.getIndex(this._tuneManagerData._builtInNames, fileName) != -1;
      }

      return builtInTune;
   }

   @Override
   public final boolean isTuneAvailable(int id) {
      return this.isTuneAvailable(this.getBuiltInTuneFileName(id));
   }

   @Override
   public final boolean isTuneAvailable(String fileName) {
      return this.getTune(fileName) != null;
   }

   @Override
   public final synchronized int getBuiltInTuneCount() {
      return this._builtinTuneCount;
   }

   @Override
   public final synchronized int getTuneCount() {
      return this.getAllTuneFileNames().length;
   }

   private final AlertPlayer getThemeTune(String name) {
      Theme theme = ThemeManager.getActiveTheme();
      if (theme != null) {
         byte[] data = theme.getRingtone(name);
         if (data != null) {
            String type = "audio/midi";
            return new ContentAlertPlayer(data, type);
         }
      }

      return null;
   }

   private final String[] getThemeTuneNames() {
      Theme theme = ThemeManager.getActiveTheme();
      if (theme != null) {
         Enumeration enumeration = theme.ringtones();
         if (enumeration != null && enumeration.hasMoreElements()) {
            Vector v = new Vector();

            while (enumeration.hasMoreElements()) {
               v.addElement(enumeration.nextElement());
            }

            String[] names = new String[v.size()];
            v.copyInto(names);
            return names;
         }
      }

      return null;
   }

   @Override
   public final void showTuneListingScreen() {
      FileBrowser fileBrowser = new FileBrowser(null, 2, this);
      fileBrowser.show(null);
   }

   @Override
   public final TuneChoiceField getTuneChoiceField(String label, String selectedTuneName, String defaultTuneName, boolean addMute) {
      return new TuneChoiceFieldImpl(label, selectedTuneName, defaultTuneName, addMute);
   }

   @Override
   public final Verb getVerbs(Object context, Verb[] verbs) {
      Verb defaultVerb = null;
      String filename = (String)ContextObject.get(context, 2765042845091913199L);
      if (filename != null && this.isRecognizedType(filename)) {
         Verb setTuneVerb = new TuneManagerImpl$SetPhoneTuneVerb();
         Arrays.add(verbs, setTuneVerb);
         if (!ContextObject.getFlag(context, 45)) {
            defaultVerb = setTuneVerb;
         }
      }

      VerbRepository verbRepository = VerbRepository.getVerbRepository(8855791131633157813L);
      if (verbRepository != null) {
         Verb[] downloadVerbs = verbRepository.getVerbs(-201905085362485851L);
         if (downloadVerbs != null) {
            int i = downloadVerbs.length;

            while (--i >= 0) {
               Arrays.add(verbs, downloadVerbs[i]);
            }
         }
      }

      return defaultVerb;
   }

   @Override
   public final void rootChanged(int type, String rootName) {
      if ((type == 0 || type == 1) && ("store/".equals(rootName) || "SDCard/".equals(rootName))) {
         this.initAllTuneNames();
      }
   }

   @Override
   public final boolean isValidTuneEntry(String entry) {
      return (StringUtilities.startsWithIgnoreCase(entry, AUTO_LOAD_PATHS[0]) || StringUtilities.startsWithIgnoreCase(entry, AUTO_LOAD_PATHS[1]))
         && this.isRecognizedType(entry);
   }

   @Override
   public final void fileJournalChanged() {
      long nextUSN = FileSystemJournal.getNextUSN();

      for (long lookUSN = nextUSN - 1; lookUSN >= this._myStoredUSN; lookUSN -= 1) {
         FileSystemJournalEntry entry = FileSystemJournal.getEntry(lookUSN);
         if (entry == null) {
            break;
         }

         String path = entry.getPath();
         if (path != null) {
            int eventType = entry.getEvent();
            if (eventType == 1) {
               int index = this.getIndex(this._tuneManagerData._allTuneFileNames, path, false);
               if (index > -1) {
                  Arrays.removeAt(this._tuneManagerData._allTuneFileNames, index);
               }
            } else {
               String oldPath = entry.getOldPath();
               if (eventType == 3 && oldPath != null) {
                  int index = this.getIndex(this._tuneManagerData._allTuneFileNames, oldPath, false);
                  if (index > -1) {
                     Arrays.removeAt(this._tuneManagerData._allTuneFileNames, index);
                  }
               }

               this.addTune(path);
            }
         }
      }

      this._myStoredUSN = nextUSN;
   }

   private final boolean addTune(String name) {
      boolean tuneAdded = false;
      if (this.isValidTuneEntry(name) && this.getIndex(this._tuneManagerData._allTuneFileNames, name, false) == -1) {
         int length = this._tuneManagerData._allTuneFileNames.length;
         int pathIndex = length - this.getBuiltInTuneCount();
         if (pathIndex >= 0) {
            Arrays.insertAt(this._tuneManagerData._allTuneFileNames, name, pathIndex);
            tuneAdded = true;
         }
      }

      return tuneAdded;
   }

   private final void buildTunes() {
      if (this._tuneManagerData != null) {
         this.initBuiltInTunes();
         this.initAllTuneNames();
      }
   }
}
