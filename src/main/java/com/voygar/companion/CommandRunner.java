package com.voygar.companion;

import com.google.gson.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

public class CommandRunner {
    private static final String[] WHITELIST = new String[]{"say", "tp", "setblock", "fill"};

    public int executePlan(MinecraftServer server, ServerPlayerEntity who, String planJson) {
        JsonArray steps = JsonParser.parseString(planJson).getAsJsonArray();
        ServerCommandSource src = who.getCommandSource().withLevel(2);
        int ok = 0;
        for (JsonElement el : steps) {
            JsonObject step = el.getAsJsonObject();
            String cmd = step.get("cmd").getAsString();
            String args = step.get("args").getAsString();
            if (allowed(cmd)) {
                String full = "/" + cmd + " " + args;
                server.getCommandManager().executeWithPrefix(src, full);
                ok++;
            }
        }
        return ok;
    }

    private boolean allowed(String c) {
        for (String s : WHITELIST) if (s.equals(c)) return true;
        return false;
    }
}
