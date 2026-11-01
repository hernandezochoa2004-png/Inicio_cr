package me.mariahernandez.cristalia

/**
 * Contrato que la persona que haga el login debe implementar.
 * Tu pantalla SOLO depende de esta interfaz, así que cuando el login
 * esté listo basta con cambiar la implementación (ver más abajo).
 */
interface SessionRepository {
    suspend fun estaLogueado(): Boolean
}

/** Implementación temporal: siempre dice "no logueado". */
// TODO(compañero del login): reemplazar por la implementación real (Firebase Auth, DataStore, etc.)
object SessionRepositoryFalso : SessionRepository {
    override suspend fun estaLogueado(): Boolean = false
}