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

package com.zextras.modules.chat.server.events;

//import com.zextras.annotations.VisibleForTesting;
import org.openzal.zal.lib.Clock;
import com.zextras.modules.chat.server.Target;
import com.zextras.modules.chat.server.address.SpecificAddress;
import com.zextras.modules.chat.server.encoding.Encoder;
import com.zextras.modules.chat.server.encoding.EncoderFactory;
import com.zextras.modules.chat.server.interceptors.EventInterceptor;

public class EventMessage extends Event
{
  private final SpecificAddress mSender;
  private final String mMessage;

  public EventMessage(
    EventId eventId,
    SpecificAddress sender,
    Target target,
    String message
  )
  {
    super(eventId, sender, target);
    mSender = sender;
    mMessage = message;
  }

  //@VisibleForTesting
  public EventMessage(
    EventId eventId,
    SpecificAddress sender,
    Target target,
    String message,
    Clock clock
  )
  {
    super(eventId, sender, target, clock);
    mSender = sender;
    mMessage = message;
  }

  public String getMessage()
  {
    return mMessage;
  }

  public SpecificAddress getSender()
  {
    return mSender;
  }

  @Override
  public <T> T interpret(EventInterpreter<T> interpreter)
  {
    return interpreter.interpret(this);
  }
}
