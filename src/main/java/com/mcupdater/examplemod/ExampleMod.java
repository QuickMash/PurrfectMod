package com.example.catmod;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.registries.ObjectHolder;

import static com.mojang.brigadier.arguments.BoolArgumentType.bool;
import static com.mojang.brigadier.arguments.BoolArgumentType.getBool;
import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;
import static net.minecraftforge.fml.server.ServerLifecycleHooks.getCurrentServer;

@Mod(CatMod.MOD_ID)
@Mod.EventBusSubscriber(modid = CatMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CatMod {
    public static final String MOD_ID = "catmod";
    private static boolean isPlayerCat = true; // Set to true by default

    @ObjectHolder(MOD_ID)
    public static final Item CAT_ITEM = null;

    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ForgeEvents {
        @SubscribeEvent
        public static void onPlayerTick(PlayerTickEvent event) {
            PlayerEntity player = event.player;
            if (player instanceof AbstractClientPlayerEntity) {
                AbstractClientPlayerEntity clientPlayer = (AbstractClientPlayerEntity) player;
                if (isPlayerCat) {
                    // Change player model to cat model
                    PlayerModel<AbstractClientPlayerEntity> model = new CatPlayerModel<>(0);
                    clientPlayer.setModel(model);
                } else {
                    // Change player model back to default
                    PlayerModel<AbstractClientPlayerEntity> model = new PlayerModel<>(0, false);
                    clientPlayer.setModel(model);
                }
            }
        }

        @SubscribeEvent
        public static void onKeyInput(InputEvent.KeyInputEvent event) {
            if (event.getAction() == GLFW.GLFW_PRESS && event.getKey() == GLFW.GLFW_KEY_X) {
                PlayerEntity player = Minecraft.getInstance().player;
                if (player != null) {
                    BlockPos pos = player.getPosition();
                    player.trySleep(pos);
                }
            }
        }

        @SubscribeEvent
        public static void onPlayerJoinWorld(PlayerEvent.PlayerLoggedInEvent event) {
            if (event.getPlayer() instanceof AbstractClientPlayerEntity) {
                isPlayerCat = true;
            }
        }
    }

    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ServerEvents {
        @SubscribeEvent
        public static void onServerStarting(FMLServerStartingEvent event) {
            CommandDispatcher<CommandSource> dispatcher = event.getCommandDispatcher();
            dispatcher.register(literal("cat").executes(CatMod::catCommand).requires((source) -> source.hasPermissionLevel(3)));
        }
    }

    private static int catCommand(CommandContext<CommandSource> context) throws CommandSyntaxException {
        isPlayerCat = getBool(context, "toggle");
        return Command.SINGLE_SUCCESS;
    }

    public static class Command {
        public static final int SINGLE_SUCCESS = 1;

        public static int execute(CommandContext<CommandSource> context) throws CommandSyntaxException {
            PlayerEntity player = context.getSource().asPlayer();
            isPlayerCat = !isPlayerCat;
            if (isPlayerCat) {
                // Change player model to cat model
                PlayerModel<AbstractClientPlayerEntity> model = new CatPlayerModel<>(0);
                ((AbstractClientPlayerEntity) player).setModel(model);
            } else {
                // Change player model back to default
                PlayerModel<AbstractClientPlayerEntity> model = new PlayerModel<>(0, false);
                ((AbstractClientPlayerEntity) player).setModel(model);
            }
            return SINGLE_SUCCESS;
        }
    }

    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ModEvents {
        @SubscribeEvent
        public static void onRegisterCommands(RegisterCommandsEvent event) {
            CommandDispatcher<CommandSource> dispatcher = event.getDispatcher();
            dispatcher.register(literal("cat")
                    .executes(context -> Command.execute(context))
                    .requires(source -> source.hasPermissionLevel(3))
                    .then(argument("toggle", bool())
                            .executes(context -> catCommand(context, "toggle")))
            );
        }
    }

    private static int catCommand(CommandContext<CommandSource> context, String toggle) throws CommandSyntaxException {
        boolean isCat = getBool(context, toggle);
        isPlayerCat = isCat;
        PlayerEntity player = context.getSource().asPlayer();
        if (isPlayerCat) {
            // Change player model to cat model
            PlayerModel<AbstractClientPlayerEntity> model = new CatPlayerModel<>(0);
            ((AbstractClientPlayerEntity) player).setModel(model);
        } else {
            // Change player model back to default
            PlayerModel<AbstractClientPlayerEntity> model = new PlayerModel<>(0, false);
            ((AbstractClientPlayerEntity) player).setModel(model);
        }
        return Command.SINGLE_SUCCESS;
    }
}
