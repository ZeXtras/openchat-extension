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

package com.zextras.modules.chat.server.soap;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.zextras.lib.log.ChatLog;
import org.openzal.zal.ContinuationThrowable;
import org.openzal.zal.Provisioning;
import org.openzal.zal.Utils;
import org.openzal.zal.Account;
import org.openzal.zal.soap.SoapHandler;
import org.openzal.zal.soap.SoapResponse;
import org.openzal.zal.soap.ZimbraContext;
import org.openzal.zal.soap.ZimbraExceptionContainer;


@Singleton
public class ChatSoapHandler implements SoapHandler
{
  private final SoapHandlerCreatorFactory mSoapHandlerCreatorFactory;
  private final Provisioning              mProvisioning;
  //private final GenericReportBuilder      mGenericReportBuilder;
  //private final ReportManager             mReportManager;

  @Inject
  public ChatSoapHandler(
    SoapHandlerCreatorFactory soapHandlerCreatorFactory,
    Provisioning provisioning
    //GenericReportBuilder genericReportBuilder,
    //ReportManager reportManager
  )
  {
    mSoapHandlerCreatorFactory = soapHandlerCreatorFactory;
    mProvisioning = provisioning;
    //mGenericReportBuilder = genericReportBuilder;
    //mReportManager = reportManager;
  }

  public String getLoggerName()
  {
    return "Chat Soap Handler";
  }

  @Override
  public void handleRequest(
    ZimbraContext context,
    SoapResponse soapResponse,
    ZimbraExceptionContainer zimbraExceptionContainer
  )
  {
    try
    {
      String targetAccountId = context.getTargetAccountId();
      Account account = mProvisioning.getAccountById(targetAccountId);

      final SoapHandlerCreator handlerCreator = mSoapHandlerCreatorFactory.create(
        account,
        soapResponse,
        context
      );

      handlerCreator.getAppropriateHandler().handleRequest();
    }
    catch( ContinuationThrowable ex )
    {
      throw ex;
    }
    catch (Throwable ex)
    {
      zimbraExceptionContainer.setException(ex);
      ChatLog.log.warn("Internal error:" + Utils.exceptionToString(ex));
      //Report report = mGenericReportBuilder.createReport(ex, ModuleName.CHAT);
      //mReportManager.doReport(report);
    }
  }

  @Override
  public boolean needsAdminAuthentication(ZimbraContext context)
  {
    return false;
  }

  @Override
  public boolean needsAuthentication(ZimbraContext context)
  {
    return true;
  }
}
