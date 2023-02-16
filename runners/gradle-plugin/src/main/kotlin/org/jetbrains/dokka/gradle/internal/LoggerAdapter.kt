package org.jetbrains.dokka.gradle.internal

import java.util.concurrent.atomic.AtomicInteger
import kotlin.reflect.KClass
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging
import org.jetbrains.dokka.utilities.DokkaLogger

/**
 * Adapt a Gradle [Logger] to a [DokkaLogger], to be used by Dokka when generating documentation.
 *
 * Using the Gradle logger means that the log-level will be controlled by the standard
 * Gradle command line options, e.g. `--info`.
 *
 * @param[logger] A Gradle logger
 * @see org.jetbrains.dokka.DokkaGenerator
 * @see org.jetbrains.dokka.DokkaBootstrap.configure
 * @see org.jetbrains.dokka.gradle.AbstractDokkaTask.generateDocumentation
 */
internal class LoggerAdapter(
  private val logger: Logger
) : DokkaLogger {

  private val warningsCounter = AtomicInteger()
  private val errorsCounter = AtomicInteger()

  override var warningsCount: Int
    get() = warningsCounter.get()
    set(value) = warningsCounter.set(value)

  override var errorsCount: Int
    get() = errorsCounter.get()
    set(value) = errorsCounter.set(value)

  override fun debug(message: String) = logger.debug(message)
  override fun progress(message: String) = logger.lifecycle(message)
  override fun info(message: String) = logger.info(message)

  override fun warn(message: String) {
    warningsCount++
    logger.warn(message)
  }

  override fun error(message: String) {
    errorsCount++
    logger.error(message)
  }
}
