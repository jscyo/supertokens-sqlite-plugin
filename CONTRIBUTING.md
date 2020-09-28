
# Contributing

We're so excited you're interested in helping with SuperTokens! We are happy to help you get started, even if you don't have any previous open-source experience :blush:

## New to Open Source?
1. Take a look at [How to Contribute to an Open Source Project on GitHub](https://egghead.io/courses/how-to-contribute-to-an-open-source-project-on-github)
2. Go thorugh the [SuperTokens Code of Conduct](https://github.com/supertokens/supertokens-sqlite-plugin/blob/master/CODE_OF_CONDUCT.md)

## Where to ask Questions?
1. Check our [Github Issues](https://github.com/supertokens/supertokens-sqlite-plugin/issues) to see if someone has already answered your question.
2. Join our community on [Discord](https://supertokens.io/discord) and feel free to ask us your questions


## Development Setup
### Prerequisites
- OS: Linux or macOS
- IDE: Intellij (recommended) or equivalent IDE

### Project Setup
1. Setup the `supertokens-core` by following [this guide](https://github.com/supertokens/supertokens-core/blob/master/CONTRIBUTING.md#development-setup). If you are not modifying the `supertokens-core` repo, then you do not need to fork that.
2. Fork the `supertokens-sqlite-plugin` repository
3. Open `modules.txt` in the `supertokens-root` directory and change it so that it looks like (the last line has changed):
   ```
   // put module name like module name,branch name,github username(if contributing with a forked repository) and then call ./loadModules script        
   core,master
   plugin-interface,master
   sqlite-plugin,master,<your github username>
   ```
4. Run `./loadModules` in the `supertokens-root` directory. This will clone your forked `supertokens-sqlite-plugin` repo.
5. Create a directory called `sqlite_db` in the `supertokens-root` directory. This directory is required to run tests with the `sqlite-plugin`
   ```
   mkdir sqlite_db
   ```
6. Follow the [CONTRIBUTING.md](https://github.com/supertokens/supertokens-core/blob/master/CONTRIBUTING.md#modifying-code) guide from `supertokens-core` repo for modifying and testing.

## Pull Request
1. Before submitting a pull request make sure all tests have passed
2. Reference the relevant issue or pull request and give a clear description of changes/features added when submitting a pull request

## SuperTokens Community
SuperTokens is made possible by a passionate team and a strong community of developers. If you have any questions or would like to get more involved in the SuperTokens community you can check out:
  - [Github Issues](https://github.com/supertokens/supertokens-sqlite-plugin/issues)
  - [Discord](https://supertokens.io/discord)
  - [Twitter](https://twitter.com/supertokensio)
  - or [email us](mailto:team@supertokens.io)
  
Additional resources you might find useful:
  - [SuperTokens Docs](https://supertokens.io/docs/community/getting-started/installation)
  - [Blog Posts](https://supertokens.io/blog/)
