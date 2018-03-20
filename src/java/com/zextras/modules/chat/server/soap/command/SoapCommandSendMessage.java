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

package com.zextras.modules.chat.server.soap.command;

import com.zextras.lib.json.JSONObject;
import com.zextras.modules.chat.server.address.SpecificAddress;
import com.zextras.modules.chat.server.events.Event;
import com.zextras.modules.chat.server.events.EventId;
import com.zextras.modules.chat.server.operations.ChatOperation;
import com.zextras.modules.chat.server.exceptions.InvalidParameterException;
import com.zextras.modules.chat.server.exceptions.MissingParameterException;
import com.zextras.modules.chat.server.operations.SendMessage;
import com.zextras.modules.chat.server.operations.SendMessageAck;
import com.zextras.modules.chat.server.response.ChatSoapResponse;
import com.zextras.modules.chat.server.session.SessionUUID;
import com.zextras.modules.chat.server.soap.SoapEncoder;
import org.openzal.zal.lib.Clock;
import org.openzal.zal.soap.SoapResponse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class SoapCommandSendMessage extends SoapCommand
{
  private final SoapResponse mSoapResponse;
  private final Clock mClock;

  public SoapCommandSendMessage(
    SoapResponse soapResponse,
    SpecificAddress senderAddress,
    Map<String, String> parameters,
    Clock clock
  )
  {
    super(senderAddress, parameters);
    mSoapResponse = soapResponse;
    mClock = clock;
  }

  @Override
  public List<ChatOperation> createOperationList()
    throws MissingParameterException, InvalidParameterException
  {
    final String message = mParameterMap.get(MESSAGE);
    SessionUUID sessionUUID = SessionUUID.fromString(mParameterMap.get(SESSION_ID));

    if (message == null)
    {
      throw new MissingParameterException("Missing parameters to create " + getClass().getName());
    }

    final EventId messageId = EventId.randomUUID();
    final long timestamp = mClock.now();

    List<ChatOperation> operations = new ArrayList<ChatOperation>(2);
    operations.add(new SendMessage(
      messageId,
      mSenderAddress,
      getTargetAddress(),
      message,
      timestamp
    ));

    ChatSoapResponse response = new ChatSoapResponse();
    SoapEncoder soapEncoder = new SoapEncoder() {
      @Override
      public void encode(ChatSoapResponse response, SpecificAddress target) {
        JSONObject message = new JSONObject();
        message.put("message_id",messageId.toString());
        message.put("message_date",timestamp);
        response.addResponse(message);
      }
    };
    soapEncoder.encode(response,mSenderAddress);
    response.encodeInSoapResponse(mSoapResponse);

    return operations;
  }
}
