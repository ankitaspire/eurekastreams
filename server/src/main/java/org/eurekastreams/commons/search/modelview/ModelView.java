/*
 * Copyright (c) 2009-2010 Lockheed Martin Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.eurekastreams.commons.search.modelview;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

import org.eurekastreams.commons.model.DomainEntity;

/**
 * Abstract class used to represent a simple, lightweight view of an entity. Subclasses just need to provide a getter
 * and setter for each property to expose, wrapping the 'setParameter' and 'getParameter' methods in this class. The
 * setters should take a property of the correct type, then call super.setParameter([parameterName], [String version])
 * with a String version of that value. The getter should return the value of super.getParameter([parameterName]), and
 * should be of type String.
 * 
 * Implementations of this abstract class should feel free to use non-string-based properties if needed, not using this
 * class's implementation, but the idea is that View objects are to display data, not manipulate it.
 * 
 * TODO: Create and implement an interface to inject a HashMap of properties so we don't have to use reflection anymore.
 */
public abstract class ModelView implements Serializable
{
    /**
     * Serializer UID, generated by Eclipse.
     */
    private static final long serialVersionUID = -2024521852254798045L;

    /**
     * Load any properties that may have been returned from the search query.
     * 
     * @param properties
     *            a Map of properties returned by the search - injest any that this class handles.
     */
    public void loadProperties(final Map<String, Object> properties)
    {
        // get the entity id if present
        if (properties.containsKey("id"))
        {
            // look for "id"
            setEntityId((Long) properties.get("id"));
        }
        else if (properties.containsKey("__HSearch_id"))
        {
            // else, fall back on the hibernate search id key name
            setEntityId((Long) properties.get("__HSearch_id"));
        }

        // get the search score if present
        if (properties.containsKey("__HSearch_Score"))
        {
            setSearchIndexScore((Float) properties.get("__HSearch_Score"));
        }

        // get the managed entity if present
        if (properties.containsKey("__HSearch_This"))
        {
            setManagedEntity((DomainEntity) properties.get("__HSearch_This"));
        }
    }

    /**
     * Get the name of this entity for toString - supplied by child.
     * 
     * @return the name of this entity for toString
     */
    protected abstract String getEntityName();

    /**
     * Value to use for uninitialized String properties.
     */
    public static final String UNINITIALIZED_STRING_VALUE = "(value not returned by query)";

    /**
     * Value to use for uninitialized Long properties.
     */
    public static final long UNINITIALIZED_LONG_VALUE = Long.MIN_VALUE;

    /**
     * Value to use for uninitialized Integer properties.
     */
    public static final int UNINITIALIZED_INTEGER_VALUE = Integer.MIN_VALUE;

    /**
     * Value to use for uninitialized Float properties.
     */
    public static final float UNINITIALIZED_FLOAT_VALUE = Float.MIN_NORMAL;

    /**
     * Value to use for uninitialized Date properties.
     */
    public static final Date UNINITIALIZED_DATE_VALUE = new Date(0L);

    /**
     * The underlying entity's id in the database.
     */
    private long entityId = UNINITIALIZED_LONG_VALUE;

    /**
     * The search index score.
     */
    private float searchIndexScore = UNINITIALIZED_FLOAT_VALUE;

    /**
     * The server date time.
     */
    private Date serverDateTime = UNINITIALIZED_DATE_VALUE;

    /**
     * The managed Hibernate entity - only loaded if requested.
     */
    private DomainEntity managedEntity;

    /**
     * Extra information used to keep track of which search words matched which search fields.
     */
    private FieldMatch fieldMatch;

    /**
     * Return the managed Hibernate entity - only loaded if requested.
     * 
     * @return the managed Hibernate entity.
     */
    protected DomainEntity getManagedEntity()
    {
        return managedEntity;
    }

    /**
     * Set the managed entity (for serialization).
     * 
     * @param theManagedEntity
     *            the managed entity to set
     */
    protected void setManagedEntity(final DomainEntity theManagedEntity)
    {
        managedEntity = theManagedEntity;
    }

    /**
     * Set the object's id (for serialization).
     * 
     * @param inEntityId
     *            the id of the entity
     */
    protected void setEntityId(final long inEntityId)
    {
        entityId = inEntityId;
    }

    /**
     * Get the Entity's id.
     * 
     * @return the Entity's id.
     */
    public long getEntityId()
    {
        return entityId;
    }

    /**
     * Get the search result's score, relative to the rest of the current request batch.
     * 
     * @return the search result's score, relative to the rest of the current request batch.
     */
    public float getSearchIndexScore()
    {
        return searchIndexScore;
    }

    /**
     * Set the search result's score, relative to the rest of the current request batch.
     * 
     * @param theSearchScore
     *            the search result's score, relative to the rest of the current request batch.
     */
    protected void setSearchIndexScore(final float theSearchScore)
    {
        searchIndexScore = theSearchScore;
    }

    /**
     * The cached value of the previous toString() call, or null if not yet determined.
     */
    private String toStringValue = null;

    /**
     * Base toString implementation - hibernate class name and ID.
     * 
     * @return a formatted message of [ClassName]#[ID]
     */
    @Override
    public String toString()
    {
        if (toStringValue != null)
        {
            return toStringValue;
        }

        if (entityId == UNINITIALIZED_LONG_VALUE)
        {
            return getEntityName();
        }
        else
        {
            toStringValue = getEntityName() + "#" + entityId;
        }
        return toStringValue;
    }

    /**
     * Check if search explanation information is loaded.
     * 
     * @return whether search explanation information is loaded.
     */
    public boolean hasSearchIndexExplanation()
    {
        return searchIndexExplanationString != UNINITIALIZED_STRING_VALUE;
    }

    /**
     * The String representation of the search index explanation.
     */
    private String searchIndexExplanationString = UNINITIALIZED_STRING_VALUE;

    /**
     * Get the String representation of the search index explanation.
     * 
     * @return the searchExplanationString
     */
    public String getSearchIndexExplanationString()
    {
        return searchIndexExplanationString;
    }

    /**
     * Set the string representation of the search index explanation.
     * 
     * @param inSearchIndexExplanationString
     *            the searchExplanationString to set
     */
    public void setSearchIndexExplanationString(final String inSearchIndexExplanationString)
    {
        searchIndexExplanationString = inSearchIndexExplanationString;
    }

    /**
     * Get the FieldMatch.
     * 
     * @return the fieldMatch
     */
    public FieldMatch getFieldMatch()
    {
        return fieldMatch;
    }

    /**
     * Set the FieldMatch.
     * 
     * @param inFieldMatch
     *            the fieldMatch to set
     */
    public void setFieldMatch(final FieldMatch inFieldMatch)
    {
        this.fieldMatch = inFieldMatch;
    }

    /**
     * Get the serverDateTime of when this object was populated by the server.
     * 
     * @return - current server Date when the object was populated.
     */
    public Date getServerDateTime()
    {
        return serverDateTime;
    }

    /**
     * Set the serverDateTime of when this object was populated by the server.
     * 
     * @param inServerDateTime
     *            - current server Date when the object was populated.
     */
    public void setServerDateTime(final Date inServerDateTime)
    {
        this.serverDateTime = inServerDateTime;
    }
}
