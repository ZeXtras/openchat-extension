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
import com.zextras.modules.chat.server.events.EventId;
import com.zextras.modules.chat.server.exceptions.InvalidParameterException;
import com.zextras.modules.chat.server.exceptions.MissingParameterException;
import com.zextras.modules.chat.server.operations.ChatOperation;
import com.zextras.modules.chat.server.operations.QueryArchiveFactory;
import com.zextras.modules.chat.server.response.ChatSoapResponse;
import com.zextras.modules.chat.server.soap.SoapEncoder;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.openzal.zal.soap.SoapResponse;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class SoapCommandQueryArchive extends SoapCommand
{
  private final QueryArchiveFactory mQueryArchiveFactory;
  private final SoapResponse mSoapResponse;

  public SoapCommandQueryArchive(
    QueryArchiveFactory queryArchiveFactory,
    SoapResponse soapResponse,
    SpecificAddress senderAddress,
    Map<String, String> parameters
  )
  {
    super(senderAddress, parameters);
    mQueryArchiveFactory = queryArchiveFactory;
    mSoapResponse = soapResponse;
  }

  @Override
  public List<ChatOperation> createOperationList()
    throws MissingParameterException, InvalidParameterException
  {
    final String node = StringUtils.defaultString(mParameterMap.get(NODE));
    final String start = StringUtils.defaultString(mParameterMap.get(START));
    final String end = StringUtils.defaultString(mParameterMap.get(END));
    final String with = StringUtils.defaultString(mParameterMap.get(WITH));
    final long max = NumberUtils.toLong(mParameterMap.get(MAX),Long.MAX_VALUE);
    final EventId queryId = EventId.randomUUID();

    ChatOperation sendMessage = mQueryArchiveFactory.create(
      mSenderAddress,
      queryId.toString(),
      with,
      start,
      end,
      node,
      max
    );

    ChatSoapResponse response = new ChatSoapResponse();
    SoapEncoder soapEncoder = new SoapEncoder() {
      @Override
      public void encode(ChatSoapResponse response, SpecificAddress target) {
        JSONObject message = new JSONObject();
        message.put("query_id",queryId.toString());
        response.addResponse(message);
      }
    };
    soapEncoder.encode(response,mSenderAddress);
    response.encodeInSoapResponse(mSoapResponse);

    return Arrays.<ChatOperation>asList(sendMessage);
  }
}
