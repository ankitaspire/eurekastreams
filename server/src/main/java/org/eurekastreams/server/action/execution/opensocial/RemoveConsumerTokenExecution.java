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
package org.eurekastreams.server.action.execution.opensocial;

import java.io.Serializable;

import org.apache.shindig.auth.SecurityToken;
import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.exceptions.ExecutionException;
import org.eurekastreams.server.action.request.opensocial.RemoveConsumerTokenRequest;
import org.eurekastreams.server.domain.OAuthConsumer;
import org.eurekastreams.server.persistence.OAuthConsumerMapper;
import org.eurekastreams.server.persistence.OAuthTokenMapper;

/**
 * Execution Strategy to Remove an OAuth consumer Token during proxied requests to oauth providers.
 * 
 */
public class RemoveConsumerTokenExecution implements ExecutionStrategy<PrincipalActionContext>
{
    /**
     * Instance of OAuth consumer mapper injected by spring.
     */
    private final OAuthConsumerMapper consumerMapper;

    /**
     * Instance of OAuth token mapper injected by spring.
     */
    private final OAuthTokenMapper tokenMapper;

    /**
     * Constructor.
     * 
     * @param inConsumerMapper
     *            instance of the {@link OAuthConsumerMapper} class.
     * @param inTokenMapper
     *            instance of the {@link OAuthTokenMapper} class.
     */
    public RemoveConsumerTokenExecution(final OAuthConsumerMapper inConsumerMapper,
            final OAuthTokenMapper inTokenMapper)
    {
        consumerMapper = inConsumerMapper;
        tokenMapper = inTokenMapper;
    }

    /**
     * {@inheritDoc}. Remove the OAuth Token based on the supplied token.
     */
    @Override
    public Serializable execute(final PrincipalActionContext inActionContext) throws ExecutionException
    {
        RemoveConsumerTokenRequest request = (RemoveConsumerTokenRequest) inActionContext.getParams();
        boolean isNullConsumer = true;

        try
        {
            SecurityToken securityToken = request.getSecurityToken();
            OAuthConsumer consumer = consumerMapper.findConsumerByServiceNameAndGadgetUrl(request.getServiceName(),
                    securityToken.getAppUrl());
            if (consumer != null)
            {
                isNullConsumer = false;
                tokenMapper.delete(consumer, securityToken.getViewerId(), securityToken.getOwnerId());
            }
        }
        catch (Exception ex)
        {
            throw new ExecutionException("An error occurred removing the consumer token.");
        }

        if (isNullConsumer)
        {
            throw new ExecutionException("OAuth Consumer was not found.");
        }

        return null;
    }
}
