package classsystem.classsystem.handlers;

import classsystem.classsystem.ClassSystem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DamageHandler implements Listener, ClassSystem.Variables {
    private final ClassSystem plugin;
    //region Register Events
    public DamageHandler(ClassSystem plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
        this.plugin = plugin;
    }
    //endregion
    //region Damage Handler
    //region Entity Damage
    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent e) {
        if (e.getDamager().getPersistentDataContainer().getKeys().contains("fireJump")) {
            e.setCancelled(true);
        }

        if (e.getDamager() instanceof Arrow) {
            Arrow a = (Arrow) e.getDamager();
            if (!(a.getShooter() instanceof Player)) return;
            Player shooter = (Player) a.getShooter();
            String shooterID = shooter.getUniqueId().toString();
            double dmg = 1.5*plugin.getConfig().getDouble(shooterID + ".dmgMultiplier");
            if (!(e.getEntity() instanceof LivingEntity)) return;
            LivingEntity entity = (LivingEntity) e.getEntity();
            if (e.getEntity().getScoreboardTags().contains("Marked")) {
                dmg *= 1.5;
                entity.removeScoreboardTag("Marked");
                entity.removePotionEffect(PotionEffectType.GLOWING);
            }
            if (!(plugin.getConfig().get(shooterID + ".class").equals("scout"))) return;
            // the D after the 4 is telling java the number is a double not an int
            double damage = e.getDamage()*dmg;
            entity.damage(damage, a);
            a.remove();
            e.setCancelled(true);
        }
    }
    //endregion
    //region Player Damage
    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        Player p = (Player) event.getEntity();
        String pUUID = p.getUniqueId().toString();
        String pName = p.getName();
        if (plugin.getConfig().get(pUUID+".class").equals("cleric")) {
            event.setDamage(event.getDamage()*2);
        }
        if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
            event.setCancelled(true);
        }
        if (!(godMode.contains(pName))) {
            return;
        } else {
            event.setCancelled(true);
        }
    }
    //endregion
    //endregion
}
