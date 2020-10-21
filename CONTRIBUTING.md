# Contribute
Thanks for deciding to contribute to our initiative, here are few ways to contribute to **Limknow**:
* [Reporting a bug or Suggesting a new Feature](#report-a-problem-or-suggestion)
* [Contributing to codebase](#contributing-to-codebase)
* [Contributing AR Model](#contributing-ar-model)

## Report a problem or suggestion
You can report a bug or suggest a new feature by opening an issue on our repository.
* Go to our [issue tracker](https://github.com/sangeetagupta2068/LimKnow-Pilot/issues)
* Open a new issue with suitable template ( [report bug](https://github.com/sangeetagupta2068/LimKnow-Pilot/issues/new?assignees=&labels=&template=bug_report.md&title=) / [request feature](https://github.com/sangeetagupta2068/LimKnow-Pilot/issues/new?assignees=&labels=&template=feature_request.md&title=) )
* Fill the template and submit the issue
**Note:** If you feel that exisiting templates are not appropriate for your issue feel free to open a [blank issue](https://github.com/sangeetagupta2068/LimKnow-Pilot/issues/new) and use it explain your issue in detail.

## Contributing to codebase 
You can contribute to the codebase by solving issues in the repository. In case you think there is a need to create a new issue feel free to create a new issue by following guidelines in [Report a problem or suggestion](#report-a-problem-or-suggestion) section.
Once you have found an appropriate issue proceed to further steps:
1. Comment on the issue that you would like to work on it.
2. If no one else is working on that issue a maintainer will review your comment and assign you the issue.
3. Once the issue is assigned to you:
  * Fork the repository to your profile
  * Clone the fork on your local
  ```bash
  $ git clone git@github.com:yourname/LimKnow-Pilot.git
  ```
  * Don't modify or work on the master branch, we'll use it to always be in sync with Limknow-Pilot upstream.
  ```bash
  $ git remote add upstream git@github.com:zhukov/webogram.git
  $ git fetch upstream
  ```
  * Make changes on a new feature branch with name in following format: `#issue-number-some-description` for e.g `23-ui-fix`
  ```bash
  $ git checkout -b 23-ui-fix
  ```
  * Once you are done with all the changes commit the changes. In case you have done multiple commits on your feature branch [`squash`](https://stackoverflow.com/a/5201642/9461853) them in a single commit.
  e.g:
  ```bash
  $ git add .
  $ git commit -m "Beautified Navigation fixes #23"
  $ git push origin 23-ui-fix
  ```
  * Push the changes to your fork and raise a Pull Request.
    * In the PR title mention the issue number you have worked on for eg. `Fixes #23 Changed UI`
    * In the Issue body mention all the details you think your reviewer should know.
  * Depending on your PR the reviewer may [request you to make some changes](#how-to-implement-changes-requested-on-a-pull-request), once everything looks good a reviewer will merge your PR.

### How to implement changes requested on a pull request
Sometimes when you submit a PR, you will be asked to correct some code. You can make the changes on your work branch and commit normally and the PR will be automatically updated.
```bash
$ git commit -am "Ops, fixing typo"
```
Once everything is OK, you will be asked to merge all commit messages into one to keep history clean.
```bash
$ git rebase -i master
```
Edit the file and mark as fixup (f) all commits you want to merge with the first one:
```
pick 1c85e07 Beautified Navigation fixes #23
f c595f79 Ops, fixing typo
```
Once rebased you can force a push to your fork branch and the PR will be automatically updated.
```bash
$ git push origin 23-ui-fix --force
$ git rebase -i master
```

### How to keep your local branches updated

To keep your local master branch updated with upstream master, regularly do:
```bash
$ git fetch upstream
$ git checkout master
$ git pull --rebase upstream master
```
To update the branch you are coding in:
```bash
$ git checkout improve-contacts-99
$ git rebase master
```
## Contributing AR Model
We have a seperate repository for the AR models used in **LimKnow**, please refer to its [contributing guidelines](https://github.com/shriaas2898/Limknow-AR-Models/blob/main/CONTRIBUTING.md).
