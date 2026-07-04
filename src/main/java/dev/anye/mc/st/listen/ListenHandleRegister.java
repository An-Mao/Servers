package dev.anye.mc.st.listen;

import dev.anye.mc.st.ST;
import dev.anye.mc.st.listen.handle.server.DefaultHandle;
import dev.anye.mc.st.listen.handle.tencent.QQHandle;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;

import java.util.function.Supplier;

public class ListenHandleRegister {
	public static final Identifier KEY = Identifier.fromNamespaceAndPath(ST.MOD_ID, "listen_handle");
	public static final ResourceKey<Registry<ListenHandle>> REGISTRY_KEY = ResourceKey.createRegistryKey(KEY);
	public static final Registry<ListenHandle> REGISTRY = new RegistryBuilder<>(REGISTRY_KEY)
			.sync(false)
			.maxId(256)
			.create();

	@EventBusSubscriber(modid = ST.MOD_ID)
	public static class reg {
		@SubscribeEvent
		public static void registerRegistries(NewRegistryEvent event) {
			event.register(REGISTRY);
		}
	}

	public static final DeferredRegister<ListenHandle> COLOR_SCHEME = DeferredRegister.create(REGISTRY, ST.MOD_ID);
	public static final DeferredHolder<ListenHandle, DefaultHandle> Default_Handle = reg("default", DefaultHandle::new);

	public static final DeferredHolder<ListenHandle, QQHandle> Tencent_QQ = reg("tencent_qq", QQHandle::new);


	public static void register(IEventBus eventBus) {
		COLOR_SCHEME.register(eventBus);
	}

	public static Component getSchemeComponent(ListenHandle colorScheme) {
		return Component.translatable(getSchemeKey(colorScheme));
	}

	public static <I extends ListenHandle> DeferredHolder<ListenHandle, I> reg(String name, Supplier<? extends I> sup) {
		return COLOR_SCHEME.register(name, sup);
	}

	public static String getSchemeKey(ListenHandle colorScheme) {

		return "color_scheme." + REGISTRY.getKey(colorScheme).toLanguageKey();
	}
}
