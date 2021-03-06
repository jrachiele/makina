package makina.learn.logic;

import makina.learn.logic.formula.EntityType;
import makina.learn.logic.formula.Predicate;
import makina.learn.logic.formula.Variable;
import makina.learn.logic.grounding.GroundPredicate;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * @author Emmanouil Antonios Platanios
 */
public class InMemoryLogicManager implements LogicManager {
    private final Logic logic;

    private final List<EntityType> entityTypes = new ArrayList<>();
    private final List<Predicate> predicates = new ArrayList<>();
    private final Map<Predicate, Map<List<Long>, GroundPredicate>> groundPredicatesMap = new HashMap<>();
    private final Map<Long, GroundPredicate> groundPredicates = new HashMap<>();
    private final List<Predicate> closedPredicates = new ArrayList<>();

    private AtomicLong newEntityTypeId = new AtomicLong();
    private AtomicLong newPredicateId = new AtomicLong();
    private AtomicLong newGroundPredicateId = new AtomicLong();

    public InMemoryLogicManager(Logic logic) {
        this.logic = logic;
    }

    public Logic logic() {
        return logic;
    }

    public EntityType addEntityType(Set<Long> allowedValues) {
        return addEntityType(null, allowedValues);
    }

    public EntityType addEntityType(String name, Set<Long> allowedValues) {
        EntityType entityType = new EntityType(newEntityTypeId.getAndIncrement(), name, allowedValues);
        entityTypes.add(entityType);
        return entityType;
    }

    public Predicate addPredicate(List<EntityType> argumentTypes, boolean closed) {
        return addPredicate(null, argumentTypes, closed);
    }

    public Predicate addPredicate(String name, List<EntityType> argumentTypes, boolean closed) {
        Predicate predicate = new Predicate(newPredicateId.getAndIncrement(), name, argumentTypes);
        predicates.add(predicate);
        groundPredicatesMap.put(predicate, new HashMap<>());
        if (closed)
            closedPredicates.add(predicate);
        return predicate;
    }

    public GroundPredicate addGroundPredicate(Predicate predicate, List<Long> argumentAssignments) {
        return addGroundPredicate(predicate, argumentAssignments, null);
    }

    public GroundPredicate addGroundPredicate(Predicate predicate, List<Long> argumentAssignments, Double value) {
        if (!groundPredicatesMap.containsKey(predicate))
            throw new IllegalArgumentException("The provided predicate identifier does not match any of the " +
                                                       "predicates currently stored in this logic manager.");
        if (groundPredicatesMap.get(predicate).containsKey(argumentAssignments)) {
            GroundPredicate groundPredicate =
                    groundPredicatesMap.get(predicate).get(argumentAssignments);
            if (!(groundPredicate.getValue() == null && value == null) && !groundPredicate.getValue().equals(value))
                throw new IllegalArgumentException("A grounding for the predicate corresponding to the provided " +
                                                           "identifier and for the provided argument assignments has " +
                                                           "already been added to this logic manager with a " +
                                                           "different value.");
            else {
                return groundPredicate;
            }
        } else {
            GroundPredicate groundPredicate = new GroundPredicate(newGroundPredicateId.getAndIncrement(),
                                                                  predicate,
                                                                  argumentAssignments,
                                                                  value);
            groundPredicates.put(groundPredicate.getId(), groundPredicate);
            groundPredicatesMap.get(predicate).put(argumentAssignments, groundPredicate);
            return groundPredicate;
        }
    }

    public GroundPredicate addOrReplaceGroundPredicate(Predicate predicate, List<Long> argumentAssignments) {
        return addOrReplaceGroundPredicate(predicate, argumentAssignments, null);
    }

    public GroundPredicate addOrReplaceGroundPredicate(Predicate predicate,
                                                       List<Long> argumentAssignments,
                                                       Double value) {
        if (!groundPredicatesMap.containsKey(predicate))
            throw new IllegalArgumentException("The provided predicate identifier does not match any of the " +
                                                       "predicates currently stored in this logic manager.");
        if (groundPredicatesMap.get(predicate).containsKey(argumentAssignments)) {
            GroundPredicate groundPredicate =
                    groundPredicatesMap.get(predicate).get(argumentAssignments);
            groundPredicate.setValue(value);
            return groundPredicate;
        } else {
            GroundPredicate groundPredicate = new GroundPredicate(newGroundPredicateId.getAndIncrement(),
                                                                  predicate,
                                                                  argumentAssignments,
                                                                  value);
            groundPredicates.put(groundPredicate.getId(), groundPredicate);
            groundPredicatesMap.get(predicate).put(argumentAssignments, groundPredicate);
            return groundPredicate;
        }
    }

    public GroundPredicate removeGroundPredicate(Predicate predicate, List<Long> argumentAssignments) {
        if (!groundPredicatesMap.containsKey(predicate)
                || !groundPredicatesMap.get(predicate).containsKey(argumentAssignments))
            return null;
        return groundPredicatesMap.get(predicate).remove(argumentAssignments);
    }

    public boolean checkIfGroundPredicateExists(Predicate predicate, List<Long> argumentAssignments) {
        if (!groundPredicatesMap.containsKey(predicate))
            throw new IllegalArgumentException("The provided predicate identifier does not match any of the " +
                                                       "predicates currently stored in this logic manager.");

        return groundPredicatesMap.get(predicate).containsKey(argumentAssignments)
                || closedPredicates.contains(predicate);
    }

    // TODO: Maybe change return type to long?
    public long getNumberOfGroundPredicates() {
        return groundPredicates.size();
    }

    public List<GroundPredicate> getGroundPredicates() {
        List<GroundPredicate> groundPredicates = new ArrayList<>();
        for (Map<List<Long>, GroundPredicate> groundedPredicatesSet : this.groundPredicatesMap.values())
            groundPredicates.addAll(groundedPredicatesSet.values().stream().collect(Collectors.toList()));
        return groundPredicates;
    }

    // TODO: Fix the way in which the grounded predicates are stored in this manager.
    public GroundPredicate getGroundPredicate(long identifier) {
        return groundPredicates.get(identifier);
    }

    public GroundPredicate getGroundPredicate(Predicate predicate, List<Long> argumentAssignments) {
        if (!groundPredicatesMap.containsKey(predicate))
            throw new IllegalArgumentException("The provided predicate identifier does not match any of the " +
                                                       "predicates currently stored in this logic manager.");
        if (!groundPredicatesMap.get(predicate).containsKey(argumentAssignments) && !closedPredicates.contains(predicate))
            throw new IllegalArgumentException("A grounding for the predicate corresponding to the provided " +
                                                       "identifier and for the provided argument assignments has " +
                                                       "not been added to this logic manager.");

        if (groundPredicatesMap.get(predicate).containsKey(argumentAssignments))
            return groundPredicatesMap.get(predicate).get(argumentAssignments);
        else
            return addGroundPredicate(predicate, argumentAssignments, logic.falseValue()); // TODO: Fix this hack for handling closed predicates.
    }

    public long getNumberOfEntityTypes() {
        return entityTypes.size();
    }

    public EntityType getEntityType(String name) {
        for (EntityType entityType : entityTypes)
            if (entityType.getName().equals(name))
                return entityType;
        return null;
    }

    public Set<Long> getVariableValues(Variable variable) {
        return variable.getType().getAllowedValues();
    }

    public Predicate getPredicate(String name) {
        for (Predicate predicate : predicates)
            if (predicate.getName().equals(name))
                return predicate;
        return null;
    }

    public List<Predicate> getClosedPredicates() {
        return closedPredicates;
    }

    /**
     * Note that this method returns null if there is no value stored for the provided arguments assignment.
     *
     * @param predicate
     * @param argumentAssignments
     * @return
     */
    public Double getPredicateAssignmentTruthValue(Predicate predicate, List<Long> argumentAssignments) {
        if (!groundPredicatesMap.containsKey(predicate))
            throw new IllegalArgumentException("The provided predicate identifier does not match any of the " +
                                                       "predicates currently stored in this logic manager.");
        if (!groundPredicatesMap.get(predicate).containsKey(argumentAssignments))
            if (closedPredicates.contains(predicate))
                return logic.falseValue();
            else
                return null;
        return groundPredicatesMap.get(predicate).get(argumentAssignments).getValue();
    }
}
