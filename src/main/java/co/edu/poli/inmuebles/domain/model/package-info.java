/**
 * Modelo de dominio: entidades y objetos de valor del negocio de inmuebles.
 *
 * <p>Es el centro del hexágono. No depende de JavaFX, JDBC ni de ninguna otra
 * tecnología: solo de Java estándar. Aquí viven las reglas de negocio.</p>
 *
 * <ul>
 *   <li>{@link co.edu.poli.inmuebles.domain.model.Inmueble} (abstracta) y sus
 *       subtipos {@link co.edu.poli.inmuebles.domain.model.Apartamento} y
 *       {@link co.edu.poli.inmuebles.domain.model.Casa}.</li>
 *   <li>{@link co.edu.poli.inmuebles.domain.model.Sotano}, parte opcional de una casa.</li>
 *   <li>{@link co.edu.poli.inmuebles.domain.model.Propietario}, dueño de uno o varios inmuebles.</li>
 * </ul>
 */
package co.edu.poli.inmuebles.domain.model;
