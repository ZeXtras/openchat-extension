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

package com.zextras.modules.chat.server.db.mappers;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.zextras.modules.chat.server.db.DbHandler;
import com.zextras.modules.chat.server.db.builders.DbInfoBuilder;
import com.zextras.modules.chat.server.db.providers.DbInfo;
import com.zextras.modules.chat.server.db.sql.DbInfoSelectStatement;
import com.zextras.modules.chat.server.exceptions.ChatDbException;

import java.sql.*;

@Singleton
public class DbInfoMapper extends AbstractMapper<DbInfo>{

  private final StatementsFactory mStatementsFactory;

  @Inject
  public DbInfoMapper(
    DbHandler dbHandler,
    StatementsFactory statementsFactory
  ) {
    super(dbHandler);
    mStatementsFactory = statementsFactory;
  }

  public DbInfo get()
    throws ChatDbException
  {
    DbInfo dbInfo;

    try {
      dbInfo = abstractFind(mStatementsFactory.buildDbInfo());
    }
    catch (ChatDbException ex)
    {
      SQLException originEx = (SQLException) ex.getCause();
      if (originEx.getErrorCode() == -5501 || originEx.getErrorCode() == -451) {
        return new DbInfo();
      } else {
        throw ex;
      }
    }

    return dbInfo;
  }

  @Override
  protected DbInfo load(ResultSet rs)
    throws ChatDbException
  {
    return new DbInfoBuilder(rs).build();
  }
}
