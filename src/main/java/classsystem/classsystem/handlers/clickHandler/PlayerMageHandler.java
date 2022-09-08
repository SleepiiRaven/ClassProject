package classsystem.classsystem.handlers.clickHandler;

import classsystem.classsystem.*;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;


public class PlayerMageHandler extends PlayerClassTemplate {
    ClassSystem plugin = ClassSystem.getInstance();
    CooldownManager cooldownManager = plugin.getCdInstance();
    PartyManager partyManager = plugin.getPartyInstance();
    @Override
    public void onTrigger(PlayerInteractEvent e) {
        //region Set Variables
        ClassSystem plugin = ClassSystem.getInstance();
        if (e.getHand() != EquipmentSlot.HAND) return;
        Player p = e.getPlayer();
        String pUUID = p.getUniqueId().toString();
        Location pLocation = p.getLocation();
        double dmgModifier = plugin.getConfig().getInt(pUUID + ".dmgMultiplier");
        double rangeModifier = plugin.getConfig().getInt(pUUID + ".rangeMultiplier");
        double kbModifier = plugin.getConfig().getInt(pUUID + ".kbMultiplier");
        double cdModifier = plugin.getConfig().getInt(pUUID + ".cdMultiplier");
        //endregion
        //region CLASS ABILITIES
        if (ItemManager.mageWeapons.contains(p.getInventory().getItemInMainHand().getType())) {
            if (e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK) {
                if (p.isSneaking()) {
                    if (!(cooldownManager.isCooldownDone(p.getUniqueId(), "Devour"))) return;
                    devour(p, rangeModifier, cooldownManager, cdModifier);
                } else {
                    if (!(cooldownManager.isCooldownDone(p.getUniqueId(), "Mana Burst"))) return;
                    manaBurst(p, dmgModifier, rangeModifier, kbModifier, cooldownManager, cdModifier);
                }
            }
            if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                if (p.isSneaking()) {
                    if (!(cooldownManager.isCooldownDone(p.getUniqueId(), "Ethereal Soul"))) return;
                    etherealSoul(p, pLocation, dmgModifier, rangeModifier, kbModifier, cdModifier);
                } else {
                    if (!(cooldownManager.isCooldownDone(p.getUniqueId(), "Fire Jump"))) return;
                    fireJump(plugin, p, pLocation, cooldownManager, cdModifier);
                }
            }
        }
        //endregion
    }

    public void onTriggerSwap(PlayerSwapHandItemsEvent e) {

    }


    private void fireJump(ClassSystem plugin, Player p, Location pLocation, CooldownManager cooldownManager, double cdModifier) {
        //region Fire Jump
        long cooldown = (long) (8000 / cdModifier);
        cooldownManager.setCooldownFromNow(p.getUniqueId(), "Fire Jump", cooldown);
        Entity fireball = p.getWorld().spawnEntity(pLocation, EntityType.FIREBALL);
        p.setVelocity(pLocation.getDirection().multiply(-2));
        PersistentDataContainer fireballContainer = fireball.getPersistentDataContainer();
        fireballContainer.set(new NamespacedKey(plugin, "fireJump"), PersistentDataType.STRING, "fireJump");
        //endregion
    }
    private void manaBurst(Player p, double dmgModifier, double rangeModifier, double kbModifier, CooldownManager cooldownManager, double cdModifier) {
         //region Mana Burst
        long cooldown = (long) (1000 / cdModifier);
        cooldownManager.setCooldownFromNow(p.getUniqueId(), "Mana Burst", cooldown);
        //region Variables
        double range = 25 * rangeModifier;
        double distance = 20 * rangeModifier;
        double collision = 1 * rangeModifier;
        double damage = 3 * dmgModifier;
        double kb = ThreadLocalRandom.current().nextDouble(0.3, 0.5) * kbModifier;
        //endregion
        //region Shoot Loop
        Location viewPos = p.getEyeLocation();
        Vector viewDir = viewPos.getDirection();
        for (double t = 0; t < distance; t += 0.5) {
            //region Particles
            double x = viewDir.getX() * t;
            double y = viewDir.getY() * t;
            double z = viewDir.getZ() * t;
            viewPos.add(x, y, z);
            p.getWorld().spawnParticle(Particle.REDSTONE, viewPos, 1, 0, 0, 0, new Particle.DustOptions(Color.PURPLE, 2));
            //endregion
            //region Damage
            Collection<Entity> closebyMonsters = p.getWorld().getNearbyEntities(viewPos, range, range, range);
            for (Entity closebyMonster : closebyMonsters) {
                // .distance is resource intensive, so get it squared (SO SQUARE YOUR RANGE)
                // make sure it's a living entity, not an armor stand or something, continue skips the current loop
                if (!(closebyMonster instanceof LivingEntity) || (closebyMonster == p)) continue;
                if (partyManager.findParty(p.getUniqueId()) != null) {
                    if (partyManager.findParty(closebyMonster.getUniqueId()) == partyManager.findParty(p.getUniqueId())) continue;
                }
                LivingEntity livingMonster = (LivingEntity) closebyMonster;
                // Get the entitie's collision box and the viewpos' xyz
                BoundingBox monsterBoundingBox = livingMonster.getBoundingBox();
                BoundingBox collisionBox = BoundingBox.of(viewPos, collision, collision, collision);
                /**double viewPosX = viewPos.getX();
                 double viewPosY = viewPos.getY();
                 double viewPosZ = viewPos.getZ();**/
                // if our particle goes through the enemy's hitbox, we keep going through the loop, if we don't we use continue;
                if (!(monsterBoundingBox.overlaps(collisionBox))) continue;
                livingMonster.damage(damage, p);
                Vector viewNormalized = (viewDir.normalize()).multiply(kb);
                livingMonster.setVelocity(viewNormalized);
            }
            //endregion
            viewPos.subtract(x, y, z);
        }
        //endregion
        //region Sound
        p.playSound(p, Sound.ITEM_CROSSBOW_SHOOT, 1.0f, 0.5f);
        //endregion
        //endregion
    }
    private void etherealSoul(Player p, Location pLocation, double dmgModifier, double rangeModifier, double kbModifier, double cdModifier) {
        //region Ethereal Soul
        long cooldown = (long) (1000 / cdModifier);
        cooldownManager.setCooldownFromNow(p.getUniqueId(), "Ethereal Soul", cooldown);
        //region Damage
        double range = 25 * rangeModifier;
        double damage = 5 * dmgModifier;
        double kb = ThreadLocalRandom.current().nextDouble(0.3, 0.5) * kbModifier;
        List<Entity> closebyMonsters = p.getNearbyEntities(range, range, range);
        for (Entity closebyMonster : closebyMonsters) {
            if (partyManager.findParty(p.getUniqueId()) != null) {
                if (partyManager.findParty(closebyMonster.getUniqueId()) == partyManager.findParty(p.getUniqueId())) continue;
            }
            Location eLocation = closebyMonster.getLocation();
            // .distance is resource intensive, so get it squared (SO SQUARE YOUR RANGE)
            double distance = eLocation.distanceSquared(pLocation);
            if (distance < range) {
                // make sure it's a living entity, not an armor stand or something, continue skips the current loop
                if (!(closebyMonster instanceof LivingEntity)) continue;
                LivingEntity livingMonster = (LivingEntity) closebyMonster;
                livingMonster.damage(damage, p);
                livingMonster.setFireTicks(40);
                livingMonster.setVelocity(new Vector(0, kb, 0));
            }
        }
        //endregion
        //region Particles
        int particleSize = (int) Math.sqrt(range);
        summonCircle(pLocation, particleSize,Particle.FLAME);
        //endregion
        //region Play Sound
        p.playSound(p, Sound.ENTITY_ENDER_DRAGON_FLAP, 1.0f, 1.0f);
        //endregion
        //endregion
    }
    private void devour(Player p, double rangeModifier, CooldownManager cooldownManager, double cdModifier) {
        //region Devour
        long cooldown = (long) (100 / cdModifier);
        double range = 30 * rangeModifier;
        RayTraceResult traceResult = p.rayTraceBlocks(range);
        if (traceResult != null) {
            cooldownManager.setCooldownFromNow(p.getUniqueId(),"Devour", cooldown);
            Location blockLocation = traceResult.getHitBlock().getLocation();
            Entity evokerFangs = p.getWorld().spawnEntity(blockLocation.add(0, 1, 0), EntityType.EVOKER_FANGS);
            evokerFangs.addScoreboardTag(p.getUniqueId().toString());
        }
    }

    public void summonCircle(Location location, int size, Particle particle) {
        for (int d = 0; d <= 90; d += 1) {
            Location particleLoc = new Location(location.getWorld(), location.getX(), location.getY(), location.getZ());
            // Cosine for X
            particleLoc.setX(location.getX() + Math.cos(d) * size);
            // Sine for Z
            particleLoc.setZ(location.getZ() + Math.sin(d) * size);
            location.getWorld().spawnParticle(particle, particleLoc, 1);
        }
    }

}