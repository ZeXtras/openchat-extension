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

package com.zextras.modules.chat.server;

import com.google.inject.Inject;
import org.openzal.zal.lib.Clock;
import com.zextras.modules.chat.server.address.SpecificAddress;
import com.zextras.modules.chat.server.events.Event;
import com.zextras.modules.chat.server.xmpp.encoders.XmppEncoderFactoryImpl;

public class QueuedEventFactory
{
  private final Clock                  mClock;
  private final XmppEncoderFactoryImpl mEncoderFactory;

  @Inject
  public QueuedEventFactory(Clock clock, XmppEncoderFactoryImpl encoderFactory)
  {
    mClock = clock;
    mEncoderFactory = encoderFactory;
  }

  public QueuedEvent createQueuedEvent(Event event, SpecificAddress recipient)
  {
    return new QueuedEvent(event, recipient, mEncoderFactory, mClock);
  }
}
