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

package com.zextras.modules.chat;

import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.zextras.lib.ZimbraSSLContextProvider;
import com.zextras.modules.chat.properties.ChatProperties;
import com.zextras.modules.chat.properties.LdapChatProperties;
import com.zextras.modules.chat.server.ChatVersion;
import com.zextras.modules.chat.server.OpenchatVersion;
import com.zextras.modules.chat.server.LocalXmppConnectionProvider;
import com.zextras.modules.chat.server.LocalXmppConnectionProviderImpl;
import com.zextras.modules.chat.server.db.DbHandler;
import com.zextras.modules.chat.server.db.MariaDbHandler;
import com.zextras.modules.chat.server.db.mappers.OpenStatementsFactory;
import com.zextras.modules.chat.server.db.mappers.StatementsFactory;
import com.zextras.modules.chat.server.db.modifiers.OpenUserModifier;
import com.zextras.modules.chat.server.db.modifiers.UserModifier;
import com.zextras.modules.chat.server.db.providers.OpenUserProvider;
import com.zextras.modules.chat.server.db.providers.UserProvider;
import com.zextras.modules.chat.server.events.EventInterceptorFactory;
import com.zextras.modules.chat.server.history.HistoryMailManagerFactory;
import com.zextras.modules.chat.server.history.ImHistoryQueueHandlerFactory;
import com.zextras.modules.chat.server.interceptors.UserEventInterceptorFactory;
import com.zextras.modules.chat.server.interceptors.UserHistoryInterceptorFactory;
import com.zextras.modules.chat.server.interceptors.UserHistoryInterceptorFactoryImpl;
import com.zextras.modules.chat.server.parsing.ParserFactory;
import com.zextras.modules.chat.server.parsing.SoapParserFactory;
import com.zextras.modules.chat.server.relationship.RelationshipModifier;
import com.zextras.modules.chat.server.relationship.RelationshipModifierProxy;
import com.zextras.modules.chat.server.relationship.RelationshipProvider;
import com.zextras.modules.chat.server.relationship.RelationshipProviderCombiner;
import com.zextras.modules.chat.server.soap.InitialSoapRequestHandlerFactory;
import com.zextras.modules.chat.server.soap.SoapHandlerCreatorFactory;
import com.zextras.modules.chat.server.soap.SoapSessionFactory;
import com.zextras.modules.chat.server.soap.encoders.SoapEncoderFactory;
import com.zextras.modules.chat.server.soap.encoders.SoapEncoderFactoryImpl;
import com.zextras.modules.chat.server.xmpp.StanzaRecognizer;
import com.zextras.modules.chat.server.xmpp.StanzaRecognizerImpl;
import com.zextras.modules.chat.server.xmpp.StanzaWriterFactory;
import com.zextras.modules.chat.server.xmpp.StanzaWriterFactoryImpl;
import com.zextras.modules.chat.server.xmpp.XmppEventFactory;
import com.zextras.modules.chat.server.xmpp.XmppEventFactoryImpl;
import com.zextras.modules.chat.server.xmpp.XmppEventFilter;
import com.zextras.modules.chat.server.xmpp.XmppEventFilterImpl;
import com.zextras.modules.chat.server.xmpp.XmppFilterOut;
import com.zextras.modules.chat.server.xmpp.XmppFilterOutImpl;
import com.zextras.modules.chat.server.xmpp.XmppHandlerFactory;
import com.zextras.modules.chat.server.xmpp.XmppHandlerFactoryImpl;
import com.zextras.modules.chat.server.xmpp.encoders.XmppEncoderFactory;
import com.zextras.modules.chat.server.xmpp.encoders.XmppEncoderFactoryImpl;
import org.openzal.zal.MailboxManager;
import org.openzal.zal.Provisioning;
import org.openzal.zal.ZimbraConnectionProvider;
import org.openzal.zal.extension.Zimbra;
import org.openzal.zal.lib.ZimbraDatabase;
import org.openzal.zal.lib.ActualClock;
import org.openzal.zal.lib.Clock;

import javax.net.ssl.SSLContext;

public class OpenChatModule extends AbstractModule
{
  public static final String MODULE_NAME = "ZimbraChat";
  private final Zimbra mZimbra;

  public OpenChatModule(
    Zimbra zimbra
  )
  {
    mZimbra = zimbra;
  }

  @Override
  protected void configure()
  {
    bind(Zimbra.class).toInstance(mZimbra);
    bind(MailboxManager.class).toInstance(mZimbra.getMailboxManager());
    bind(Provisioning.class).toInstance(mZimbra.getProvisioning());
    bind(ZimbraDatabase.ConnectionProvider.class).to(ZimbraConnectionProvider.class);
    bind(DbHandler.class).to(MariaDbHandler.class);
    bind(Clock.class).to(ActualClock.class);
    bind(ChatProperties.class).to(LdapChatProperties.class);
    bind(StatementsFactory.class).to(OpenStatementsFactory.class);
    bind(ParserFactory.class).to(SoapParserFactory.class);
    bind(EventInterceptorFactory.class).to(UserEventInterceptorFactory.class);
    bind(SoapEncoderFactory.class).to(SoapEncoderFactoryImpl.class);
    bind(UserHistoryInterceptorFactory.class).to(UserHistoryInterceptorFactoryImpl.class);
    bind(UserProvider.class).to(OpenUserProvider.class);
    bind(UserModifier.class).to(OpenUserModifier.class);
    bind(StanzaRecognizer.class).to(StanzaRecognizerImpl.class);
    bind(XmppEventFactory.class).to(XmppEventFactoryImpl.class);
    bind(LocalXmppConnectionProvider.class).to(LocalXmppConnectionProviderImpl.class);
    bind(XmppEncoderFactory.class).to(XmppEncoderFactoryImpl.class);
    bind(XmppEventFilter.class).to(XmppEventFilterImpl.class);
    bind(XmppFilterOut.class).to(XmppFilterOutImpl.class);
    bind(XmppHandlerFactory.class).to(XmppHandlerFactoryImpl.class);
    bind(StanzaWriterFactory.class).to(StanzaWriterFactoryImpl.class);
    bind(SSLContext.class).toProvider(ZimbraSSLContextProvider.class);
    bind(RelationshipProvider.class).to(RelationshipProviderCombiner.class);
    bind(RelationshipModifier.class).to(RelationshipModifierProxy.class);
    bind(ChatVersion.class).to(OpenchatVersion.class);
    install(new FactoryModuleBuilder().build(SoapHandlerCreatorFactory.class));
    install(new FactoryModuleBuilder().build(InitialSoapRequestHandlerFactory.class));
    install(new FactoryModuleBuilder().build(SoapSessionFactory.class));
    install(new FactoryModuleBuilder().build(ImHistoryQueueHandlerFactory.class));
    install(new FactoryModuleBuilder().build(HistoryMailManagerFactory.class));
  }
}
