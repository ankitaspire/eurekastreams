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
package org.eurekastreams.web.client.ui.common.stream.renderers;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import org.eurekastreams.server.action.request.stream.SetActivityLikeRequest.LikeActionType;
import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.eurekastreams.web.client.events.ActivityLikedChangeEvent;
import org.eurekastreams.web.client.events.EventBus;
import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.jsni.EffectsFacade;
import org.eurekastreams.web.client.model.LikeResourceModel;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.TimerFactory;
import org.eurekastreams.web.client.ui.TimerHandler;
import org.eurekastreams.web.client.ui.common.avatar.AvatarLinkPanel;
import org.eurekastreams.web.client.ui.common.avatar.AvatarWidget.Size;
import org.eurekastreams.web.client.ui.common.dialog.Dialog;
import org.eurekastreams.web.client.ui.common.dialog.DialogContent;
import org.eurekastreams.web.client.ui.pages.master.StaticResourceBundle;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Like count widget.
 */
public class ResourceCountWidget extends Composite
{
    /**
     * Main widget.
     */
    private final FocusPanel widget = new FocusPanel();

    /**
     * Shows users who have liked this.
     */
    private static FlowPanel usersWhoLikedPanel = new FlowPanel();

    /**
     * Shows users who have liked this.
     */
    private static FlowPanel usersWhoLikedPanelWrapper;

    /**
     * Added to root panel.
     */
    private static boolean hasUsersWhoLikedBeenAddedToRoot = false;

    /**
     * Like count.
     */
    private Integer likeCount = 0;

    /**
     * Link for the like count link.
     */
    final Anchor likeCountLink = new Anchor();

    /**
     * Liked label.
     */
    private static Label likedLabel = new Label();

    /**
     * Avatar panel.
     */
    private static FlowPanel avatarPanel = new FlowPanel();

    /**
     * Likers.
     */
    private final List<PersonModelView> likers;

    /**
     * Liker overflow.
     */
    private PersonModelView likerOverflow;

    /**
     * Resource ID.
     */
    private final String thisResourceUrl;

    /**
     * Liked status.
     */
    private Boolean thisIsLiked;

    /**
     * The current panel.
     */
    private static ResourceCountWidget currentPanel;

    /**
     * Link for the like count link.
     */
    private static Anchor innerLikeCountLink = new Anchor();
    /**
     * The view all link.
     */
    private static Anchor viewAll = new Anchor("view all");

    /**
     * Current resource being shown.
     */
    private static String currentResourceUrl = "";

    /**
     * Current activity like status..
     */
    private static Boolean isLiked;

    /**
     * The body.
     */
    private static FlowPanel userLikedBody = new FlowPanel();
    /**
     * How many do we show before the view all link shows up.
     */
    private static final int MAXLIKERSSHOWN = 4;

    private Widget containerWidget = null;

    /**
     * Count Type.
     */
    public enum CountType
    {
        LIKES, SHARES
    }

    /**
     * Setup the floating avatar panel.
     */
    private static void setup()
    {
        // Reimplementing Focus panel, GWT seems to break otherwise.
        usersWhoLikedPanelWrapper = new FlowPanel()
        {
            private final TimerFactory timerFactory = new TimerFactory();
            private boolean actuallyOut = false;
            private static final int TIMER_EXPIRATION = 250;

            @Override
            public void onBrowserEvent(final Event event)
            {
                super.onBrowserEvent(event);

                if (DOM.eventGetType(event) == Event.ONMOUSEOUT)
                {
                    actuallyOut = true;

                    timerFactory.runTimer(TIMER_EXPIRATION, new TimerHandler()
                    {
                        public void run()
                        {
                            if (actuallyOut)
                            {
                                usersWhoLikedPanelWrapper.setVisible(false);
                            }

                        }
                    });
                }
                else if (DOM.eventGetType(event) == Event.ONMOUSEOVER)
                {
                    actuallyOut = false;
                }
                else if (DOM.eventGetType(event) == Event.ONCLICK)
                {
                    usersWhoLikedPanelWrapper.setVisible(false);
                }
            }
        };

        final Label arrow = new Label();
        arrow.addStyleName(StaticResourceBundle.INSTANCE.coreCss().eurekaConnectPopoutArrow());

        usersWhoLikedPanelWrapper.add(arrow);

        viewAll.setVisible(false);
        viewAll.addClickHandler(new ClickHandler()
        {
            public void onClick(final ClickEvent arg0)
            {
                DialogContent dialogContent = new LikersDialogContent(0L);
                Dialog dialog = new Dialog(dialogContent);
                dialog.setBgVisible(true);
                dialog.addStyleName(StaticResourceBundle.INSTANCE.coreCss().likerModal());
                dialog.center();
            }

        });

        usersWhoLikedPanelWrapper.setVisible(false);
        usersWhoLikedPanelWrapper.addStyleName(StaticResourceBundle.INSTANCE.coreCss().usersWhoLikedActivityWrapper());
        usersWhoLikedPanelWrapper.addStyleName(StaticResourceBundle.INSTANCE.coreCss()
                .eurekaConnectLikedActivityWrapper());
        usersWhoLikedPanelWrapper.addStyleName(StaticResourceBundle.INSTANCE.coreCss().likeCountWidget());
        RootPanel.get().add(usersWhoLikedPanelWrapper);

        usersWhoLikedPanelWrapper.add(usersWhoLikedPanel);
        usersWhoLikedPanel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().usersWhoLikedActivity());
        usersWhoLikedPanel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().eurekaConnectUsersWhoLikedActivity());
        usersWhoLikedPanel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().eurekaConnectLikedActivity());

        FlowPanel userLikedHeader = new FlowPanel();
        userLikedHeader.addStyleName(StaticResourceBundle.INSTANCE.coreCss().usersWhoLikedActivityHeader());
        usersWhoLikedPanel.add(userLikedHeader);

        userLikedBody.addStyleName(StaticResourceBundle.INSTANCE.coreCss().usersWhoLikedActivityBody());
        usersWhoLikedPanel.add(userLikedBody);

        FlowPanel userLikedFooter = new FlowPanel();
        userLikedFooter.addStyleName(StaticResourceBundle.INSTANCE.coreCss().usersWhoLikedActivityFooter());
        usersWhoLikedPanel.add(userLikedFooter);

        userLikedBody.add(likedLabel);
        userLikedBody.add(avatarPanel);
        userLikedBody.add(viewAll);
        usersWhoLikedPanelWrapper.sinkEvents(Event.ONMOUSEOUT | Event.ONMOUSEOVER | Event.ONCLICK);

        hasUsersWhoLikedBeenAddedToRoot = true;
    }

    /**
     * Constructor.
     * 
     * @param inCountType
     *            the count type.
     * @param inResoureceUrl
     *            activity url.
     * @param thumbs
     *            thumbnails.
     * @param desc
     *            description.
     * @param title
     *            title.
     * @param inLikeCount
     *            like count.
     * @param inLikers
     *            who has liked this activity.
     * @param inIsLiked
     *            if the person has liked the current activity.
     */
    public ResourceCountWidget(final CountType inCountType, final String inResoureceUrl, final String title,
            final String desc, final String[] thumbs, final Integer inLikeCount, final List<PersonModelView> inLikers,
            final Boolean inIsLiked)
    {
        thisResourceUrl = inResoureceUrl;
        thisIsLiked = inIsLiked;

        initWidget(widget);

        if (!hasUsersWhoLikedBeenAddedToRoot)
        {
            setup();
        }

        widget.addMouseOverHandler(new MouseOverHandler()
        {
            public void onMouseOver(final MouseOverEvent arg0)
            {
                showPanel();

                usersWhoLikedPanelWrapper.setVisible(true);
            }
        });

        likers = inLikers;
        likeCount = inLikeCount;

        likeCountLink.addStyleName(StaticResourceBundle.INSTANCE.coreCss().eurekaConnectCount());

        final FlowPanel likeCountPanel = new FlowPanel();

        if (inCountType.equals(CountType.LIKES))
        {
            FlowPanel likeContainer = new FlowPanel();
            likeContainer.addStyleName(StaticResourceBundle.INSTANCE.coreCss().eurekaConnectLikeButton());

            if (thisIsLiked)
            {
                likeContainer.addStyleName(StaticResourceBundle.INSTANCE.coreCss().eurekaConnectUnlikeButton());
            }
            else
            {
                likeContainer.addStyleName(StaticResourceBundle.INSTANCE.coreCss().eurekaConnectLikeButton());
            }

            Anchor likeLink = new Anchor();

            likeLink.addClickHandler(new ClickHandler()
            {

                public void onClick(ClickEvent arg0)
                {
                    thisIsLiked = !thisIsLiked;
                    final HashMap<String, Serializable> request = new HashMap<String, Serializable>();
                    request.put("resourceurl", inResoureceUrl);
                    request.put("liked", thisIsLiked);

                    LikeResourceModel.getInstance().insert(request);
                }
            });

            likeContainer.add(likeLink);
            likeContainer.add(likeCountLink);
            likeCountPanel.add(likeContainer);
            containerWidget = likeContainer;
        }
        else
        {
            FlowPanel shareContainer = new FlowPanel();
            shareContainer.addStyleName(StaticResourceBundle.INSTANCE.coreCss().eurekaConnectShareButton());

            Anchor shareLink = new Anchor();
            shareLink.addClickHandler(new ClickHandler()
            {
                public void onClick(ClickEvent arg0)
                {
                    String thumbsStr = "";

                    for (String thumb : thumbs)
                    {
                        if (thumbsStr.length() > 0)
                        {
                            thumbsStr += ",";
                        }
                        thumbsStr += thumb;
                    }

                    Window.open(
                            "/widget.html?widget=sharedialog&width=300&thumbs=http://www.google.com/images/logos/ps_logo2.png&desc="
                                    + desc + "&title=" + title + "&resourceurl=" + inResoureceUrl, "_,blank",
                            "width=600,height=400");
                }
            });

            shareContainer.add(shareLink);
            shareContainer.add(likeCountLink);
            likeCountPanel.add(shareContainer);
            containerWidget = shareContainer;
        }

        widget.add(likeCountPanel);

        EventBus.getInstance().addObserver(ActivityLikedChangeEvent.class, new Observer<ActivityLikedChangeEvent>()
        {
            public void update(final ActivityLikedChangeEvent event)
            {
                updatePanel(event.getActionType());
                currentPanel.showPanel();
            }
        });

        updatePanel(null);
    }

    /**
     * Called to update the panel.
     */
    private void showPanel()
    {
        currentPanel = this;
        isLiked = thisIsLiked;
        currentResourceUrl = thisResourceUrl;
        viewAll.setVisible(false);
        avatarPanel.clear();
        DOM.setStyleAttribute(usersWhoLikedPanelWrapper.getElement(), "top", widget.getAbsoluteTop() + 10 + "px");
        DOM.setStyleAttribute(usersWhoLikedPanelWrapper.getElement(), "left", widget.getAbsoluteLeft()
                + containerWidget.getElement().getClientWidth() + "px");

        for (PersonModelView liker : likers)
        {
            avatarPanel.add(new AvatarLinkPanel(EntityType.PERSON, liker.getUniqueId(), liker.getId(), liker
                    .getAvatarId(), Size.VerySmall, liker.getDisplayName()));
        }

        if (likeCount > MAXLIKERSSHOWN)
        {
            viewAll.setVisible(true);
        }
        likedLabel.setText(likeCount + " people liked this");
        innerLikeCountLink.setText(likeCount.toString());
    }

    /**
     * Update the panel.
     * 
     * @param likeActionType
     *            the action that's being taken.
     */
    private void updatePanel(final LikeActionType likeActionType)
    {
        if (null != likeActionType)
        {
            thisIsLiked = !thisIsLiked;
            if (likeActionType == LikeActionType.ADD_LIKE)
            {
                PersonModelView currentPerson = new PersonModelView();
                currentPerson.setEntityId(Session.getInstance().getCurrentPerson().getId());
                currentPerson.setAvatarId(Session.getInstance().getCurrentPerson().getAvatarId());
                currentPerson.setDisplayName(Session.getInstance().getCurrentPerson().getDisplayName());
                currentPerson.setAccountId(Session.getInstance().getCurrentPerson().getAccountId());

                if (likers.size() >= MAXLIKERSSHOWN)
                {
                    likerOverflow = likers.get(MAXLIKERSSHOWN - 1);
                    likers.remove(MAXLIKERSSHOWN - 1);
                }

                likers.add(0, currentPerson);

                likeCount++;

                if (!widget.isVisible())
                {
                    EffectsFacade.nativeFadeIn(widget.getElement(), true);
                }
            }
            else
            {
                for (PersonModelView liker : likers)
                {
                    if (likeActionType == LikeActionType.REMOVE_LIKE
                            && liker.getId() == Session.getInstance().getCurrentPerson().getId())
                    {
                        likers.remove(liker);

                        if (likerOverflow != null)
                        {
                            likers.add(9, likerOverflow);
                        }
                    }
                }

                likeCount--;
            }
        }

        likeCountLink.setText(likeCount.toString());
    }
}
