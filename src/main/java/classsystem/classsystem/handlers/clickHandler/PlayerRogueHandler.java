package classsystem.classsystem.handlers.clickHandler;

import classsystem.classsystem.ClassSystem;
import classsystem.classsystem.CooldownManager;
import classsystem.classsystem.ItemManager;
import classsystem.classsystem.PartyManager;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import sun.jvm.hotspot.debugger.win32.coff.DebugVC50SymbolEnums;

import java.util.Collection;

public class PlayerRogueHandler extends PlayerClassTemplate {
    ClassSystem plugin = ClassSystem.getInstance();
    CooldownManager cooldownManager = plugin.getCdInstance();

    PartyManager partyManager = plugin.getPartyInstance();

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
        //region CLASS ABILITIES
        if (ItemManager.rogueWeapons.contains(p.getInventory().getItemInMainHand().getType())) {
            if (e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR) {
                if (!p.isSneaking()) {
                    if (!(cooldownManager.isCooldownDone(p.getUniqueId(), "Teleport"))) return;
                    teleport(e, p, pLocation, rangeModifier, cdModifier);
                } else {
                    if (!(cooldownManager.isCooldownDone(p.getUniqueId(), "Shadow Sneak"))) return;
                    if (!(shadowSneak(p, pLocation, rangeModifier, cdModifier, cooldownManager))) return;
                }
            }
            if (e.getAction() == Action.LEFT_CLICK_BLOCK || e.getAction() == Action.LEFT_CLICK_AIR) {
                if (!p.isSneaking()) return;
                if (!(cooldownManager.isCooldownDone(p.getUniqueId(), "Smoke Bomb"))) return;
                smokeBomb(p, pLocation, rangeModifier, cdModifier, cooldownManager);
            }
        }
        //endregion
    }
    public void onTriggerSwap(PlayerSwapHandItemsEvent e) {

    }

    private void teleport(PlayerInteractEvent e, Player p, Location pLocation, double rangeModifier, double cdModifier) {
        //region Teleport
        long cooldown = (long) (5000 / cdModifier);
        cooldownManager.setCooldownFromNow(p.getUniqueId(), "Teleport", cooldown);
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
    }

    private boolean shadowSneak(Player p, Location pLocation, double rangeModifier, double cdModifier, CooldownManager cooldownManager) {
        //region Shadow Sneak
        long cooldown = (long) (3000 / cdModifier);
        double range = 32 * rangeModifier;
        RayTraceResult traceResult = p.getWorld().rayTraceEntities(p.getEyeLocation(), p.getEyeLocation().getDirection(), range, (entity -> {
            return entity != p;
        }));
        if (traceResult == null) return true;
        if (!(traceResult.getHitEntity() instanceof LivingEntity)) return true;
        LivingEntity entity = (LivingEntity) traceResult.getHitEntity();
        if (partyManager.findParty(p.getUniqueId()) != null) {
            if (partyManager.findParty(entity.getUniqueId()) == partyManager.findParty(p.getUniqueId())) return false;
        }
        cooldownManager.setCooldownFromNow(p.getUniqueId(), "Shadow Sneak", cooldown);
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
        return false;
    }
    //endregion
    private void smokeBomb(Player p, Location pLocation, double rangeModifier, double cdModifier, CooldownManager cooldownManager) {
        //region Smoke Bomb
        long cooldown = (long) (10000 / cdModifier);
        cooldownManager.setCooldownFromNow(p.getUniqueId(), "Smoke Bomb", cooldown);
        double range = 8* rangeModifier;
        p.getWorld().spawnParticle(Particle.SMOKE_LARGE, pLocation, 1000);
        p.playSound(pLocation, Sound.ENTITY_BLAZE_SHOOT, 1, 1);
        Collection<Entity> nearbyEntities = p.getNearbyEntities(range,range,range);
        for (Entity nearbyEntity : nearbyEntities) {
            if (!(nearbyEntity instanceof LivingEntity)) continue;
            if (partyManager.findParty(p.getUniqueId()) != null) {
                if (partyManager.findParty(nearbyEntity.getUniqueId()) == partyManager.findParty(p.getUniqueId())) continue;
            }
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

    public Location calcTarget(Location pLoc, Vector dir, int range) {
        dir.normalize().multiply((range - 1));
        Location target = pLoc.add(dir);
        while (target.getBlock().getType().isSolid()) target.add(0, 1, 0);
        return target;
    }


}
