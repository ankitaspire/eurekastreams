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
package org.eurekastreams.server.action.execution.settings;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.eurekastreams.commons.actions.TaskHandlerExecutionStrategy;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.actions.context.TaskHandlerActionContext;
import org.eurekastreams.commons.exceptions.ExecutionException;
import org.eurekastreams.commons.server.UserActionRequest;

/**
 * Submits the refresh membership action to the queue for async processing.
 */
public class UpdateMembershipExecution implements TaskHandlerExecutionStrategy<PrincipalActionContext>
{
    /**
     * {@inheritDoc}.
     *
     * Prepare the Task request to be submitted.
     */
    @Override
    public Serializable execute(final TaskHandlerActionContext<PrincipalActionContext> inActionContext)
            throws ExecutionException
    {
        List<UserActionRequest> userActionRequests = new ArrayList<UserActionRequest>();
        userActionRequests.add(new UserActionRequest("refreshMembershipAction", null, null));
        inActionContext.getUserActionRequests().addAll(userActionRequests);
        return null;
    }

}
