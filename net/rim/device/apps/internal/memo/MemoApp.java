package net.rim.device.apps.internal.memo;

import net.rim.device.api.synchronization.SyncManager;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.apps.api.framework.registration.VerbRepository;
import net.rim.device.apps.api.search.GlobalSearchRegistry;

final class MemoApp extends UiApplication {
   private static final long MEMO_SEARCHABLE_ID = -4956786003477159331L;

   private MemoApp() {
   }

   public static final void main(String[] args) {
      if (args != null && args.length == 1 && args[0].equals("init")) {
         Application.setAcceptEventsForProcess(false);
         MemoOptions memoOptions = MemoOptions.getOptions();
         memoOptions.enableSynchronization();
         ApplicationRegistry.getApplicationRegistry().put(-3309887157296949036L, MemoModelFactory.getInstance());
         SyncManager.getInstance().enableSynchronization(MemoCollectionImpl.getInstance(), memoOptions.isWirelessSyncAllowed(), 4);
         GlobalSearchRegistry.register(-4956786003477159331L, new MemoSearchable());
         VerbRepository.getVerbRepository(725314104275387162L).register(new EditMemoVerb(null), 4738722199580714034L);
         VerbRepository.getVerbRepository(1725319375636856359L).register(new MemoList$NewMemoVerb(""), 4738722199580714034L);
      } else {
         MemoApp app = new MemoApp();
         app.pushScreen(new MemoList());
         app.enterEventDispatcher();
      }
   }
}
