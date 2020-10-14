package com.hp.asi.hpic4vc.provider.dam.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class RelationSet {

    private final Set<RelationDescriptor> supportedRelations;
    private final Map<RelationDescriptor, RelationDescriptor> inverseByForward;
    
    public RelationSet() {
        this.supportedRelations = new HashSet<RelationDescriptor>();
        this.inverseByForward   = new HashMap<RelationDescriptor, RelationDescriptor>();
    }
    
    public void addRelation(final String sourceType, 
                            final String relationName, 
                            final String targetType) {
        supportedRelations.add(new RelationDescriptor(sourceType, relationName, targetType));
    }
    
    public void addRelation(final RelationDescriptor relation) {
        supportedRelations.add(relation);
    }
    
    public void addInverseRelation(final String sourceType, 
                                   final String relationName,
                                   final String inverseRelationName,
                                   final String targetType) {
        inverseByForward.put(new RelationDescriptor(sourceType, relationName, targetType),
                             new RelationDescriptor(sourceType, inverseRelationName, targetType));
    }
    
    public void addInverseRelation(final RelationDescriptor relation,
                                   final RelationDescriptor inverseRelation) {
        inverseByForward.put(relation, inverseRelation);
    }

    /**
     * Checks if the supplied relation is supported by this adapter.
     */
    public boolean containsRelation(final String sourceType,
                                    final String relation,
                                    final String targetType) {
       return this.supportedRelations.contains(
             new RelationDescriptor(sourceType, relation, targetType));
    }
    
    public boolean containsRelation(final RelationDescriptor relation) {
        return this.supportedRelations.contains(relation);
    }
    
    public boolean containsInverseRelation(final String sourceType,
                                           final String relation,
                                           final String targetType) {
        return this.inverseByForward.containsKey(new RelationDescriptor(sourceType, relation, targetType));
    }
    
    public boolean containsInverseRelation(final RelationDescriptor relation) {
        return this.inverseByForward.containsKey(relation);
    }
    
    public RelationDescriptor getInverseRelation(final String sourceType,
                                           final String relation,
                                           final String targetType) {
        return this.inverseByForward.get(new RelationDescriptor(sourceType, relation, targetType));
    }
    
    public RelationDescriptor getInverseRelation(final RelationDescriptor relation) {
              return this.inverseByForward.get(relation);
          }
    
    /**
     * Describe a relation between types. Typically a relation implies a property
     * on the source type which would yield an object of the target type.
     */
    public static final class RelationDescriptor {
       // Type of the source of the relation.
       public final String sourceType;
       // Name of the relation.
       public final String name;
       // Type of the target of the relation.
       public final String targetType;

       private volatile int _hashCode = 0;

       public RelationDescriptor(
             String relationSourceType,
             String relationName,
             String relationTargetType) {
          sourceType = relationSourceType;
          name = relationName;
          targetType = relationTargetType;
       }

       // Since multiple instances of RelationDescriptor could describe the identical
       // relation it is necessary to override the hashcode() and equals(...) methods
       // to guarantee that the HashMap treats them as the same object.

       @Override
       public boolean equals(Object value) {
          if (!(value instanceof RelationDescriptor)) {
             return false;
          }
          RelationDescriptor comparable = (RelationDescriptor)value;
          return this.sourceType.equals(comparable.sourceType)
                 && this.name.equals(comparable.name)
                 && this.targetType.equals(comparable.targetType);
       }

       @Override
       public int hashCode() {

          if (_hashCode != 0) {
             return _hashCode;
          }
          _hashCode = 7;
          if (this.sourceType != null) {
             _hashCode = 31 * _hashCode + this.sourceType.hashCode();
          }
          if (this.name != null) {
             _hashCode = 31 * _hashCode + this.name.hashCode();
          }
          if (this.targetType != null) {
             _hashCode = 31 * _hashCode + this.targetType.hashCode();
          }
          return _hashCode;
       }
    }
}
