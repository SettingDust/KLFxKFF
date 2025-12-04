package settingdust.klf_x_kff;

import cpw.mods.jarhandling.SecureJar;
import cpw.mods.jarhandling.impl.Jar;
import cpw.mods.jarhandling.impl.SimpleJarMetadata;
import cpw.mods.modlauncher.api.IEnvironment;
import cpw.mods.modlauncher.api.ITransformationService;
import cpw.mods.modlauncher.api.ITransformer;
import cpw.mods.modlauncher.api.IncompatibleEnvironmentException;
import net.minecraftforge.fml.loading.moddiscovery.ModFile;
import org.jetbrains.annotations.NotNull;
import settingdust.klf_x_kff.accessor.JarAccessor;
import settingdust.klf_x_kff.accessor.SimpleJarMetadataAccessor;
import settingdust.preloading_tricks.api.PreloadingTricksCallbacks;
import settingdust.preloading_tricks.lexforge.LexForgeModManager;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class KLFxKFFTransformationService implements ITransformationService {
    public KLFxKFFTransformationService() {
        PreloadingTricksCallbacks.SETUP_MODS.register(_manager -> {
            var manager = (LexForgeModManager) _manager;
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
        });
    }

    @Override
    public @NotNull String name() {
        return "KLFxKFF";
    }

    @Override
    public void initialize(final IEnvironment iEnvironment) {

    }

    @Override
    public void onLoad(final IEnvironment iEnvironment, final Set<String> set) throws IncompatibleEnvironmentException {

    }

    @Override
    public @NotNull List<ITransformer> transformers() {
        return List.of();
    }
}
