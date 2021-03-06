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

package com.zextras.modules.chat.server.operations;

import com.zextras.modules.chat.server.*;
import com.zextras.modules.chat.server.address.SpecificAddress;
import com.zextras.modules.chat.server.db.providers.UserProvider;
import com.zextras.modules.chat.server.events.Event;
import com.zextras.modules.chat.server.events.EventQueue;
import com.zextras.modules.chat.server.events.EventSoapSessionRegistred;
import com.zextras.modules.chat.server.exceptions.ChatDbException;
import com.zextras.modules.chat.server.exceptions.ChatException;
import com.zextras.modules.chat.server.response.ChatSoapResponse;
import com.zextras.modules.chat.server.session.SessionManager;
import com.zextras.modules.chat.server.session.SessionUUID;
import com.zextras.modules.chat.server.soap.SoapEncoder;
import com.zextras.modules.chat.server.soap.SoapSession;
import com.zextras.modules.chat.server.soap.SoapSessionFactory;
import com.zextras.modules.chat.server.soap.encoders.SoapEncoderFactory;
import com.zextras.modules.chat.server.soap.encoders.SoapEncoderFactoryImpl;
import org.openzal.zal.lib.Version;
import org.openzal.zal.soap.SoapResponse;

import java.util.Arrays;
import java.util.List;

public class RegisterSoapSession implements ChatOperation
{
  private       SoapSessionFactory mSoapSessionFactory;
  private final String             mClientVersion;
  private final boolean            mSilentErrorReportingEnabled;
  private final SessionUUID        mNewSessionId;
  private final SoapResponse       mSoapResponse;
  private final SoapEncoderFactory mSoapEncoderFactory;
  private final SpecificAddress    mSenderAddress;

  public RegisterSoapSession(
    SessionUUID newSessionId,
    SoapResponse soapResponse,
    SoapEncoderFactory soapEncoderFactory,
    SpecificAddress senderAddress,
    SoapSessionFactory soapSessionFactory,
    String clientVersion,
    boolean silentErrorReportingEnabled
  )
  {
    mNewSessionId = newSessionId;
    mSoapResponse = soapResponse;
    mSoapEncoderFactory = soapEncoderFactory;
    mSenderAddress = senderAddress;
    mSoapSessionFactory = soapSessionFactory;
    mClientVersion = clientVersion;
    mSilentErrorReportingEnabled = silentErrorReportingEnabled;
  }

  public SessionUUID getNewSessionId()
  {
    return mNewSessionId;
  }

  @Override
  public List<Event> exec(SessionManager sessionManager, UserProvider userProvider)
    throws ChatException, ChatDbException
  {
    final Version version;
    if (mClientVersion.isEmpty())
    {
      version = new Version(0);
    }
    else
    {
      version = new Version(mClientVersion);
    }

    final User user = userProvider.getUser(mSenderAddress);
    SoapSession newSession = mSoapSessionFactory.create(
      mNewSessionId,
      new EventQueue(),
      user,
      mSenderAddress,
      version
    );

    sessionManager.addSession(newSession);

    Event event = new EventSoapSessionRegistred(
      mSenderAddress,
      newSession.getId(),
      version,
      mSilentErrorReportingEnabled,
      true
    );

    ChatSoapResponse response = new ChatSoapResponse();
    SoapEncoder soapEncoder = (SoapEncoder)event.interpret(mSoapEncoderFactory);
    soapEncoder.encode(response,mSenderAddress);
    response.encodeInSoapResponse(mSoapResponse);

    return Arrays.asList();
  }
}
