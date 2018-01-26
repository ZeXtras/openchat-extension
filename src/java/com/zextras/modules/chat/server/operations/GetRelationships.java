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

package com.zextras.modules.chat.server.operations;

import com.zextras.modules.chat.server.*;
import com.zextras.modules.chat.server.address.SpecificAddress;
import com.zextras.modules.chat.server.db.providers.UserProvider;
import com.zextras.modules.chat.server.events.Event;
import com.zextras.modules.chat.server.events.EventGetRelationships;
import com.zextras.modules.chat.server.events.EventId;
import com.zextras.modules.chat.server.exceptions.ChatDbException;
import com.zextras.modules.chat.server.exceptions.ChatException;
import com.zextras.modules.chat.server.relationship.Relationship;
import com.zextras.modules.chat.server.session.Session;
import com.zextras.modules.chat.server.session.SessionManager;
import com.zextras.modules.chat.server.session.SessionUUID;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class GetRelationships implements ChatOperation
{
  private final EventId         mEventId;
  private final SpecificAddress mSender;
  private final SessionUUID mSessionId;

  public GetRelationships(
    EventId eventId,
    SpecificAddress sender,
    SessionUUID sessionId
  )
  {
    mEventId = eventId;
    mSender = sender;
    mSessionId = sessionId;
  }

  @Override
  public List<Event> exec(SessionManager sessionManager, UserProvider userProvider) throws ChatException, ChatDbException
  {
    Session session = sessionManager.getSessionById(mSessionId);

    User user = session.getUser();
    Collection<Relationship> relationships = user.getRelationships();

    Event event = new EventGetRelationships(
      mEventId,
      mSender,
      new Target(),
      relationships
    );
    session.getEventQueue().queueEvent(event);

    return Collections.emptyList();
  }
}
