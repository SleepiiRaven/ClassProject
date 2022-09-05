package classsystem.classsystem.handlers.clickHandler;

import classsystem.classsystem.ClassSystem;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import sun.jvm.hotspot.debugger.win32.coff.DebugVC50SymbolEnums;

import java.util.Collection;

public class PlayerRogueHandler extends PlayerClassTemplate {

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
        //endregion
        //region CLASS ABILITIES
        if (p.getInventory().getItemInMainHand().getType().equals(Material.DIAMOND_SWORD)) {
            if (e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR) {
                if (!p.isSneaking()) {
                    //region Teleport
                    Location target = null;
                    Player player = e.getPlayer();
                    Vector lookDir = player.getEyeLocation().getDirection();

                    int rayDistance = (int) (8 * rangeModifier);
                    RayTraceResult rayResult = player.rayTraceBlocks(rayDistance);
                    if (rayResult != null && rayResult.getHitBlock() != null) {

                        BlockFace face = rayResult.getHitBlockFace();
                        target = rayResult.getHitBlock().getLocation().add(face.getModX() * 1.2, face.getModY() * 1.2, face.getModZ() * 1.2);
                    } else {
                        target = calcTarget(player.getLocation(), lookDir, rayDistance);
                    }


                    target.setYaw(player.getLocation().getYaw());
                    target.setPitch(player.getLocation().getPitch());
                    player.teleport(target);

                    //region Particles
                    p.getWorld().spawnParticle(Particle.SMOKE_LARGE, pLocation, 10);
                    p.getWorld().spawnParticle(Particle.SMOKE_LARGE, target, 10);
                    //endregion
                    //endregion
                    //region Sound
                    p.playSound(p, Sound.ITEM_CHORUS_FRUIT_TELEPORT, 1.0f, 0.5f);
                    //endregion
                    //endregion
                } else {
                    //region Shadow Sneak
                    double range = 32 * rangeModifier;
                    RayTraceResult traceResult = p.getWorld().rayTraceEntities(p.getEyeLocation(), p.getEyeLocation().getDirection(), range, (entity -> {
                        return entity != p;
                    }));
                    if (traceResult == null) return;
                    if (!(traceResult.getHitEntity() instanceof LivingEntity)) return;
                    LivingEntity entity =(LivingEntity) traceResult.getHitEntity();
                    Location targetLocation = entity.getEyeLocation().add(pLocation.getDirection());
                    if (targetLocation.getBlock().getType().isSolid()) {
                        targetLocation = entity.getEyeLocation();
                    }
                    Vector playerVector = ((targetLocation.toVector()).add(new Vector(0, 1.8, 0)));
                    Vector entityVector = entity.getEyeLocation().toVector();
                    entityVector.subtract(playerVector);
                    entityVector.normalize();
                    targetLocation.setDirection(entityVector);
                    p.teleport(targetLocation);
                    p.playSound(pLocation, Sound.ENTITY_FOX_TELEPORT, 1, 1);
                    //endregion
                }
            }
            if (e.getAction() == Action.LEFT_CLICK_BLOCK || e.getAction() == Action.LEFT_CLICK_AIR) {
                if (!p.isSneaking()) return;
                //region Smoke Bomb
                double range = 8*rangeModifier;
                p.getWorld().spawnParticle(Particle.SMOKE_LARGE, pLocation, 1000);
                p.playSound(pLocation, Sound.ENTITY_BLAZE_SHOOT, 1, 1);
                Collection<Entity> nearbyEntities = p.getNearbyEntities(range,range,range);
                for (Entity nearbyEntity : nearbyEntities) {
                    if (!(nearbyEntity instanceof LivingEntity)) continue;
                    LivingEntity entity = (LivingEntity) nearbyEntity;
                    entity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 40, 2));
                    entity.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 100, 5));
                    entity.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 100, 5));
                    entity.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 20, -20));
                    if (!(entity instanceof Player)) continue;
                    Player player = (Player) entity;
                    player.playSound(pLocation, Sound.ENTITY_BLAZE_SHOOT, 1, 1);
                }
                //endregion
            }
        }
        //endregion
    }

    public Location calcTarget(Location pLoc, Vector dir, int range) {
        dir.normalize().multiply((range - 1));
        Location target = pLoc.add(dir);
        while (target.getBlock().getType().isSolid()) target.add(0, 1, 0);
        return target;
    }


}
