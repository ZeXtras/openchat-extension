package com.zextras.modules.chat.server.relationship;

import com.google.inject.Inject;
import com.zextras.lib.log.ChatLog;
import com.zextras.modules.chat.server.Relationship;
import com.zextras.modules.chat.server.address.SpecificAddress;
import com.zextras.modules.chat.server.exceptions.ChatDbException;
import org.openzal.zal.Account;
import org.openzal.zal.Provisioning;
import org.openzal.zal.Utils;
import org.openzal.zal.exceptions.NoSuchAccountException;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;

/**
 * Class provides user's distribution-list-relationship's information.
 * Distribution-list-relationship is a relationship derive from a
 * distribution list (contains the user's address) where all members has to be
 * friend.
 */
public class DistributionListsRelationshipProvider extends RelationshipProviderAdapter
{
  private final DistributionListExtractor mDistributionListExtractor;

  @Inject
  public DistributionListsRelationshipProvider(
    DistributionListExtractor distributionListExtractor
  )
  {
    mDistributionListExtractor = distributionListExtractor;
  }

  @Override
  public Collection<Relationship> getUserRelationships(int userId, SpecificAddress userAddress)
  {
    return mDistributionListExtractor.getDistributionListsRelationships(userAddress);
  }

  @Override
  public Relationship getUserRelationshipByBuddyAddress(
    int userId,
    SpecificAddress userAddress,
    SpecificAddress buddyAddress
  )
  {
    if ( mDistributionListExtractor.areBuddies(userAddress, buddyAddress))
    {
      return mDistributionListExtractor.buildRelationship(
        buddyAddress
      );
    }

    return null;
  }

  @Nullable
  @Override
  public Relationship.RelationshipType userRelationshipType(int userId, SpecificAddress userAddress, SpecificAddress buddyAddress)
  {
    return mDistributionListExtractor.areBuddies(
      userAddress,
      buddyAddress
    ) ? Relationship.RelationshipType.ACCEPTED : null;
  }
}
