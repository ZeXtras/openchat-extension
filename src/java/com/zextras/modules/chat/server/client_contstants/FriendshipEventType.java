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

package com.zextras.modules.chat.server.client_contstants;

/**
 * This class contains the friendship request constants already defined in ZxChatEvent.js
 */
public class FriendshipEventType
{
  public static final int FRIENDSHIP_REQUEST  = 0;
  public static final int FRIENDSHIP_ACCEPTED = 1;
  public static final int FRIENDSHIP_DENIED   = 2;
  public static final int FRIENDSHIP_BLOCKED  = 3;
  public static final int FRIENDSHIP_DELETED  = 4;
  public static final int FRIENDSHIP_RENAME   = 5;
}
