/*
 * Copyright (c) 2010 Lockheed Martin Corporation
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
package org.eurekastreams.web.client.model;

import java.util.ArrayList;

import org.eurekastreams.server.search.modelview.PersonModelView;
import org.eurekastreams.web.client.events.data.GotActivityLikersResponseEvent;
import org.eurekastreams.web.client.ui.Session;

/**
 * Model to get everyone who liked an activity.
 *
 */
public class ActivityLikersModel extends BaseModel implements Fetchable<Long>
{
    /**
     * Singleton.
     */
    private static ActivityLikeModel model = new ActivityLikeModel();

    /**
     * Gets the singleton.
     *
     * @return the singleton.
     */
    public static ActivityLikeModel getInstance()
    {
        return model;
    }

    /**
     * {@inheritDoc}
     */
    public void fetch(final Long request, final boolean useClientCacheIfAvailable)
    {
        super.callReadAction("getActivityLikers", request, new OnSuccessCommand<ArrayList<PersonModelView>>()
        {
            public void onSuccess(final ArrayList<PersonModelView> response)
            {
                Session.getInstance().getEventBus().notifyObservers(new GotActivityLikersResponseEvent(response));
            }
        }, useClientCacheIfAvailable);

    }

}
