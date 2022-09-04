package classsystem.classsystem.handlers.classhandler;

import classsystem.classsystem.ClassSystem;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.function.Predicate;

public class PlayerClericHandler extends PlayerClassTemplate {
    @Override
    public void onTrigger(PlayerInteractEvent e) {
        ClassSystem plugin = ClassSystem.getInstance();
        if (e.getHand() != EquipmentSlot.HAND) return;
        Player p = e.getPlayer();
        String pUUID = p.getUniqueId().toString();
        Location pLocation = p.getLocation();
        int dmgModifier = plugin.getConfig().getInt(pUUID + ".dmgMultiplier");
        int rangeModifier = plugin.getConfig().getInt(pUUID + ".rangeMultiplier");
        int kbModifier = plugin.getConfig().getInt(pUUID + ".kbMultiplier");

        //region CLASS ABILITIES
        if (e.getAction() == Action.LEFT_CLICK_AIR) {
            if (p.getInventory().getItemInMainHand().getType().equals(Material.DIAMOND_HOE)) {
                if (p.isSneaking()) {
                    //region Heal
                    int range = 8;
                    int healing = 4;
                    RayTraceResult traceResult = p.getWorld().rayTraceEntities(p.getEyeLocation(), p.getEyeLocation().getDirection(), range, (entity -> {
                        return entity != p;
                    }));
                    if (traceResult != null) {
                        if (!(traceResult.getHitEntity() instanceof Player)) return;
                        Player target = (Player) traceResult.getHitEntity();
                        if (target.getHealth() < target.getMaxHealth() && p.getHealth() > healing) {
                            p.setHealth(p.getHealth() - healing);
                        }
                        target.setHealth(Math.min((target.getHealth() + healing), target.getMaxHealth()));
                    }
                    //endregion
                } else {
                    //region Super Jump
                    Vector jumpSpeed = new Vector(3, 2.5, 3).multiply(kbModifier);
                    Vector pLooking = pLocation.getDirection();
                    Vector jumpVelocity = pLooking.multiply(jumpSpeed);
                    p.setVelocity(jumpVelocity);
                    //endregion
                }
            }
        }
        if (e.getAction() == Action.RIGHT_CLICK_AIR) {
            if (p.getInventory().getItemInMainHand().getType().equals(Material.DIAMOND_HOE)) {
                double range = 4 * rangeModifier;
                Collection<Entity> entitiesInRange = p.getNearbyEntities(range, range, range);
                if (!p.isSneaking()) {
                    for (Entity entity : entitiesInRange) {
                        if (!(entity instanceof Player)) continue;
                        ((Player) entity).addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 60, dmgModifier));

                    }
                    p.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 60, dmgModifier));
                } else {
                    for (Entity entity : entitiesInRange) {
                        if (!(entity instanceof Monster)) continue;
                        Monster monster = (Monster) entity;
                        monster.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 300, 3 * dmgModifier));
                        monster.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 300, -3 * dmgModifier));
                    }
                }
            }
        }
        //endregion
    }
}