package classsystem.classsystem.handlers.clickHandler;

import classsystem.classsystem.ClassSystem;
import classsystem.classsystem.CooldownManager;
import classsystem.classsystem.ItemManager;
import classsystem.classsystem.PartyManager;
import jdk.javadoc.internal.doclint.HtmlTag;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class PlayerWarriorHandler extends PlayerClassTemplate {
    ClassSystem plugin = ClassSystem.getInstance();
    PartyManager partyManager = plugin.getPartyInstance();
    CooldownManager cooldownManager = plugin.getCdInstance();
    @Override
    public void onTrigger(PlayerInteractEvent e) {
        ClassSystem plugin = ClassSystem.getInstance();
        if (e.getHand() != EquipmentSlot.HAND) return;
        //region Set Variables
        Player p = e.getPlayer();
        String pUUID = p.getUniqueId().toString();
        Location pLocation = p.getLocation();
        double dmgModifier = plugin.getConfig().getInt(pUUID + ".dmgMultiplier");
        double rangeModifier = plugin.getConfig().getInt(pUUID + ".rangeMultiplier");
        double kbModifier = plugin.getConfig().getInt(pUUID + ".kbMultiplier");
        double cdModifier = plugin.getConfig().getInt(pUUID + ".cdMultiplier");
        //endregion
        if (ItemManager.warriorWeapons.contains(p.getInventory().getItemInMainHand().getType())) {
            if (e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK) {
                if (p.isSneaking()) {

                } else {

                }
            } if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                if (p.isSneaking()) {
                    if (!(cooldownManager.isCooldownDone(p.getUniqueId(), "Stomp"))) return;
                    stomp(kbModifier, rangeModifier, p, dmgModifier, cdModifier);
                } else {

                }
            }
        }
    }
    public void onTriggerSwap(PlayerSwapHandItemsEvent e) {

    }

    public void stomp(double kbModifier, double rangeModifier, Player p, double dmgModifier, double cdModifier) {
        double speed = 1 * kbModifier;
        double kb = ThreadLocalRandom.current().nextDouble(0.3, 0.5) * kbModifier;
        double range = 25 * rangeModifier;
        double damage = 10 * dmgModifier;
        long cooldown = (long) (5000 / cdModifier);
        cooldownManager.setCooldownFromNow(p.getUniqueId(), "Stomp", cooldown);
        if (p.isOnGround()) {
            p.setVelocity(new Vector(0D, speed, 0D));
            BukkitScheduler scheduler = Bukkit.getScheduler();
            scheduler.runTaskLater(plugin, () -> {
                p.setVelocity(new Vector(0D, -speed, 0D));
            }, 10L);
        } else {
            p.setVelocity(new Vector(0D, -speed, 0D));
        }
    }
}
