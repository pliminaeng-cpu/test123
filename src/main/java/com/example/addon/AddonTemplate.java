package com.example.addon;

//import com.example.addon.commands.Crash;
import com.example.addon.modules.*;
import com.mojang.logging.LogUtils;
import meteordevelopment.meteorclient.addons.GithubRepo;
import meteordevelopment.meteorclient.addons.MeteorAddon;
import meteordevelopment.meteorclient.commands.Commands;
import meteordevelopment.meteorclient.systems.hud.HudGroup;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Modules;
import org.slf4j.Logger;

public class AddonTemplate extends MeteorAddon {
    public static final Logger LOG = LogUtils.getLogger();
    public static final Category CATEGORY = new Category("Crashers");
    public static final HudGroup HUD_GROUP = new HudGroup("Crashers");

    @Override
    public void onInitialize() {
        LOG.info("Initializing meteor client crash addon Made By mari_gold_27153");

        // Modules
        Modules.get().add(new AACCRASHER());
        Modules.get().add(new COMPLETIONCRASH());
        Modules.get().add(new PAPERWINDOWEXPLOIT());
        Modules.get().add(new PARTICLECRASHER());
        Modules.get().add(new FIREBALLCRASHER());
//        Commands.add(new Crash());

    }

    @Override
    public void onRegisterCategories() {
        Modules.registerCategory(CATEGORY);
    }

    @Override
    public String getPackage() {
        return "com.example.addon";
    }

    @Override
    public GithubRepo getRepo() {
        return new GithubRepo("mari_gold_27153", "meteor-client-crash-addon");
    }
}
