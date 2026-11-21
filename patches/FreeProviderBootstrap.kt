package com.zhousl.aether.data

/**
 * Free OpenAI-compatible providers for Pi.
 *
 * Pi requires a non-empty apiKey for custom openai-compatible providers
 * even when the upstream is keyless — use a dummy value.
 * Always use piProviderId = "openai-compatible".
 */
object FreeProviderBootstrap {
    const val POLLINATIONS_OPENAI_ID = "aether-free-pollinations-openai"
    const val POLLINATIONS_FAST_ID = "aether-free-pollinations-openai-fast"
    const val LLM7_ID = "aether-free-llm7"

    private const val OPENAI_COMPAT = "openai-compatible"
    private const val POLLINATIONS_BASE = "https://text.pollinations.ai/v1"
    private const val LLM7_BASE = "https://api.llm7.io/v1"
    private const val POLLINATIONS_KEY = "pollinations"
    private const val LLM7_KEY = "free"

    fun defaultFreeProviders(): List<LlmProviderConfig> = listOf(
        LlmProviderConfig(
            id = POLLINATIONS_FAST_ID,
            providerId = OPENAI_COMPAT,
            name = "openai-fast (Pollinations)",
            piProviderId = OPENAI_COMPAT,
            apiKey = POLLINATIONS_KEY,
            baseUrl = POLLINATIONS_BASE,
            authMethod = ProviderAuthMethod.ApiKey,
            modelId = "openai-fast",
            manualModelIds = listOf("openai-fast", "openai", "gpt-oss-20b"),
            cachedModels = listOf("openai-fast", "openai", "gpt-oss-20b"),
            enabledModelIds = listOf("openai-fast", "openai", "gpt-oss-20b"),
            isEnabled = true,
        ),
        LlmProviderConfig(
            id = POLLINATIONS_OPENAI_ID,
            providerId = OPENAI_COMPAT,
            name = "openai (Pollinations)",
            piProviderId = OPENAI_COMPAT,
            apiKey = POLLINATIONS_KEY,
            baseUrl = POLLINATIONS_BASE,
            authMethod = ProviderAuthMethod.ApiKey,
            modelId = "openai",
            manualModelIds = listOf("openai", "openai-fast", "gpt-oss-20b"),
            cachedModels = listOf("openai", "openai-fast", "gpt-oss-20b"),
            enabledModelIds = listOf("openai", "openai-fast", "gpt-oss-20b"),
            isEnabled = true,
        ),
        LlmProviderConfig(
            id = LLM7_ID,
            providerId = OPENAI_COMPAT,
            name = "LLM7 (Inkling-Small)",
            piProviderId = OPENAI_COMPAT,
            apiKey = LLM7_KEY,
            baseUrl = LLM7_BASE,
            authMethod = ProviderAuthMethod.ApiKey,
            modelId = "Inkling-Small",
            manualModelIds = listOf("Inkling-Small"),
            cachedModels = listOf("Inkling-Small"),
            enabledModelIds = listOf("Inkling-Small"),
            isEnabled = true,
        ),
    )

    fun mergeInto(existing: List<LlmProviderConfig>): List<LlmProviderConfig> {
        val freeById = defaultFreeProviders().associateBy { it.id }
        val byId = existing.associateBy { it.id }.toMutableMap()
        for ((id, free) in freeById) {
            byId[id] = free
        }
        val badPi = setOf("pollinations", "pollinations-fast", "llm7")
        for ((id, cfg) in byId.toList()) {
            if (id in freeById) continue
            if (cfg.piProviderId in badPi) {
                byId[id] = cfg.copy(
                    providerId = OPENAI_COMPAT,
                    piProviderId = OPENAI_COMPAT,
                    baseUrl = if ("llm7" in cfg.piProviderId || "llm7" in cfg.baseUrl) LLM7_BASE else POLLINATIONS_BASE,
                    authMethod = ProviderAuthMethod.ApiKey,
                    apiKey = if ("llm7" in cfg.baseUrl) LLM7_KEY else POLLINATIONS_KEY,
                )
            }
        }
        return byId.values.toList()
    }
}
