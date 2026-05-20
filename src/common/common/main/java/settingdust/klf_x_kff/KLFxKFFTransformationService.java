package settingdust.klf_x_kff;

import cpw.mods.jarhandling.SecureJar;
import cpw.mods.jarhandling.impl.Jar;
import cpw.mods.jarhandling.impl.SimpleJarMetadata;
import cpw.mods.modlauncher.api.IEnvironment;
import cpw.mods.modlauncher.api.ITransformationService;
import cpw.mods.modlauncher.api.ITransformer;
import cpw.mods.modlauncher.api.IncompatibleEnvironmentException;
import org.jetbrains.annotations.NotNull;
import settingdust.klf_x_kff.accessor.JarAccessor;
import settingdust.klf_x_kff.accessor.SimpleJarMetadataAccessor;
import settingdust.preloading_tricks.api.PreloadingTricksCallbacks;
import settingdust.preloading_tricks.forge.modlauncher.LexForgeModManager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class KLFxKFFTransformationService implements ITransformationService {
    public KLFxKFFTransformationService() {
        PreloadingTricksCallbacks.SETUP_MODS.register(_manager -> {
            var manager = (LexForgeModManager) _manager;
            var klfFile = manager.all()
                    .stream()
                    .filter(it -> it.getSecureJar().name().equals("klf"))
                    .findAny()
                    .orElse(null);
            var klfPackages = klfFile.getSecureJar().getPackages();
            var klfProvides =
                    klfFile.getSecureJar()
                            .getProviders()
                            .stream()
                            .map(SecureJar.Provider::serviceName)
                            .collect(Collectors.toSet());

            for (var modFile : manager.all()) {
                if (modFile == klfFile) continue;
                var jar = (Jar) modFile.getSecureJar();
                if (!(JarAccessor.getMetadata(jar) instanceof SimpleJarMetadata metadata)) continue;
                var packages = new HashSet<>(jar.getPackages());
                var providers = new ArrayList<>(metadata.providers());
                var needRemove = false;
                for (var packageName : jar.getPackages()) {
                    if (klfPackages.contains(packageName)) {
                        packages.remove(packageName);
                        needRemove = true;
                    }
                }
                if (!needRemove) continue;
                providers.removeIf(provider -> klfProvides.contains(provider.serviceName()));
                if (packages.size() != jar.getPackages().size()) {
                    SimpleJarMetadataAccessor.setPkgs(metadata, Set.copyOf(packages));
                }
                if (providers.size() != metadata.providers().size()) {
                    SimpleJarMetadataAccessor.setProviders(metadata, List.copyOf(providers));
                }
            }
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
