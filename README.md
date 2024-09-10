# hello-scalatest-scala

Small project to get started with Scala and ScalaTest.


## Running the main program

```
$ sbt run
```


## Running the tests

```
$ sbt test
```

or

```
$ sbt coverage test coverageReport
```

to see the code coverage percentages of your test suite.


## Running successive tasks with sbt

To speed up the edit-compile-test/run cycle, you can start sbt without arguments

```
$ sbt
```

and repeatedly run individual tasks

```
sbt> run
```

```
sbt> test
```

To exit sbt, enter

```
sbt> exit
```


## Running outside of sbt

This lets you use your application on the command-line.

First, create the startup script:

```
sbt stage
```

Then run outside of sbt like this:

```
./target/universal/stage/bin/hello-scalatest-scala
```

On Windows, you might need backslashes. WSL (Windows Subsystem for Linux) recommended instead.
