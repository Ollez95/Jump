package com.example.jump.core.account

enum class GuestDataCategory { WORKOUTS, SETTINGS, REWARDS }

enum class GuestDataMergeStrategy {
  UNION_BY_STABLE_ID,
  LOCAL_WINS,
  MAX_PROGRESS,
}

data class GuestDataInventory(
  val workoutCount: Int = 0,
  val hasSettings: Boolean = false,
  val rewardRecordCount: Int = 0,
) {
  val isEmpty: Boolean get() = workoutCount == 0 && !hasSettings && rewardRecordCount == 0
}

data class GuestDataMergePolicy(
  val workouts: GuestDataMergeStrategy = GuestDataMergeStrategy.UNION_BY_STABLE_ID,
  val settings: GuestDataMergeStrategy = GuestDataMergeStrategy.LOCAL_WINS,
  val rewards: GuestDataMergeStrategy = GuestDataMergeStrategy.MAX_PROGRESS,
)

data class GuestDataMergeDecision(
  val category: GuestDataCategory,
  val strategy: GuestDataMergeStrategy,
  val localItemCount: Int,
  val reason: String,
)

data class GuestDataMergePlan(
  val localUserId: LocalUserId,
  val guestAccountId: AccountId,
  val destinationAccountId: AccountId,
  val decisions: List<GuestDataMergeDecision>,
) {
  fun decisionFor(category: GuestDataCategory): GuestDataMergeDecision =
    requireNotNull(decisions.firstOrNull { it.category == category }) {
      "Missing merge decision for $category"
    }
}

sealed interface GuestDataMergeResult {
  data object NoGuestData : GuestDataMergeResult

  data class Pending(val plan: GuestDataMergePlan) : GuestDataMergeResult

  data class Completed(
    val plan: GuestDataMergePlan,
    val workoutsMerged: Int,
    val settingsMerged: Boolean,
    val rewardsMerged: Int,
  ) : GuestDataMergeResult

  data class Partial(
    val plan: GuestDataMergePlan,
    val completedCategories: Set<GuestDataCategory>,
    val failures: Map<GuestDataCategory, AccountOperationError>,
  ) : GuestDataMergeResult

  data class Failure(
    val plan: GuestDataMergePlan,
    val error: AccountOperationError,
  ) : GuestDataMergeResult
}

/** Applies a plan through adapters owned by workout, settings, and rewards integration code. */
interface GuestDataMerger {
  suspend fun merge(plan: GuestDataMergePlan): GuestDataMergeResult
}

class GuestDataMergePlanner(
  private val policy: GuestDataMergePolicy = GuestDataMergePolicy(),
) {
  fun plan(
    session: AccountSession,
    destination: AuthenticatedAccount,
    inventory: GuestDataInventory = GuestDataInventory(),
  ): GuestDataMergePlan = GuestDataMergePlan(
    localUserId = session.localUserId,
    guestAccountId = session.guestAccountId,
    destinationAccountId = destination.id,
    decisions = listOf(
      GuestDataMergeDecision(
        GuestDataCategory.WORKOUTS,
        policy.workouts,
        inventory.workoutCount,
        "Union workouts by stable ID; retain distinct local and remote sessions.",
      ),
      GuestDataMergeDecision(
        GuestDataCategory.SETTINGS,
        policy.settings,
        if (inventory.hasSettings) 1 else 0,
        "Keep explicitly configured guest settings when local settings exist.",
      ),
      GuestDataMergeDecision(
        GuestDataCategory.REWARDS,
        policy.rewards,
        inventory.rewardRecordCount,
        "Keep the greatest earned progress and deduplicate workout reward IDs.",
      ),
    ),
  )
}
