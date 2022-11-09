# Contributing

## Contributor Agreement
A pull-request will only be considered for merging into the upstream codebase after you have signed our [contributor agreement](https://gist.github.com/snyksec/201fc2fd188b4a68973998ec30b57686#file-snyk-oss-contributor-agreement-md), assigning us the rights to the contributed code and granting you a license to use it in return. If you submit a pull request, you will be prompted to review and sign the agreement with one click (we use [CLA assistant](https://cla-assistant.io/)).

## Commit messages

Commit messages must follow the [Angular-style](https://github.com/angular/angular.js/blob/master/CONTRIBUTING.md#commit-message-format) commit format (but excluding the scope).

i.e:

```text
fix: minified scripts being removed

Also includes tests
```

This will allow for the automatic changelog to generate correctly.

### Commit types

Must be one of the following:

* **feat**: A new feature
* **fix**: A bug fix
* **docs**: Documentation only changes
* **test**: Adding missing tests
* **chore**: Changes to the build process or auxiliary tools and libraries such as documentation generation
* **refactor**: A code change that neither fixes a bug nor adds a feature
* **style**: Changes that do not affect the meaning of the code (white-space, formatting, missing semi-colons, etc)
* **perf**: A code change that improves performance

To release a major you need to add `BREAKING CHANGE: ` to the start of the body and the detail of the breaking change.
