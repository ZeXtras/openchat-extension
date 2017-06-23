/*
 * Copyright (C) 2017 ZeXtras S.r.l.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation, version 2 of
 * the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License.
 * If not, see <http://www.gnu.org/licenses/>.
 */

package com.zextras.modules.chat.server.xmpp;

import com.zextras.lib.log.ChatLog;
import io.netty.channel.Channel;
import org.openzal.zal.lib.Filter;
import com.zextras.modules.chat.server.filters.EventFilter;
import com.zextras.modules.chat.server.session.BaseSession;
import com.zextras.modules.chat.server.events.EventQueue;
import com.zextras.modules.chat.server.session.SessionUUID;
import com.zextras.modules.chat.server.User;
import com.zextras.modules.chat.server.address.SpecificAddress;
import com.zextras.modules.chat.server.events.Event;

import java.nio.channels.SocketChannel;
import java.util.Collection;
import java.util.Collections;


public class XmppSession extends BaseSession
{
  private final Filter<Event> mOutFiler;
  private final Channel       mChannel;
  private       boolean       mUsingSSL;
  private String mDomain = "";
  private boolean     mFirstPresence;
  private EventFilter mFilter;
  private boolean mIsProxy = false;

  public XmppSession(
    SessionUUID id,
    EventQueue eventQueue,
    Channel channel,
    User user,
    SpecificAddress mainAddress,
    XmppEventFilter xmppEventFilter,
    Filter<Event> outFilter
  )
  {
    super(id, eventQueue, user, mainAddress);
    mChannel = channel;
    mUsingSSL = false;
    mFirstPresence = true;
    mFilter = xmppEventFilter;
    mOutFiler = outFilter;
  }

  @Override
  public EventFilter getEventFilter()
  {
    return mFilter;
  }

  @Override
  public void refuseInputEvents()
  {
    ChatLog.log.info("Ignoring new commands from " + getUser().getAddress());
    mChannel.config().setAutoRead(false);
  }

  @Override
  public void acceptInputEvents()
  {
    ChatLog.log.info("Accepting new commands from " + getUser().getAddress());
    mChannel.config().setAutoRead(true);
  }

  @Override
  public void renew(long newExpireTime)
  {
  }

  @Override
  public boolean isExpired(long now)
  {
    return false;
  }

  @Override
  public Filter<Event> getOutFilter()
  {
    return mOutFiler;
  }

  public Collection<XmppAuthentication> getAvailableAuthentications()
  {
    return Collections.emptyList();
  }

  public boolean isBindable() {
    return true;
  }

  public String getDomain()
  {
    return mDomain;
  }

  public void setDomain(String domain) {
    mDomain = domain;
  }

  public void setUsingSSL(boolean ssl)
  {
    mUsingSSL = ssl;
  }

  public boolean isUsingSSL()
  {
    return mUsingSSL;
  }

  public boolean isFirstPresence()
  {
    return mFirstPresence;
  }

  public void setFirstPresence(boolean firstPresence)
  {
    mFirstPresence = firstPresence;
  }

  public void setIsProxy(boolean isProxy)
  {
    mIsProxy = isProxy;
  }

  public Channel getChannel()
  {
    return mChannel;
  }

  /*
    * This is true for proxy server and account remote host
    */
  public boolean isProxy()
  {
    return mIsProxy;
  }
}
