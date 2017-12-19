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

package com.zextras.modules.chat.server.events;

import com.zextras.modules.chat.server.exceptions.ChatDbException;
import com.zextras.modules.chat.server.exceptions.ChatException;
import com.zextras.modules.chat.server.session.SessionUUID;
import com.zextras.modules.chat.server.Target;
import com.zextras.modules.chat.server.address.SpecificAddress;
import org.openzal.zal.lib.Version;

public class EventSoapSessionRegistered extends Event
{
  private final SessionUUID     mSessionId;
  private final Version         mClientVersion;
  private final boolean         mSilentErrorReportingEnabled;
  private final boolean         mIsHistoryEnabled;

  public EventSoapSessionRegistered(
    SpecificAddress senderAddress,
    SessionUUID sessionId,
    Version clientVersion,
    boolean silentErrorReportingEnabled,
    boolean isHistoryEnabled
  )
  {
    super(senderAddress, new Target());
    mSessionId = sessionId;
    mClientVersion = clientVersion;
    mSilentErrorReportingEnabled = silentErrorReportingEnabled;
    mIsHistoryEnabled = isHistoryEnabled;
  }

  public SessionUUID getSessionId()
  {
    return mSessionId;
  }

  public Version getClientVersion()
  {
    return mClientVersion;
  }

  public boolean isHistoryEnabled()
  {
    return mIsHistoryEnabled;
  }

  public boolean isSilentErrorReportingEnabled()
  {
    return mSilentErrorReportingEnabled;
  }

  @Override
  public <T> T interpret(EventInterpreter<T> interpreter) throws ChatException
  {
    return interpreter.interpret(this);
  }
}