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

package com.zextras.modules.chat.server.interceptors;

import com.zextras.modules.chat.server.events.EventInterceptorFactory;
import com.zextras.modules.chat.server.events.EventMessageHistory;
import com.zextras.modules.chat.server.events.EventMessageHistoryLast;

public interface QueryArchiveInterceptorFactory extends EventInterceptorFactory
{
  interface MessageHistoryFactory
  {
    void create(EventMessageHistory message);
    void create(EventMessageHistoryLast message);
  }

  void register(MessageHistoryFactory callback);
  void unRegister(MessageHistoryFactory callback);
}