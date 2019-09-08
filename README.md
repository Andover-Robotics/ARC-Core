[![Codacy Badge](https://api.codacy.com/project/badge/Grade/0ed9efc1a65747c9ad806a66419d4054)](https://www.codacy.com/app/michael_47/ARC-Core?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=Andover-Robotics/ARC-Core&amp;utm_campaign=Badge_Grade)

## Welcome!
This is the common code base for the Andover Robotics teams. The code base of all three teams will include this library as a Git submodule.

## Features
* Tank Drive and Mecanum Drivetrain implementations with encoder support
* Support for reading files from storage as configuration
* PID controller implementation
* Task System support using Jython

## Setting up your Team's Repository
1. Perform `git init` locally to initialize an empty repository
2. Perform `git remote add upstream https://github.com/OpenFTC/OpenRC-Turbo.git` in order to set OpenRC Turbo as the upstream remote
3. Perform `git pull upstream master --allow-unrelated-histories -f` in order to pull OpenRC's source code into your local repository
4. Perform `git submodule add https://github.com/Andover-Robotics/ARC-Core`
5. Set the `origin` remote to your team's repository by performing `git remote add origin <origin url in either https or ssh>`
6. Push the local copy to the `origin` remote by performing `git push -u origin master`

## Getting Help
The Chief Software Officers of the 2019-2020 season, Michael Peng and Daniel Ivanovich, are eager to help you achieve your best. Feel free to approach them with questions.

You should also join the [FTC Discord](https://discord.gg/first-tech-challenge), where you can receive help from some of the best teams in the world.

## JavaDoc Reference Material
Our JavaDocs are located [here](https://Andover-Robotics.github.io/ARC-Core).