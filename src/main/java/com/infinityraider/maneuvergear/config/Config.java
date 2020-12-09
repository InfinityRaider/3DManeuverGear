package com.infinityraider.maneuvergear.config;

import com.infinityraider.infinitylib.config.ConfigurationHandler;
import com.infinityraider.maneuvergear.reference.Constants;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;

public abstract class Config implements ConfigurationHandler.SidedModConfig {
    private Config() {}

    public abstract int getDurability();

    public abstract float getDamage();

    public abstract float getBootFallDmgReduction();

    public abstract int getCableLength();

    public abstract boolean disableMusicDisc();

    public abstract boolean disableFallBoots();

    @OnlyIn(Dist.CLIENT)
    public abstract boolean overridePlayerRenderer();

    @OnlyIn(Dist.CLIENT)
    public abstract double getRetractingSpeed();

    @OnlyIn(Dist.CLIENT)
    public abstract boolean useConfigKeyBinds();

    @OnlyIn(Dist.CLIENT)
    public abstract int retractLeftKey();

    @OnlyIn(Dist.CLIENT)
    public abstract int retractRightKey();

    public abstract boolean debug();

    public static class Common extends Config {
        //numbers
        public final ForgeConfigSpec.IntValue durability;
        public final ForgeConfigSpec.DoubleValue damage;
        public final ForgeConfigSpec.DoubleValue bootFallDamageReduction;
        public final ForgeConfigSpec.IntValue cableLength;

        //content
        public final ForgeConfigSpec.BooleanValue disableMusicDisc;
        public final ForgeConfigSpec.BooleanValue disableFallBoots;

        //debug
        public final ForgeConfigSpec.BooleanValue debug;


        public Common(ForgeConfigSpec.Builder builder) {
            builder.push("numbers");
            this.durability = builder
                    .comment("The number of attacks after which a sword blade breaks")
                    .defineInRange("Blade durability", 64, 10, 100);
            this.damage = builder
                    .comment("The damage dealt by sword blade attacks")
                    .defineInRange("Blade damage",10, 1.0, 30.0);
            this.bootFallDamageReduction = builder
                    .comment("The fraction of fall damage reduction factor when wearing fall boots")
                    .defineInRange("Fall boots damage reduction",0.85, 0.0, 1.0);
            this.cableLength = builder
                    .comment("The maximum length of a cable, this defines how far grapples will fly before being automatically retracted if they don't hit anything." +
                            "(must match on client and server)")
                    .defineInRange("Cable length", 64, 30, 100);
            builder.pop();

            builder.push("content");
            this.disableMusicDisc = builder
                    .comment("Set to true to disable the music disc")
                    .define("Disable music disc", false);
            this.disableFallBoots = builder
                    .comment("Set to true to disable the fall boots")
                    .define("Disable fall boots", false);
            builder.pop();

            builder.push("debug");
            this.debug = builder
                    .comment("Set to true if you wish to enable debug mode")
                    .define("debug", false);
            builder.pop();
        }

        @Override
        public ModConfig.Type getSide() {
            return ModConfig.Type.COMMON;
        }

        @Override
        public int getDurability() {
            return this.durability.get();
        }

        @Override
        public float getDamage() {
            return this.damage.get().floatValue();
        }

        @Override
        public float getBootFallDmgReduction() {
            return this.bootFallDamageReduction.get().floatValue();
        }

        @Override
        public int getCableLength() {
            return this.cableLength.get();
        }

        @Override
        public boolean disableMusicDisc() {
            return this.disableMusicDisc.get();
        }

        @Override
        public boolean disableFallBoots() {
            return this.disableFallBoots.get();
        }

        @Override
        @OnlyIn(Dist.CLIENT)
        public boolean overridePlayerRenderer() {
            return false;
        }

        @Override
        @OnlyIn(Dist.CLIENT)
        public double getRetractingSpeed() {
            return 0;
        }

        @Override
        @OnlyIn(Dist.CLIENT)
        public boolean useConfigKeyBinds() {
            return false;
        }

        @Override
        @OnlyIn(Dist.CLIENT)
        public int retractLeftKey() {
            return 0;
        }

        @Override
        @OnlyIn(Dist.CLIENT)
        public int retractRightKey() {
            return 0;
        }

        @Override
        public boolean debug() {
            return this.debug.get();
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class Client extends Common {
        public final ForgeConfigSpec.BooleanValue overridePlayerRenderer;
        public final ForgeConfigSpec.DoubleValue retractingSpeed;
        public final ForgeConfigSpec.BooleanValue useConfigKeyBinds;
        public final ForgeConfigSpec.IntValue retractLeftKey;
        public final ForgeConfigSpec.IntValue retractRightKey;

        public Client(ForgeConfigSpec.Builder builder) {
            super(builder);

            builder.push("client");
            this.overridePlayerRenderer = builder
                    .comment("Set to false if you experience issues with player rendering, " +
                            "disabling this will have the effect of not animating the left arm of players using the maneuver gear sword." +
                            "(This is a client side only config and does not have to match the server)")
                    .define("Left arm swing animation", true);
            this.retractingSpeed = builder
                    .comment("The speed at which a grapple is reeled in while holding the hot key." +
                            "(note that this is a client side config and does not need to match the server, can be tweaked to personal preference)")
                    .defineInRange("Retracting velocity",12.0, 5.0, 50.0);
            this.useConfigKeyBinds = builder
                    .comment("Set this to false if you want to use the vanilla minecraft key bind system. This is set to true by default to avoid keybind clutter" +
                            "If this is true, the in-game key binds will no longer work, so you can unbind them to make space for others,"+
                            " if you make them overlap, make sure they are overlapping with something that doesn't result in weird behaviour." +
                            "(This is a client side only config and does not have to match the server)")
                    .define("Use config keybinds", true);
            this.retractLeftKey = builder
                    .comment("This is the hotkey to toggle the retracting of the left cable." +
                            "This will only work when 'Use config keybinds' is set to true." +
                            "You can find a list of all keys and their numbering here: https://www.glfw.org/docs/3.3/group__keys.html" +
                            "(This is a client side only config and does not have to match the server)")
                    .defineInRange("Retract left cable hotkey", Constants.KEY_Z, 0, Constants.KEYBOARD_SIZE);
            this.retractRightKey = builder
                    .comment("This is the hotkey to toggle the retracting of the right cable." +
                            "This will only work when 'Use config keybinds' is set to true." +
                            "You can find a list of all keys and their numbering here: https://www.glfw.org/docs/3.3/group__keys.html" +
                            "(This is a client side only config and does not have to match the server)")
                    .defineInRange("Retract right cable hotkey", Constants.KEY_X, 0, Constants.KEYBOARD_SIZE);
            builder.pop();
        }

        @Override
        public ModConfig.Type getSide() {
            return ModConfig.Type.CLIENT;
        }

        @Override
        @OnlyIn(Dist.CLIENT)
        public boolean overridePlayerRenderer() {
            return this.overridePlayerRenderer.get();
        }

        @Override
        @OnlyIn(Dist.CLIENT)
        public double getRetractingSpeed() {
            return this.retractingSpeed.get();
        }

        @Override
        @OnlyIn(Dist.CLIENT)
        public boolean useConfigKeyBinds() {
            return this.useConfigKeyBinds.get();
        }

        @Override
        @OnlyIn(Dist.CLIENT)
        public int retractLeftKey() {
            return this.retractLeftKey.get();
        }

        @Override
        @OnlyIn(Dist.CLIENT)
        public int retractRightKey() {
            return this.retractRightKey.get();
        }
    }
}
