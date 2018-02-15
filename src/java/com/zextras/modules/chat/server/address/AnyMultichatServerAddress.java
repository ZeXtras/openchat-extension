package com.zextras.modules.chat.server.address;

import com.zextras.modules.chat.server.db.providers.UserProvider;
import com.zextras.modules.chat.server.dispatch.AnyRoomServerDispatcher;
import com.zextras.modules.chat.server.dispatch.Dispatcher;
import com.zextras.modules.chat.server.dispatch.RoomServerHostSetProvider;
import com.zextras.modules.chat.server.events.EventRouter;
import com.zextras.modules.chat.server.session.SessionUUID;

public class AnyMultichatServerAddress implements ChatAddress
{
  public static final AnyMultichatServerAddress sInstance = new AnyMultichatServerAddress();

  @Override
  public Dispatcher createDispatcher(
    EventRouter eventRouter,
    UserProvider openUserProvider,
    RoomServerHostSetProvider roomServerHostSetProvider
  )
  {
    return new AnyRoomServerDispatcher(
      eventRouter,
      roomServerHostSetProvider
    );
  }

  @Override
  public String resource()
  {
    return "";
  }

  @Override
  public String resourceAddress()
  {
    return "";
  }

  @Override
  public ChatAddress withoutSession()
  {
    return this;
  }

  @Override
  public ChatAddress withoutResource()
  {
    return this;
  }

  @Override
  public boolean isFromSession(SessionUUID sessionUUID)
  {
    return false;
  }
}