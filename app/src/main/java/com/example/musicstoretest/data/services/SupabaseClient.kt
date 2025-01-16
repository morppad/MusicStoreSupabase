package com.example.musicstoretest.data.services

import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage

val supabase = createSupabaseClient(
    supabaseUrl = "https://durmtdhhnkqnkpcckepo.supabase.co",
    supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImR1cm10ZGhobmtxbmtwY2NrZXBvIiwicm9sZSI6InNlcnZpY2Vfcm9sZSIsImlhdCI6MTczNjkzNDQ0OCwiZXhwIjoyMDUyNTEwNDQ4fQ.JiHci4UTEqN1-LIVQ9Q_Ig0ox6V410LaGfFfWDaDw50"
) {
    install(Auth) {
        // Настройки Auth, например, URL для редиректа
        scheme = "http"
    }
    install(Postgrest)
    install(Storage)
}