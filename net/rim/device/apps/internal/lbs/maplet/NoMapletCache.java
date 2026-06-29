package net.rim.device.apps.internal.lbs.maplet;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.util.IntVector;

public final class NoMapletCache {
   private IntVector _maplets = (IntVector)(new Object());
   private static NoMapletCache _instance;
   public static final long GUID;

   public static final void registerOnStartup() {
      getInstance();
   }

   public static final NoMapletCache getInstance() {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      _instance = (NoMapletCache)ar.getOrWaitFor(-4533593630392893058L);
      if (_instance == null) {
         _instance = new NoMapletCache();
         ar.put(-4533593630392893058L, _instance);
      }

      return _instance;
   }

   NoMapletCache() {
   }

   public final void add(int x, int y, int level) {
      this.add(Maplet.getHashKey(x, y, level));
   }

   protected final void add(int maplet) {
      if (!this._maplets.contains(maplet)) {
         this._maplets.addElement(maplet);
      }
   }

   public final void remove(int x, int y, int level) {
      this._maplets.removeElement(Maplet.getHashKey(x, y, level));
   }

   public final boolean contains(int x, int y, int level) {
      return this._maplets.contains(Maplet.getHashKey(x, y, level));
   }
}
