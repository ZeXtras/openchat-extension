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

package com.zextras.modules.chat.server.soap.command;

import com.zextras.modules.chat.properties.ChatProperties;
import com.zextras.lib.Error.DelegatedOrResourcesNotAllowedToChatError;
import com.zextras.modules.chat.server.operations.ChatOperation;
import com.zextras.modules.chat.server.operations.NotifyFriendsStatus;
import com.zextras.modules.chat.server.session.SessionUUID;
import com.zextras.modules.chat.server.address.SpecificAddress;
import com.zextras.modules.chat.server.events.EventId;
import com.zextras.modules.chat.server.operations.*;
import com.zextras.modules.chat.server.soap.SoapSessionFactory;
import com.zextras.modules.chat.server.soap.encoders.SoapEncoderFactory;
import com.zextras.modules.chat.server.soap.encoders.SoapEncoderFactoryImpl;
import com.zextras.modules.chat.server.exceptions.InvalidParameterException;
import com.zextras.modules.chat.server.exceptions.MissingParameterException;
import org.openzal.zal.Account;
import org.openzal.zal.Provisioning;
import org.openzal.zal.exceptions.ZimbraException;
import org.openzal.zal.soap.SoapResponse;
import org.openzal.zal.soap.ZimbraContext;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class SoapCommandRegister extends SoapCommand
{
  private final SoapResponse       mSoapResponse;
  private final SoapEncoderFactory mSoapEncoderFactory;
  private final ChatProperties     mChatProperties;
  private       SoapSessionFactory mSoapSessionFactory;
  private final Provisioning       mProvisioning;
  private final ZimbraContext      mZimbraContext;

  public SoapCommandRegister(
    SoapResponse soapResponse,
    SoapEncoderFactory soapEncoderFactory,
    SpecificAddress senderAddress,
    Map<String, String> parameters,
    SoapSessionFactory soapSessionFactory,
    Provisioning provisioning,
    ZimbraContext zimbraContext,
    ChatProperties chatProperties)
  {
    super(
      senderAddress,
      parameters
    );
    mSoapResponse = soapResponse;
    mSoapEncoderFactory = soapEncoderFactory;
    mSoapSessionFactory = soapSessionFactory;
    mProvisioning = provisioning;
    mZimbraContext = zimbraContext;
    mChatProperties = chatProperties;
  }

  public ChatOperation createRegisterSoapSession(
    SessionUUID newSessionUUID,
    String clientVersion,
    boolean silentErrorReportingEnabled
  )
  {
    return new RegisterSoapSession(
      newSessionUUID,
      mSoapResponse,
      mSoapEncoderFactory,
      mSenderAddress,
      mSoapSessionFactory,
      clientVersion,
      silentErrorReportingEnabled
    );
  }

  public ChatOperation createSetStatusLoginOrLogout(SessionUUID newSessionUUID)
  {
    return new SetStatusOnLoginOrLogout(
      newSessionUUID
    );
  }

  public ChatOperation createGetRelationships(SessionUUID newSessionUUID)
  {
    return new GetRelationships(
      EventId.randomUUID(),
      mSenderAddress,
      newSessionUUID
    );
  }

  public ChatOperation createNotifyFriendsStatus()
  {
    return new NotifyFriendsStatus(
      mSenderAddress
    );
  }

  public boolean isChatAllowed(String accountId) throws InvalidParameterException
  {
    try
    {
      Account account = mProvisioning.getAccountById(accountId);

      if (mZimbraContext.isDelegatedAuth() || account == null || account.isCalendarResource() || account.isIsSystemResource())
      {
        mSoapResponse.setValue("session_id", "");
        mSoapResponse.setValue("error", new DelegatedOrResourcesNotAllowedToChatError(mChatProperties.getProductName()).toJSON().toString());
        return false;
      }
    } catch (ZimbraException e) {
      throw new InvalidParameterException("The account " + accountId + " doesn't exists.");
    }

    return true;
  }

  @Override
  public List<ChatOperation> createOperationList()
    throws MissingParameterException, InvalidParameterException
  {
    // Check if the request comes from a delegated admin or it's a resource
    String accountId = mZimbraContext.getTargetAccountId();

    if (!isChatAllowed(accountId))
    {
      return Collections.emptyList();
    }

    boolean silentErrorReportingEnabled = mChatProperties.isSilentErrorReportingEnabled();

    String clientVersion = mParameterMap.get("clientVersion");
    if( clientVersion == null ) {
      clientVersion = "";
    }

    final SessionUUID newSessionUUID = SessionUUID.randomUUID();

    ChatOperation registerSoapSession = createRegisterSoapSession(
      newSessionUUID,
      clientVersion,
      silentErrorReportingEnabled
    );

    ChatOperation setStatus = createSetStatusLoginOrLogout(
      newSessionUUID
    );

    ChatOperation sendRelationships = createGetRelationships(newSessionUUID);

    ChatOperation writeUserStatus = createNotifyFriendsStatus();

    return Arrays.<ChatOperation>asList(
      registerSoapSession,
      //sendStatues,
      setStatus,
      sendRelationships,
      writeUserStatus
    );
  }
}
