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

package com.zextras.modules.chat.server.xmpp.decoders;

import com.zextras.modules.chat.server.address.SpecificAddress;
import com.zextras.modules.chat.server.events.Event;
import com.zextras.modules.chat.server.events.EventId;
import com.zextras.modules.chat.server.events.EventMessageAck;
import com.zextras.modules.chat.server.xmpp.parsers.MessageAckParser;
import com.zextras.modules.chat.server.xmpp.xml.SchemaProvider;

import javax.xml.stream.XMLStreamException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

public class MessageAckDecoder implements EventDecoder
{
  private final SchemaProvider mSchemaProvider;

  public MessageAckDecoder(SchemaProvider schemaProvider) {
    mSchemaProvider = schemaProvider;
  }

  @Override
  public List<Event> decode(InputStream inputStream) throws XMLStreamException
  {
    MessageAckParser parser = new MessageAckParser(inputStream, mSchemaProvider);
    parser.parse();

    SpecificAddress sender = new SpecificAddress(parser.getFrom());
    SpecificAddress target = new SpecificAddress(parser.getTo());
    EventId messageId = EventId.fromString(parser.getMessageId());

    Event event = new EventMessageAck(
      sender,
      target,
      messageId,
      parser.getTimestamp()
    );

    return Arrays.asList(event);
  }
}
