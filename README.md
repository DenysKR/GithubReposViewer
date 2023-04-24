# GithubReposViewer

Test project that provides functionality for fetching Github user basic and user repositories' info.
The application is written using MVVM, Kotlin Flows, Koin, Jetpack Compose, Retrofit and custom okhttp client.
Repository layer is coverd by unit tests.
For signing in a user should type Github token on Sign in page.
Github token is a bit long and it's not convenient to type it manually, so the following command can be used for typing(precondition - make text input focusable by touch):

adb shell input text <YOUR_GITHUB_TOKEN>

For sign in you need to create fine-grained Github token(in Developer settings) with the following permissions:
  1. Read and Write access to followers, profile, starring, and watching
  2. Read access to metadata
  3. Read and Write access to actions, administration, code, codespaces, commit statuses, repository advisories, and workflows
