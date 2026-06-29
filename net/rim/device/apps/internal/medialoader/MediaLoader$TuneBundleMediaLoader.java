package net.rim.device.apps.internal.medialoader;

import net.rim.device.apps.api.framework.profiles.TuneProvider;

public final class MediaLoader$TuneBundleMediaLoader implements TuneProvider {
   private String[] _tuneFileNames;

   public MediaLoader$TuneBundleMediaLoader(String[] tuneFileNames) {
      this._tuneFileNames = tuneFileNames;
   }

   @Override
   public final String[] getTuneFilenames() {
      return this._tuneFileNames;
   }
}
