package settingdust.klf_x_kff;

import cpw.mods.jarhandling.SecureJar;
import cpw.mods.jarhandling.impl.Jar;
import cpw.mods.jarhandling.impl.SimpleJarMetadata;
import net.minecraftforge.fml.loading.moddiscovery.ModFile;
import settingdust.klf_x_kff.accessor.JarAccessor;
import settingdust.klf_x_kff.accessor.SimpleJarMetadataAccessor;
import settingdust.preloading_tricks.api.PreloadingTricksCallback;
import settingdust.preloading_tricks.api.PreloadingTricksModManager;
import settingdust.preloading_tricks.lexforge.LexForgeModManager;

import java.util.stream.Collectors;

public class KLFxKFFPreloadingCallback implements PreloadingTricksCallback {
    @Override
    public void onSetupMods() {
        var manager = PreloadingTricksModManager.<LexForgeModManager>get();
        ModFile kffFile = null;
        ModFile klfFile = null;
        for (final var modFile : manager.all()) {
            if (modFile.getSecureJar().name().equals("thedarkcolour.kotlinforforge")) {
                kffFile = modFile;
            }
            if (modFile.getSecureJar().name().equals("klf")) {
                klfFile = modFile;
            }
        }
        if (kffFile == null || klfFile == null) return;
        var klfPackages = klfFile.getSecureJar().getPackages();
        var klfProvides =
            klfFile.getSecureJar().getProviders().stream().map(SecureJar.Provider::serviceName).collect(Collectors.toSet());
        var kffJar = (Jar) kffFile.getSecureJar();

        var metadata = (SimpleJarMetadata) JarAccessor.getMetadata(kffJar);
        SimpleJarMetadataAccessor.setPkgs(
            metadata, kffJar.getPackages().stream()
                            .filter(it -> !klfPackages.contains(it))
                            .collect(Collectors.toSet())
        );
        SimpleJarMetadataAccessor.setProviders(
            metadata,
            metadata.providers()
                    .stream()
                    .filter(it -> !klfProvides.contains(it.serviceName()))
                    .toList()
        );
    }
}
