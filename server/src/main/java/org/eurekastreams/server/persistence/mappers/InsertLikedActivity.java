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
package org.eurekastreams.server.persistence.mappers;

<<<<<<< HEAD
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

=======
>>>>>>> c4e10f6f59ac4a3eafeb4d29b2197373fe4cb8a9
import javax.persistence.Query;

import org.eurekastreams.server.domain.stream.LikedActivity;
import org.eurekastreams.server.persistence.mappers.cache.CacheKeys;
import org.eurekastreams.server.persistence.mappers.stream.CachedDomainMapper;

/**
 * Insert entry into LikedActivity table.
 *
 */
public class InsertLikedActivity extends CachedDomainMapper
{
    /**
     * Mapper to read liked activities.
     */
    private DomainMapper<Collection<Long>, Collection<Collection<Long>>> likedActivitiesMapper;

    /**
     * Constructor.
     *
     * @param inLikedActivitiesMapper
     *            the liked activity read mapper.
     */
    public InsertLikedActivity(
            final DomainMapper<Collection<Long>, Collection<Collection<Long>>> inLikedActivitiesMapper)
    {
        likedActivitiesMapper = inLikedActivitiesMapper;
    }

    /**
     * Inserts entry into LikedActivity table.
     *
     * @param inLikedActivity
     *            the {@link LikedActivity} to insert.
     * @return True if successful.
     */
    public Boolean execute(final LikedActivity inLikedActivity)
    {
        Query q = getEntityManager().createQuery(
                "FROM LikedActivity where personId=:personId and activityId=:activityId").setParameter("personId",
                inLikedActivity.getPersonId()).setParameter("activityId", inLikedActivity.getActivityId());

        if (q.getResultList().size() > 0)
        {
            // already liked
            return Boolean.TRUE;
        }

        this.getEntityManager().persist(inLikedActivity);

<<<<<<< HEAD
        getCache().addToTopOfList(CacheKeys.LIKED_BY_PERSON_ID + inLikedActivity.getPersonId(), inLikedActivity.getActivityId());
=======
        // sets in cache.
        getCache().addToTopOfList(
                CacheKeys.LIKED_BY_PERSON_ID + inLikedActivity.getPersonId(), inLikedActivity.getActivityId());

        getCache().addToTopOfList(
                CacheKeys.LIKERS_BY_ACTIVITY_ID + inLikedActivity.getActivityId(), inLikedActivity.getPersonId());
>>>>>>> c4e10f6f59ac4a3eafeb4d29b2197373fe4cb8a9

        return Boolean.TRUE;
    }

}
