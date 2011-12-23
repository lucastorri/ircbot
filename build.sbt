import AssemblyKeys._

organization := "co.torri"

name := "ircbot"

version := "1.0"

scalaVersion := "2.9.1"

unmanagedBase <<= baseDirectory { base => base / "custom_lib" }

seq(assemblySettings: _*)
