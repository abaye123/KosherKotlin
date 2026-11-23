# About this fork

A fork of [kdroidFilter/KosherKotlin](https://github.com/kdroidFilter/KosherKotlin) that keeps the Itim Labina
(עתים לבינה) calculation mode, which upstream removed in 4.0.0. It is not an upstream release, and is not affiliated
with upstream or with the luach. The code is LGPL 2.1, as is upstream.

## Background

- The mode was added upstream in 3.0.0 and removed on 2026-11-22 (`6a89449`, released as 4.0.0). The same day upstream
  force-pushed a rewritten history in which its code and names are gone from older commits and tags too.
- **Do not `git revert 6a89449`**: it only restores blank lines and broken code.
- The sources were taken from `582e904`, upstream master just before the rewrite, fetched through a public fork that
  still carried it. A copy may exist as the local ref `refs/rescue/pre-rewrite` in the original clone. It is no longer
  needed: everything restored is in this branch.
- The upstream 4.0.0 release notes ask users to stop displaying times under the name of a luach. Consider that before
  showing the name to end users.

## Branches

| Branch | Content |
|---|---|
| `master` | Plain mirror of upstream master. Never commit here. |
| `itim-labina` | Upstream master plus our commits. Build and tag from here. |

Remotes: `origin` is this fork; `upstream` is kdroidFilter (fetch only). In a fresh clone:

    git remote add upstream https://github.com/kdroidFilter/KosherKotlin.git
    git remote set-url --push upstream DISABLED
    git config rerere.enabled true

## What we change

Own files (cannot conflict), under `zmanim/src/commonMain/kotlin/io/github/kdroidfilter/kosherkotlin/`:

- `ItimLabinaZmanim.kt`: the ten zmanim, as an interface so they remain members of `ComplexZmanimCalendar`.
- `util/ItimLabinaCalculator.kt`: the calculator.
- `util/HorizonRefraction.kt`: refraction, dip and `ItimLabinaClimate`.
- Test: `zmanim/src/jvmTest/kotlin/util/ItimLabinaCalculatorTest.kt`.

Small edits in upstream files (may conflict):

- `ComplexZmanimCalendar.kt`: the class implements `ItimLabinaZmanim`; `*itimLabinaZmanim()` in `allZmanim`.
- `metadata/ZmanAuthority.kt`: `ITIM_LABINA` and its name.
- `app/composeApp/src/commonMain/kotlin/app/domain/` (`Model.kt`, `HebrewLabels.kt`, `ZmanimRepository.kt`): the luach
  option, which is the default.
- `zmanim/build.gradle.kts`: `groupId` `io.github.abaye123`, project URLs, signing only when a key exists.
- `.github/workflows/PublishToMavenCentral.yml`: runs only in the upstream repository.

Upstream extension points we depend on: `AstronomicalCalculator.horizonZenith` and `getUTCMidnight`, `SpaCalculator`,
`SolarPosition`/`SolarDay`, `AtmosphereModels`, `getSunrise/SunsetOffsetByDegreesBelowHorizon` and
`ZmanCalculationMethod.DegreesBelowHorizon`. If upstream drops any of these, carry them over into our own files, taking
them from our last tag.

To change a zman, edit its degrees in `ItimLabinaZmanim.kt`. To add one, add it there and to `itimLabinaZmanim()`.

## Versions

`<upstream version>-itim.<n>`, tagged `v<upstream version>-itim.<n>`: `4.0.0-itim.1` is our first build on upstream
4.0.0. Increase `n` for a change of ours; restart at 1 on a new upstream version. The version is read from
`GITHUB_REF`; without it the version is `dev`. Semver sorts `4.0.0-itim.1` before `4.0.0`, but the different `groupId`
keeps the two from being compared. The demo app keeps the upstream app version (1.0.0).

Tags keep old releases reachable, so `itim-labina` itself may be rebased and force-pushed. Push tags one by one,
never with `--tags`.

## Updating from upstream

Current base: `d650c69` (upstream v4.0.0). **Update this line on every rebase.**

    git fetch --no-tags upstream
    git switch itim-labina
    git rebase --onto <new upstream release commit> <current base> itim-labina
    ./gradlew :zmanim:jvmTest --tests "util.*"
    git push --force-with-lease origin itim-labina

Always use `--onto` with the recorded base: it works even if upstream rewrites its history again. Prefer the commit of
an upstream release, so that our version number is accurate. Afterwards refresh the mirror with
`git push --force-with-lease origin upstream/master:master`.

Then check that upstream did not bring back its own version of the mode, and compare the zmanim with the previous tag.

## Building

Needs JDK 21 for the Gradle daemon, JDK 17 as the toolchain, and the Android SDK (`ANDROID_HOME`).

    GITHUB_REF=refs/tags/v4.0.0-itim.1 ./gradlew :zmanim:publishToMavenLocal

Without JDK 17, build in a temporary copy whose `zmanim/build.gradle.kts` has `jvmToolchain(21)` and `jvmTarget` 17 on
the `jvm()` target, so that the result still runs on Java 17. The Kotlin packages are unchanged; consumers use
`io.github.abaye123:kosherkotlin:4.0.0-itim.1`. Do not depend on the upstream artifact at the same time: the classes
are the same.

`RegressionTest` walks every day from 1900 to the Hebrew year 6000 and runs for a very long time; select other tests
with `--tests`.
