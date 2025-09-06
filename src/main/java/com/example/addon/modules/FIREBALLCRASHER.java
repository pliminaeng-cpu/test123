package com.example.addon.modules;

import meteordevelopment.meteorclient.events.game.GameLeftEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.c2s.play.CommandExecutionC2SPacket;
import net.minecraft.network.packet.c2s.play.RequestCommandCompletionsC2SPacket;
import com.example.addon.AddonTemplate;
import net.minecraft.text.Text;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class FIREBALLCRASHER extends Module {
    private final MinecraftClient client = MinecraftClient.getInstance();
    private static final String CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private final SettingGroup sgGeneral = settings.createGroup("Rate");

    public FIREBALLCRASHER() {
        super(AddonTemplate.CATEGORY, "FireBallCrash", "Only Working No Spam Limit Server!!!!!!!");
    }

    private final Setting<Integer> packets = sgGeneral.add(new IntSetting.Builder()
        .name("Packets per tick")
        .description("Amount of packets sent each tick")
        .defaultValue(20)
        .min(2)
        .sliderMax(400)
        .build()
    );

    @EventHandler
    private void onTick(TickEvent.Post event) {
        Random rand = new Random();
        double px = client.player.getX();
        double py = client.player.getY();
        double pz = client.player.getZ();
        double x = px + (rand.nextDouble() * 20 - 10);
        double z = pz + (rand.nextDouble() * 20 - 10);
        double y = py + -1; // 위에서 떨어뜨리기
        String message = String.format(
            "summon fireball %.2f %.2f %.2f {ExplosionPower:128, Motion:[0.0,-10.0,0.0]}",
            x, y, z
        );
        for (int i = 0; i < packets.get(); i++) {
            MinecraftClient.getInstance().player.networkHandler.sendPacket(new CommandExecutionC2SPacket(message));
        }
    }

    @EventHandler
    private void onWorldChange(GameLeftEvent event) {
        if (this.isActive()) if (this.isActive()) this.toggle();
    }
}
