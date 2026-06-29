package net.rim.device.apps.internal.phone.model;

import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.util.StringPatternRepository;
import net.rim.device.apps.api.framework.registration.DeviceFeature;
import net.rim.device.apps.api.framework.registration.RIMModelFactoryRepository;
import net.rim.device.apps.api.framework.registration.RecognizerRepository;
import net.rim.device.apps.internal.phone.pattern.PhoneNumberMnemonicStringPattern;
import net.rim.device.apps.internal.phone.pattern.PhoneNumberRawStringPattern;
import net.rim.device.apps.internal.phone.pattern.PhoneNumberStringPattern;

public final class PackageManager {
   public static final void libMain(String[] args) {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      PhoneNumberModelFactory factory = new PhoneNumberModelFactory(3797587162219887872L);
      ar.put(3797587162219887872L, factory);
      RecognizerRepository.registerRecognizer(3797587162219887872L, factory);
      long[] types = new long[]{
         8414046446004092553L,
         476826571898366139L,
         7064935308737611579L,
         7076766837289517896L,
         -442687637293762776L,
         6627402073208639065L,
         2862138288634470671L,
         -1843891697376347796L,
         -7685858347957223420L,
         -875369058269133536L,
         -3455949626482418797L,
         -9028600078154395990L,
         -8729128157678578387L,
         -8042842673471032397L,
         -7506426366345717619L,
         -5337593350237507827L,
         -4327325975911070432L,
         -3185441175409080634L,
         -1232353381798389424L,
         -495464000086470534L,
         1305734903532297107L,
         1921075894755307008L,
         2088979871722912326L,
         3099247246168910617L,
         3842944447372064823L,
         8093829281380449978L,
         8731565186412401203L,
         -9040115700218593275L,
         -8729128157678667760L,
         566818545604148099L,
         -9040115700218593266L,
         -7685858349254707184L,
         -5007997147586597827L,
         -3542437882277683952L,
         567202400337268858L,
         3099259628324493155L,
         3842944447372064823L,
         -3455949603255155818L,
         -9028600078154395990L,
         -8729128157611712083L,
         -7518325843822225267L,
         -7506426366304029728L,
         -7197808905607781934L,
         -5053934343963120579L,
         -5008381001630017536L,
         -4524760760605211360L,
         -1783046538546406128L,
         -875369057503070611L,
         567202396378087030L,
         2088608399962474585L,
         2743093911910979427L,
         3831428823837231158L,
         5708419812574945693L,
         6098188357202270077L,
         7108455731648268368L,
         7305985267662006654L,
         -3455949590221766586L,
         -9028600078154395990L,
         -8042855055861746765L,
         -7345721510606776179L,
         -6858999353821895382L,
         -6379636885604127027L,
         -5019896625063108358L,
         -3542437882277686835L
      };

      for (int i = types.length - 1; i >= 0; i--) {
         long type = types[i];
         factory = new PhoneNumberModelFactory(type);
         ar.put(type, factory);
         RIMModelFactoryRepository.addFactory(-5785746452676094833L, factory);
         RIMModelFactoryRepository.addFactory(-5785746452676094832L, factory);
         RecognizerRepository.registerRecognizer(type, factory);
      }

      StringPatternRepository.addPattern(new PhoneNumberStringPattern());
      StringPatternRepository.addPattern(new PhoneNumberRawStringPattern());
      StringPatternRepository.addPattern(new PhoneNumberMnemonicStringPattern());
   }

   public static final boolean isPhoneEnabled() {
      return DeviceFeature.isPhoneEnabled() && ITPolicy.getBoolean(1, true);
   }
}
