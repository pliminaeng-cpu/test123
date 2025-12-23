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

public class KABOOMPYRAMID extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    public KABOOMPYRAMID() {
        super(AddonTemplate.CATEGORY, "KaBoomPyramid", "Create pyramids with optional hollow interior.");
    }

    // --- 좌표 설정 ---
    private final Setting<Integer> x1 = sgGeneral.add(new IntSetting.Builder().name("x1").defaultValue(100).min(-4000).sliderMax(4000).build());
    private final Setting<Integer> y1 = sgGeneral.add(new IntSetting.Builder().name("y1").defaultValue(1).min(-4000).sliderMax(4000).build());
    private final Setting<Integer> z1 = sgGeneral.add(new IntSetting.Builder().name("z1").defaultValue(100).min(-4000).sliderMax(4000).build());
    private final Setting<Integer> x2 = sgGeneral.add(new IntSetting.Builder().name("x2").defaultValue(-100).min(-4000).sliderMax(4000).build());
    private final Setting<Integer> y2 = sgGeneral.add(new IntSetting.Builder().name("y2").defaultValue(100).min(-4000).sliderMax(4000).build());
    private final Setting<Integer> z2 = sgGeneral.add(new IntSetting.Builder().name("z2").defaultValue(-100).min(-4000).sliderMax(4000).build());

    private final Setting<Block> block = sgGeneral.add(new BlockSetting.Builder()
        .name("block")
        .description("Block to use in pyramid.")
        .defaultValue(Blocks.SANDSTONE) // 기본값 사암
        .build()
    );

    private final Setting<Boolean> hollow = sgGeneral.add(new BoolSetting.Builder()
        .name("hollow")
        .description("If true, the pyramid interior will be air.")
        .defaultValue(false)
        .build()
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
            MinecraftClient.getInstance().player.networkHandler.sendPacket(new CommandExecutionC2SPacket(cmd));
        }
    }

    // --- 영역 자동 분할 fill ---
    private void bigFill(int x1, int y1, int z1, int x2, int y2, int z2, String blockId) {
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

                    String command;
                    if (pos.get()) {
                        command = String.format("fill ~%d ~%d ~%d ~%d ~%d ~%d %s", x, y, z, endX, endY, endZ, blockId);
                    } else {
                        command = String.format("fill %d %d %d %d %d %d %s", x, y, z, endX, endY, endZ, blockId);
                    }
                    commandQueue.add(command);
                }
            }
        }
    }

    // --- 피라미드 생성 ---
    private void queuePyramid(int cx, int cy, int cz, int width, int height, String blockId, boolean hollow) {
        for (int y = 0; y < height; y++) {
            int layerWidth = width - y * 2;
            if (layerWidth <= 0) break;

            int minX = cx - layerWidth / 2;
            int maxX = cx + layerWidth / 2;
            int minZ = cz - layerWidth / 2;
            int maxZ = cz + layerWidth / 2;
            int yLevel = cy + y;

            if (hollow && layerWidth > 2) {
                // 외벽만 쌓기
                bigFill(minX, yLevel, minZ, maxX, yLevel, minZ, blockId); // 앞 벽
                bigFill(minX, yLevel, maxZ, maxX, yLevel, maxZ, blockId); // 뒤 벽
                bigFill(minX, yLevel, minZ + 1, minX, yLevel, maxZ - 1, blockId); // 왼쪽 벽
                bigFill(maxX, yLevel, minZ + 1, maxX, yLevel, maxZ - 1, blockId); // 오른쪽 벽
            } else {
                // 내부까지 채움
                bigFill(minX, yLevel, minZ, maxX, yLevel, maxZ, blockId);
            }
        }
    }

    @Override
    public void onActivate() {
        String blockId = "";
        if (air.get()) {
            blockId = Registries.BLOCK.getId(Blocks.AIR).toString();
        } else {
            blockId = Registries.BLOCK.getId(block.get()).toString();
        }
        int centerX = (x1.get() + x2.get()) / 2;
        int centerZ = (z1.get() + z2.get()) / 2;
        int width = Math.abs(x2.get() - x1.get()) + 1;
        int height = Math.abs(y2.get() - y1.get()) + 1;

        queuePyramid(centerX, y1.get(), centerZ, width, height, blockId, hollow.get());
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
            tickDelay = 1; // 1틱 = 0.05초 대기
        } else {
            MinecraftClient mc = MinecraftClient.getInstance();
            mc.inGameHud.getChatHud().addMessage(
                Text.literal("Complete!").styled(style -> style.withColor(Formatting.GREEN))
            );

            this.toggle(); // 완료되면 모듈 자동 종료
        }
    }
}
