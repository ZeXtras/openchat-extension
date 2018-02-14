package com.zextras.modules.chat.server.xmpp.decoders;

import com.zextras.lib.DateUtils;
import com.zextras.modules.chat.server.Target;
import com.zextras.modules.chat.server.address.SpecificAddress;
import com.zextras.modules.chat.server.events.Event;
import com.zextras.modules.chat.server.events.EventId;
import com.zextras.modules.chat.server.events.EventMessage;
import com.zextras.modules.chat.server.events.EventMessageHistory;
import com.zextras.modules.chat.server.events.EventMessageHistoryLast;
import com.zextras.modules.chat.server.xmpp.parsers.MessageHistoryLastParser;
import com.zextras.modules.chat.server.xmpp.parsers.MessageHistoryParser;
import com.zextras.modules.chat.server.xmpp.xml.SchemaProvider;
import org.openzal.zal.lib.FakeClock;

import javax.xml.stream.XMLStreamException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MessageHistoryLastDecoder implements EventDecoder
{
  private final SchemaProvider mSchemaProvider;

  public MessageHistoryLastDecoder(SchemaProvider schemaProvider)
  {
    mSchemaProvider = schemaProvider;
  }

  @Override
  public List<Event> decode(InputStream inputStream) throws XMLStreamException
  {
    MessageHistoryLastParser parser = new MessageHistoryLastParser(inputStream, mSchemaProvider);
    parser.parse();

    String eventId = parser.getId();
    if (eventId.isEmpty())
    {
      eventId = EventId.randomUUID().toString();
    }
    return Collections.<Event>singletonList(new EventMessageHistoryLast(
      EventId.fromString(eventId),
      new SpecificAddress(parser.getSender()),
      parser.getQueryId(),
      new SpecificAddress(parser.getTo()),
      parser.getFirst(),
      parser.getLast()
    ));
  }
}