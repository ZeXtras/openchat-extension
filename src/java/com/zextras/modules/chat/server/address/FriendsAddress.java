/*
 * ZAL - The abstraction layer for Zimbra.
 * Copyright (C) 2017 ZeXtras S.r.l.
 *
 * This file is part of ZAL.
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
 * You should have received a copy of the GNU General Public License
 * along with ZAL. If not, see <http://www.gnu.org/licenses/>.
 */

package com.zextras.modules.chat.server.address;

import com.zextras.modules.chat.server.db.providers.UserProvider;
import com.zextras.modules.chat.server.events.EventRouter;
import com.zextras.modules.chat.server.relationship.Relationship;
import com.zextras.modules.chat.server.exceptions.ChatDbException;
import com.zextras.modules.chat.server.session.SessionUUID;
import com.zextras.modules.chat.server.User;
import com.zextras.modules.chat.server.dispatch.Dispatcher;
import com.zextras.modules.chat.server.dispatch.FriendsDispatcher;

import java.util.HashSet;

public class FriendsAddress implements ChatAddress
{
  private final SpecificAddress mOrigin;

  /**
   * Creates a dispatcher that sends messages
   * to all the friends of the origin address.
   */
  public FriendsAddress(SpecificAddress origin)
  {
    mOrigin = origin;
  }

  @Override
  public Dispatcher createDispatcher(EventRouter eventRouter, UserProvider openUserProvider)
  {
    return new FriendsDispatcher(mOrigin, eventRouter, openUserProvider);
  }

  @Override
  public String resourceAddress()
  {
    return "";
  }

  @Override
  public void explode(HashSet<SpecificAddress> explodedSet, UserProvider openUserProvider) throws ChatDbException
  {
    User user = openUserProvider.getUser(mOrigin);
    for( Relationship relationship : user.getRelationships() ) {
      explodedSet.add(relationship.getBuddyAddress());
    }
  }

  @Override
  public boolean equals(Object object)
  {
    return object instanceof FriendsAddress;
  }

  @Override
  public int hashCode()
  {
    return mOrigin.hashCode();
  }

  @Override
  public ChatAddress withoutSession()
  {
    return this;
  }

  @Override
  public boolean isFromSession(SessionUUID sessionUUID)
  {
    return mOrigin.isFromSession(sessionUUID);
  }
}
