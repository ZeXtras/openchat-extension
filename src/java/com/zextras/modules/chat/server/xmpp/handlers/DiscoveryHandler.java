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

package com.zextras.modules.chat.server.xmpp.handlers;

import com.zextras.modules.chat.server.Target;
import com.zextras.modules.chat.server.address.SpecificAddress;
import com.zextras.modules.chat.server.db.providers.UserProvider;
import com.zextras.modules.chat.server.events.Event;
import com.zextras.modules.chat.server.events.EventDiscovery;
import com.zextras.modules.chat.server.exceptions.ChatDbException;
import com.zextras.modules.chat.server.exceptions.ChatException;
import com.zextras.modules.chat.server.operations.ChatOperation;
import com.zextras.modules.chat.server.events.EventId;
import com.zextras.modules.chat.server.session.SessionManager;
import com.zextras.modules.chat.server.xmpp.StanzaHandler;
import com.zextras.modules.chat.server.xmpp.XmppSession;
import com.zextras.modules.chat.server.xmpp.parsers.DiscoveryParser;
import com.zextras.modules.chat.server.xmpp.xml.SchemaProvider;

import javax.xml.stream.XMLStreamException;
import java.io.ByteArrayInputStream;
import java.util.Collections;
import java.util.List;

public class DiscoveryHandler implements StanzaHandler {
  private XmppSession     mSession;
  private DiscoveryParser mParser;

  public DiscoveryHandler(XmppSession session)
  {
    mSession = session;
  }

  @Override
  public List<ChatOperation> handle()
  {
    return Collections.<ChatOperation>singletonList(
      new ChatOperation()
      {
        @Override
        public List<Event> exec(SessionManager sessionManager, UserProvider userProvider) throws ChatException, ChatDbException
        {
          if (!EventDiscovery.DiscoveryQuery.isSupported(mParser.getQuery()))
          {
            return Collections.<Event>emptyList();
          }

          EventId eventId = EventId.fromString(mParser.getId());
          SpecificAddress senderAddress = mSession.getExposedAddress();
          EventDiscovery.DiscoveryQuery query = EventDiscovery.DiscoveryQuery.fromUrl(mParser.getQuery());

          if (mParser.getTarget() != null && mParser.getTarget().equals(mSession.getDomain()))
          {
            mSession.getEventQueue().queueEvent(new EventDiscovery(
              eventId,
              new SpecificAddress(mSession.getDomain()),
              new Target(senderAddress),
              "result",
              mParser.getFeatures(),
              Collections.<EventDiscovery.Result>emptyList(),
              query
            ));
            return Collections.<Event>emptyList();
          }

          if (mParser.getTarget() == null)
          {
            return Collections.<Event>emptyList();
          }

          SpecificAddress targetAddress = new SpecificAddress(mParser.getTarget());

          return Collections.<Event>singletonList(
            new EventDiscovery(
              eventId,
              senderAddress,
              new Target(targetAddress),
              mParser.getType(),
              mParser.getFeatures(),
              Collections.<EventDiscovery.Result>emptyList(),
              query
            )
          );
        }
      }
    );
  }

  @Override
  public void parse(
    ByteArrayInputStream xmlInputStream,
    SchemaProvider schemaProvider)
    throws XMLStreamException
  {
    mParser = new DiscoveryParser(xmlInputStream, schemaProvider);
    mParser.parse();
  }
}
