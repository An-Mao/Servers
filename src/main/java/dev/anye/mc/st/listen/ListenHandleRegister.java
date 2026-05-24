package dev.anye.mc.st.listen;

import net.minecraft.network.chat.Component;
import net.minecraftforge.eventbus.api.bus.BusGroup;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;
import net.minecraft.resources.Identifier;
import dev.anye.mc.st.ST;
import dev.anye.mc.st.listen.handle.server.DefaultHandle;
import dev.anye.mc.st.listen.handle.tencent.QQHandle;

import java.util.function.Supplier;

public class ListenHandleRegister {
    public static final Identifier KEY = Identifier.fromNamespaceAndPath(ST.MOD_ID, "listen_handle");
    public static final DeferredRegister<ListenHandle> Handles = DeferredRegister.create(KEY, ST.MOD_ID);

    public static final Supplier<IForgeRegistry<ListenHandle>> REGISTRY = Handles.makeRegistry(()->new RegistryBuilder<ListenHandle>().disableSync());

    public static final RegistryObject<DefaultHandle> Default_Handle = reg("default", DefaultHandle::new);

    public static final RegistryObject<QQHandle> Tencent_QQ = reg("tencent_qq", QQHandle::new);



    public static void register(BusGroup eventBus){
        Handles.register(eventBus);
    }

    public static Component getSchemeComponent(ListenHandle listenHandle){
        return Component.translatable(getSchemeKey(listenHandle));
    }

    public static <I extends ListenHandle> RegistryObject<I>  reg(String name, Supplier<? extends I> sup) {
        return Handles.register(name, sup);
    }

    public static String getSchemeKey(ListenHandle listenHandle){

        return "color_scheme."+REGISTRY.get().getKey(listenHandle).toLanguageKey();
    }
}
