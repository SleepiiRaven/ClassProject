package classsystem.classsystem.handlers.clickHandler;

import classsystem.classsystem.ClassSystem;
import classsystem.classsystem.CooldownManager;
import classsystem.classsystem.ItemManager;
import classsystem.classsystem.PartyManager;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.*;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class PlayerAvatarHandler extends PlayerClassTemplate {
    ClassSystem plugin = ClassSystem.getInstance();
    PartyManager partyManager = plugin.getPartyInstance();
    @Override
    public void onTrigger(PlayerInteractEvent e) {
        //region Set Variables
        if (e.getHand() != EquipmentSlot.HAND) return;
        Player p = e.getPlayer();
        String pUUID = p.getUniqueId().toString();
        Location pLocation = p.getLocation();
        double hpModifier = plugin.getConfig().getInt(pUUID + ".hpMultiplier");
        double dmgModifier = plugin.getConfig().getInt(pUUID + ".dmgMultiplier");
        double rangeModifier = plugin.getConfig().getInt(pUUID + ".rangeMultiplier");
        double kbModifier = plugin.getConfig().getInt(pUUID + ".kbMultiplier");
        double cdModifier = plugin.getConfig().getInt(pUUID + ".cdMultiplier");
        //endregion
        //region CLASS ABILITIES
        //region Mage
        if (ItemManager.mageWeapons.contains(p.getInventory().getItemInMainHand().getType())) {
            if (e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK) {
                if (p.isSneaking()) {
                    devour(p, rangeModifier, cdModifier);
                } else {
                    manaBurst(p, dmgModifier, rangeModifier, kbModifier, cdModifier);
                }
            }
            if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                if (p.isSneaking()) {
                    etherealSoul(p, pLocation, dmgModifier, rangeModifier, kbModifier, cdModifier);
                } else {
                    fireJump(plugin, p, pLocation, cdModifier);
                }
            }
        }
        //endregion
        //region Rogue
        if (ItemManager.rogueWeapons.contains(p.getInventory().getItemInMainHand().getType())) {
            if (e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR) {
                if (!p.isSneaking()) {
                    teleport(e, p, pLocation, rangeModifier, cdModifier);
                } else {
                    if (!(shadowSneak(p, pLocation, rangeModifier, cdModifier))) return;
                }
            }
            if (e.getAction() == Action.LEFT_CLICK_BLOCK || e.getAction() == Action.LEFT_CLICK_AIR) {
                if (!p.isSneaking()) return;
                smokeBomb(p, pLocation, rangeModifier, cdModifier);
            }
        }
        //endregion
        //region Cleric
        if (ItemManager.clericWeapons.contains(p.getInventory().getItemInMainHand().getType())) {
            if (e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK) {
                double range = 4 * rangeModifier;
                Collection<Entity> entitiesInRange = p.getNearbyEntities(range, range, range);
                if (p.isSneaking()) {
                    demeterBlessing(p, pLocation, (int) hpModifier, cdModifier, (int) range, entitiesInRange);
                } else {
                    dionysusIntoxication(p, pLocation, dmgModifier, cdModifier, (int) range, entitiesInRange);
                }
            }
            if (e.getAction() == Action.RIGHT_CLICK_AIR) {
                if (!p.isSneaking()) {
                    hermesLeap(p, pLocation, kbModifier, cdModifier);
                } else {
                    aphroditeLove(p, pLocation, hpModifier, rangeModifier, cdModifier);
                }
            }
        }
        //endregion
        //region Scout

        //endregion
        //region Summoner
        //endregion
        //region Warrior
        //endregion
        //endregion
    }
    public void onTriggerSwap(PlayerSwapHandItemsEvent e) {

    }
    //region Functions
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
    public Location calcTarget(Location pLoc, Vector dir, int range) {
        dir.normalize().multiply((range - 1));
        Location target = pLoc.add(dir);
        while (target.getBlock().getType().isSolid()) target.add(0, 1, 0);
        return target;
    }
    //region Cleric Abilities
    private void demeterBlessing(Player p, Location pLocation, int hpModifier, double cdModifier, int range, Collection<Entity> entitiesInRange) {
        //region Demeter's Blessing
        long cooldown = (long) (10000 / cdModifier);
        summonCircle(pLocation, range, Particle.HEART);
        if (partyManager.findParty(p.getUniqueId()) != null) {
            for (Entity entity : entitiesInRange) {
                if (!(entity instanceof LivingEntity)) continue;
                LivingEntity ally = (LivingEntity) entity;
                if (!(partyManager.findParty(ally.getUniqueId()) == partyManager.findParty(p.getUniqueId()))) continue;
                ally.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 80, hpModifier));
            }
        }
        p.addPotionEffect(new PotionEffect(PotionEffectType.HEAL, 1, hpModifier));
        p.playSound(pLocation, Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
        //endregion
    }
    private void dionysusIntoxication(Player p, Location pLocation, double dmgModifier, double cdModifier, int range, Collection<Entity> entitiesInRange) {
        //region Dionysus' Intoxication
        long cooldown = (long) (6000 / cdModifier);
        for (Entity entity : entitiesInRange) {
            if (!(entity instanceof LivingEntity)) continue;
            if (partyManager.findParty(p.getUniqueId()) != null) {
                if (partyManager.findParty(entity.getUniqueId()) == partyManager.findParty(p.getUniqueId())) continue;
            }
            LivingEntity enemy = (LivingEntity) entity;
            enemy.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 200, (int)(3 * dmgModifier)));
            enemy.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 200, (int)(-3 * dmgModifier)));
        }
        summonCircle(pLocation, range, Particle.SOUL);
        p.playSound(pLocation, Sound.ENTITY_WANDERING_TRADER_DRINK_POTION, 1, 1);
        //endregion
    }
    private void hermesLeap(Player p, Location pLocation, double kbModifier, double cdModifier) {
        //region Hermes' Leap
        long cooldown = (long) (8000 / cdModifier);
        summonCircle(pLocation, 4, Particle.CLOUD);
        Vector jumpSpeed = new Vector(3, 2.5, 3).multiply(kbModifier);
        Vector pLooking = pLocation.getDirection();
        Vector jumpVelocity = pLooking.multiply(jumpSpeed);
        p.setVelocity(jumpVelocity);
        p.playSound(pLocation, Sound.ENTITY_SLIME_SQUISH, 1, 1);
        //endregion
    }
    private void aphroditeLove(Player p, Location pLocation, double hpModifier, double rangeModifier, double cdModifier) {
        //region Aphrodite's Love
        long cooldown = (long) (250 / cdModifier);
        double range = 32 * rangeModifier;
        double healing = 4 * hpModifier;
        RayTraceResult traceResult = p.getWorld().rayTraceEntities(p.getEyeLocation(), p.getEyeLocation().getDirection(), range, (entity -> {
            return entity != p;
        }));
        if (traceResult != null) {
            if (traceResult.getHitEntity() instanceof Player && (partyManager.findParty(traceResult.getHitEntity().getUniqueId()) == partyManager.findParty(p.getUniqueId())) && partyManager.findParty(p.getUniqueId()) != null) {
                Player target = (Player) traceResult.getHitEntity();
                if (target.getHealth() < target.getMaxHealth() && p.getHealth() > healing) {
                    p.setHealth(p.getHealth() - healing);
                    p.getWorld().spawnParticle(Particle.CRIT, pLocation, 10);
                    p.playSound(pLocation, Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1, 1);
                }
                target.setHealth(Math.min((target.getHealth() + healing), target.getMaxHealth()));
                target.getWorld().spawnParticle(Particle.HEART, target.getLocation(), 10);
                return;
            } else if (traceResult.getHitEntity() instanceof LivingEntity) {
                LivingEntity enemy = (LivingEntity) traceResult.getHitEntity();
                if (p.getHealth() > healing) {
                    enemy.damage(healing, p);
                    p.setHealth(p.getHealth() - healing);
                    p.getWorld().spawnParticle(Particle.CRIT, pLocation, 10);
                    enemy.getWorld().spawnParticle(Particle.CRIT, enemy.getLocation(), 10);
                    p.playSound(pLocation, Sound.ENTITY_GHAST_DEATH, 1, 0.5f);
                }
            }
        }
        //endregion
    }
    //endregion
    //region Mage Abilities
    private void fireJump(ClassSystem plugin, Player p, Location pLocation, double cdModifier) {
        //region Fire Jump
        long cooldown = (long) (8000 / cdModifier);
        Entity fireball = p.getWorld().spawnEntity(pLocation, EntityType.FIREBALL);
        p.setVelocity(pLocation.getDirection().multiply(-2));
        PersistentDataContainer fireballContainer = fireball.getPersistentDataContainer();
        fireballContainer.set(new NamespacedKey(plugin, "fireJump"), PersistentDataType.STRING, "fireJump");
        //endregion
    }
    private void manaBurst(Player p, double dmgModifier, double rangeModifier, double kbModifier, double cdModifier) {
        //region Mana Burst
        long cooldown = (long) (1000 / cdModifier);
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
    private void devour(Player p, double rangeModifier, double cdModifier) {
        //region Devour
        long cooldown = (long) (100 / cdModifier);
        double range = 30 * rangeModifier;
        RayTraceResult traceResult = p.rayTraceBlocks(range);
        if (traceResult != null) {
            Location blockLocation = traceResult.getHitBlock().getLocation();
            Entity evokerFangs = p.getWorld().spawnEntity(blockLocation.add(0, 1, 0), EntityType.EVOKER_FANGS);
            evokerFangs.addScoreboardTag(p.getUniqueId().toString());
        }
    }
    //endregion
    //region Rogue Abilities
    private void teleport(PlayerInteractEvent e, Player p, Location pLocation, double rangeModifier, double cdModifier) {
        //region Teleport
        long cooldown = (long) (5000 / cdModifier);
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

    private boolean shadowSneak(Player p, Location pLocation, double rangeModifier, double cdModifier) {
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
    private void smokeBomb(Player p, Location pLocation, double rangeModifier, double cdModifier) {
        //region Smoke Bomb
        long cooldown = (long) (10000 / cdModifier);
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
    //endregion
    //region Scout Abilities
    private void evade(Player p, Location pLocation, double kbModifier, double cdModifier) {
        //region Evade
        long cooldown = (long) (10000 / cdModifier);
        summonCircle(pLocation, 4, Particle.FIREWORKS_SPARK);
        Vector jumpSpeed = new Vector(4, 3.5, 4).multiply(kbModifier);
        Vector pLooking = pLocation.getDirection();
        Vector jumpVelocity = pLooking.multiply(jumpSpeed);
        p.setVelocity(jumpVelocity);
        p.playSound(pLocation, Sound.ENTITY_ENDER_DRAGON_FLAP, 1, 1);
        //endregion
    }
    private boolean hunterMark(ClassSystem plugin, Player p, Location pLocation, double rangeModifier, double cdModifier, CooldownManager cooldownManager) {
        //region Hunter's Mark
        long cooldown = (long) (5000 / cdModifier);
        double range = 32 * rangeModifier;
        RayTraceResult traceResult = p.getWorld().rayTraceEntities(p.getEyeLocation(), p.getEyeLocation().getDirection(), range, (entity -> {
            return entity != p;
        }));
        if (traceResult == null || !(traceResult.getHitEntity() instanceof LivingEntity)) return true;
        if (partyManager.findParty(p.getUniqueId()) != null) {
            if (partyManager.findParty(traceResult.getHitEntity().getUniqueId()) == partyManager.findParty(p.getUniqueId())) return false;
        }
        LivingEntity livingEntity = (LivingEntity) traceResult.getHitEntity();
        livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 300, 1));
        livingEntity.addScoreboardTag("Marked");
        p.playSound(pLocation, Sound.BLOCK_CONDUIT_ACTIVATE, 1, 1);
        BukkitScheduler scheduler = Bukkit.getScheduler();
        scheduler.runTaskLater(plugin, () -> {
            livingEntity.removeScoreboardTag("Marked");
        }, 300L);
        return false;
        //endregion
    }
    //endregion
    //region Summoner Abilities
    //endregion
    //region Warrior Abilities
    public void stomp(double kbModifier, double rangeModifier, Player p, double dmgModifier, double cdModifier) {
        double speed = 1 * kbModifier;
        double kb = ThreadLocalRandom.current().nextDouble(0.3, 0.5) * kbModifier;
        double range = 25 * rangeModifier;
        double damage = 10 * dmgModifier;
        long cooldown = (long) (5000 / cdModifier);
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
    //endregion
    //endregion
}
