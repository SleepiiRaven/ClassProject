package classsystem.classsystem.handlers.clickHandler;

import classsystem.classsystem.ClassSystem;
import classsystem.classsystem.CooldownManager;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.*;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class PlayerAvatarHandler extends PlayerClassTemplate {
    @Override
    public void onTrigger(PlayerInteractEvent e) {
        //region Set Variables
        ClassSystem plugin = ClassSystem.getInstance();
        if (e.getHand() != EquipmentSlot.HAND) return;
        Player p = e.getPlayer();
        String pUUID = p.getUniqueId().toString();
        Location pLocation = p.getLocation();
        double hpModifier = plugin.getConfig().getInt(pUUID + ".hpMultiplier");
        double dmgModifier = plugin.getConfig().getInt(pUUID + ".dmgMultiplier");
        double rangeModifier = plugin.getConfig().getInt(pUUID + ".rangeMultiplier");
        double kbModifier = plugin.getConfig().getInt(pUUID + ".kbMultiplier");
        //endregion
        //region CLASS ABILITIES
        //region Mage
        if (p.getInventory().getItemInMainHand().getType().equals(Material.STICK)) {
            if (e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK) {
                if (p.isSneaking()) {
                    //region Devour
                    double range = 30 * rangeModifier;
                    RayTraceResult traceResult = p.rayTraceBlocks(range);
                    if (traceResult != null) {
                        Location blockLocation = traceResult.getHitBlock().getLocation();
                        p.getWorld().spawnEntity(blockLocation.add(0,1,0), EntityType.EVOKER_FANGS);
                    }
                    //endregion
                } else {
                    //region Ethereal Soul
                    //region Damage
                    double range = 25 * rangeModifier;
                    double damage = 5 * dmgModifier;
                    double kb = ThreadLocalRandom.current().nextDouble(0.3, 0.5) * kbModifier;
                    List<Entity> closebyMonsters = p.getNearbyEntities(range, range, range);
                    for (Entity closebyMonster : closebyMonsters) {
                        Location eLocation = closebyMonster.getLocation();
                        // .distance is resource intensive, so get it squared (SO SQUARE YOUR RANGE)
                        double distance = eLocation.distanceSquared(pLocation);
                        if (distance < range) {
                            // make sure it's a living entity, not an armor stand or something, continue skips the current loop
                            if (!(closebyMonster instanceof LivingEntity)) continue;
                            LivingEntity livingMonster = (LivingEntity) closebyMonster;
                            livingMonster.damage(damage, p);
                            livingMonster.setVelocity(new Vector(0, kb, 0));
                        }
                    }
                    //endregion
                    //region Particles
                    int particleSize = (int) Math.sqrt(range);
                    summonCircle(pLocation, particleSize, Particle.CRIT_MAGIC);
                    //endregion
                    //region Play Sound
                    p.playSound(p, Sound.ENTITY_ENDER_DRAGON_FLAP, 1.0f, 1.0f);
                    //endregion
                    //endregion
                }
            }
            if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                if (p.isSneaking()) {
                    //region Mana Burst
                    //region Variables
                    double range = 25 * rangeModifier;
                    double damage = 10 * dmgModifier;
                    double kb = ThreadLocalRandom.current().nextDouble(0.3, 0.5) * kbModifier;
                    //endregion
                    //region Shoot Loop
                    Location viewPos = p.getEyeLocation();
                    Vector viewDir = viewPos.getDirection();
                    for (double t = 0; t < 10; t += 0.5) {
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
                            LivingEntity livingMonster = (LivingEntity) closebyMonster;
                            // Get the entitie's collision box and the viewpos' xyz
                            BoundingBox boundingBox = livingMonster.getBoundingBox();
                            double viewPosX = viewPos.getX();
                            double viewPosY = viewPos.getY();
                            double viewPosZ = viewPos.getZ();
                            // if our particle goes through the enemy's hitbox, we keep going through the loop, if we don't we use continue;
                            if (!(boundingBox.contains(viewPosX, viewPosY, viewPosZ))) continue;
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
                } else {
                    //region Fire Jump
                    Entity fireball = p.getWorld().spawnEntity(pLocation, EntityType.FIREBALL);
                    p.setVelocity(pLocation.getDirection().multiply(-1));
                    PersistentDataContainer fireballContainer = fireball.getPersistentDataContainer();
                    fireballContainer.set(new NamespacedKey(plugin, "fireJump"), PersistentDataType.STRING, "fireJump");
                    //endregion
                }
            }
        }
        //endregion
        //region Rogue
        if (p.getInventory().getItemInMainHand().getType().equals(Material.DIAMOND_SWORD)) {
            if (e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR) {

                if (!p.isSneaking()) {
                    //region Teleport
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
                    //endregion
                }
            }
        }
        //endregion
        //region Cleric
        if (p.getInventory().getItemInMainHand().getType().equals(Material.DIAMOND_HOE)) {
            if (e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK) {
                double range = 4 * rangeModifier;
                Collection<Entity> entitiesInRange = p.getNearbyEntities(range, range, range);
                if (p.isSneaking()) {
                    //region Demeter's Saturation
                    summonCircle(pLocation, (int)range, Particle.HEART);
                    for (Entity entity : entitiesInRange) {
                        if (!(entity instanceof Player)) continue;
                        ((Player) entity).addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20, (int) hpModifier));
                        p.playSound(pLocation, Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
                    }
                    p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20, (int) hpModifier));
                    //endregion
                } else {
                    //region Dionysus' Intoxication
                    for (Entity entity : entitiesInRange) {
                        summonCircle(pLocation, (int)range, Particle.SOUL);
                        if (!(entity instanceof Monster)) continue;
                        Monster monster = (Monster) entity;
                        monster.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 300, (int)(3 * dmgModifier)));
                        monster.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 300, (int)(-3 * dmgModifier)));
                    }
                    p.playSound(pLocation, Sound.ENTITY_WANDERING_TRADER_DRINK_POTION, 1, 1);
                    //endregion
                }
            }
            if (e.getAction() == Action.RIGHT_CLICK_AIR) {
                if (!p.isSneaking()) {
                    //region Hermes' Leap
                    summonCircle(pLocation, 4, Particle.CLOUD);
                    Vector jumpSpeed = new Vector(3, 2.5, 3).multiply(kbModifier);
                    Vector pLooking = pLocation.getDirection();
                    Vector jumpVelocity = pLooking.multiply(jumpSpeed);
                    p.setVelocity(jumpVelocity);
                    p.playSound(pLocation, Sound.ENTITY_SLIME_SQUISH, 1, 1);
                    //endregion
                } else {
                    //region Aphrodite's Love
                    double range = 8 * rangeModifier;
                    double healing = 4 * hpModifier;
                    RayTraceResult traceResult = p.getWorld().rayTraceEntities(p.getEyeLocation(), p.getEyeLocation().getDirection(), range, (entity -> {
                        return entity != p;
                    }));
                    if (traceResult != null) {
                        if (traceResult.getHitEntity() instanceof Player) {
                            Player target = (Player) traceResult.getHitEntity();
                            if (target.getHealth() < target.getMaxHealth() && p.getHealth() > healing) {
                                p.setHealth(p.getHealth() - healing);
                                p.getWorld().spawnParticle(Particle.CRIT, pLocation, 10);
                                p.playSound(pLocation, Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1, 1);
                            }
                            target.setHealth(Math.min((target.getHealth() + healing), target.getMaxHealth()));
                            target.getWorld().spawnParticle(Particle.HEART, target.getLocation(), 10);
                            return;
                        } else if (traceResult.getHitEntity() instanceof Monster) {
                            Monster monster = (Monster) traceResult.getHitEntity();
                            if (p.getHealth() > healing) {
                                monster.damage(healing, p);
                                p.setHealth(p.getHealth() - healing);
                                p.getWorld().spawnParticle(Particle.CRIT, pLocation, 10);
                                monster.getWorld().spawnParticle(Particle.CRIT, monster.getLocation(), 10);
                                p.playSound(pLocation, Sound.ENTITY_GHAST_DEATH, 1, 0.5f);
                            }
                        }
                    }
                    //endregion
                }
            }
        }
        //endregion

        //endregion
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
    //endregion
}
