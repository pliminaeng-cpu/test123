package com.example.addon.modules;

import com.example.addon.AddonTemplate;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.c2s.play.CommandExecutionC2SPacket;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.ArrayDeque;
import java.util.Queue;

public class KABOOMFILL extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    public KABOOMFILL() {
        super(AddonTemplate.CATEGORY, "KaBoomFill", "Only Working Kaboom.pw");
    }

    private final Setting<Integer> x1 = sgGeneral.add(new IntSetting.Builder()
        .name("x1").defaultValue(100).min(-4000).sliderMax(4000).build()
    );
    private final Setting<Integer> y1 = sgGeneral.add(new IntSetting.Builder()
        .name("y1").defaultValue(1).min(-4000).sliderMax(4000).build()
    );
    private final Setting<Integer> z1 = sgGeneral.add(new IntSetting.Builder()
        .name("z1").defaultValue(100).min(-4000).sliderMax(4000).build()
    );
    private final Setting<Integer> x2 = sgGeneral.add(new IntSetting.Builder()
        .name("x2").defaultValue(-100).min(-4000).sliderMax(4000).build()
    );
    private final Setting<Integer> y2 = sgGeneral.add(new IntSetting.Builder()
        .name("y2").defaultValue(100).min(-4000).sliderMax(4000).build()
    );
    private final Setting<Integer> z2 = sgGeneral.add(new IntSetting.Builder()
        .name("z2").defaultValue(-100).min(-4000).sliderMax(4000).build()
    );
    private final Setting<Block> block = sgGeneral.add(new BlockSetting.Builder()
        .name("block").description("Block to use in fill.")
        .defaultValue(Blocks.GRASS_BLOCK).build()
    );
    private final Setting<Boolean> air = sgGeneral.add(new BoolSetting.Builder()
        .name("Remove?")
        .description("fill air")
        .defaultValue(false)
        .build()
    );
    private final Setting<Boolean> pos = sgGeneral.add(new BoolSetting.Builder()
        .name("relative position")
        .description("Use relative coordinates (~).")
        .defaultValue(false)
        .build()
    );

    // --- 큐와 딜레이 관리 ---
    private final Queue<String> commandQueue = new ArrayDeque<>();
    private int tickDelay = 0;

    private static void sendFill(String cmd) {
        if (MinecraftClient.getInstance().player != null) {
            MinecraftClient.getInstance().player.networkHandler.sendPacket(
                new CommandExecutionC2SPacket(cmd)
            );
        }
    }

    // --- 영역을 잘라서 fill 명령어 큐에 쌓기 ---
    private void bigFill(int x1, int y1, int z1, int x2, int y2, int z2, String block) {
        int minX = Math.min(x1, x2);
        int maxX = Math.max(x1, x2);
        int minY = Math.min(y1, y2);
        int maxY = Math.max(y1, y2);
        int minZ = Math.min(z1, z2);
        int maxZ = Math.max(z1, z2);

        final int MAX = 32768;

        int stepX = 32;
        int stepZ = 32;
        int stepY = Math.max(1, MAX / (stepX * stepZ));

        for (int x = minX; x <= maxX; x += stepX) {
            for (int y = minY; y <= maxY; y += stepY) {
                for (int z = minZ; z <= maxZ; z += stepZ) {
                    int endX = Math.min(x + stepX - 1, maxX);
                    int endY = Math.min(y + stepY - 1, maxY);
                    int endZ = Math.min(z + stepZ - 1, maxZ);

                    int count = (endX - x + 1) * (endY - y + 1) * (endZ - z + 1);
                    if (count > MAX) continue;
                    if (pos.get()) {
                        String command = String.format(
                            "fill ~%d ~%d ~%d ~%d ~%d ~%d %s",
                            x, y, z,
                            endX, endY, endZ,
                            block
                        );
                        commandQueue.add(command);
                    } else {
                        String command = String.format(
                            "fill %d %d %d %d %d %d %s",
                            x, y, z,
                            endX, endY, endZ,
                            block
                        );
                        commandQueue.add(command);
                    }
                }
            }
        }
    }

    @Override
    public void onActivate() {
        if (air.get()) {
            String blockId = Registries.BLOCK.getId(Blocks.AIR).toString();
            bigFill(x1.get(), y1.get(), z1.get(), x2.get(), y2.get(), z2.get(), blockId);
        } else {
            String blockId = Registries.BLOCK.getId(block.get()).toString();
            bigFill(x1.get(), y1.get(), z1.get(), x2.get(), y2.get(), z2.get(), blockId);
        }
    }

    // --- 매 틱마다 큐에서 하나씩 꺼내서 실행 ---
    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (tickDelay > 0) {
            tickDelay--;
            return;
        }

        if (!commandQueue.isEmpty()) {
            String cmd = commandQueue.poll();
            sendFill(cmd);
            tickDelay = 1; // 1틱(0.05초) 대기
        } else {
            MinecraftClient mc = MinecraftClient.getInstance();
            mc.inGameHud.getChatHud().addMessage(
                Text.literal("Complete!").styled(style -> style.withColor(Formatting.GREEN))
            );
            this.toggle();
        }
    }
}
