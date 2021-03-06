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

package com.zextras.modules.chat.server.db.sql.relationship;

import com.zextras.modules.chat.server.relationship.Relationship.RelationshipType;
import com.zextras.modules.chat.server.address.SpecificAddress;
import com.zextras.modules.chat.server.db.sql.SqlParameter;
import com.zextras.modules.chat.server.db.sql.SqlStatement;

import java.util.ArrayList;
import java.util.Collection;

public class RelationshipUpdateStatement implements SqlStatement
{
  private Collection<SqlParameter> mParameters = new ArrayList<SqlParameter>(5);

  public RelationshipUpdateStatement(
    final int userId,
    final RelationshipType relationshipType,
    final SpecificAddress buddyAddress,
    final String buddyNickname,
    final String group
  )
  {
    mParameters.add(new SqlParameter<Byte>(1, "`TYPE`", relationshipType.toByte()));
    mParameters.add(new SqlParameter<String>(2, "BUDDYNICKNAME", buddyNickname));
    mParameters.add(new SqlParameter<String>(3, "`GROUP`", group));
    mParameters.add(new SqlParameter<String>(4, "BUDDYADDRESS", buddyAddress.toString(), SqlParameter.DO_NO_UPDATE));
    mParameters.add(new SqlParameter<Integer>(5, "USERID", userId, SqlParameter.DO_NO_UPDATE));
  }

  @Override
  public String sql() {
    StringBuilder sb = new StringBuilder("UPDATE chat.RELATIONSHIP SET ");
    for (SqlParameter parameter : parameters()) {
      if (parameter.isFieldToUpdate()) {
        sb.append(parameter.getColumnName()).append("=?,");
      }
    }
    sb.setLength(sb.length() - 1);
    sb.append(" WHERE BUDDYADDRESS=? AND USERID=?;");
    return sb.toString();
  }

  @Override
  public Collection<SqlParameter> parameters() {
    return mParameters;
  }
}
