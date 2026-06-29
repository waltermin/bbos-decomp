package net.rim.device.apps.internal.idlescreen;

import javax.microedition.io.file.FileSystemListener;
import net.rim.device.api.io.file.FileSystemJournal;
import net.rim.device.api.io.file.FileSystemJournalEntry;
import net.rim.device.api.io.file.FileSystemJournalListener;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.internal.io.file.FileUtilities;
import net.rim.device.internal.proxy.Proxy;

final class IdleScreenApplication$GlobalData implements FileSystemListener, FileSystemJournalListener, GlobalEventListener, ScreenClearer {
   private ApplicationDescriptor _showDescriptor;
   private IdleScreen _screen;
   private IdleScreenApplication$MyRealtimeClockListener _clockListener;
   private IdleScreenApplication _instance;
   private Application _hook;
   private boolean _launchPending;
   private long _myStoredUSN;
   static final long GUID = 5892374562583112875L;

   IdleScreenApplication$GlobalData() {
      ApplicationDescriptor descriptor = ApplicationDescriptor.currentApplicationDescriptor();
      this._showDescriptor = new ApplicationDescriptor(descriptor, new String[]{"show"});
      this._clockListener = new IdleScreenApplication$MyRealtimeClockListener();
      Proxy proxy = Proxy.getInstance();
      proxy.addGlobalEventListener(this);
      proxy.addFileSystemListener(this);
      proxy.addFileSystemJournalListener(this);
   }

   @Override
   public final void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid == 2573494863350550132L) {
         this.nullifyImageNameAndClear();
      }
   }

   @Override
   public final void rootChanged(int type, String rootName) {
      if ("store/".equals(rootName)) {
         this.clearScreen();
      }
   }

   @Override
   public final void fileJournalChanged() {
      String idleFilename = IdleScreenOptions.getIdleScreenFilename();
      long nextUSN = FileSystemJournal.getNextUSN();

      for (long lookUSN = nextUSN - 1; lookUSN >= this._myStoredUSN; lookUSN -= 1) {
         FileSystemJournalEntry entry = FileSystemJournal.getEntry(lookUSN);
         if (entry == null) {
            break;
         }

         String path = entry.getPath();
         switch (entry.getEvent()) {
            case 1:
               if (FileUtilities.filenamesMatch(path, idleFilename) || StringUtilities.strEqual(path, "/store/appdata/rim/idlescreen/carrier/brand")) {
                  this.nullifyImageNameAndClear();
               }
               break;
            case 3:
               if (FileUtilities.filenamesMatch(entry.getOldPath(), idleFilename)) {
                  IdleScreenOptions.setIdleScreenFilename(path);
               }
         }
      }

      this._myStoredUSN = nextUSN;
   }

   private final void nullifyImageNameAndClear() {
      IdleScreenOptions.setIdleScreenFilename(null);
      this.clearScreen();
   }

   @Override
   public final void clearScreen() {
      this._screen = null;
   }
}
