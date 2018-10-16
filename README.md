# NovusPunishment
Player punishment plugin for Spigot

# Build Status
[![Build Status](https://ci.codemc.org/view/Author/job/EbonJaeger/job/NovusPunishment/badge/icon)](https://ci.codemc.org/view/Author/job/EbonJaeger/job/NovusPunishment/)
[![codecov](https://codecov.io/gh/EbonJaeger/NovusPunishment/branch/master/graph/badge.svg)](https://codecov.io/gh/EbonJaeger/NovusPunishment)

# Description
This plugin provides Spigot server moderators the ability to take various actions against misbehaving players. All actions taken are logged to a MySQL database in order to generate reports. The plugin only targets 1.13, but it may work on 1.12. We'll see.

### Features:
* Mute players, temporarily or permanently
* Give players a warning (configurable ability to auto-kick after x number of warnings)
* Kick a player from the server
* Temporarily ban a player from the server
* Permanently ban a player from the server (and unban, of course)
* View a player's past incidents (TODO)

# Building
This is a gradle project. All you have to do is invoke `gradle build`, and the project will compile, run tests, and build the jar.
