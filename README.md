Random text detector
====================
[![Build Status](https://travis-ci.org/mkrcah/random-text-detector.svg?branch=master)](https://travis-ci.org/mkrcah/random-text-detector)

Unsupervised automatic detector of random text based on the algorithm described in [this blog post](http://marcelkrcah.net/blog/random-word-detector/). The interactive version is available on  [random-text-detector.herokuapp.com](http://random-text-detector.herokuapp.com). Sample training and result data is in the `data` dictionary.

## How to run

- Install sbt with `brew install sbt` or [in any other way]((http://www.scala-sbt.org/0.13/tutorial/Setup.html)) 
- `git clone https://github.com/mkrcah/random-text-detector.git`
- `cd random-text-detect`
- `sbt stage` 
- Run `./target/universal/stage/bin/random-text-detector`

## Usage

Usage: `random-text-detector start-server|batch filename`

Options:

- `batch` Compute suspicious score for each word in a given list and print the result to stdin.
- `start-server`  Start an http server on the port specified by the PORT env property or 8080 if not specified. Call `GET /api/detect?q=your-word` to get a suspicious score for your-word. Navigate your browser to `/` or `/index.html` for the interactive version.
- `filename` File containing a list of words, each word on a separate line.

## Heroku

The code should be directly deployable to Heroku.
