import com.typesafe.sbt.SbtNativePackager._
import NativePackagerKeys._

organization := "net.marcelkrcah"

name := "Random Text Detector"

version := "1.0"

packageArchetype.java_server

scalaVersion := "2.10.3"

resolvers += "spray" at "http://repo.spray.io/"

resolvers += Resolver.sonatypeRepo("public")

libraryDependencies ++= {
  val akkaV = "2.3.0"
  val sprayV = "1.3.1"
  Seq(
    "org.scalatest"       % "scalatest_2.10"  % "2.2.0" % "test",
    "io.spray"            %   "spray-can"     % sprayV,
    "io.spray"            %   "spray-routing" % sprayV,
    "io.spray"            %   "spray-testkit" % sprayV  % "test",
    "io.spray"            %%  "spray-json"    % "1.2.6",
    "com.typesafe.akka"   %%  "akka-actor"    % akkaV,
    "com.typesafe.akka"   %%  "akka-testkit"  % akkaV   % "test",
    "org.specs2"          %%  "specs2-core"   % "2.3.7" % "test",
    "com.github.scopt"    %% "scopt" % "3.2.0"
  )
}

seq(Revolver.settings: _*)
