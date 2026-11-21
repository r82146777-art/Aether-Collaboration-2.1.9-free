package com.zhousl.aether.data

/**
 * Free keyless OpenAI-compatible providers.
 * Always use piProviderId = "openai-compatible".
 * Pollinations: https://text.pollinations.ai/v1 (~1 req / 45s anonymous).
 */
object FreeProviderBootstrap {
    const val POLLINATIONS_OPENAI_ID = "aether-free-pollinations-openai"
    const val POLLINATIONS_FAST_ID = "aether-free-pollinations-openai-fast"
    const val LLM7_ID = "aether-free-llm7"

    private const val OPENAI_COMPAT = "openai-compatible"
    private const val POLLINATIONS_BASE = "https://text.pollinations.ai/v1"
    private const val LLM7_BASE = "https://api.llm7.io/v1"

    fun defaultFreeProviders(): List<LlmProviderConfig> = listOf(
        LlmProviderConfig(
            id = POLLINATIONS_FAST_ID,
            providerId = OPENAI_COMPAT,
            name = "openai-fast (Pollinations)",
            piProviderId = OPENAI_COMPAT,
            apiKey = "",
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
            apiKey = "",
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
            apiKey = "free",
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
        val byId = existing.associateBy { it.id }.toMutableMap()
        val badIds = setOf("pollinations", "pollinations-fast", "llm7")
        for ((id, cfg) in byId.toList()) {
            if (cfg.piProviderId in badIds ||
                (cfg.id.startsWith("aether-free-") && cfg.piProviderId != OPENAI_COMPAT)
            ) {
                byId[id] = defaultFreeProviders().firstOrNull { it.id == id }
                    ?: cfg.copy(
                        providerId = OPENAI_COMPAT,
                        piProviderId = OPENAI_COMPAT,
                        baseUrl = when {
                            "llm7" in cfg.baseUrl -> LLM7_BASE
                            else -> POLLINATIONS_BASE
                        },
                        authMethod = ProviderAuthMethod.ApiKey,
                        apiKey = if ("llm7" in cfg.baseUrl) "free" else "",
                    )
            }
            if (cfg.id.startsWith("aether-free-pollinations") &&
                cfg.baseUrl.contains("text.pollinations.ai") &&
                !cfg.baseUrl.contains("/v1")
            ) {
                byId[id] = (byId[id] ?: cfg).copy(baseUrl = POLLINATIONS_BASE, apiKey = "")
            }
        }
        for (free in defaultFreeProviders()) {
            val current = byId[free.id]
            if (current == null || current.piProviderId != OPENAI_COMPAT) {
                byId[free.id] = free
            } else {
                byId[free.id] = free.copy(id = free.id)
            }
        }
        return byId.values.toList()
    }
}
