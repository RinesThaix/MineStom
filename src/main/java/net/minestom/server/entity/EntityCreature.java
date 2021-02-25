package net.minestom.server.entity;

import com.extollit.gaming.ai.path.HydrazinePathFinder;
import net.minestom.server.attribute.Attributes;
import net.minestom.server.entity.ai.EntityAI;
import net.minestom.server.entity.ai.GoalSelector;
import net.minestom.server.entity.ai.TargetSelector;
import net.minestom.server.entity.pathfinding.NavigableEntity;
import net.minestom.server.entity.pathfinding.Navigator;
import net.minestom.server.event.entity.EntityAttackEvent;
import net.minestom.server.instance.Instance;
import net.minestom.server.utils.Position;
import net.minestom.server.utils.time.TimeUnit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class EntityCreature extends LivingEntity implements NavigableEntity, EntityAI {

    private int removalAnimationDelay = 1000;

    protected final List<GoalSelector> goalSelectors = new ArrayList<>();
    protected final List<TargetSelector> targetSelectors = new ArrayList<>();
    private GoalSelector currentGoalSelector;

    private final Navigator navigator = new Navigator(this);

    private Entity target;

    public EntityCreature(@NotNull EntityType entityType, @NotNull Position spawnPosition) {
        super(entityType, spawnPosition);

        heal();
    }

    public EntityCreature(@NotNull EntityType entityType, @NotNull Position spawnPosition, @Nullable Instance instance) {
        this(entityType, spawnPosition);

        if (instance != null) {
            setInstance(instance);
        }
    }

    @Override
    public void update(long time) {
        // AI
        aiTick(time);

        // Path finding
        this.navigator.tick(getAttributeValue(Attributes.MOVEMENT_SPEED));

        // Fire, item pickup, ...
        super.update(time);
    }

    @Override
    public void setInstance(@NotNull Instance instance) {
        this.navigator.setPathFinder(new HydrazinePathFinder(navigator.getPathingEntity(), instance.getInstanceSpace()));

        super.setInstance(instance);
    }

    @Override
    public void spawn() {

    }

    @Override
    public void kill() {
        super.kill();

        if (removalAnimationDelay > 0) {
            // Needed for proper death animation (wait for it to finish before destroying the entity)
            scheduleRemove(removalAnimationDelay, TimeUnit.MILLISECOND);
        } else {
            // Instant removal without animation playback
            remove();
        }
    }

    /**
     * Gets the kill animation delay before vanishing the entity.
     *
     * @return the removal animation delay in milliseconds, 0 if not any
     */
    public int getRemovalAnimationDelay() {
        return removalAnimationDelay;
    }

    /**
     * Changes the removal animation delay of the entity.
     * <p>
     * Testing shows that 1000 is the minimum value to display the death particles.
     *
     * @param removalAnimationDelay the new removal animation delay in milliseconds, 0 to remove it
     */
    public void setRemovalAnimationDelay(int removalAnimationDelay) {
        this.removalAnimationDelay = removalAnimationDelay;
    }

    @NotNull
    @Override
    public List<GoalSelector> getGoalSelectors() {
        return goalSelectors;
    }

    @NotNull
    @Override
    public List<TargetSelector> getTargetSelectors() {
        return targetSelectors;
    }

    @Nullable
    @Override
    public GoalSelector getCurrentGoalSelector() {
        return currentGoalSelector;
    }

    @Override
    public void setCurrentGoalSelector(GoalSelector currentGoalSelector) {
        this.currentGoalSelector = currentGoalSelector;
    }

    /**
     * Gets the entity target.
     *
     * @return the entity target, can be null if not any
     */
    @Nullable
    public Entity getTarget() {
        return target;
    }

    /**
     * Changes the entity target.
     *
     * @param target the new entity target, null to remove
     */
    public void setTarget(@Nullable Entity target) {
        this.target = target;
    }

    @NotNull
    @Override
    public Navigator getNavigator() {
        return navigator;
    }

    /**
     * Calls a {@link EntityAttackEvent} with this entity as the source and {@code target} as the target.
     *
     * @param target    the entity target
     * @param swingHand true to swing the entity main hand, false otherwise
     */
    public void attack(@NotNull Entity target, boolean swingHand) {
        if (swingHand)
            swingMainHand();
        EntityAttackEvent attackEvent = new EntityAttackEvent(this, target);
        callEvent(EntityAttackEvent.class, attackEvent);
    }

    /**
     * Calls a {@link EntityAttackEvent} with this entity as the source and {@code target} as the target.
     * <p>
     * This does not trigger the hand animation.
     *
     * @param target the entity target
     */
    public void attack(@NotNull Entity target) {
        attack(target, false);
    }

}
