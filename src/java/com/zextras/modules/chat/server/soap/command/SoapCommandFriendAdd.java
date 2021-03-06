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

import com.zextras.modules.chat.server.address.SpecificAddress;
import com.zextras.modules.chat.server.exceptions.ChatException;
import com.zextras.modules.chat.server.exceptions.NoSuchAccountChatException;
import com.zextras.modules.chat.server.operations.AddFriend;
import com.zextras.modules.chat.server.operations.ChatOperation;
import com.zextras.modules.chat.server.exceptions.InvalidParameterException;
import com.zextras.modules.chat.server.exceptions.MissingParameterException;
import org.openzal.zal.Account;
import org.openzal.zal.Provisioning;
import org.openzal.zal.exceptions.NoSuchAccountException;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class SoapCommandFriendAdd extends SoapCommand
{
  private Provisioning mProvisioning;

  public SoapCommandFriendAdd(
    SpecificAddress senderAddress,
    Map<String, String> parameters,
    Provisioning provisioning
  )
  {
    super(senderAddress, parameters);
    mProvisioning = provisioning;
  }

  @Override
  public List<ChatOperation> createOperationList()
    throws MissingParameterException, InvalidParameterException, ChatException
  {
    String group = getTargetGroup();

    String addressToAdd = getTargetAddress().toString();
    Account account;
    try
    {
      account = mProvisioning.assertAccountByName(addressToAdd);
    }
    catch (NoSuchAccountException e)
    {
      throw new NoSuchAccountChatException(addressToAdd);
    }

    ChatOperation addFriend = new AddFriend(
      mSenderAddress,
      new SpecificAddress(account.getName()),
      getTargetUsername(),
      group,
      mProvisioning
    );

    return Arrays.<ChatOperation>asList(addFriend);
  }
}
