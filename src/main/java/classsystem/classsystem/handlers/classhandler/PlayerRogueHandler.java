package classsystem.classsystem.handlers.classhandler;

import classsystem.classsystem.ClassSystem;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

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
        int dmgModifier = plugin.getConfig().getInt(pUUID + ".dmgMultiplier");
        int rangeModifier = plugin.getConfig().getInt(pUUID + ".rangeMultiplier");
        int kbModifier = plugin.getConfig().getInt(pUUID + ".kbMultiplier");
        //endregion
        //region Check Class
        String playerClass = plugin.getConfig().getString(pUUID + ".class");
        if (playerClass == null || playerClass.equals("none")) return;
        //endregion
        //region CLASS ABILITIES
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR) {
            if (p.getInventory().getItemInMainHand().getType().equals(Material.DIAMOND_SWORD)) {
                //region Shadow Sneak
                //region Variables
                Location targetLocation;
                Vector targetLook = p.getEyeLocation().getDirection();
                int distance = 8 * rangeModifier;
                double range = 2 * rangeModifier;
                //endregion
                //region Teleport
                RayTraceResult traceResult = p.rayTraceBlocks(Math.sqrt(distance));
                if (!(traceResult == null)) {
                    Block hitBlock = traceResult.getHitBlock();
                    if (hitBlock.getType().isSolid()) {
                        //Get the block's face's location
                        BlockFace hitFace = traceResult.getHitBlockFace();
                        Location hitLocation = traceResult.getHitBlock().getLocation();
                        targetLocation = hitLocation.add(hitFace.getModX() * 1.2, hitFace.getModY() * 1.2, hitFace.getModZ() * 1.2);
                        targetLocation.setYaw(pLocation.getYaw());
                        targetLocation.setPitch(pLocation.getPitch());
                    } else {
                        targetLocation = calculateTargetLocation(targetLook, distance, pLocation);
                    }
                } else {
                    targetLocation = calculateTargetLocation(targetLook, distance, pLocation);
                }
                //region Particles
                p.getWorld().spawnParticle(Particle.SMOKE_LARGE, pLocation, 10);
                p.getWorld().spawnParticle(Particle.SMOKE_LARGE, targetLocation, 10);
                //endregion
                Collection<Entity> entitiesInRange = p.getWorld().getNearbyEntities(targetLocation, range, range, range);
                if (!(entitiesInRange.isEmpty())) {
                    double maxDistance = 0;
                    Entity topScore = null;
                    for (Entity entity : entitiesInRange) {
                        if (!(entity instanceof Monster)) continue;
                        double eDistance = entity.getLocation().distanceSquared(targetLocation);
                        if (eDistance > maxDistance) {
                            maxDistance = eDistance;
                            topScore = entity;
                        }
                    }
                    if (topScore == null) {
                        for (Entity entity : entitiesInRange) {
                            double eDistance = entity.getLocation().distanceSquared(targetLocation);
                            if (eDistance > maxDistance) {
                                maxDistance = eDistance;
                                topScore = entity;
                            }
                        }
                    }
                    Vector playerVector = ((targetLocation.toVector()).add(new Vector(0, 1.8, 0)));
                    Vector entityVector = topScore.getLocation().toVector();
                    entityVector.subtract(playerVector);
                    entityVector.normalize();
                    targetLocation.setDirection(entityVector);
                }
                p.teleport(targetLocation);
                //endregion
                //region Sound
                p.playSound(p, Sound.ITEM_CHORUS_FRUIT_TELEPORT, 1.0f, 0.5f);
                //endregion
                //endregion

            }
        }
        //endregion
        //region Scout
        /**
         if (playerClass.equals("scout")) {
         if (e.getAction() == Action.LEFT_CLICK_BLOCK || e.getAction() == Action.LEFT_CLICK_AIR) {
         if (p.getInventory().getItemInMainHand().getType().equals(Material.BOW)) {
         //region Volley
         p.sendMessage("Volley! Scout!");
         //endregion
         }
         }
         }
         //endregion
         //region Cleric
         if (playerClass.equals("cleric")) {
         if (e.getAction() == Action.LEFT_CLICK_BLOCK || e.getAction() == Action.LEFT_CLICK_AIR) {
         if (p.getInventory().getItemInMainHand().getType().equals(Material.TOTEM_OF_UNDYING)) {
         //region Buff
         p.sendMessage("Buff! Cleric!");
         //endregion
         }
         }
         if (e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR) {
         if (p.getInventory().getItemInMainHand().getType().equals(Material.TOTEM_OF_UNDYING)) {
         //region Heal
         p.sendMessage("Heal! Cleric!");
         //endregion
         }
         }
         }
         //endregion
         //region Warrior
         if (playerClass.equals("warrior")) {
         if (e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR) {
         if (p.getInventory().getItemInMainHand().getType().equals(Material.DIAMOND_AXE)) {
         //region Parry
         p.sendMessage("Parry! Warrior!");
         //endregion
         //region Necomancer
         if (playerClass.equals("necromancer")) {
         if (e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR) {
         if (p.getInventory().getItemInMainHand().getType().equals(Material.DIAMOND_AXE)) {
         //region Parry
         p.sendMessage("Parry! Warrior!");
         //endregion
         }
         }
         }
         **/
        //endregion
        //endregion
    }

    public Location calculateTargetLocation(Vector targetLook, int distance, Location pLocation) {
        targetLook.normalize();
        targetLook.multiply(distance);
        Location targetLocation = pLocation.add(targetLook);
        // Cloning the target location so it can be the same, then subtracting 1 from the y value, then getting that block
        Block underBlock = targetLocation.clone().subtract(0, 1, 0).getBlock();
        if (!(underBlock.getType().isAir())) {
            targetLocation.add(0, 1, 0);
        }
        return targetLocation;
    }


}
