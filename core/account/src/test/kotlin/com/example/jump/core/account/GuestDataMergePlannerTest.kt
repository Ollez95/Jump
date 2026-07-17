package com.example.jump.core.account

import org.junit.Assert.assertEquals
import org.junit.Test

class GuestDataMergePlannerTest {
  private val guest = AccountSession(
    localUserId = LocalUserId("local-1"),
    guestAccountId = AccountId("guest-1"),
    mode = AccountMode.Guest(AccountId("guest-1")),
  )

  @Test
  fun defaultPlanUsesDocumentedPolicyForEveryLocalDataCategory() {
    val plan = GuestDataMergePlanner().plan(
      guest,
      authenticatedAccount(),
      GuestDataInventory(workoutCount = 12, hasSettings = true, rewardRecordCount = 5),
    )

    assertEquals(
      GuestDataMergeStrategy.UNION_BY_STABLE_ID,
      plan.decisionFor(GuestDataCategory.WORKOUTS).strategy,
    )
    assertEquals(
      GuestDataMergeStrategy.LOCAL_WINS,
      plan.decisionFor(GuestDataCategory.SETTINGS).strategy,
    )
    assertEquals(
      GuestDataMergeStrategy.MAX_PROGRESS,
      plan.decisionFor(GuestDataCategory.REWARDS).strategy,
    )
    assertEquals(12, plan.decisionFor(GuestDataCategory.WORKOUTS).localItemCount)
    assertEquals(1, plan.decisionFor(GuestDataCategory.SETTINGS).localItemCount)
    assertEquals(5, plan.decisionFor(GuestDataCategory.REWARDS).localItemCount)
  }

  @Test
  fun customPolicyIsReflectedWithoutChangingIdentityMapping() {
    val policy = GuestDataMergePolicy(
      workouts = GuestDataMergeStrategy.LOCAL_WINS,
      settings = GuestDataMergeStrategy.LOCAL_WINS,
      rewards = GuestDataMergeStrategy.MAX_PROGRESS,
    )

    val plan = GuestDataMergePlanner(policy).plan(guest, authenticatedAccount())

    assertEquals(guest.localUserId, plan.localUserId)
    assertEquals(guest.guestAccountId, plan.guestAccountId)
    assertEquals(GuestDataMergeStrategy.LOCAL_WINS, plan.decisionFor(GuestDataCategory.WORKOUTS).strategy)
  }
}
