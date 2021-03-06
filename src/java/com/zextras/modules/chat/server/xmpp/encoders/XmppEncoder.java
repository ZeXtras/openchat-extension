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

package com.zextras.modules.chat.server.xmpp.encoders;

import com.zextras.modules.chat.server.address.SpecificAddress;
import com.zextras.modules.chat.server.encoding.Encoder;
import com.ctc.wstx.stax.WstxOutputFactory;
import com.zextras.modules.chat.server.xmpp.xml.SchemaProvider;
import org.codehaus.stax2.XMLOutputFactory2;
import org.codehaus.stax2.XMLStreamProperties;
import org.codehaus.stax2.XMLStreamWriter2;
import org.codehaus.stax2.validation.XMLValidationSchema;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import java.io.OutputStream;

public abstract class XmppEncoder implements Encoder
{
  private final String            mSchemaName;
  private final SchemaProvider    mSchemaProvider;
  private final XMLOutputFactory2 mFactory;

  protected XmppEncoder(String schemaName, SchemaProvider schemaProvider)
  {
    mSchemaName = schemaName;
    mSchemaProvider = schemaProvider;
    mFactory = new WstxOutputFactory();
    mFactory.setProperty(XMLStreamProperties.XSP_NAMESPACE_AWARE,Boolean.TRUE);
    mFactory.setProperty(XMLOutputFactory.IS_REPAIRING_NAMESPACES,Boolean.TRUE);
  }

  protected XMLStreamWriter2 getStreamWriter(OutputStream outputStream) throws XMLStreamException
  {
    XMLStreamWriter2 writer = (XMLStreamWriter2) mFactory.createXMLStreamWriter(outputStream);
    return writer;
  }

  protected boolean validate()
  {
    return false;
  }

  protected XMLValidationSchema getSchema(String name)
    throws XMLStreamException
  {
    return mSchemaProvider.getSchema(name);
  }

  protected XMLValidationSchema getDefaultSchema()
    throws XMLStreamException
  {
    return getSchema(mSchemaName);
  }

  public abstract void encode(OutputStream outputStream, SpecificAddress target) throws XMLStreamException;
}
