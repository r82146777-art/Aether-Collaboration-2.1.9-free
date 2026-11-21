package com.zhousl.aether.data

object FreeProviderBootstrap {
    const val POLLINATIONS_OPENAI_ID = "aether-free-pollinations-openai"
    const val POLLINATIONS_FAST_ID = "aether-free-pollinations-openai-fast"
    const val LLM7_ID = "aether-free-llm7"

    private const val OPENAI_COMPAT = "openai-compatible"
    private const val POLLINATIONS_BASE = "https://text.pollinations.ai/openai"
    private const val LLM7_BASE = "https://api.llm7.io/v1"

    fun defaultFreeProviders(): List<LlmProviderConfig> = listOf(
        LlmProviderConfig(
            id = POLLINATIONS_OPENAI_ID,
            providerId = OPENAI_COMPAT,
            name = "openai (Pollinations)",
            piProviderId = OPENAI_COMPAT,
            apiKey = "free",
            baseUrl = POLLINATIONS_BASE,
            authMethod = ProviderAuthMethod.ApiKey,
            modelId = "openai",
            manualModelIds = listOf("openai", "openai-fast"),
            cachedModels = listOf("openai", "openai-fast"),
            enabledModelIds = listOf("openai", "openai-fast"),
            isEnabled = true,
        ),
        LlmProviderConfig(
            id = POLLINATIONS_FAST_ID,
            providerId = OPENAI_COMPAT,
            name = "openai-fast (Pollinations)",
            piProviderId = OPENAI_COMPAT,
            apiKey = "free",
            baseUrl = POLLINATIONS_BASE,
            authMethod = ProviderAuthMethod.ApiKey,
            modelId = "openai-fast",
            manualModelIds = listOf("openai-fast", "openai"),
            cachedModels = listOf("openai-fast", "openai"),
            enabledModelIds = listOf("openai-fast", "openai"),
            isEnabled = true,
        ),
        LlmProviderConfig(
            id = LLM7_ID,
            providerId = OPENAI_COMPAT,
            name = "LLM7 Free",
            piProviderId = OPENAI_COMPAT,
            apiKey = "free",
            baseUrl = LLM7_BASE,
            authMethod = ProviderAuthMethod.ApiKey,
            modelId = "gpt-4o-mini",
            manualModelIds = listOf("gpt-4o-mini", "gpt-4o", "deepseek-v3"),
            cachedModels = listOf("gpt-4o-mini", "gpt-4o", "deepseek-v3"),
            enabledModelIds = listOf("gpt-4o-mini", "gpt-4o", "deepseek-v3"),
            isEnabled = true,
        ),
    )

    fun mergeInto(existing: List<LlmProviderConfig>): List<LlmProviderConfig> {
        val byId = existing.associateBy { it.id }.toMutableMap()
        val badIds = setOf("pollinations", "pollinations-fast", "llm7")
        for ((id, cfg) in byId.toList()) {
            if (cfg.piProviderId in badIds) {
                val fixedBase = when {
                    "pollinations" in cfg.piProviderId || "pollinations" in cfg.baseUrl -> POLLINATIONS_BASE
                    "llm7" in cfg.piProviderId || "llm7" in cfg.baseUrl -> LLM7_BASE
                    else -> cfg.baseUrl.ifBlank { POLLINATIONS_BASE }
                }
                byId[id] = cfg.copy(
                    providerId = OPENAI_COMPAT,
                    piProviderId = OPENAI_COMPAT,
                    baseUrl = fixedBase,
                    authMethod = ProviderAuthMethod.ApiKey,
                    apiKey = cfg.apiKey.ifBlank { "free" },
                )
            }
        }
        for (free in defaultFreeProviders()) {
            val current = byId[free.id]
            if (current == null || current.piProviderId in badIds || current.piProviderId != OPENAI_COMPAT) {
                byId[free.id] = free
            }
        }
        return byId.values.toList()
    }
}
