package net.bunten.enderscape.entity.ai.goal;

import net.bunten.enderscape.entity.ai.HomeAnchoredMob;
import net.bunten.enderscape.entity.wraith.Wraith;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

/**
 * When there is no attack target, keeps the mob near its spawn home (first placement / NBT).
 * Uses separate horizontal and vertical comfort bands (End towers / bridges). Paths home and
 * occasionally teleports if far in either axis or not making progress.
 * <p>
 * This goal uses the default {@link #requiresUpdateEveryTick()} ({@code false}) so it runs on
 * {@link Goal#reducedTickDelay} like other goals — {@link #STUCK_TICKS} counts goal updates, not
 * every server world tick; raise it if teleports feel too eager.
 */
public class StayNearSpawnGoal extends Goal {

  private static final double COMFORT_DISTANCE = 6.0;
  private static final double COMFORT_DISTANCE_SQ = COMFORT_DISTANCE * COMFORT_DISTANCE;
  private static final double TELEPORT_DISTANCE = 12.0;
  private static final double TELEPORT_DISTANCE_SQ = TELEPORT_DISTANCE * TELEPORT_DISTANCE;
  private static final double VERTICAL_COMFORT = 4.0;
  private static final double VERTICAL_TELEPORT = 8.0;
  private static final double HYSTERESIS = 0.85;
  private static final int HOME_TELEPORT_COOLDOWN = 35;
  /** Counts {@linkplain Goal#reducedTickDelay throttled} goal ticks, not every world tick. */
  private static final int STUCK_TICKS = 55;
  private static final int WRAITH_REPATH_PERIOD = 8;

  private final HomeAnchoredMob mob;
  private int teleportCooldown;
  private int stuckTicks;
  private double bestDist3DSq = Double.MAX_VALUE;

  public StayNearSpawnGoal(HomeAnchoredMob mob) {
    this.mob = mob;
    this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
  }

  @Override
  public boolean canUse() {
    Monster m = mob.asMonster();
    if (m.level().isClientSide() || !m.isAlive() || m.isPassenger()) {
      return false;
    }
    if (m.getTarget() != null) {
      return false;
    }
    mob.ensureSpawnHomeCaptured();
    BlockPos home = mob.getSpawnHomeBlock();
    return outsideComfort(m, home, COMFORT_DISTANCE_SQ, VERTICAL_COMFORT);
  }

  @Override
  public boolean canContinueToUse() {
    Monster m = mob.asMonster();
    if (m.level().isClientSide() || !m.isAlive() || m.isPassenger() || m.getTarget() != null) {
      return false;
    }
    BlockPos home = mob.getSpawnHomeBlock();
    return outsideComfort(m, home, COMFORT_DISTANCE_SQ * HYSTERESIS, VERTICAL_COMFORT * HYSTERESIS);
  }

  @Override
  public void start() {
    teleportCooldown = 0;
    stuckTicks = 0;
    bestDist3DSq = Double.MAX_VALUE;
    BlockPos home = mob.getSpawnHomeBlock();
    moveHome(home);
  }

  @Override
  public void tick() {
    Monster m = mob.asMonster();
    BlockPos home = mob.getSpawnHomeBlock();
    Vec3 homeCenter = Vec3.atBottomCenterOf(home);
    double dist3DSq = m.distanceToSqr(homeCenter);
    Vec3 look = homeCenter;
    m.getLookControl().setLookAt(look.x, look.y, look.z);

    if (dist3DSq < bestDist3DSq - 0.25) {
      bestDist3DSq = dist3DSq;
      stuckTicks = 0;
    } else {
      stuckTicks++;
    }

    if (teleportCooldown > 0) {
      teleportCooldown--;
    }

    boolean wantTeleport = outsideTeleport(m, home) || stuckTicks >= STUCK_TICKS;
    if (wantTeleport && teleportCooldown <= 0 && mob.tryTeleportNearSpawnHome(home)) {
      teleportCooldown = HOME_TELEPORT_COOLDOWN;
      stuckTicks = 0;
      bestDist3DSq = m.distanceToSqr(homeCenter);
      return;
    }

    if (m instanceof Wraith wraith) {
      if (!wraith.getMoveControl().hasWanted()
          || (m.tickCount + m.getId()) % WRAITH_REPATH_PERIOD == 0) {
        moveHome(home);
      }
    } else if (!m.getNavigation().isInProgress()) {
      moveHome(home);
    }
  }

  @Override
  public void stop() {
    Monster m = mob.asMonster();
    m.getNavigation().stop();
    if (m instanceof Wraith wraith) {
      wraith.clearMoveTarget();
    }
  }

  private void moveHome(BlockPos home) {
    mob.pathTowardSpawnHome(home, mob.spawnHomePathSpeed());
  }

  private static boolean outsideComfort(Monster m, BlockPos home, double horizComfortSq,
      double verticalComfort) {
    return horizontalDistSq(m, home) > horizComfortSq || verticalExceeds(m, home, verticalComfort);
  }

  private static boolean outsideTeleport(Monster m, BlockPos home) {
    return horizontalDistSq(m, home) > TELEPORT_DISTANCE_SQ || verticalExceeds(m, home,
        VERTICAL_TELEPORT);
  }

  private static boolean verticalExceeds(Monster m, BlockPos home, double maxAbsDy) {
    double homeY = Vec3.atBottomCenterOf(home).y;
    return Math.abs(m.getY() - homeY) > maxAbsDy;
  }

  private static double horizontalDistSq(Monster m, BlockPos home) {
    double dx = m.getX() - (home.getX() + 0.5);
    double dz = m.getZ() - (home.getZ() + 0.5);
    return dx * dx + dz * dz;
  }
}
