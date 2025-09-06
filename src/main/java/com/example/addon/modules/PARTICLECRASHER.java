package com.example.addon.modules;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import meteordevelopment.meteorclient.events.game.GameLeftEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;

import com.example.addon.AddonTemplate;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import net.minecraft.item.ItemStack;

import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.CommandExecutionC2SPacket;
import net.minecraft.network.packet.c2s.play.RequestCommandCompletionsC2SPacket;
import net.minecraft.screen.slot.SlotActionType;


public class PARTICLECRASHER extends Module {
    private final SettingGroup sgGeneral = settings.createGroup("Rate");

    public PARTICLECRASHER() {
        super(AddonTemplate.CATEGORY, "ParticleCrash", "Use Particle Command LOL(Need OP)");
    }
    private final Setting<Boolean> BLATANT = sgGeneral.add(new BoolSetting.Builder()
        .name("BLATANT MODE")
        .description("Including Self")
        .defaultValue(false)
        .build()
    );
    @EventHandler
    public void onActivate() {
        if (MinecraftClient.getInstance().player.hasPermissionLevel(2))
        {
            if (BLATANT.get()) {
                this.toggle();
                MinecraftClient.getInstance().player.networkHandler.sendPacket(
                    new CommandExecutionC2SPacket(
                        "execute as @a at @s run particle minecraft:ash ~ ~ ~ ~ ~ ~ 0 2147483647 force @a"
                    )
                );
            } else {
                this.toggle();
                MinecraftClient.getInstance().player.networkHandler.sendPacket(
                    new CommandExecutionC2SPacket(
                        "execute as @a[name=!" + MinecraftClient.getInstance().player.getName().getLiteralString() +
                            "] at @s run particle minecraft:ash ~ ~ ~ ~ ~ ~ 0 2147483647 force @a[name=!" +
                            MinecraftClient.getInstance().player.getName().getLiteralString() + "]"
                    )
                );
            }
        }

    }

}
