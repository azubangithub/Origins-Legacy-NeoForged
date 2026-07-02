package io.github.apace100.origins.mixin;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Optional;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(targets = "carpet.CarpetSettings", remap = false)
public class CarpetSettingsMixin {

    @Redirect(
        method = "<clinit>",
        at = @At(
            value = "INVOKE",
            target = "Ljava/util/Optional;orElseThrow()Ljava/lang/Object;"
        )
    )
    private static Object redirectOrElseThrow(Optional<?> optional) {
        if (optional.isPresent()) {
            return optional.get();
        }
        return createMockModContainer();
    }

    private static Object createMockModContainer() {
        try {
            // First try NeoForge ModContainer
            try {
                Class<?> neoforgeContainerClass = Class.forName("net.neoforged.fml.ModContainer");
                Class<?> iModInfoClass = Class.forName("net.neoforged.neoforgespi.language.IModInfo");
                Class<?> artifactVersionClass = Class.forName("org.apache.maven.artifact.versioning.ArtifactVersion");

                Object mockVersion = Proxy.newProxyInstance(
                    artifactVersionClass.getClassLoader(),
                    new Class<?>[] { artifactVersionClass },
                    new InvocationHandler() {
                        @Override
                        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                            if (method.getName().equals("toString")) {
                                return "1.4.147+v1.0.8-251027";
                            }
                            if (method.getName().equals("getMinorVersion")) {
                                return 21;
                            }
                            if (method.getName().equals("getIncrementalVersion")) {
                                return 1;
                            }
                            return null;
                        }
                    }
                );

                Object mockModInfo = Proxy.newProxyInstance(
                    iModInfoClass.getClassLoader(),
                    new Class<?>[] { iModInfoClass },
                    new InvocationHandler() {
                        @Override
                        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                            if (method.getName().equals("getVersion")) {
                                return mockVersion;
                            }
                            if (method.getName().equals("getModId")) {
                                return "carpet";
                            }
                            return null;
                        }
                    }
                );

                return new MockNeoForgeModContainer((net.neoforged.neoforgespi.language.IModInfo) mockModInfo);
            } catch (ClassNotFoundException e) {
                // Not on NeoForge, try Fabric
            }

            Class<?> modContainerClass = Class.forName("net.fabricmc.loader.api.ModContainer");
            Class<?> modMetadataClass = Class.forName("net.fabricmc.loader.api.metadata.ModMetadata");
            Class<?> versionClass = Class.forName("net.fabricmc.loader.api.Version");

            Object mockVersion = Proxy.newProxyInstance(
                versionClass.getClassLoader(),
                new Class<?>[] { versionClass },
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        if (method.getName().equals("getFriendlyString")) {
                            return "1.4.147+v1.0.8-251027";
                        }
                        if (method.getName().equals("toString")) {
                            return "1.4.147+v1.0.8-251027";
                        }
                        return null;
                    }
                }
            );

            Object mockMetadata = Proxy.newProxyInstance(
                modMetadataClass.getClassLoader(),
                new Class<?>[] { modMetadataClass },
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        switch (method.getName()) {
                            case "getId":
                                return "carpet";
                            case "getVersion":
                                return mockVersion;
                            case "getName":
                                return "Carpet";
                            case "toString":
                                return "Carpet Metadata Proxy";
                            default:
                                return null;
                        }
                    }
                }
            );

            return Proxy.newProxyInstance(
                modContainerClass.getClassLoader(),
                new Class<?>[] { modContainerClass },
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        switch (method.getName()) {
                            case "getMetadata":
                                return mockMetadata;
                            case "toString":
                                return "Carpet ModContainer Proxy";
                            default:
                                return null;
                        }
                    }
                }
            );
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static class MockNeoForgeModContainer extends net.neoforged.fml.ModContainer {
        public MockNeoForgeModContainer(net.neoforged.neoforgespi.language.IModInfo info) {
            super(info);
        }

        @Override
        public net.neoforged.bus.api.IEventBus getEventBus() {
            return null;
        }
    }
}
