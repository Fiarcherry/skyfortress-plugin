package ru.jekarus.skyfortress.v3.listener;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.manipulator.mutable.PotionEffectData;
import org.spongepowered.api.effect.potion.PotionEffect;
import org.spongepowered.api.effect.potion.PotionEffectTypes;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.living.humanoid.player.RespawnPlayerEvent;
import org.spongepowered.api.event.filter.Getter;
import ru.jekarus.skyfortress.v3.SkyFortressPlugin;
import ru.jekarus.skyfortress.v3.game.SfGameStageType;
import ru.jekarus.skyfortress.v3.lobby.SfLobbySettings;
import ru.jekarus.skyfortress.v3.player.PlayerZone;
import ru.jekarus.skyfortress.v3.player.SfPlayer;
import ru.jekarus.skyfortress.v3.player.SfPlayers;
import ru.jekarus.skyfortress.v3.team.SfGameTeam;
import ru.jekarus.skyfortress.v3.team.SfTeam;
import ru.jekarus.skyfortress.v3.utils.SfLocation;

public class PlayerRespawnListener {

    private final SkyFortressPlugin plugin;
    private SfPlayers players;

    public PlayerRespawnListener(SkyFortressPlugin plugin)
    {
        this.plugin = plugin;
        this.players = SfPlayers.getInstance();
    }

    public void register()
    {
        Sponge.getEventManager().registerListeners(this.plugin, this);
    }

    public void unregister()
    {
        Sponge.getEventManager().unregisterListeners(this);
    }

    @Listener
    public void onRespawn(RespawnPlayerEvent event, @Getter("getTargetEntity") Player player) {
        SfPlayer sfPlayer = this.players.getOrCreatePlayer(player);
        SfTeam playerTeam = sfPlayer.getTeam();
        SfGameStageType gameStage = plugin.getGame().getStage();
        if (playerTeam.getType() == SfTeam.Type.GAME && gameStage == SfGameStageType.IN_GAME) {
            SfGameTeam gameTeam = (SfGameTeam) playerTeam;
            SfLocation respawn = gameTeam.getCastle().getPositions().getRespawn();
            event.setToTransform(new Transform<>(
                    respawn.getLocation().getExtent(),
                    respawn.getLocation().getPosition(),
                    respawn.getRotation()
            ));
            player.getOrCreate(PotionEffectData.class).ifPresent(effects -> {
                effects.addElement(PotionEffect.builder().potionType(PotionEffectTypes.STRENGTH).duration(60).amplifier(0).particles(false).build());
                effects.addElement(PotionEffect.builder().potionType(PotionEffectTypes.RESISTANCE).duration(60).amplifier(0).particles(false).build());
            });
        }

    }

}
