package com.voygar.companion;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class VoygarCompanionMod implements ModInitializer {
    public static final String MODID = "voygar_companion";
    public static final String PREFIX = "@voy";

    private static WsClient wsClient;
    private static CommandRunner runner;

    @Override
    public void onInitialize() {
        wsClient = new WsClient("ws://127.0.0.1:8765");
        runner = new CommandRunner();

        ServerMessageEvents.CHAT_MESSAGE.register((message, sender, params) -> {
            String raw = message.getSignedContent();
            if (raw == null) return;
            if (!raw.startsWith(PREFIX)) return;

            String prompt = raw.substring(PREFIX.length()).trim();
            MinecraftServer server = sender.getServer();

            if (!wsClient.isOpen()) {
                sender.sendMessage(Text.literal("[Voy] Agent offline. Start agent.py"));
                return;
            }

            String payload = RequestJson.builder()
                    .player(sender.getNameForScoreboard())
                    .x(sender.getX()).y(sender.getY()).z(sender.getZ())
                    .dimension(sender.getWorld().getRegistryKey().getValue().toString())
                    .prompt(prompt)
                    .build();

            try {
                String plan = wsClient.requestPlan(payload);
                int executed = runner.executePlan(server, sender, plan);
                sender.sendMessage(Text.literal("[Voy] Executed steps: " + executed));
            } catch (Exception e) {
                sender.sendMessage(Text.literal("[Voy] Error: " + e.getMessage()));
            }
        });
    }
}
