package com.eynnzerr.bandoristation.business

import com.eynnzerr.bandoristation.business.base.UseCase
import com.eynnzerr.bandoristation.data.AppRepository
import com.eynnzerr.bandoristation.model.GithubRelease
import com.eynnzerr.bandoristation.model.UseCaseResult
import kotlinx.coroutines.CoroutineDispatcher

class GetLatestReleaseUseCase(
    private val repository: AppRepository,
    dispatcher: CoroutineDispatcher,
) : UseCase<Unit, GithubRelease, String>(dispatcher) {
    override suspend fun execute(parameters: Unit): UseCaseResult<GithubRelease, String> {
        return try {
            val release = repository.fetchLatestRelease("Eynnzerr", "BandoriStationMobile")
            return if (release.tagName.isNotBlank()) UseCaseResult.Success(release) else UseCaseResult.Error("Not Found")
        } catch (e: Exception) {
            e.printStackTrace()
            UseCaseResult.Error(e.message ?: "")
        }
    }
}
